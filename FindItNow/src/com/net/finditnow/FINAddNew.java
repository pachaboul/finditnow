package com.net.finditnow;

import android.app.Activity;
import android.content.Intent;
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

import com.google.android.maps.GeoPoint;

public class FINAddNew extends Activity {
	
	private RadioButton rs;
	private static GeoPoint tappedPoint;
	View geopointConfirm;
	Building selectedBuilding;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew_popup);
		setTitle("FindItNow > Add New Item");
		
		geopointConfirm = findViewById(R.id.addmap_confirm);
		rs = (RadioButton) findViewById(R.id.addnew_in);
		final RadioButton radio_in = (RadioButton) findViewById(R.id.addnew_in);
		final RadioButton radio_out = (RadioButton) findViewById(R.id.addnew_out);
		radio_in.setOnClickListener(radio_listener);
		radio_out.setOnClickListener(radio_listener);
		
		final Button next = (Button) findViewById(R.id.addnew_next);
		next.setOnClickListener(next_listener);
	}

	private OnClickListener next_listener = new OnClickListener() {
	    public void onClick(View v) {    	
	    	if (rs.getId() == R.id.addnew_in) { //Adding indoor location
	    		handleIndoorItem();
	    	} else if (rs.getId() == R.id.addnew_out) { //Adding outdoor location
				Intent myIntent = new Intent(v.getContext(), FINAddMap.class);
                startActivity(myIntent);
	    	}
	    }
	};
	
	private OnClickListener radio_listener = new OnClickListener() {
	    public void onClick(View v) {
	        rs = (RadioButton) v;
	    }
	};
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    
    protected void handleIndoorItem() {
    	setContentView(R.layout.addnew_indoor);
    	
    	Spinner bSpinner = (Spinner) findViewById(R.id.addnew_bspinner);
		ArrayAdapter<String> bAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FINMenu.getBuildingsList());
		bAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bSpinner.setAdapter(bAdapter);
		bSpinner.setOnItemSelectedListener(bspinner_listener);
		selectedBuilding = FINMenu.getBuilding(FINMenu.getGeoPointFromBuilding(FINMenu.getBuildingsList().get(0))); //uhhhh well it works...	
	
		Spinner cSpinner = (Spinner) findViewById(R.id.addnew_cspinner);
		ArrayAdapter<String> cAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, FINUtil.capFirstChar(FINMenu.getCategoriesList()));
		cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cSpinner.setAdapter(cAdapter);
    }
    
    protected void setFloorSpinner() {
    	Spinner fSpinner = (Spinner) findViewById(R.id.addnew_fspinner);
		ArrayAdapter<String> fAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, selectedBuilding.getFloorName());
		fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fSpinner.setAdapter(fAdapter);
//		fSpinner.setOnItemSelectedListener(fspinner_listener);
    }
    
    private OnItemSelectedListener bspinner_listener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
			selectedBuilding = FINMenu.getBuilding(FINMenu.getGeoPointFromBuilding(parent.getItemAtPosition(pos).toString()));
			setFloorSpinner();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.findItem(R.id.add_new_button).setVisible(false);
    	menu.findItem(R.id.my_location_button).setVisible(false);
    	menu.findItem(R.id.help_button).setVisible(false);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.categories_button:
                startActivity(new Intent(this, FINMenu.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    public static GeoPoint getTappedPoint() {
    	return tappedPoint;
    }
    
    public static void setTappedPoint(GeoPoint point) {
    	tappedPoint = point;
    }
}
