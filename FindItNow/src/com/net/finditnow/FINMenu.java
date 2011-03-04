/*
 * This class displays the menu of buttons
 * each corresponding to the eight categories
 * Simple options will launch the Map; options
 * with sub-categories will launch CategoryList.
 * 
 * This is the class that is first shown when FIN is
 * launched.
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
	
	// On launch, show menu.xml layout, set up grid.
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
        // Check connection of Android device
		checkConnection();
		
		// Generate our list of categories from the database
		JSONArray listOfCategories = Request.requestFromDB(null, null, null);
		categories = getCategoriesList(listOfCategories);
		Collections.sort(categories);
		
        // Store a map from categories to icons so that other modules can use it
        iconsMap = createIconsList();
		
		// Generate list of buildings from the database
		JSONArray listOfBuildings = Request.requestFromDB("", null, null);
		buildingsMap = JsonParser.parseBuildingJson(listOfBuildings.toString());
		buildings = createBuildingList(buildingsMap);
		Collections.sort(buildings);
		
		GridView buttonGrid = (GridView) findViewById(R.id.gridview);
        buttonGrid.setAdapter(new ButtonAdapter(this));
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.findItem(R.id.categories_button).setVisible(false);
    	menu.findItem(R.id.add_new_button).setVisible(true);
    	menu.findItem(R.id.my_location_button).setVisible(false);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.help_button:
	        	startActivity(new Intent(this, FINHelp.class));
	            return true;
	        case R.id.add_new_button:
	        	startActivity(new Intent(this, FINAddNew.class));
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
	
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
	
	// Check if we have a data connection available
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
	
    /** This method returns a map from categories to icons (icons must be the same name as the category, in lowercase */
    private HashMap<String, Integer> createIconsList() {
    	HashMap<String, Integer> iconsMap = new HashMap<String, Integer>();

    	// Loop over each category and map it to the icon file associated with it
    	for (String str : categories) {
			iconsMap.put(str, getResources().getIdentifier("drawable/"+str, null, getPackageName()));
			iconsMap.put(str + "-icon", getResources().getIdentifier("drawable/"+str+"_big", null, getPackageName()));
		}
    	
		return iconsMap;
    }

	// This class/list feeds into the grid view.
	public class ButtonAdapter extends BaseAdapter {
    	private Context mContext;
    	
    	public ButtonAdapter(Context c) {
    		setmContext(c);
    	}

    	public int getCount() {
    		return categories.size();
    	}

    	public Object getItem(int position) {
    		return null;
    	}

    	public long getItemId(int position) {
    		return 0;
    	}

    	// Sets up the view shown within each grid cell.
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
			
			if (position == 1 || position == 6) {
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

		public void setmContext(Context mContext) {
			this.mContext = mContext;
		}

		public Context getmContext() {
			return mContext;
		}
    }

	
	public static ArrayList<String> createBuildingList(HashMap<GeoPoint, Building> map) {
		ArrayList<String> list = new ArrayList<String>();
		for (GeoPoint point : map.keySet()) {
			list.add(map.get(point).getName());
		}
		return list;
	}
	
	public static ArrayList<String> getBuildingsList() {
		return buildings;
	}
	
	public static ArrayList<String> getCategoriesList() {
		return categories;
	}
    
    public static Building getBuilding(GeoPoint point) {
    	return buildingsMap.get(point);
    }
    
    /** This method returns the icons map */
    public static Integer getIcon(String category) {
    	int icon = iconsMap.get(category);
    	if (icon == 0) {
    		return R.drawable.android;
    	} else {
    		return icon;
    	}
    }
    
    public static Integer getBigIcon(String category) {
    	int bigIcon = iconsMap.get(category + "-icon");
    	if (bigIcon == 0) {
    		return R.drawable.android;
    	} else {
    		return bigIcon;
    	}
    }
    
	public static GeoPoint getGeoPointFromBuilding(String buildingName) {
		for (GeoPoint point : buildingsMap.keySet()) {
			if (getBuilding(point).getName().equals(buildingName)) {
				return point;
			}
		}
		return null;
	}
}
