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

import java.util.Map;
import java.util.HashMap;

import org.json.JSONArray;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

public class FINMenu extends Activity {
	
	private static Map<GeoPoint, Building> buildings;
	private static HashMap<String, Integer> icons;
	
	// On launch, show menu.xml layout, set up grid.
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
        // Store a map from categories to icons so that other modules can use it
        icons = createIconsList();
		
		checkConnection();
		
		JSONArray listOfBuildings = Request.requestFromDB("", "", null);
		buildings = JsonParser.parseBuildingJson(listOfBuildings.toString());
		
		GridView buttonGrid = (GridView) findViewById(R.id.gridview);
        buttonGrid.setAdapter(new ButtonAdapter(this));
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
			iconsMap.put(str.toLowerCase(), getResources().getIdentifier("drawable/"+str.toLowerCase(), null, getPackageName()));
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
    		return categories.length;
    	}

    	public Object getItem(int position) {
    		return null;
    	}

    	public long getItemId(int position) {
    		return 0;
    	}

    	// Sets up the view shown within each grid cell.
    	public View getView(int position, View convertView, ViewGroup parent) {
    		View myView = convertView;
			
    		// If not created yet, initialize it.
    		if (convertView == null) {	
    			LayoutInflater li = getLayoutInflater();
    			myView = li.inflate(R.layout.grid_item, null);
    			
    			// Add image button
    			ImageButton ib = (ImageButton) myView.findViewById(R.id.grid_item_button);
    			
    			final String category = categories[position];
    			ib.setImageResource(getIcon(category));
    			
    			if (position == 1 || position == 6) {
    				// Jump to CategoryList
    				ib.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							Intent myIntent = new Intent(v.getContext(), CategoryList.class);
			                myIntent.putExtra("category", category.toLowerCase());
			                startActivity(myIntent);
						}
	    			});
    			} else {
    				// Otherwise, jump to map
	    			ib.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							Intent myIntent = new Intent(v.getContext(), FINMap.class);
			                myIntent.putExtra("category", category.toLowerCase());
			                startActivity(myIntent);
						}
	    			});
    			}
    			
    			// Add text above button.
    			TextView tv = (TextView) myView.findViewById(R.id.grid_item_text);
    			tv.setText(categories[position]);
    		}
    		return myView;
    	}

		public void setmContext(Context mContext) {
			this.mContext = mContext;
		}

		public Context getmContext() {
			return mContext;
		}
    }
	
	// Category selections.
    protected static String[] categories = {
        "ATMs",
        "BUILDINGS",
        "COFFEE",
        "DINING",
        "MAILBOXES",
        "RESTROOMS",
        "SUPPLIES",
        "VENDING"
    };
    
    public static java.util.Map<GeoPoint, Building> getBuildings() {
		return buildings;
    }
    
    /** This method returns the icons map */
    public static Integer getIcon(String category) {
    	return icons.get(category.toLowerCase());
    }
}
