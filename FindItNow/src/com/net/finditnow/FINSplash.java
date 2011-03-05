package com.net.finditnow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class FINSplash extends Activity {
	
	protected boolean active = true;
	protected int splashTime = 1500; // time to display the splash screen in ms
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fin_splash);
        
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
}
