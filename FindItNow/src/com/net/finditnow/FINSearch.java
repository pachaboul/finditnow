package com.net.finditnow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		distances = new ArrayList<String>();
		walking_times = new ArrayList<String>();
		building_names = new ArrayList<String>();
		special_info = new ArrayList<String>();

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
		if (appData == null) {
			appData = getIntent().getBundleExtra("appData");
		}
		
		boolean search = Intent.ACTION_SEARCH.equals(intent.getAction()) && appData != null;
		
		final String category = appData.getString("category");
		final String query = search? " AND special_info LIKE '%"  + intent.getStringExtra(SearchManager.QUERY) + "%'" : "";
		final ProgressDialog myDialog = ProgressDialog.show(FINSearch.this, "" , !search? "Loading " + category + "..." : "Searching for " + intent.getStringExtra(SearchManager.QUERY) + "...", true);
	
		setTitle(getString(R.string.app_name) + " > " + category + " > " + "Search");
		
		if (search) {
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getBaseContext(), SearchSuggestions.AUTHORITY, SearchSuggestions.MODE);
			suggestions.saveRecentQuery(intent.getStringExtra(SearchManager.QUERY), null);
		}
		
		Thread searchThread = new Thread() {

			@Override
			public void run() {
				HashMap<Integer, GeoPoint> map = search(category, query, getBaseContext());
				
				ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
				for (Integer id : map.keySet()) {
					if (!points.contains(map.get(id))) {
						points.add(map.get(id));
					}
				}
				Collections.sort(points, new ComparableGeoPoint());
				
				searchMap = new HashMap<Integer, GeoPoint>();
				for (int i = 0; i < points.size(); i++) {
					searchMap.put(i, points.get(i));
				}

				for (GeoPoint point : points) {
					Building build = FINHome.getBuilding(point, getBaseContext());
					
					double dist = FINMap.distanceBetween(FINMap.getLocation(), point);
					String distance = String.format("%.1f", dist);
					distances.add((dist == -1? "N/A" : distance) + "\nmiles");
					
					int walking_time = FINMap.walkingTime(dist, 35);
					walking_times.add((dist == -1? "N/A" : walking_time) + " minute" + (walking_time != 1? "s" : ""));
					
					building_names.add(build == null? "Outdoor Location" : build.getName());

					CategoryItem item = FINMap.getCategoryItem(point, category, getBaseContext());
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
		
		searchThread.start();
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
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			String color = prefs.getString("color", "green");
			
			dist.setTextColor(FINTheme.getFontColor(color, getBaseContext()));

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
					
					// Get the intent, verify the action and get the query
					Bundle appData = getIntent().getBundleExtra(SearchManager.APP_DATA);
					if (appData == null) {
						appData = getIntent().getBundleExtra("appData");
					}

					myIntent.putExtra("building", "");
					myIntent.putExtra("category", appData.getString("category"));
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
	
	public HashMap<Integer, GeoPoint> search(String cat, String sString, Context context) {
		HashMap<Integer, GeoPoint> map = new HashMap<Integer, GeoPoint>();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SQLiteDatabase db = new FINDatabase(context).getReadableDatabase();
				
		int rid = prefs.getInt("rid", 0);
		
		Cursor cursor = db.query("categories", null, "full_name = '" + cat + "'", null, null, null, null);
		cursor.moveToFirst();
		int cat_id = cursor.getInt(cursor.getColumnIndex("cat_id"));
		
		cursor = db.query("items", null, "rid = " + rid + " AND cat_id = " + cat_id + sString, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			int latitude = cursor.getInt(cursor.getColumnIndex("latitude"));
			int longitude = cursor.getInt(cursor.getColumnIndex("longitude"));
			
			int item_id = cursor.getInt(cursor.getColumnIndex("item_id"));
			
			map.put(item_id, new GeoPoint(latitude, longitude));
		
			cursor.moveToNext();
		}
		
		cursor.close();
		db.close();
		
		return map;
	}
}
