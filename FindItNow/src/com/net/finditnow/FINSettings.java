package com.net.finditnow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class FINSettings extends PreferenceActivity {
	
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
		setTitle(getString(R.string.app_name) + " > Settings");
		
		context = this;
		
		// Get the custom preference
		Preference searchHistory = (Preference) findPreference("clearHistory");
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
			final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
    		
    		String result = DBCommunicator.logout(phone_id, getBaseContext());
    		if (result.equals(getString(R.string.logged_out))) {
    			FINHome.setLoggedIn(false);
    		}
    		
    		Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
    		
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
}