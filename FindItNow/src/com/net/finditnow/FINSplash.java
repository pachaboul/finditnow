package com.net.finditnow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;

public class FINSplash extends Activity {
	
	protected boolean active = true;
	protected int splashTime = 2000; // time to display the splash screen in ms
	
	public static GeoPoint lastLocation;
	public static GeoPoint mapCenter;
	public static int zoomLevel;
	public static boolean isLoggedIn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fin_splash);
        
        // Initialize shared map data
		lastLocation = null;
		mapCenter = FINMap.DEFAULT_LOCATION;
		zoomLevel = 17;
		isLoggedIn = false;
        
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
                	// Check logged in status
            		final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
            		isLoggedIn = SuperUser.login(phone_id, "", "", getBaseContext()).equals(getString(R.string.login_already));
                    startActivity(new Intent(getBaseContext(), FINHome.class));
            		finish();
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
}
