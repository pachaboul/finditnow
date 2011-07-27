/*
 * This class provides the intermediate list screen
 * that is displayed for Items.
 * Selection of any option will launch the map class.
 * 
 * Auto-completion suggestions are enabled.
 */

package com.net.finditnow;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SubcategoryList extends FINListActivity {

	private ProgressDialog myDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_bg);

		Bundle extras = getIntent().getExtras(); 
		final String category = extras.getString("category");
		setTitle(getString(R.string.app_name) + " > " + category);
		
		FINDatabase db = new FINDatabase(getBaseContext());
		Cursor cursor = db.getReadableDatabase().query("categories", null, "full_name = '" + category+ "'", null, null, null, null);
		cursor.moveToFirst();
		int cat_id = cursor.getInt(cursor.getColumnIndex("cat_id"));
		
		cursor = db.getReadableDatabase().query("categories", null, "parent = " + cat_id, null, null, null, null);
		cursor.moveToFirst();
		ArrayList<String> subcategories = new ArrayList<String>();
		
		while (!cursor.isAfterLast()) {
			subcategories.add(cursor.getString(cursor.getColumnIndex("full_name")));
			cursor.moveToNext();
		}

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, subcategories));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		// Every item will launch the map
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				final String subcategory = ((TextView) v).getText().toString();

				myDialog = ProgressDialog.show(SubcategoryList.this, "" , "Loading " + subcategory + "...", true);
				Thread itemThread = new Thread() {
					@Override
					public void run() {
						Intent myIntent = new Intent(getBaseContext(), FINMap.class);

						myIntent.putExtra("category", subcategory);
						myIntent.putExtra("building", "");
						
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
						String rid = prefs.getInt("rid", 0)+"";

						String locations = DBCommunicator.getLocations(subcategory, rid, 0+"", getBaseContext());
						myIntent.putExtra("locations", locations);

						startActivity(myIntent);
						myDialog.dismiss();
					}
				};
				itemThread.start();
			}
		});
	}
}
