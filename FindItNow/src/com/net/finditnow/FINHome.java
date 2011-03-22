package com.net.finditnow;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

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
	}
}
