package com.net.finditnow;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class FINHome extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.home);
	    
	    Resources res = getResources();
	    TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
	    tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

	    addTab(new TextView(this),
	    		"Categories",
	    		res.getIdentifier("@android:drawable/ic_menu_agenda", null, getPackageName()),
	    		tabHost,
	    		new Intent().setClass(this, FINMenu.class));
	    addTab(new TextView(this),
	    		"Buildings",
	    		res.getIdentifier("@android:drawable/ic_dialog_dialer", null, getPackageName()),
	    		tabHost,
	    		new Intent().setClass(this, BuildingList.class));
	    
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
	
	private void addTab(final View view, final String tag, final Integer id, TabHost host, Intent intent) {
		View tabview = createTabView(host.getContext(), tag, id);
		TabSpec setContent = host.newTabSpec(tag).setIndicator(tabview).setContent(intent);
		host.addTab(setContent);
	}
		
	private static View createTabView(final Context context, final String text, Integer imageID) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_background, null);
		
		// Set up icon -- to be implemented later
		/*
		ImageView iv = (ImageView) view.findViewById(R.id.tabIcon);
		iv.setImageResource(imageID);
		*/
		
		// Set up label
		TextView tv = (TextView) view.findViewById(R.id.tabLabel);
		tv.setText(text);
		return view;
	}
}
