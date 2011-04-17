/*
 * This class provides the intermediate list screen
 * that is displayed for Buildings and Supplies.
 * Selection of any option will launch the map class.
 * 
 * Auto-completion suggestions are enabled.
 */

package com.net.finditnow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

public class BuildingList extends FINListActivity {

	private ProgressDialog myDialog;
	private EditText filterText;
	private ArrayAdapter<String> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_bg_with_filter);
		
		adapter = new ArrayAdapter<String>(this, R.layout.list_item, FINHome.getBuildingsList());
		
		filterText = (EditText) findViewById(R.id.search_box);
	    filterText.addTextChangedListener(filterTextWatcher);
	    
		setListAdapter(adapter);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		// Every item will launch the map
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				final String selectedBuilding = ((TextView) v).getText().toString();

				myDialog = ProgressDialog.show(BuildingList.this, "" , "Loading " + selectedBuilding + "...", true);
				Thread buildingThread = new Thread() {
					@Override
					public void run() {
						Intent myIntent = new Intent(getBaseContext(), FINMap.class);

						myIntent.putExtra("building", selectedBuilding);
						myIntent.putExtra("category", "");
						myIntent.putExtra("itemName", "");

						GeoPoint loc = FINHome.getGeoPointFromBuilding(selectedBuilding);
						String locations = DBCommunicator.getAllLocations(FINUtil.allCategories(FINHome.getCategoriesList()), loc.getLatitudeE6()+"", loc.getLongitudeE6()+"", getBaseContext());
						myIntent.putExtra("locations", locations);

						startActivity(myIntent);
						myDialog.dismiss();
					}
				};
				buildingThread.start();
			}
		});
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);		
		menu.findItem(R.id.home_button).setVisible(false);

		return true;
	}
	
	private TextWatcher filterTextWatcher = new TextWatcher() {

	    public void afterTextChanged(Editable s) {
	    }

	    public void beforeTextChanged(CharSequence s, int start, int count,
	            int after) {
	    }

	    public void onTextChanged(CharSequence s, int start, int before,
	            int count) {
	        adapter.getFilter().filter(s);
	    }

	};

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    filterText.removeTextChangedListener(filterTextWatcher);
	}
}
