package com.net.finditnow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class FINAddNew extends FINActivity {

	//Interface (View) variables
	private RadioButton radioSelection;
	View geopointConfirm;
	AlertDialog.Builder builder;	
	String selectedCategory;
	String special_info;
	boolean[] supplyTypes = {false, false, false};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Restore the saved instance and generate the primary (main) layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew_popup);

		// Set the text in the titlebar
		setTitle(getString(R.string.app_name) + " > Add New Item");

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
		ArrayAdapter<String> cAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FINUtil.capFirstChar(FINHome.getCategoriesList()));
		cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cSpinner.setAdapter(cAdapter);
		cSpinner.setOnItemSelectedListener(cspinner_listener);
		selectedCategory = FINHome.getCategoriesList().get(0);

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
			//Adding indoor location, start associated activity
			Class<? extends Activity> nextClass = (radioSelection.getId() == R.id.addnew_in? FINAddIndoor.class : FINAddOutdoor.class);
			
			Intent myIntent = new Intent(v.getContext(), nextClass);
			myIntent.putExtra("selectedCategory", selectedCategory);
			myIntent.putExtra("supplyTypes", supplyTypes);
			
			//Grab special info
			EditText info = (EditText) findViewById(R.id.addnew_specialinfo);
			special_info = info.getText().toString();			
			myIntent.putExtra("special_info", special_info);
			
			startActivity(myIntent);
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
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);		
		menu.findItem(R.id.add_new_button).setVisible(false);

		return true;
	}
}
