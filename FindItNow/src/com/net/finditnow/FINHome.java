package com.net.finditnow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

import com.google.android.maps.GeoPoint;

public class FINHome extends TabActivity {

	private static HashMap<GeoPoint, Building> buildingsMap;
	private static HashMap<String, String[]> categoriesMap;
	private static HashMap<String, Integer> iconsMap;
	private static HashMap<String, String> itemsMap;
	private static ArrayList<String> buildings;
	private static ArrayList<String> categories;
	
	private static boolean loggedin;
	
	// A constant representing the default location of the user
	// Change this the coordinates of another campus if desired (defaults to UW Seattle)
	public static final GeoPoint DEFAULT_LOCATION = new GeoPoint(47654799,-122307776);

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
				categories = JsonParser.getCategoriesList(extras.getString("categories"));
				Collections.sort(categories);
				
				// Grab the list of items and put it in the map
				// TODO: Extend this so that we make no references in the frontend to school_supplies
				categoriesMap = createCategoriesMap(categories);
				itemsMap = createItemsMap(categories);

				// Store a map from categories to icons so that other modules can use it
				iconsMap = createIconsMap(categories);

				buildingsMap = JsonParser.parseBuildingJson(extras.getString("buildings"));
				buildings = createBuildingList(buildingsMap);
				Collections.sort(buildings);

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
		}
	}

	private void addTab(final View view, final String tag, final Integer id, TabHost host, Intent intent) {
		View tabview = createTabView(host.getContext(), tag, id);
		TabSpec setContent = host.newTabSpec(tag).setIndicator(tabview).setContent(intent);
		host.addTab(setContent);
	}

	private static View createTabView(final Context context, final String text, Integer imageID) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_background, null);

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
			iconsMap.put(str, getResources().getIdentifier("drawable/"+FINUtil.sendCategory(str), null, getPackageName()));
			iconsMap.put(str + "-big", getResources().getIdentifier("drawable/"+FINUtil.sendCategory(str)+"_big", null, getPackageName()));
		}
		
		return iconsMap;
	}
	
	private HashMap<String, String[]> createCategoriesMap(ArrayList<String> categories) {
		HashMap<String, String[]> categoriesMap = new HashMap<String, String[]>();
		String[] items = getResources().getStringArray(R.array.school_supplies_items);
		
		for (String category : categories) {
			categoriesMap.put(category, category.equals("School Supplies")? items : null);
		}
		
		return categoriesMap;
	}
	
	private HashMap<String, String> createItemsMap(ArrayList<String> categories) {
		HashMap<String, String> itemsMap = new HashMap<String, String>();
		String[] items = getResources().getStringArray(R.array.school_supplies_items);
		
		for (String item : items) {
			itemsMap.put(item, "School Supplies");
		}
		
		return itemsMap;
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
	
	/**
	 * Returns the category associated with an item
	 */
	public static String getCategoryFromItem(String item) {
		return itemsMap.get(item);
	}
	
	/**
	 * Returns the item associated with a category
	 */
	public static String[] getItemsFromCategory(String category) {
		return categoriesMap.get(category);
	}
	
	/**
	 * returns true if the given category name has sub-categories (items)
	 */
	public static boolean hasItems(String category) {
		return categoriesMap.get(category) != null;
	}
	
	public static boolean isItem(String item) {
		return itemsMap.keySet().contains(item);
	}

	public static void setLoggedIn(boolean loggedin) {
		FINHome.loggedin = loggedin;
	}

	public static boolean isLoggedIn() {
		return loggedin;
	}
}
