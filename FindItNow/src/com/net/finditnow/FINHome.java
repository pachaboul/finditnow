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
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

import com.google.android.maps.GeoPoint;

public class FINHome extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		Bundle extras = getIntent().getExtras(); 
		// Generate our list of categories from the database
		if (getIntent().hasCategory("App Startup")) {
			
			if (isLoggedIn(getBaseContext())) {
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
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String color = prefs.getString("color", "green");
		
		View tabStrip = (View) findViewById(R.id.fronttab_strip);
		tabStrip.setBackgroundResource(FINTheme.getBrightColor(color, getBaseContext()));
	}


	private void addTab(final View view, final String tag, final Integer id, TabHost host, Intent intent) {
		View tabview = createTabView(host.getContext(), tag, id);
		TabSpec setContent = host.newTabSpec(tag).setIndicator(tabview).setContent(intent);
		host.addTab(setContent);
	}

	private View createTabView(final Context context, final String text, Integer imageID) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String color = prefs.getString("color", "green");
		
		View view = LayoutInflater.from(context).inflate(R.layout.tab_background, null);
		view.setBackgroundDrawable(getResources().getDrawable(FINTheme.getTabSelector(color, getBaseContext())));        

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
	public static Integer getBigIcon(String category, Context context) {
		SQLiteDatabase db = new FINDatabase(context).getReadableDatabase();
		
		Cursor cursor = db.query("categories", null, "full_name = '" + category + "'", null, null, null, null);
		cursor.moveToFirst();
		
		int parent = cursor.getInt(cursor.getColumnIndex("parent"));
		if (parent != 0) {
			cursor = db.query("categories", null, "cat_id = " + parent, null, null, null, null);
			cursor.moveToFirst();
			category = cursor.getString(cursor.getColumnIndex("full_name"));
		}
		
		cursor.close();
		db.close();
		
		if (!category.equals("")) {
			int bigIcon = context.getResources().getIdentifier("drawable/" + FINUtil.sendCategory(category, context) + "_big", null, context.getPackageName());
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
		Building build = null;
		
		SQLiteDatabase db = new FINDatabase(context).getReadableDatabase();
		Cursor cursor = db.query("buildings", null, "latitude = '" + point.getLatitudeE6() + "' AND longitude = '" + point.getLongitudeE6() + "'", null, null, null, null);
		
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			int bid = cursor.getInt(cursor.getColumnIndex("bid"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			
			cursor = db.query("floors", null, "bid = " + bid, null, null, null, "fnum DESC");
			cursor.moveToFirst();
			
			int count = cursor.getCount();
			
			int[] fids = new int[count];
			String[] names = new String[count];
			for (int i = 0; i < count; i++) {
				fids[i] = cursor.getInt(cursor.getColumnIndex("fid"));
				names[i] = cursor.getString(cursor.getColumnIndex("name"));
				
				cursor.moveToNext();
			}
			
			build = new Building(bid, name, fids, names);
		}
		
		cursor.close();
		db.close();
		
		return build;
	}

	/**
	 * Returns an ArrayList of buildings
	 */
	public static ArrayList<String> getBuildingsList(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int rid = prefs.getInt("rid", 0);
		
		SQLiteDatabase db = new FINDatabase(context).getReadableDatabase();
				
		ArrayList<String> buildings = new ArrayList<String>();
		Cursor cursor = db.query("buildings", null, "rid = " + rid, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			buildings.add(cursor.getString(cursor.getColumnIndex("name")));
			cursor.moveToNext();
		}
		
		Collections.sort(buildings);
		
		cursor.close();
		db.close();
		
		return buildings;
	}


	/**
	 * Returns an ArrayList of top-level categories
	 */
	public static ArrayList<String> getCategoriesList(boolean subcategories, Context context) {
		ArrayList<String> categories = new ArrayList<String>();
		
		SQLiteDatabase db = new FINDatabase(context).getReadableDatabase();
		
		Cursor cursor = db.query("categories", null, subcategories? null : "parent = 0", null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			categories.add(cursor.getString(cursor.getColumnIndex("full_name")));
			cursor.moveToNext();
		}

		Collections.sort(categories);
		
		cursor.close();
		db.close();
		
		return categories;
	}
	
	public static ArrayList<String> getSubcategories(String cat, Context context) {
		ArrayList<String> subcategories = new ArrayList<String>();
		
		SQLiteDatabase db = new FINDatabase(context).getReadableDatabase();
		
		Cursor cursor = db.query("categories", null, "full_name = '" + cat + "'", null, null, null, null);
		cursor.moveToFirst();
		int cat_id = cursor.getInt(cursor.getColumnIndex("cat_id"));
		cursor = db.query("categories", null, "parent = " + cat_id, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			subcategories.add(cursor.getString(cursor.getColumnIndex("full_name")));
			cursor.moveToNext();
		}

		Collections.sort(subcategories);
		
		cursor.close();
		db.close();
		
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
		SQLiteDatabase db = new FINDatabase(context).getReadableDatabase();
		
		Cursor cursor = db.query("buildings", null, "name = '" + buildingName + "'", null, null, null, null);
		cursor.moveToFirst();
		
		int latitude = cursor.getInt(cursor.getColumnIndex("latitude"));
		int longitude = cursor.getInt(cursor.getColumnIndex("longitude"));
		
		cursor.close();
		db.close();
		
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
		SQLiteDatabase db = new FINDatabase(context).getReadableDatabase();
		
		Cursor cursor = db.query("categories", null, "full_name = '" + category+ "'", null, null, null, null);
		cursor.moveToFirst();
		
		int parent = cursor.getInt(cursor.getColumnIndex("parent"));
		if (parent != 0) {
			cursor = db.query("categories", null, "cat_id = " + parent, null, null, null, null);
			cursor.moveToFirst();
			category = cursor.getString(cursor.getColumnIndex("full_name"));
		}
		
		if (!category.equals("")) {
			int icon = context.getResources().getIdentifier("drawable/" + FINUtil.sendCategory(category, context), null, context.getPackageName());
			if (icon != 0) {
				return icon;
			}
		}
		
		cursor.close();
		db.close();
		
		return R.drawable.android;
	}

	public static void setLoggedIn(boolean loggedin, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.putBoolean("loggedin", loggedin);
		editor.commit();
	}

	public static boolean isLoggedIn(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		return prefs.getBoolean("loggedin", false);
	}
}
