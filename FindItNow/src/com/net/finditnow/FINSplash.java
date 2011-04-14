package com.net.finditnow;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;

public class FINSplash extends Activity {

	protected boolean active = true;
	protected int splashTime = 1500; // time to display the splash screen in ms
	protected Thread splashThread;

	private HashMap<String, GeoPoint> campuses;
	private HashMap<String, Integer> splashes;

	public static String campus;
	public static GeoPoint DEFAULT_LOCATION;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// thread for displaying the SplashScreen
		splashThread = new Thread() {

			@Override
			public void run() {

				Intent myIntent = null;

				//TODO: Clean up post 1.1 release.  Sync with backend
				campuses = new HashMap<String, GeoPoint>();
				campuses.put("University of Washington", new GeoPoint(47654799,-122307776));
				campuses.put("Western Washington University", new GeoPoint(48733550, -122486830));

				splashes = new HashMap<String, Integer>();
				splashes.put("University of Washington", R.drawable.uw_splash);
				splashes.put("Western Washington University", R.drawable.wwu_splash);

				// Set default location
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());  
				campus = prefs.getString("changeCampus", "");
				DEFAULT_LOCATION = campuses.get(campus);

				handler.sendEmptyMessage(0);

				// Check logged in status
				final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);

				String loggedinstr = DBCommunicator.loggedIn(phone_id, getBaseContext());
				String categories = DBCommunicator.getCategories(getBaseContext());
				String buildings = DBCommunicator.getBuildings(DEFAULT_LOCATION.getLatitudeE6()+"", DEFAULT_LOCATION.getLongitudeE6()+"", getBaseContext());

				boolean loggedin = loggedinstr.contains(getString(R.string.login_already));
				boolean readytostart = !(loggedinstr.equals(getString(R.string.timeout)) || categories.equals(getString(R.string.timeout)) 
						|| buildings.equals(getString(R.string.timeout)));

				myIntent = new Intent(getBaseContext(), FINHome.class);
				myIntent.addCategory("App Startup");
				myIntent.putExtra("categories", categories);
				myIntent.putExtra("buildings", buildings);
				myIntent.putExtra("loggedin", loggedin);
				myIntent.putExtra("readytostart", readytostart);

				if (loggedin) {
					myIntent.putExtra("username", loggedinstr.substring(21, loggedinstr.length()));
				}
				try {
					int waited = 0;

					while(active && (waited < splashTime)) {
						sleep(100);
						if (active) {
							waited += 100;
						}
					}
				} catch (InterruptedException e) {
					// do nothing
				} finally {
					startActivity(myIntent);
					finish();
				}
			}
		};

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);  
		campus = prefs.getString("changeCampus", "");

		if (campus.equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select your campus or region");
			builder.setItems(getResources().getStringArray(R.array.campuses), campus_listener);		
			builder.setCancelable(false);		

			AlertDialog alert = builder.create();
			alert.show();
		} else {
			setContentView(R.layout.fin_splash);
			splashThread.start();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			active = false;
		}
		return true;
	}

	// Listener for campus popup
	private OnClickListener campus_listener = new OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			setContentView(R.layout.fin_splash);

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());  
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("changeCampus", getBaseContext().getResources().getStringArray(R.array.campuses)[which]);
			editor.commit();

			splashThread.start();
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void  handleMessage(Message msg) {
			ImageView image = (ImageView) findViewById(R.id.splash);
			image.setImageResource(splashes.get(campus));
		}
	};
}
