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
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BuildingList extends FINListActivity {
	
	private ProgressDialog myDialog;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, FINHome.getBuildingsList()));
    	
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
}
