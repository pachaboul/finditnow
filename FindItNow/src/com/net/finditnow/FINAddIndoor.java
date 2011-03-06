package com.net.finditnow;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class FINAddIndoor extends Activity {

	Building selectedBuilding;
	String selectedFloor;
	String selectedCategory;
	boolean[] supplyTypes;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew_indoor);
		setTitle("FindItNow > Add New Item > Indoor Item");

		Spinner bSpinner = (Spinner) findViewById(R.id.addnew_bspinner);
		ArrayAdapter<String> bAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, FINMenu
						.getBuildingsList());
		bAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bSpinner.setAdapter(bAdapter);
		bSpinner.setOnItemSelectedListener(bspinner_listener);
		selectedBuilding = FINMenu.getBuilding(FINMenu
				.getGeoPointFromBuilding(FINMenu.getBuildingsList().get(0))); // uhhhh
																				// well
																				// it
																				// works...

		Bundle extras = getIntent().getExtras();
		selectedCategory = extras.getString("selectedCategory");
		supplyTypes = extras.getBooleanArray("supplyTypes");

		Button addItem = (Button) findViewById(R.id.addnew_additem);
		addItem.setOnClickListener(additem_listener);
	}

	private OnItemSelectedListener bspinner_listener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
				long arg3) {
			selectedBuilding = FINMenu.getBuilding(FINMenu
					.getGeoPointFromBuilding(parent.getItemAtPosition(pos)
							.toString()));
			setFloorSpinner();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	private OnClickListener additem_listener = new OnClickListener() {
		public void onClick(View v) {
			HashMap<String, Integer> map = selectedBuilding.floorMap();
			String bb = "", sc = "", pr = "";
			if (selectedCategory.equals("Supplies") && supplyTypes[0])
				bb = "bb";
			if (selectedCategory.equals("Supplies") && supplyTypes[1])
				sc = "sc";
			if (selectedCategory.equals("Supplies") && supplyTypes[2])
				pr = "print";

			Create.sendToDB(FINUtil.reverseCapFirstChar(selectedCategory), null, map.get(selectedFloor), "", bb, sc, pr);

			Intent myIntent = new Intent(v.getContext(), FINMenu.class);
			startActivity(myIntent);
			Toast.makeText(getBaseContext(), selectedCategory + " location added successfully!", Toast.LENGTH_LONG).show();
		}
	};

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

	private OnItemSelectedListener fspinner_listener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
				long arg3) {
			selectedFloor = parent.getItemAtPosition(pos).toString();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};
}
