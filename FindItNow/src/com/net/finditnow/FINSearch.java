package com.net.finditnow;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.maps.GeoPoint;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FINSearch extends FINListActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		Bundle appData = getIntent().getBundleExtra(SearchManager.APP_DATA);

		final String category = appData.getString("category");
		final String itemName = appData.getString("itemName");
		
		setTitle(getString(R.string.app_name) + " > " + category + " > " + "Search");

		if (Intent.ACTION_SEARCH.equals(intent.getAction()) && appData != null) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchSuggestions.AUTHORITY, SearchSuggestions.MODE);
			suggestions.saveRecentQuery(query, null);

			String result = DBCommunicator.searchLocations(category, appData.getString("lat"), 
					appData.getString("lon"), query, getBaseContext());

			final HashMap<Integer, GeoPoint> searchMap = JsonParser.parseSearchJson(result);

			ArrayList<String> foundLocations = new ArrayList<String>();
			for (Integer id : searchMap.keySet()) {
				foundLocations.add(FINMap.getCategoryItem(searchMap.get(id), appData.getString("category")).getInfo().get(0).replace("<br />", "\n"));
			}

			if (!searchMap.keySet().isEmpty()) {

				setListAdapter(new ArrayAdapter<String>(this, R.layout.building_list, foundLocations));

				ListView lv = getListView();
				lv.setTextFilterEnabled(true);

				// Every item will launch the map
				lv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
						GeoPoint selectedLoc = searchMap.get(position);

						Intent myIntent = new Intent(v.getContext(), FINMap.class);

						myIntent.putExtra("building", "");
						myIntent.putExtra("category", category);
						myIntent.putExtra("itemName", itemName);
						myIntent.putExtra("centerLat", selectedLoc.getLatitudeE6());
						myIntent.putExtra("centerLon", selectedLoc.getLongitudeE6());

						startActivity(myIntent);
					}
				});
			} else {
				Toast.makeText(getBaseContext(), "No results found", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}
}
