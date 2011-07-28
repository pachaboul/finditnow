package com.net.finditnow;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.MotionEvent;
import android.widget.ImageView;

public class FINSplash extends Activity {

	protected boolean active = true;
	protected int splashTime = 100; // time to display the splash screen in ms
	protected Thread splashThread;

	private ArrayList<String> campuses;
	private Cursor cursor;
	
	private boolean manual;
	
	private SQLiteDatabase db;

	private String campus;
	private int rid;
	private String campusJson;
	private ProgressDialog myDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fin_splash);
		manual = false;
		
		db = new FINDatabase(this).getReadableDatabase();

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		rid = prefs.getInt("rid", 0);

		if (rid == 0) {
			// Acquire a reference to the system Location Manager
			final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			
			// Define a listener that responds to location updates
			final LocationListener locationListener = new LocationListener() {
			    public void onLocationChanged(Location location) {
					SharedPreferences.Editor editor = prefs.edit();
					
					editor.putInt("location_lat", (int)(location.getLatitude()*1E6));
					editor.putInt("location_lon", (int)(location.getLatitude()*1E6));				
					editor.commit();
					
	        		handler4.sendEmptyMessage(0);

					locationManager.removeUpdates(this);
			    }
	
			    public void onStatusChanged(String provider, int status, Bundle extras) {}
	
			    public void onProviderEnabled(String provider) {}
	
			    public void onProviderDisabled(String provider) {}
			};
	
			// Register the listener with the Location Manager to receive location updates
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			
			myDialog = new ProgressDialog(this);
			myDialog.setTitle("Welcome to FIN");
			myDialog.setMessage("Please wait while we detect the regions nearest you...");
			myDialog.setIcon(R.drawable.icon);
	        myDialog.setCancelable(false);
			myDialog.setButton("Manually Choose", new DialogInterface.OnClickListener() {

	            public void onClick(DialogInterface dialog, int which) {
					locationManager.removeUpdates(locationListener);
					manual=true;
					myDialog.dismiss();
	        		handler4.sendEmptyMessage(0);
	            }
	         });
			myDialog.show();
		} else {		
			handler3.sendEmptyMessage(0);
		}

		// thread for displaying the SplashScreen
		splashThread = new Thread() {

			@Override
			public void run() {

				Intent myIntent = null;
				
				// Set color theme (hardcoded for now).
				Cursor cursor = db.query("colors", null, "rid = " + prefs.getInt("rid", 0)+"", null, null, null, null);
				cursor.moveToFirst();
				String color = cursor.getString(cursor.getColumnIndex("color1"));
								
				FINTheme.setTheme(color, getBaseContext());
				
				cursor = db.query("regions", null, "rid = " + prefs.getInt("rid", 0)+"", null, null, null, null);
				cursor.moveToFirst();
				campus = cursor.getString(cursor.getColumnIndex("name"));
				
				// Set default location
				handler.sendEmptyMessage(0);
				
				myIntent = new Intent(getBaseContext(), FINHome.class);
				myIntent.addCategory("App Startup");

				// Check logged in status
				final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
				
				String loggedinstr = DBCommunicator.loggedIn(phone_id, getBaseContext());
				boolean loggedin = loggedinstr.contains(getString(R.string.login_already));
				
				campusJson = DBCommunicator.getRegions(prefs.getInt("location_lat", 0)+"", prefs.getInt("location_lon", 0)+"", getBaseContext());
				if (!campusJson.equals(getString(R.string.timeout))) JsonParser.parseRegionJson(campusJson, getBaseContext());	
								
				String categories = DBCommunicator.getCategories(getBaseContext());
				if (!categories.equals(getString(R.string.timeout))) JsonParser.parseCategoriesList(categories, getBaseContext());
				
				String buildings = DBCommunicator.getBuildings(prefs.getInt("rid", 0)+"", getBaseContext());
				if (!buildings.equals(getString(R.string.timeout)))JsonParser.parseBuildingJson(buildings, getBaseContext());
				
				String items = DBCommunicator.getItems(prefs.getInt("rid", 0)+"", getBaseContext());
				if (!items.equals(getString(R.string.timeout)))JsonParser.parseItemJson(items, getBaseContext());
				
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("loggedin", loggedin);
				editor.putString("lastOpened", System.currentTimeMillis() / 1000 + "");
				editor.commit();
				
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
					cursor.close();
					
					startActivity(myIntent);
					finish();
				}
			}
		};
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (cursor != null) {
			cursor.close();
		}
		
		db.close();
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
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());  
			SharedPreferences.Editor editor = prefs.edit();
			
			cursor.moveToPosition(which);
			
			campus = cursor.getString(cursor.getColumnIndex("full_name"));
			editor.putInt("rid", cursor.getInt(cursor.getColumnIndex("rid")));
			editor.commit();

			splashThread.start();
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setContentView(R.layout.fin_splash);
			
			ImageView image = (ImageView) findViewById(R.id.splash_img);
			image.setImageResource(getSplash(campus));
		}
	};
	
	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {			
			cursor = db.query("regions", null, null, null, null, null, null);
			cursor.moveToFirst();
			
			if (manual) {
				selectCampus();
			} else {
				String region = cursor.getString(cursor.getColumnIndex("full_name"));
				
				AlertDialog.Builder builder = new AlertDialog.Builder(FINSplash.this);
				builder.setTitle("Region Selection");
				builder.setIcon(R.drawable.icon);
				builder.setMessage("We have detected your nearest campus/region as:\n\n" + region +"\n\nIs this correct?");
				builder.setCancelable(false);
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());  
						SharedPreferences.Editor editor = prefs.edit();						
						editor.putInt("rid", cursor.getInt(cursor.getColumnIndex("rid")));
						editor.commit();
										
						splashThread.start();
					}
				});
				builder.setNegativeButton("No, Let me choose", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {					
						selectCampus();
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}
	};
	
	private void selectCampus() {
		AlertDialog.Builder builder = new AlertDialog.Builder(FINSplash.this);
		builder.setTitle("Select your campus or region");
		
		campuses = new ArrayList<String>();
		while (!cursor.isAfterLast()) {
			campuses.add(cursor.getString(cursor.getColumnIndex("full_name")));
			cursor.moveToNext();
		}
		
		builder.setItems((String[])campuses.toArray(new String[campuses.size()]), campus_listener);
		builder.setCancelable(false);		

		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private Handler handler3 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			splashThread.start();
		}
	};
	
	private Handler handler4 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (myDialog.isShowing()) {
				myDialog.dismiss();
			}
			
			if (!connectionThread.isAlive()) {
				connectionThread.start();
			}
		}
	};
	
	Thread connectionThread = new Thread() {
		public void run() {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			campusJson = DBCommunicator.getRegions(prefs.getInt("location_lat", 0)+"", prefs.getInt("location_lon", 0)+"", getBaseContext());
			
    		if (campusJson.equals(getString(R.string.timeout))) {
    			failureHandler.sendEmptyMessage(0);
    		} else {
				JsonParser.parseRegionJson(campusJson, getBaseContext());				
				handler2.sendEmptyMessage(0);
    		}
		}
	};
	
	private Handler failureHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
    		ConnectionChecker conCheck = new ConnectionChecker(getBaseContext(), FINSplash.this);
    		conCheck.connectionError();
		}
	};
	
	public int getSplash(String reg) {
		return getResources().getIdentifier("com.net.finditnow:drawable/splash_" + reg, "drawable", getPackageName());
	}
}
