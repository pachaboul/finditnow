package com.net.finditnow;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class FINAddNew extends Activity {
	
	//Interface (View) variables
	private RadioButton radioSelection;
	View geopointConfirm;
	AlertDialog.Builder builder;	
	String selectedCategory;
	boolean[] supplyTypes = {true, true, true};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Restore the saved instance and generate the primary (main) layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew_popup);
		
		// Set the text in the titlebar
		setTitle("FindItNow > Add New Item");
		
		//Set up interface for indoor/outdoor and category selection screen
		geopointConfirm = findViewById(R.id.addmap_confirm);
		
		//Set up radio buttons for indoor/outdoor
		radioSelection = (RadioButton) findViewById(R.id.addnew_in);
		final RadioButton radio_in = (RadioButton) findViewById(R.id.addnew_in);
		final RadioButton radio_out = (RadioButton) findViewById(R.id.addnew_out);
		radio_in.setOnClickListener(radio_listener);
		radio_out.setOnClickListener(radio_listener);
		
		//Set up the category spinner
		Spinner cSpinner = (Spinner) findViewById(R.id.addnew_cspinner);
		ArrayAdapter<String> cAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, parseForUserSubmittableCategories(FINUtil.capFirstChar(FINMenu.getCategoriesList())));
		cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cSpinner.setAdapter(cAdapter);
		cSpinner.setOnItemSelectedListener(cspinner_listener);
		selectedCategory = FINMenu.getCategoriesList().get(0);
		
		//Set up "next" button for indoor/outdoor and category selection screen
		final Button next = (Button) findViewById(R.id.addnew_next);
		next.setOnClickListener(next_listener);
	}
	
	//Listener for spinner, called when item in spinner is selected
	private OnItemSelectedListener cspinner_listener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
			//Assign selected category to variable
			selectedCategory = parent.getItemAtPosition(pos).toString();
			
			//Handle special case of school supplies
			if(selectedCategory.equals("School Supplies")) {
				showSuppliesPopup();
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};
	
	//Listener for school supply popup
	private OnMultiChoiceClickListener supply_listener = new OnMultiChoiceClickListener() {
		
		public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				supplyTypes[which] = isChecked;
		}
	};
	
	//Listener for next button on indoor/outdoor and category selection screen
	private OnClickListener next_listener = new OnClickListener() {
	    public void onClick(View v) {    	
	    	if (radioSelection.getId() == R.id.addnew_in) { 
	    		//Adding indoor location, start associated activity
	    		Intent myIntent = new Intent(v.getContext(), FINAddIndoor.class);
	    		myIntent.putExtra("selectedCategory", selectedCategory);
	    		myIntent.putExtra("supplyTypes", supplyTypes);
                startActivity(myIntent);
	    	} else if (radioSelection.getId() == R.id.addnew_out) { 
	    		//Adding outdoor location, start associated activity
				Intent myIntent = new Intent(v.getContext(), FINAddOutdoor.class);
				myIntent.putExtra("selectedCategory", selectedCategory);
				myIntent.putExtra("supplyTypes", supplyTypes);
                startActivity(myIntent);
	    	}
	    }
	};
	
	//Listener for indoor/outdoor radio buttons
	private OnClickListener radio_listener = new OnClickListener() {
	    public void onClick(View v) {
	        radioSelection = (RadioButton) v;
	    }
	};
	
	/**
     * Creates and displays a popup for specifying school supply types
     */
	private void showSuppliesPopup() {
		builder = new AlertDialog.Builder(this);
		builder.setTitle("What school supplies are offered?");
		builder.setMultiChoiceItems(R.array.specific_supplies, supplyTypes, supply_listener);		
		builder.setCancelable(false);		
		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		        }
		    });
		
		AlertDialog alert = builder.create();
		alert.show();	
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
	        case R.id.home_button:
                startActivityForResult(new Intent(this, FINMenu.class), 0);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
	
	/**
     * Prepares the options menu before being displayed.
     * Leaves only the "Categories" button enabled
     */
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.findItem(R.id.add_new_button).setVisible(false);
    	menu.findItem(R.id.my_location_button).setVisible(false);
    	menu.findItem(R.id.help_button).setVisible(false);
    	return true;
    }
    
	/**
	 * 
	 * @param al The array list of strings representing category names
	 * @return Return the list after removing any categories users cannot submit (just Buildings at the moment)
	 */
    private ArrayList<String> parseForUserSubmittableCategories(ArrayList<String> al) {
		al.remove("Buildings");
		return al;
	}
}
