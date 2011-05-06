package com.net.finditnow;

import java.util.HashMap;

import com.google.android.maps.GeoPoint;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class FINSettings extends PreferenceActivity {

	private Context context;
	private ProgressDialog myDialog;
	private String result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
		setTitle(getString(R.string.app_name) + " > Settings");

		context = this;

		// Get the custom preference
		Preference searchHistory = findPreference("clearHistory");
		searchHistory.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Are you sure you want to clear your search history?");
				builder.setCancelable(false);
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Toast.makeText(context, "Your search history has been cleared", Toast.LENGTH_LONG).show();
						SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getBaseContext(), SearchSuggestions.AUTHORITY, SearchSuggestions.MODE);
						suggestions.clearHistory();
					}
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();

				return true;
			}

		});
		
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		final ListPreference lp = (ListPreference) findPreference("changeCampus");
		myDialog = ProgressDialog.show(context, "" , "Retrieving list of regions...", true);

		Thread myThread = new Thread() {
			public void run() {
				String campusJson = DBCommunicator.getUniversities(prefs.getInt("locationLat", 0)+"", prefs.getInt("locationLon", 0)+"", getBaseContext());
				HashMap<String, Region> campuses = JsonParser.parseUniversityJson(campusJson);
				
				String[] entries = (String[])campuses.keySet().toArray(new String[campuses.size()]);
				String[] entryValues = (String[])campuses.keySet().toArray(new String[campuses.size()]);
				lp.setEntries(entries);
				lp.setEntryValues(entryValues);
				
				myDialog.dismiss();
			}
		};
		
		myThread.run();
		
		SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {

			public void onSharedPreferenceChanged(SharedPreferences prefs, String pref) {
				if (pref.equals("changeCampus")) {
					restartFirstActivity();
				}
			}

		};
		
		prefs.registerOnSharedPreferenceChangeListener(spChanged);
	}

	/**
	 * Create the Android options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	/**
	 * Expand and define the Android options menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle the item selected
		switch (item.getItemId()) {

		// Return to the categories screen
		case R.id.home_button:
			startActivity(new Intent(this, FINHome.class));
			return true;
		case R.id.login_button:
			startActivity(new Intent(this, FINLogin.class));
			return true;
		case R.id.logout_button:
			myDialog = ProgressDialog.show(FINSettings.this, "" , "Logging out...", true);
			Thread thread = new Thread() {
				@Override
				public void run() {
					final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);

					result = DBCommunicator.logout(phone_id, getBaseContext());
					if (result.equals(getString(R.string.logged_out))) {
						FINHome.setLoggedIn(false);
					}

					myDialog.dismiss();      	

					handler.sendEmptyMessage(0);
				}
			};
			thread.start();

			return true;
		case R.id.add_new_button:
			startActivity(new Intent(this, FINAddNew.class));
			return true;
		case R.id.settings_button:
			startActivity(new Intent(this, FINSettings.class));
			return true;
		case R.id.help_button:
			startActivity(new Intent(this, FINHelp.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		menu.findItem(R.id.settings_button).setVisible(false);
		menu.findItem(R.id.search_button).setVisible(false);

		if (FINHome.isLoggedIn()) {
			menu.findItem(R.id.login_button).setVisible(false);
			menu.findItem(R.id.logout_button).setVisible(true);
		} else {
			menu.findItem(R.id.logout_button).setVisible(false);
			menu.findItem(R.id.login_button).setVisible(true);
		}

		return true;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
		}
	};

	private void restartFirstActivity() {
		Intent i = getBaseContext().getPackageManager()
		.getLaunchIntentForPackage(getBaseContext().getPackageName() );

		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
		startActivity(i);
	}
}