/*
 * This class provides the intermediate list screen
 * that is displayed for Buildings and Supplies.
 * Selection of any option will launch the map class.
 * 
 * Auto-completion suggestions are enabled.
 */

package com.net.finditnow;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CategoryList extends ListActivity {
	
	/**
     * On launch, determines which category type was passed
     * and displays the appropriate list.
     * Defaults to supplies if category is unrecognized.
     */
	
	private ProgressDialog myDialog;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	Bundle extras = getIntent().getExtras(); 
    	final String category = extras.getString("category");
    	setTitle("FindItNow > " + FINUtil.capFirstChar(category));
    	List<String> list = new ArrayList<String>();
    	
    	// Grab the correct list to show.
    	if (category.equals("buildings")) {
    		list = FINMenu.getBuildingsList();
    	} else {
    		list = FINUtil.capFirstChar(supplies());
    	}
    	
    	setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, list));
    	
    	ListView lv = getListView();
    	lv.setTextFilterEnabled(true);
    	
    	// Every item will launch the map
    	lv.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				myDialog = ProgressDialog.show(CategoryList.this, "Items Loading" , "Loading " + FINUtil.capFirstChar(((TextView) v).getText().toString()) + "...", true);
    			Intent myIntent = new Intent(v.getContext(), FINMap.class);
    			myIntent.putExtra("category", category);
    			myIntent.putExtra("itemName", ((TextView) v).getText().toString());
    			startActivity(myIntent);
    		}
    	});
    }
    
	@Override
	public void onResume() {
		super.onResume();
		if (myDialog != null) {
			myDialog.dismiss();
		}
	}
	
	/**
     * List of school supplies item types
     * Note: this is hard-coded in our application
     */
	private static ArrayList<String> supplies() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Blue Books");
		list.add("Scantrons");
		list.add("Printing");
		return list;
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
	        case R.id.categories_button:
	        	startActivity(new Intent(this, FINMenu.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * Prepares the options menu before being displayed.
     * Removes centering location option (special for the Map only).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.findItem(R.id.my_location_button).setVisible(false);
    	return true;
    }
}
