package com.net.finditnow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

public class FINHome extends TabActivity {

	private static HashMap<String, Integer> iconsMap;

	private static boolean loggedin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		Bundle extras = getIntent().getExtras(); 

		ConnectionChecker conCheck = new ConnectionChecker(this, FINHome.this);
		if (getIntent().hasExtra("readytostart") && !extras.getBoolean("readytostart")) {
			conCheck.connectionError();
		} else {
			// Generate our list of categories from the database
			if (getIntent().hasCategory("App Startup")) {
				// Grab the list of items and put it in the map
				// TODO: Extend this so that we make no references in the frontend to school_supplies
				ArrayList<String> categories = getCategoriesList(false, getBaseContext());

				// Store a map from categories to icons so that other modules can use it
				iconsMap = createIconsMap(categories);

				loggedin = extras.getBoolean("loggedin");
				if (isLoggedIn()) {
					Toast.makeText(getBaseContext(), "Welcome back " + extras.getString("username"), Toast.LENGTH_LONG).show();
				}
			}

			Resources res = getResources();
			TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
			tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

			addTab(new TextView(this),
					"Categories",
					res.getIdentifier("@drawable/categories_tab_icon", null, getPackageName()),
					tabHost,
					new Intent().setClass(this, FINMenu.class));
			addTab(new TextView(this),
					"Buildings",
					res.getIdentifier("@drawable/buildings_tab_icon", null, getPackageName()),
					tabHost,
					new Intent().setClass(this, BuildingList.class));
			
			// And recolor the strip.
			View tabStrip = (View) findViewById(R.id.fronttab_strip);
			tabStrip.setBackgroundResource(FINTheme.getBrightColor());
		}
	}

	private void addTab(final View view, final String tag, final Integer id, TabHost host, Intent intent) {
		View tabview = createTabView(host.getContext(), tag, id);
		TabSpec setContent = host.newTabSpec(tag).setIndicator(tabview).setContent(intent);
		host.addTab(setContent);
	}

	private View createTabView(final Context context, final String text, Integer imageID) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_background, null);
		view.setBackgroundDrawable(getResources().getDrawable(FINTheme.getTabSelector()));        

		// Set up icon 
		ImageView iv = (ImageView) view.findViewById(R.id.tabIcon);
		iv.setImageResource(imageID);

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
	public HashMap<String, Integer> createIconsMap(ArrayList<String> categories) {
		HashMap<String, Integer> iconsMap = new HashMap<String, Integer>();

		// Loop over each category and map it to the icon file associated with it
		for (String str : categories) {
			iconsMap.put(str, getResources().getIdentifier("drawable/"+FINUtil.sendCategory(str, getBaseContext()), null, getPackageName()));
			iconsMap.put(str + "-big", getResources().getIdentifier("drawable/"+FINUtil.sendCategory(str, getBaseContext())+"_big", null, getPackageName()));
		}

		return iconsMap;
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
		if (!category.equals("")) {
			int bigIcon = iconsMap.get(category + "-big");
			if (bigIcon != 0) {
				return bigIcon;
			}
		}
		return R.drawable.android;
	}

	/**
	 * Returns the building associated with the GeoPoint
	 */
	public static Building getBuilding(GeoPoint point, Context context) {
		FINDatabase db = new FINDatabase(context);
		Cursor cursor = db.getReadableDatabase().query("buildings", null, "latitude = '" + point.getLatitudeE6() + "' AND longitude = '" + point.getLongitudeE6() + "'", null, null, null, null);
		cursor.moveToFirst();
		
		int bid = cursor.getInt(cursor.getColumnIndex("bid"));
		String name = cursor.getString(cursor.getColumnIndex("name"));
		
		cursor = db.getReadableDatabase().query("floors", null, "bid = '" + bid + "'", null, null, null, null);
		cursor.moveToLast();
		int size = cursor.getPosition() + 1;
		cursor.moveToFirst();
		
		int[] fids = new int[size];
		String[] names = new String[size];
		for (int i = 0; i < size; i++) {
			fids[i] = cursor.getInt(cursor.getColumnIndex("fid"));
			names[i] = cursor.getString(cursor.getColumnIndex("name"));
			cursor.moveToNext();
		}
		cursor.close();
		
		return new Building(bid, name, fids, names);
	}

	/**
	 * Returns an ArrayList of buildings
	 */
	public static ArrayList<String> getBuildingsList(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int rid = prefs.getInt("rid", 0);
		
		ArrayList<String> buildings = new ArrayList<String>();
		FINDatabase db = new FINDatabase(context);
		Cursor cursor = db.getReadableDatabase().query("buildings", null, "rid = " + rid, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			buildings.add(cursor.getString(cursor.getColumnIndex("name")));
			Log.v("BUILDINZ", cursor.getString(cursor.getColumnIndex("name")));
			cursor.moveToNext();
		}
		cursor.close();
		Collections.sort(buildings);
		
		return buildings;
	}


	/**
	 * Returns an ArrayList of top-level categories
	 */
	public static ArrayList<String> getCategoriesList(boolean subcategories, Context context) {
		ArrayList<String> categories = new ArrayList<String>();
		
		FINDatabase db = new FINDatabase(context);
		Cursor cursor = db.getReadableDatabase().query("categories", null, subcategories? null : "parent = 0", null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			categories.add(cursor.getString(cursor.getColumnIndex("full_name")));
			cursor.moveToNext();
		}

		Collections.sort(categories);
		return categories;
	}
	
	public static ArrayList<String> getSubcategories(String cat, Context context) {
		ArrayList<String> subcategories = new ArrayList<String>();
		
		FINDatabase db = new FINDatabase(context);
		Cursor cursor = db.getReadableDatabase().query("categories", null, "full_name = '" + cat + "'", null, null, null, null);
		cursor.moveToFirst();
		int cat_id = cursor.getInt(cursor.getColumnIndex("cat_id"));
		cursor = db.getReadableDatabase().query("categories", null, "parent = " + cat_id, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			subcategories.add(cursor.getString(cursor.getColumnIndex("full_name")));
			cursor.moveToNext();
		}

		Collections.sort(subcategories);
		return subcategories;
	}


	/**
	 * Returns the GeoPoint associated with the building.
	 * Icon overlays are positioned on these GeoPoints.
	 * 
	 * @param buildingName The full name of the building.
	 * @return GeoPoint representing the 'center' of the building.
	 */
	public static GeoPoint getGeoPointFromBuilding(String buildingName, Context context) {
		FINDatabase db = new FINDatabase(context);
		Cursor cursor = db.getReadableDatabase().query("buildings", null, "name = '" + buildingName + "'", null, null, null, null);
		cursor.moveToFirst();
		
		int latitude = cursor.getInt(cursor.getColumnIndex("latitude"));
		int longitude = cursor.getInt(cursor.getColumnIndex("longitude"));
		
		return new GeoPoint(latitude, longitude);
	}

	/**
	 * Returns a miniature-sized icon associated with the category
	 * These icons are used for the map overlays and dialog windows.
	 * 
	 * @param category The top-level category
	 * @return If no icon is found, return the default Android icon.
	 * otherwise, return the appropriate category icon.
	 */
	public static Integer getIcon(String category, Context context) {
		
		FINDatabase db = new FINDatabase(context);
		Cursor cursor = db.getReadableDatabase().query("categories", null, "full_name = '" + category+ "'", null, null, null, null);
		cursor.moveToFirst();
		
		int parent = cursor.getInt(cursor.getColumnIndex("parent"));
		if (parent != 0) {
			cursor = db.getReadableDatabase().query("categories", null, "cat_id = " + parent, null, null, null, null);
			cursor.moveToFirst();
			category = cursor.getString(cursor.getColumnIndex("full_name"));
		}
		
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
