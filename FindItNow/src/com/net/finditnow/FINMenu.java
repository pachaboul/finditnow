/**
 * This class displays the menu of buttons
 * each corresponding each category.  Simple options will launch the Map;
 * options with sub-categories will launch CategoryList.
 * 
 * Initial database retrieving routines are also executed before
 * the button grid is drawn.  An Internet connection is also checked.
 * 
 * This is the class that is first shown when FIN is
 * launched, after the splash screen.
 */
package com.net.finditnow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

public class FINMenu extends Activity {
	
	private static HashMap<GeoPoint, Building> buildingsMap;
	private static HashMap<String, Integer> iconsMap;
	private static ArrayList<String> categories;
	private static ArrayList<String> buildings;
	
	/**
     * Check for a connection, generate our categories and buildings list
     * from the database, and set up the grid layout of buttons.
     */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
        // Check connection of Android device
		checkConnection();
		
		// Generate our list of categories from the database
		JSONArray listOfCategories = Get.requestFromDB(null, null, null);
		categories = getCategoriesList(listOfCategories);
		Collections.sort(categories);
		
        // Store a map from categories to icons so that other modules can use it
        iconsMap = createIconsList();
		
		// Generate list of buildings from the database
		JSONArray listOfBuildings = Get.requestFromDB("", null, null);
		buildingsMap = JsonParser.parseBuildingJson(listOfBuildings.toString());
		buildings = createBuildingList(buildingsMap);
		Collections.sort(buildings);
		
		// Populate the grid with category buttons.
		GridView buttonGrid = (GridView) findViewById(R.id.gridview);
        buttonGrid.setAdapter(new ButtonAdapter(this));
	}
	
	/**
     * Creates the Android options menu
     */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    
	/**
     * Prepares the options menu before being displayed.
     * Removes redundant Category option, and centering location
     * option (special for the Map only).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.findItem(R.id.categories_button).setVisible(false);
    	menu.findItem(R.id.my_location_button).setVisible(false);
    	return true;
    }
    
    /**
     * Expand and define the Android options menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        	case R.id.add_new_button:
        		startActivity(new Intent(this, FINAddNew.class));
        		return true;
	        case R.id.help_button:
	        	startActivity(new Intent(this, FINHelp.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
	
	/**
     * Returns an ArrayList of categories, duplicated from the given JSONArray list.
     * The list is almost exactly the same, with the exclusion of regions, floors,
     * and exception of "school_supplies" (which is added as "supplies").
     * 
     * @param listOfCategories List of categories, in JSONArray form.
     */
	public ArrayList<String> getCategoriesList(JSONArray listOfCategories) {
		ArrayList<String> category_list = new ArrayList<String>();
		for (int i = 0; i < listOfCategories.length(); i++) {
		    try {
		    	String category = listOfCategories.getString(i);
		    	if (!category.equals("regions") && !category.equals("floors")) {
		    		if (category.equals("school_supplies")) {
		    			category_list.add("supplies");
		    		} else {
		    			category_list.add(category);
		    		}
		    	}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return category_list;
	}
	
	/**
     * Checks for an Internet connection.
     * If there is no connection, or we are unable to retrieve information about our connection,
     * display a message alerting the user about lack of connection.
     */
	public void checkConnection() {
		ConnectivityManager conman=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conman.getActiveNetworkInfo();
		
		if (info == null || !info.isConnected()) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Error: You must enable your data connection (Wifi or 3g) to use this app")
			
				.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						FINMenu.this.finish();
					}
				});
			
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	
    /**
     * Returns a map from categories to icons (icons must be the same lower-case string as the category)
     * Populates the HashMap with both small icons and bigger icons.
     * Key for small icons: "<category>"
     * Key for big icons:  "<category>-icon"
	 */
    private HashMap<String, Integer> createIconsList() {
    	HashMap<String, Integer> iconsMap = new HashMap<String, Integer>();

    	// Loop over each category and map it to the icon file associated with it
    	for (String str : categories) {
			iconsMap.put(str, getResources().getIdentifier("drawable/"+str, null, getPackageName()));
			iconsMap.put(str + "-icon", getResources().getIdentifier("drawable/"+str+"_big", null, getPackageName()));
		}
    	
		return iconsMap;
    }

	/**
	 * This class populates the grid view.
	 * It is a list of image buttons
	 */
	public class ButtonAdapter extends BaseAdapter {
    	private Context mContext;
    	
    	/**
    	 * Constructor for ButtonAdapter
    	 * @param c The context of the client class.
    	 */
    	public ButtonAdapter(Context c) {
    		setmContext(c);
    	}
    	
    	/**
    	 * The number of items in the list
    	 */
    	public int getCount() {
    		return categories.size();
    	}

    	/**
    	 * Stub method; it doesn't matter what we return.
    	 * @param position
    	 * @return null (default)
    	 */
    	public Object getItem(int position) {
    		return null;
    	}
    	
    	/**
    	 * Stub method; it doesn't matter what we return.
    	 * @param position
    	 * @return 0 (default)
    	 */
    	public long getItemId(int position) {
    		return 0;
    	}

    	/**
    	 * Sets up the view shown in each grid cell:
    	 * The image button and the text displayed on top
    	 * @param position The index of the button
    	 * @param convertView The view to be returned
    	 * @param parent The parent ViewGroup that will house the view.
    	 * @return The generated view for this position.
    	 */
    	public View getView(int position, View convertView, ViewGroup parent) {
    		View myView;
			
    		// If not created yet, initialize it.
    		if (convertView == null) {	
    			LayoutInflater li = getLayoutInflater();
    			myView = li.inflate(R.layout.grid_item, null);
    		} else {
    			myView = convertView;
    		}
    		
    		// Add image button
			ImageButton ib = (ImageButton) myView.findViewById(R.id.grid_item_button);
			
			final String category = categories.get(position);
			ib.setImageResource(getBigIcon(category));
			
			if (category.equals("supplies") || category.equals("buildings")) {
				// Jump to CategoryList
				ib.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent myIntent = new Intent(v.getContext(), CategoryList.class);
		                myIntent.putExtra("category", category);
		                startActivity(myIntent);
					}
    			});
			} else {
				// Otherwise, jump to map
    			ib.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent myIntent = new Intent(v.getContext(), FINMap.class);
		                myIntent.putExtra("category", category);
		                startActivity(myIntent);
					}
    			});
			}
			
			// Add text above button.
			TextView tv = (TextView) myView.findViewById(R.id.grid_item_text);
	    	tv.setText(FINUtil.capFirstChar(category));
    		
    		return myView;
    	}
    	
    	/**
    	 * Sets the passed context to be our own context
    	 * @param mContext
    	 */
		public void setmContext(Context mContext) {
			this.mContext = mContext;
		}

		/**
    	 * Returns the class's own context
    	 * @return Returns the class's private context
    	 */
		public Context getmContext() {
			return mContext;
		}
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
     * Returns the building associated with the GeoPoint
     */
    public static Building getBuilding(GeoPoint point) {
    	return buildingsMap.get(point);
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
    	int icon = iconsMap.get(category);
    	if (icon == 0) {
    		return R.drawable.android;
    	} else {
    		return icon;
    	}
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
    	int bigIcon = iconsMap.get(category + "-icon");
    	if (bigIcon == 0) {
    		return R.drawable.android;
    	} else {
    		return bigIcon;
    	}
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
}