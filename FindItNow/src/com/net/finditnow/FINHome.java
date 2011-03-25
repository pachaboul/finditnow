package com.net.finditnow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.google.android.maps.GeoPoint;
import com.net.finditnow.FINMenu.ButtonAdapter;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class FINHome extends TabActivity {
	
	private static HashMap<GeoPoint, Building> buildingsMap;
	private static HashMap<String, Integer> iconsMap;
	private static ArrayList<String> categories;
	private static ArrayList<String> buildings;
	private static boolean loggedin;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.home);
	    
	    Bundle extras = getIntent().getExtras(); 

		// Generate our list of categories from the database
	    if (getIntent().hasCategory("App Startup")) {
			categories = JsonParser.getCategoriesList(extras.getString("categories"));
			Collections.sort(categories);
			
			// Store a map from categories to icons so that other modules can use it
			iconsMap = createIconsList(categories, getApplicationContext());
			
			buildingsMap = JsonParser.parseBuildingJson(extras.getString("buildings"));
			buildings = createBuildingList(buildingsMap);
			Collections.sort(buildings);
			
			loggedin = extras.getBoolean("loggedin");
			if (isLoggedIn()) {
		    	Toast.makeText(getBaseContext(), "Welcome back " + extras.getString("username"), Toast.LENGTH_LONG).show();
			}
	    }
	    
	    if (getIntent().hasExtra("result")) {
	    	Toast.makeText(getBaseContext(), extras.getString("result"), Toast.LENGTH_LONG).show();
	    }
	    
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
	
	/**
	 * Returns a map from categories to icons (icons must be the same lower-case string as the category)
	 * Populates the HashMap with both small icons and bigger icons.
	 * Key for small icons: "<category>"
	 * Key for big icons:  "<category>-big"
	 */
	public static HashMap<String, Integer> createIconsList(ArrayList<String> categories, Context c) {
		HashMap<String, Integer> iconsMap = new HashMap<String, Integer>();

		// Loop over each category and map it to the icon file associated with it
		for (String str : categories) {
			iconsMap.put(str, c.getResources().getIdentifier("drawable/"+str, null, c.getPackageName()));
			iconsMap.put(str + "-big", c.getResources().getIdentifier("drawable/"+str+"_big", null, c.getPackageName()));
		}

		return iconsMap;
	}
	
	/**
	 * Returns an ArrayList of unique buildings from a HashMap of GeoPoints and buildings.
	 */
	public static ArrayList<String> createBuildingList(HashMap<GeoPoint, Building> map) {
		ArrayList<String> list = new ArrayList<String>();
		for (GeoPoint point : map.keySet()) {
			list.add(map.get(point).getName());
		}
		return list;
	}

	/**
	 * Returns a large-sized icon associated with the category
	 * These icons are used for the menu's grid buttons.
	 * 
	 * @param category The top-level category
	 * @return If no icon is found, return the default Android icon.
	 * Otherwise, return the appropriate category icon.
	 */
	public static Integer getBigIcon(String category) {
		int bigIcon = iconsMap.get(category + "-big");
		if (bigIcon == 0) {
			return R.drawable.android;
		} else {
			return bigIcon;
		}
	}

	/**
	 * Returns the building associated with the GeoPoint
	 */
	public static Building getBuilding(GeoPoint point) {
		return buildingsMap.get(point);
	}

	/**
	 * Returns an ArrayList of buildings
	 */
	public static ArrayList<String> getBuildingsList() {
		return buildings;
	}


	/**
	 * Returns an ArrayList of top-level categories
	 */
	public static ArrayList<String> getCategoriesList() {
		return categories;
	}


	/**
	 * Returns the GeoPoint associated with the building.
	 * Icon overlays are positioned on these GeoPoints.
	 * 
	 * @param buildingName The full name of the building.
	 * @return GeoPoint representing the 'center' of the building.
	 */
	public static GeoPoint getGeoPointFromBuilding(String buildingName) {
		for (GeoPoint point : buildingsMap.keySet()) {
			if (getBuilding(point).getName().equals(buildingName)) {
				return point;
			}
		}
		return null;
	}

	/**
	 * Returns a miniature-sized icon associated with the category
	 * These icons are used for the map overlays and dialog windows.
	 * 
	 * @param category The top-level category
	 * @return If no icon is found, return the default Android icon.
	 * otherwise, return the appropriate category icon.
	 */
	public static Integer getIcon(String category) {
		if (!category.equals("")) {
			int icon = iconsMap.get(category);
			if (icon != 0) {
				return icon;
			}
		}
		return R.drawable.android;
	}

	public static void setLoggedIn(boolean loggedin) {
		FINHome.loggedin = loggedin;
	}

	public static boolean isLoggedIn() {
		return loggedin;
	}
}
