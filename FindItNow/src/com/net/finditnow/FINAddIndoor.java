package com.net.finditnow;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class FINAddIndoor extends Activity {

	Building selectedBuilding;
	String selectedFloor;
	String selectedCategory;
	boolean[] supplyTypes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew_indoor);
		setTitle("FindItNow > Add New Item > Indoor Item");

		// Set up spinner for building selection
		Spinner bSpinner = (Spinner) findViewById(R.id.addnew_bspinner);
		ArrayAdapter<String> bAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, FINMenu
						.getBuildingsList());
		bAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bSpinner.setAdapter(bAdapter);
		bSpinner.setOnItemSelectedListener(bspinner_listener);
		selectedBuilding = FINMenu.getBuilding(FINMenu
				.getGeoPointFromBuilding(FINMenu.getBuildingsList().get(0)));

		//Get category and types of school supplies from previous activity
		Bundle extras = getIntent().getExtras();
		selectedCategory = extras.getString("selectedCategory");
		supplyTypes = extras.getBooleanArray("supplyTypes");

		//Set up "add item" button
		Button addItem = (Button) findViewById(R.id.addnew_additem);
		addItem.setOnClickListener(additem_listener);
	}

	//Listener for building spinner
	private OnItemSelectedListener bspinner_listener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
				long arg3) {
			selectedBuilding = FINMenu.getBuilding(FINMenu
					.getGeoPointFromBuilding(parent.getItemAtPosition(pos)
							.toString()));
			//Set floor spinner accordingly
			setFloorSpinner();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	};

	//Listener for "add item" button
	private OnClickListener additem_listener = new OnClickListener() {
		public void onClick(View v) {
			HashMap<String, Integer> map = selectedBuilding.floorMap();
			String bb = "", sc = "", pr = "";
			if (selectedCategory.equals("School Supplies") && supplyTypes[0])
				bb = "bb";
			if (selectedCategory.equals("School Supplies") && supplyTypes[1])
				sc = "sc";
			if (selectedCategory.equals("School Supplies") && supplyTypes[2])
				pr = "print";
			
			//Send new item to database
			final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
			String result = Create.sendToDB(FINUtil
					.deCapFirstChar(selectedCategory), null, map
					.get(selectedFloor), "", bb, sc, pr, phone_id, getBaseContext());
       	
        	//Return to categories screen
	    	Intent myIntent = new Intent(getBaseContext(), FINHome.class);
	    	myIntent.putExtra("result", result);
	    	
            startActivity(myIntent);
		}
	};

	//Listener for floor spinner
	private OnItemSelectedListener fspinner_listener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
				long arg3) {
			selectedFloor = parent.getItemAtPosition(pos).toString();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	/**
	 * Set up the spinner for selecting a floor based on current choice of building
	 */
	protected void setFloorSpinner() {
		Spinner fSpinner = (Spinner) findViewById(R.id.addnew_fspinner);
		ArrayAdapter<String> fAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, selectedBuilding
						.getFloorNames());
		fAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fSpinner.setAdapter(fAdapter);
		fSpinner.setOnItemSelectedListener(fspinner_listener);
	}
}
