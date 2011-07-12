package com.net.finditnow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class FINAddNew extends FINActivity {

	//Interface (View) variables
	private RadioButton radioSelection;
	View geopointConfirm;
	AlertDialog.Builder builder;	
	String selectedCategory;
	String special_info;
	boolean[] supplyTypes = {false, false, false};
	String buildingName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Restore the saved instance and generate the primary (main) layout
		super.onCreate(savedInstanceState);

		if (!FINHome.isLoggedIn()) {
			Intent myIntent = new Intent(this, FINLogin.class);
			myIntent.putExtra("result", getString(R.string.must_login));

			startActivityForResult(myIntent, 0);
		} else {

			setContentView(R.layout.addnew_popup);
			themePage();
			
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
			ArrayAdapter<String> cAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FINHome.getCategoriesList());
			cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			cSpinner.setAdapter(cAdapter);
			cSpinner.setOnItemSelectedListener(cspinner_listener);
			selectedCategory = FINHome.getCategoriesList().get(0);

			//Set up "next" button for indoor/outdoor and category selection screen
			final Button next = (Button) findViewById(R.id.addnew_next);
			next.setOnClickListener(next_listener);

			// Special case: if we were sent here via PopUpDialog.java,
			// there may be a building name we'll need to set as a default option.
			Bundle extras = getIntent().getExtras(); 
			if (extras != null) {
				buildingName = extras.getString("building");
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		if (requestCode == 0) {		
			if (resultCode == RESULT_OK) {		
				Intent intent = getIntent();		
				overridePendingTransition(0, 0);		
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);		
				finish();		

				overridePendingTransition(0, 0);		
				startActivity(intent);		
			} else {		
				finish();		
			}		
		}		
	}

	//Listener for spinner, called when item in spinner is selected
	private OnItemSelectedListener cspinner_listener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
			//Assign selected category to variable
			selectedCategory = parent.getItemAtPosition(pos).toString();

			//Handle special case of categories with items
			if(FINHome.getItemsFromCategory(selectedCategory) != null) {
				showItemsPopup(selectedCategory);
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

			if (buildingName != null) {
				myIntent.putExtra("building", buildingName);
			}

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
	private void showItemsPopup(String catWithItems) {
		builder = new AlertDialog.Builder(this);
		builder.setTitle("What " + catWithItems.toLowerCase() + " are offered?");
		builder.setMultiChoiceItems(FINHome.getItemsFromCategory(catWithItems), supplyTypes, supply_listener);		
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
	
	/**
	 * Does all the coloring.
	 */
	private void themePage() {
		View header = (View) findViewById(R.id.addnew_header);
		header.setBackgroundResource(FINTheme.getMainColor());
		
		View container = (View) findViewById(R.id.addnew_location_container);
		container.setBackgroundResource(FINTheme.getLightColor());
		
		container = (View) findViewById(R.id.addnew_category_container);
		container.setBackgroundResource(FINTheme.getLightColor());
		
		container = (View) findViewById(R.id.addnew_special_container);
		container.setBackgroundResource(FINTheme.getLightColor());
	}
}
