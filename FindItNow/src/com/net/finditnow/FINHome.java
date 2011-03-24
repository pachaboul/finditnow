package com.net.finditnow;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;

public class FINHome extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.home);
	    
	    Resources res = getResources(); 
	    TabHost tabHost = getTabHost(); 
	    TabHost.TabSpec spec; 
	    Intent intent;  

	    intent = new Intent().setClass(this, FINMenu.class);

	    // Add Categories tab
	    spec = tabHost.newTabSpec("categories").setIndicator("Categories", res.getDrawable(android.R.drawable.ic_menu_agenda)).setContent(intent);
	    tabHost.addTab(spec);

	    // Add Buildings tab
	    intent = new Intent().setClass(this, BuildingList.class);
	    spec = tabHost.newTabSpec("buildings").setIndicator("Buildings", res.getDrawable(android.R.drawable.ic_dialog_dialer)).setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	    
	    if (getIntent().hasExtra("username")) {
	    	Bundle extras = getIntent().getExtras(); 
			String username = extras.getString("username");
	    	Toast.makeText(getBaseContext(), "Welcome back " + username, Toast.LENGTH_LONG).show();
	    }
	    
	    if (getIntent().hasExtra("result")) {
	    	Bundle extras = getIntent().getExtras(); 
			String result = extras.getString("result");
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
	    }
	}
}
