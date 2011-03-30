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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FINSearch extends FINListActivity {
	
	ArrayList<String> distances;
	ArrayList<String> walking_times;
	ArrayList<String> building_names;
	ArrayList<String> special_info;
	HashMap<Integer, GeoPoint> searchMap;
	
	private String category;
	private String itemName;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		Bundle appData = getIntent().getBundleExtra(SearchManager.APP_DATA);
		
		distances = new ArrayList<String>();
		walking_times = new ArrayList<String>();
		building_names = new ArrayList<String>();
		special_info = new ArrayList<String>();

		category = appData.getString("category");
		itemName = appData.getString("itemName");
		
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
			searchMap = new HashMap<Integer, GeoPoint>();
			for (Integer id : unsortedSearchMap.keySet()) {
				searchMap.put(id, points.get(id));
			}
			
			for (GeoPoint point : points) {
				Building build = FINHome.getBuilding(point);
				BigDecimal dist = FINMap.distanceBetween(FINMap.getLocation(), point);
				distances.add((dist.equals(new BigDecimal(-1))? "N/A" : dist) + " mi");
				int walking_time = dist != null? FINMap.walkingTime(dist, 35) : -1;
				walking_times.add((dist.equals(new BigDecimal(-1))? "N/A" : walking_time) + " minute" + (walking_time != 1? "s" : ""));
				building_names.add(build == null? "Outdoor Location" : build.getName());
				special_info.add(FINMap.getCategoryItem(point, category).getInfo().get(0).replace("<br />", "\n"));
				
				
			}

			if (!points.isEmpty()) {
				
				ListView lv = getListView();
				lv.setTextFilterEnabled(true);
				
				lv.setAdapter(new SearchAdapter(this));
				
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
		
		public View getView(final int position, View convertView, ViewGroup parent) {   
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
			dist.setText(distances.get(position));
			
			TextView time = (TextView) myView.findViewById(R.id.walking_time);
			time.setText(walking_times.get(position));
			
			TextView bldg = (TextView) myView.findViewById(R.id.building_name);
			bldg.setText(building_names.get(position));
			
			TextView info = (TextView) myView.findViewById(R.id.special_info);
			info.setText(special_info.get(position));
			
			/*
			put your onclick listener here
			*/
			// Every item will launch the map
			myView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
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

	    	
            return myView;
		}

		public int getCount() {
			return building_names.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return 0;
		}
	}
}
