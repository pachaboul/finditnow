/*
 * This class provides the intermediate list screen
 * that is displayed for Buildings and Supplies.
 * Selection of any option will launch the map class.
 * 
 * Auto-completion suggestions are enabled.
 */

package com.net.finditnow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.google.android.maps.GeoPoint;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class CategoryList extends ListActivity {
	
	// On launch, determine which category type was passed
	// and display the appropriate list.
	// Defaults to supplies if category is unrecognized.
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	Bundle extras = getIntent().getExtras(); 
    	final String category = extras.getString("category");
    	List<String> list = new ArrayList<String>();
    	
    	// Grab the correct list to show.
    	if (category.equals("buildings")) {
    		HashMap<GeoPoint, Building> map = FINMenu.getBuildings();
    		for (GeoPoint point : map.keySet()) {
    			list.add(map.get(point).getName());
    		}
    		Collections.sort(list);
    	} else {
    		list = supplies();
    	}
    	
    	setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, list));
    	
    	ListView lv = getListView();
    	lv.setTextFilterEnabled(true);
    	
    	lv.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    			Intent myIntent = new Intent(v.getContext(), FINMap.class);
    			myIntent.putExtra("category", category);
    			myIntent.putExtra("itemName", ((TextView) v).getText());
    			startActivity(myIntent);
    		}
    	});
    }
	 
    // List of supplies item types.
	private static ArrayList<String> supplies() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Blue books");
		list.add("Scantrons");
		return list;
	}
	
	public static GeoPoint getGeoPointFromBuilding(String buildingName) {
		for (GeoPoint point : FINMenu.getBuildings().keySet()) {
			if (FINMenu.getBuildings().get(point).getName().equals(buildingName)) {
				return point;
			}
		}
		return null;
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.findItem(R.id.add_new_button).setVisible(false);
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
	        case R.id.categories_button:
	        	startActivity(new Intent(this, FINMenu.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
}
