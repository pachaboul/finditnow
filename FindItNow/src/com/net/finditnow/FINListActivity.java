package com.net.finditnow;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class FINListActivity extends ListActivity {

	private String result;

	/** 
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_bg);
		setTitle(getString(R.string.app_name));

		if (getIntent().hasExtra("result")) {
			Toast.makeText(getBaseContext(), getIntent().getExtras().getString("result"), Toast.LENGTH_LONG).show();
		}
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
			final ProgressDialog myDialog = ProgressDialog.show(FINListActivity.this, "" , "Logging out...", true);
			Thread thread = new Thread() {
				@Override
				public void run() {
					final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);

					result = DBCommunicator.logout(phone_id, getBaseContext());
					if (result.equals(getString(R.string.logged_out))) {
						FINHome.setLoggedIn(false, getBaseContext());
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

		menu.findItem(R.id.search_button).setVisible(false);

		if (FINHome.isLoggedIn(getBaseContext())) {
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
}
