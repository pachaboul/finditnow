/*
 * This class provides the intermediate list screen
 * that is displayed for Items.
 * Selection of any option will launch the map class.
 * 
 * Auto-completion suggestions are enabled.
 */

package com.net.finditnow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CategoryList extends FINListActivity {
	
	private ProgressDialog myDialog;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.list_bg);
    	
    	Bundle extras = getIntent().getExtras(); 
    	final String category = extras.getString("category");
    	setTitle(getString(R.string.app_name) + " > " + category);
    	
    	setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, FINHome.getItemsFromCategory(category)));
    	
    	ListView lv = getListView();
    	lv.setTextFilterEnabled(true);
    	
    	// Every item will launch the map
    	lv.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    			final String itemName = ((TextView) v).getText().toString();
    			
				myDialog = ProgressDialog.show(CategoryList.this, "" , "Loading " + itemName + "...", true);
    			Thread itemThread = new Thread() {
    				public void run() {
		    			Intent myIntent = new Intent(getBaseContext(), FINMap.class);
		    			
		    			myIntent.putExtra("category", category);
		                myIntent.putExtra("building", "");
		    			myIntent.putExtra("itemName", itemName);
		    			
		    			startActivity(myIntent);
		    			myDialog.dismiss();
    				}
    			};
    			itemThread.start();
    		}
    	});
    }
}
