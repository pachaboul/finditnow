package com.net.finditnow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;

public class FINSplash extends Activity {
	
	protected boolean active = true;
	protected int splashTime = 1500; // time to display the splash screen in ms
	
	public static GeoPoint lastLocation;
	public static GeoPoint mapCenter;
	public static int zoomLevel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fin_splash);
        
        // Check connection of Android device
		checkConnection();
		lastLocation = null;
		mapCenter = FINMap.DEFAULT_LOCATION;
		zoomLevel = 17;
        
        // thread for displaying the SplashScreen
        Thread splashThread = new Thread() {
        	
            @Override
            public void run() {
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
                    finish();
                    startActivity(new Intent(getBaseContext(), FINMenu.class));
                    stop();
                }
            }
        };
        splashThread.start();
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        active = false;
	    }
	    return true;
	}
	
	/**
	 * This method returns whether the user's internet connection is functioning
	 * 
	 * @param context The context with which to do the check
	 * 
	 * @return True if the internet connection is functional
	 */
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		
		return (netInfo != null && netInfo.isConnectedOrConnecting());
	}
	
	/**
     * Checks for an Internet connection.
     * If there is no connection, or we are unable to retrieve information about our connection,
     * display a message alerting the user about lack of connection.
     * 
     * @param context The context with which to do the check
     * 
	 * @return True if the internet connection is functional
     */
	public void checkConnection() {
		if (!isOnline(this)) {		
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Error: You must enable your data connection (Wifi or 3G) to use this app")
			
				.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						FINSplash.this.finish();
					}
				});
			
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}
