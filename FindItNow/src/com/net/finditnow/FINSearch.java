package com.net.finditnow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.google.android.maps.GeoPoint;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FINSearch extends FINListActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.search);
		
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

			HashMap<Integer, GeoPoint> unsortedSearchMap = JsonParser.parseSearchJson(result);
			ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
			for (Integer id : unsortedSearchMap.keySet()) {
				points.add(unsortedSearchMap.get(id));
			}
			Collections.sort(points, new ComparableGeoPoint());
			final HashMap<Integer, GeoPoint> searchMap = new HashMap<Integer, GeoPoint>();
			for (Integer id : unsortedSearchMap.keySet()) {
				searchMap.put(id, points.get(id));
			}
			
			ArrayList<String> foundLocations = new ArrayList<String>();
			for (GeoPoint point : points) {
				Building build = FINHome.getBuilding(point);
				BigDecimal dist = FINMap.distanceBetween(FINMap.getLocation(), point);
				String distance = dist.equals(new BigDecimal(-1))? "Cannot calculate" : dist + " mi.";
				String str = (build == null? "Outdoor Location \n" : build.getName() + " \n")  + "Distance to here: " + distance + "\n" + 
						FINMap.getCategoryItem(point, category).getInfo().get(0).replace("<br />", "\n");
				foundLocations.add(str);
			}

			if (!points.isEmpty()) {

				//setListAdapter(new ArrayAdapter<String>(this, R.layout.building_list, foundLocations));
				
				ListView lv = getListView();
				lv.setTextFilterEnabled(true);
				
				lv.setAdapter(new SearchAdapter(this));

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
	
	private class SearchAdapter extends BaseAdapter {
		Context mContext;
		
		public SearchAdapter(Context context) {               
			mContext = context;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {   
			View myView;
			
			if (convertView == null){
				LayoutInflater li = getLayoutInflater();
				myView = li.inflate(R.layout.search_item, null);
			} else {
				myView = convertView;
			}
			
			/*
			replace strings
			*/
			TextView dist = (TextView) myView.findViewById(R.id.walking_distance);
			dist.setText("1.4 mi");
			
			TextView time = (TextView) myView.findViewById(R.id.walking_time);
			time.setText("20 minutes");
			
			TextView bldg = (TextView) myView.findViewById(R.id.building_name);
			bldg.setText("Suzzallo Building");
			
			TextView info = (TextView) myView.findViewById(R.id.special_info);
			info.setText("The coffee is really delicious.  It's hand-grounded by Oompa Loompas.  It is not fair trade BTW.");
			
			/*
			put your onclick listener here
			*/
	    	
            return myView;
		}

		public int getCount() {
			/*
			change this
			*/
			return 8;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return 0;
		}
	}
}
