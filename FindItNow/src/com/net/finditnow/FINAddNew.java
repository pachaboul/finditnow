package com.net.finditnow;
//Blah
import java.util.ArrayList;
import java.util.HashMap;

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
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class FINAddNew extends Activity {
	
	private RadioButton rs;
	View geopointConfirm;
	String selectedCategory;
	
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
		
		Spinner cSpinner = (Spinner) findViewById(R.id.addnew_cspinner);
		ArrayAdapter<String> cAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, parseForUserSubmittableCategories(FINUtil.capFirstChar(FINMenu.getCategoriesList())));
		cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cSpinner.setAdapter(cAdapter);
		cSpinner.setOnItemSelectedListener(cspinner_listener);
		selectedCategory = FINMenu.getCategoriesList().get(0);
		
		final Button next = (Button) findViewById(R.id.addnew_next);
		next.setOnClickListener(next_listener);
	}
	
	private ArrayList<String> parseForUserSubmittableCategories(ArrayList<String> al) {
		al.remove("Buildings");
		return al;
	}
	
	private OnItemSelectedListener cspinner_listener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
			selectedCategory = parent.getItemAtPosition(pos).toString();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};
	private OnClickListener next_listener = new OnClickListener() {
	    public void onClick(View v) {    	
	    	if (rs.getId() == R.id.addnew_in) { //Adding indoor location
	    		Intent myIntent = new Intent(v.getContext(), FINAddIndoor.class);
	    		myIntent.putExtra("selectedCategory", selectedCategory);
                startActivity(myIntent);
	    	} else if (rs.getId() == R.id.addnew_out) { //Adding outdoor location
				Intent myIntent = new Intent(v.getContext(), FINAddOutdoor.class);
				myIntent.putExtra("selectedCategory", selectedCategory);
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
                startActivityForResult(new Intent(this, FINMenu.class), 0);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
}
