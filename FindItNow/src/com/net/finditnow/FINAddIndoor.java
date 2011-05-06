package com.net.finditnow;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class FINAddIndoor extends FINActivity {

	Building selectedBuilding;
	String selectedFloor;
	String selectedCategory;
	boolean[] supplyTypes;
	String special_info;
	String defaultBuilding;
	ProgressDialog myDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew_indoor);

		// Set the text in the titlebar
		setTitle(getString(R.string.app_name) + " > Add New Item > Indoor Item");

		// Set up spinner for building selection
		Spinner bSpinner = (Spinner) findViewById(R.id.addnew_bspinner);
		ArrayAdapter<String> bAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, FINHome.getBuildingsList());
		bAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bSpinner.setAdapter(bAdapter);
		bSpinner.setOnItemSelectedListener(bspinner_listener);

		//Get category and types of items from previous activity
		Bundle extras = getIntent().getExtras();
		selectedCategory = extras.getString("selectedCategory");
		supplyTypes = extras.getBooleanArray("supplyTypes");
		special_info = extras.getString("special_info");
		defaultBuilding = extras.getString("building");

		// Special case where we set the default building.
		if (defaultBuilding != null) {
			int index = FINHome.getBuildingsList().indexOf(defaultBuilding);
			bSpinner.setSelection(index);
			selectedBuilding = FINHome.getBuilding(
					FINHome.getGeoPointFromBuilding(FINHome.getBuildingsList().get(index)));
		} else {
			selectedBuilding = FINHome.getBuilding(
					FINHome.getGeoPointFromBuilding(FINHome.getBuildingsList().get(0)));
		}

		//Set up "add item" button
		Button addItem = (Button) findViewById(R.id.addnew_additem);
		addItem.setOnClickListener(additem_listener);
	}

	//Listener for building spinner
	private OnItemSelectedListener bspinner_listener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
				long arg3) {
			selectedBuilding = FINHome.getBuilding(FINHome
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

			myDialog = ProgressDialog.show(FINAddIndoor.this, "" , "Adding " + selectedCategory + "...", true);
			Thread thread = new Thread() {
				@Override
				public void run() {
					HashMap<String, Integer> map = selectedBuilding.floorMap();

					String bb = supplyTypes[0]? "bb" : "";
					String sc = supplyTypes[1]? "sc" : "";
					String pr = supplyTypes[2]? "print" : "";
					String item = bb.length() > 0? "Blue Books" : sc.length() > 0? "Scantrons" : pr.length() > 0? "Printing" : "";

					//Send new item to database
					final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
					String result = DBCommunicator.create(phone_id, selectedCategory, map
							.get(selectedFloor)+"", special_info, "", "", bb, sc, pr, getBaseContext());

					// Load the map with the new item
					Intent myIntent = new Intent(getBaseContext(), FINMap.class);
					myIntent.putExtra("result", result);
					myIntent.putExtra("category", selectedCategory);
					myIntent.putExtra("building", "");
					myIntent.putExtra("itemName", item);
					myIntent.putExtra("centerLat", FINHome.getGeoPointFromBuilding(selectedBuilding.getName()).getLatitudeE6());
					myIntent.putExtra("centerLon", FINHome.getGeoPointFromBuilding(selectedBuilding.getName()).getLongitudeE6());
					
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

					String locations = DBCommunicator.getLocations(selectedCategory, item, prefs.getInt("campusLat", 0)+"", prefs.getInt("campusLon", 0)+"", getBaseContext());
					myIntent.putExtra("locations", locations);

					startActivity(myIntent);

					myDialog.dismiss();
				}
			};
			thread.start();
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

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);		
		menu.findItem(R.id.add_new_button).setVisible(false);

		return true;
	}
}
