package com.net.finditnow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

public class FINSearch extends FINListActivity {

	ArrayList<String> distances;
	ArrayList<String> walking_times;
	ArrayList<String> building_names;
	ArrayList<String> special_info;
	HashMap<Integer, GeoPoint> searchMap;

	private Bundle appData;
	private String query;
	private String category;

	protected Thread searchThread;
	protected ProgressDialog myDialog;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		distances = new ArrayList<String>();
		walking_times = new ArrayList<String>();
		building_names = new ArrayList<String>();
		special_info = new ArrayList<String>();

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		appData = intent.getBundleExtra(SearchManager.APP_DATA);
		if (appData == null) {
			appData = getIntent().getBundleExtra("appData");
		}
		category = appData.getString("category");

		setTitle(getString(R.string.app_name) + " > " + category + " > " + "Search");

		searchThread = new Thread() {

			@Override
			public void run() {
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				String rid = prefs.getInt("rid", 0)+"";

				String result = DBCommunicator.searchLocations(category, rid, query, getBaseContext());

				HashMap<Integer, GeoPoint> unsortedSearchMap = JsonParser.parseSearchJson(result);
				ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
				for (Integer id : unsortedSearchMap.keySet()) {
					if (!points.contains(unsortedSearchMap.get(id))) {
						points.add(unsortedSearchMap.get(id));
					}
				}
				Collections.sort(points, new ComparableGeoPoint());
				searchMap = new HashMap<Integer, GeoPoint>();
				for (int i = 0; i < points.size(); i++) {
					searchMap.put(i, points.get(i));
				}

				for (GeoPoint point : points) {
					Building build = FINHome.getBuilding(point, getBaseContext());
					BigDecimal dist = FINMap.distanceBetween(FINMap.getLocation(), point);
					distances.add((dist.equals(new BigDecimal(-1))? "N/A" : dist) + "\nmiles");
					int walking_time = dist != null? FINMap.walkingTime(dist, 35) : -1;
					walking_times.add((dist.equals(new BigDecimal(-1))? "N/A" : walking_time) + " minute" + (walking_time != 1? "s" : ""));
					building_names.add(build == null? "Outdoor Location" : build.getName());

					CategoryItem item = FINMap.getCategoryItem(point, category);
					special_info.add(item == null? "" : item.getInfo().get(0).replace("<br />", "\n"));
				}

				myDialog.dismiss();

				if (!points.isEmpty()) {
					handler1.sendEmptyMessage(0);
				} else {
					handler2.sendEmptyMessage(0);
				}
			}
		};

		if (Intent.ACTION_SEARCH.equals(intent.getAction()) && appData != null) {
			query = intent.getStringExtra(SearchManager.QUERY);

			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getBaseContext(), SearchSuggestions.AUTHORITY, SearchSuggestions.MODE);
			suggestions.saveRecentQuery(query, null);
			myDialog = ProgressDialog.show(FINSearch.this, "" , "Searching for " + query + "...", true);

			searchThread.start();
		} else {
			query = "";

			myDialog = ProgressDialog.show(FINSearch.this, "" , "Loading " + category + "...", true);

			searchThread.start();
		}
	}


	private Handler handler1 = new Handler() {
		@Override
		public void  handleMessage(Message msg) {
			ListView lv = FINSearch.this.getListView();
			lv.setTextFilterEnabled(true);

			lv.setAdapter(new SearchAdapter());
		}
	};

	private Handler handler2 = new Handler() {
		@Override
		public void  handleMessage(Message msg) {
			Toast.makeText(getBaseContext(), "No results found", Toast.LENGTH_SHORT).show();
			finish();
		}
	};

	private class SearchAdapter extends BaseAdapter {

		public SearchAdapter() {               
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
					myIntent.putExtra("centerLat", selectedLoc.getLatitudeE6());
					myIntent.putExtra("centerLon", selectedLoc.getLongitudeE6());
					myIntent.putExtra("locations", appData.getString("locations"));

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
