/***
 * FINMap.java by Eric Hare
 * This class handles the MapView activity of our application
 * It includes user location detection and overlay support
 * The Google Maps API is used to accomplish these tasks
 */

package com.net.finditnow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

// DESIGN PATTERN: Sub-Classing.  This module extends the MapActivity class to implement a map view
//				   It adds some non-standard functionality, including location and item overlays
public class FINMap extends FINMapActivity {

	// Map and Location Variables
	private FINMapView mapView;
	private MapController mapController;
	private MyLocationOverlay locOverlay;

	// Overlay Variables
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private IconOverlay itemizedOverlay;

	// Shared static variables that the other modules can access
	private String category;    
	private String building;

	// Location and GeoPoint Variables
	private static GeoPoint location;
	
	private static SQLiteDatabase db;

	public static final String PREFS_NAME = "MyPrefsFile";

	/** 
	 * Called when the activity is first created.
	 * It initializes the map layout, detects the user's category, and builds the map
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Restore the saved instance and generate the primary (main) layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		db = new FINDatabase(getBaseContext()).getReadableDatabase();

		// Get the category, building, and potentially item name
		Bundle extras = getIntent().getExtras(); 
		category = extras.getString("category");
		building = extras.getString("building");
		
		// Set the Breadcrumb in the titlebar
		String title = (!building.equals("")? building : category);
		setTitle(getString(R.string.app_name) + " > " + title);

		// Create the map and the map view and detect user location
		createMap();
		locateUser();

		// Add these locations to the map view
		placeOverlays();
	}

	/**
	 * Called when the activity is paused to disable the location services
	 */
	@Override
	public void onPause() {
		super.onPause();

		locOverlay.disableMyLocation();
		locOverlay.disableCompass();

		mapOverlays.remove(locOverlay);
	}

	/**
	 * Called when the activity is resumed to enable the location services
	 */
	@Override
	public void onResume() {
		super.onResume();

		locOverlay.enableMyLocation();
		locOverlay.enableCompass();

		mapOverlays.add(locOverlay);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		db.close();
	}

	/**
	 * This method returns the distance from the user's current location to a given point
	 * It returns this in BigDecimal format for ease of processing
	 * 
	 * @param point A GeoPoint corresponding to the location under consideration
	 * 
	 * @return A BigDecimal representing the distance to this point in miles
	 */
	public static double distanceBetween(GeoPoint point1, GeoPoint point2) {

		// Define two location variables to process
		Location loc1 = new Location("");
		Location loc2 = new Location("");

		// This method is valid so long as the location is not the default and not null
		if (point1 != null && point2 != null) {

			// Compute the latitude and longitude of the two points
			// Add these values to our location variable
			loc1.setLatitude((float)(point1.getLatitudeE6()*1E-6));
			loc1.setLongitude((float)(point1.getLongitudeE6()*1E-6));
			loc2.setLatitude((float)(point2.getLatitudeE6()*1E-6));
			loc2.setLongitude((float)(point2.getLongitudeE6()*1E-6));

			// Return this value in miles rounded
			return(loc1.distanceTo(loc2) * 0.000621371192);
		} else {

			// Return -1 if the location was not valid
			return -1;
		}
	}

	/**
	 * This method returns the user's current location
	 * @return GeoPoint representing the user's location
	 */
	public static GeoPoint getLocation() {
		return location;
	}

	@Override
	public boolean onSearchRequested() {
		if (building.equals("")) {
			Bundle appData = new Bundle();

			appData.putString("category", category);			

			startSearch(null, false, appData, false);
		}
		return true;
	}

	/**
	 * This method computes the walking time for a given distance based on the mile time
	 * 
	 * @param distance The distance to calculate walking time over
	 * @param mile_time The amount of time in minutes to walk a mile
	 * 
	 * @return The walking time in minutes that it takes to walk the given distance rounded
	 */
	public static int walkingTime(double distance, int mile_time) {
		return (int)Math.round(mile_time * distance);
	}

	/**
	 * This method creates the map and displays the overlays on top of it 
	 */
	private void createMap() {

		GeoPoint center = getRegionCenter(getBaseContext());

		// Initialize our MapView and MapController
		mapView = (FINMapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		mapController = mapView.getController();
		mapController.setZoom(18);
		mapController.setCenter(center);

		Bundle extras = getIntent().getExtras();
		if (getIntent().hasExtra("centerLat") && getIntent().hasExtra("centerLon")) {
			mapController.animateTo(new GeoPoint(extras.getInt("centerLat"), extras.getInt("centerLon")));
		}

		// Build up our overlays and initialize our "UWOverlay" class
		mapOverlays = mapView.getOverlays();
		drawable = getResources().getDrawable(building.equals("")? FINHome.getIcon(category, getBaseContext()) : R.drawable.buildings);
		itemizedOverlay = new IconOverlay(drawable, this, category);

		// Setup the ImageButtons
		ImageButton list = (ImageButton) findViewById(R.id.list_button);
		ImageButton myLocation = (ImageButton) findViewById(R.id.my_location_button);
		ImageButton defaultLocation = (ImageButton) findViewById(R.id.default_location_button);
		if (building.equals("")) {
			list.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent myIntent = new Intent(getBaseContext(), FINSearch.class);
					Bundle appData = new Bundle();

					appData.putString("category", category);
					myIntent.putExtra("appData", appData);

					startActivity(myIntent);
				}
			});
		} else {
			list.setVisibility(View.GONE);
		}
		myLocation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (location != null) {
					mapController.animateTo(location);
					Toast.makeText(getBaseContext(), "Centering on your location...", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), "Error: Could not detect your location", Toast.LENGTH_SHORT).show();
				}
			}
		});

		defaultLocation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mapController.animateTo(getRegionCenter(getBaseContext()));
				Toast.makeText(getBaseContext(), "Centering on the default location...", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * Required for Android Maps API compatibility
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * This method locates the user and displays the user's location in an overlay icon
	 */
	private void locateUser() {

		// Define a new LocationOverlay and enable it
		locOverlay = new MyLocationOverlay(this, mapView) {

			// Extend onLocationChanged() to add the result to the location variable
			@Override
			public void onLocationChanged(Location loc) {
				super.onLocationChanged(loc);
				location = new GeoPoint((int)(loc.getLatitude()*1E6), (int)(loc.getLongitude()*1E6));
			}
		};
		
		Runnable runnable = new Runnable() {
			public void run() {
				mapController.animateTo(location);
			}
		};
		
		if (!getIntent().hasExtra("centerLat") && !getIntent().hasExtra("centerLon") && building.equals("")) {
			locOverlay.runOnFirstFix(runnable);
		}
	}

	/**
	 * This method places the locations retrieved from the database onto the map
	 */
	private void placeOverlays() {

		// If the category is buildings, then we only put the single point on the map
		if (!building.equals("")) {
			GeoPoint point = FINHome.getGeoPointFromBuilding(building, getBaseContext());
			mapController.animateTo(point);
			itemizedOverlay.addOverlay(new OverlayItem(point, "", ""));

			// Otherwise, we must loop over all the overlays
		} else {
			for (GeoPoint point : getItemsOfCategory(category, getBaseContext())) {
				OverlayItem overlayItem = new OverlayItem(point, "", "");
				itemizedOverlay.addOverlay(overlayItem);
			}
		}

		// If we have retrieved items to display, add them to the overlay list
		if (itemizedOverlay.size() > 0) {
			mapOverlays.add(itemizedOverlay);
		}
	}

	/**
	 * Expand and define the Android options menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle the item selected
		switch (item.getItemId()) {

		// Return to the categories screen
		case R.id.home_button:
			startActivity(new Intent(this, FINHome.class));
			return true;
		case R.id.search_button:
			onSearchRequested();
			return true;
		case R.id.login_button:
			startActivity(new Intent(this, FINLogin.class));
			return true;
		case R.id.logout_button:
			final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);

			String result = DBCommunicator.logout(phone_id, getBaseContext());
			if (result.equals(getString(R.string.logged_out))) {
				FINHome.setLoggedIn(false);
			}

			Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();

			return true;
		case R.id.add_new_button:
			startActivity(new Intent(this, FINAddNew.class));
			return true;
		case R.id.settings_button:
			startActivity(new Intent(this, FINSettings.class));
			return true;
		case R.id.help_button:
			startActivity(new Intent(this, FINHelp.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (building.equals("")) {
			menu.findItem(R.id.search_button).setVisible(true);
		}

		return true;
	}
	
	public static GeoPoint getRegionCenter(Context context) {
		// Restore preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		int rid = prefs.getInt("rid", 0);
		
		Cursor cursor = db.query("regions", null, "regions.rid = " + rid, null, null, null, null);
		cursor.moveToFirst();
		
		int latitude = cursor.getInt(cursor.getColumnIndex("latitude"));
		int longitude = cursor.getInt(cursor.getColumnIndex("longitude"));
		
		cursor.close();
		
		return new GeoPoint(latitude, longitude);
	}
	
	public static ArrayList<GeoPoint> getItemsOfCategory(String category, Context context) {
		ArrayList<GeoPoint> items = new ArrayList<GeoPoint>();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int rid = prefs.getInt("rid", 0);
		

		Cursor catCursor = db.query("categories", null, "full_name = '" + category + "'", null, null, null, null);
		catCursor.moveToFirst();
		int cat_id = catCursor.getInt(catCursor.getColumnIndex("cat_id"));
		
		Cursor cursor = db.query("items", null, "rid = " + rid + " AND cat_id = " + cat_id, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			int latitude = cursor.getInt(cursor.getColumnIndex("latitude"));
			int longitude = cursor.getInt(cursor.getColumnIndex("longitude"));
			
			items.add(new GeoPoint(latitude, longitude));
			
			cursor.moveToNext();
		}
		
		cursor.close();
		catCursor.close();
		
		return items;
	}
	
	public static CategoryItem getCategoryItem(GeoPoint point, String category, Context context) {
		return getItemsAtLocation(point, context).get(category);
	}
	
	public static HashMap<String, CategoryItem> getItemsAtLocation(GeoPoint GeoPoint, Context context) {
		HashMap<String, CategoryItem> itemsAtLocation = new HashMap<String, CategoryItem>();
		int latitude = GeoPoint.getLatitudeE6();
		int longitude = GeoPoint.getLongitudeE6();
		
		Cursor catCursor = db.query("categories", null, null, null, null, null, null);
		catCursor.moveToFirst();
		while (!catCursor.isAfterLast()) {
			CategoryItem item = new CategoryItem();
			int cat_id = catCursor.getInt(catCursor.getColumnIndex("cat_id"));
			String name = catCursor.getString(catCursor.getColumnIndex("full_name"));
			
			Cursor cursor = db.query("items", null, "cat_id = " + cat_id + " AND latitude = " + latitude + " AND longitude = " + longitude, null, null, null, "cat_id ASC");
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				cursor.getInt(cursor.getColumnIndex("cat_id"));
				int fid = cursor.getInt(cursor.getColumnIndex("fid"));
				int item_id = cursor.getInt(cursor.getColumnIndex("item_id"));
				
				Cursor cursor2 = db.query("floors", null, "fid = " + fid, null, null, null, null);
				String fname = "";
				if (cursor2.getCount() > 0) {
					cursor2.moveToFirst();
					fname = cursor2.getString(cursor2.getColumnIndex("name"));
				}
				
				String info = cursor.getString(cursor.getColumnIndex("special_info"));
				
				item.addId(item_id);
				item.addFloor_names(fname);
				item.addInfo(info);
				
				cursor2.close();
				cursor.moveToNext();
			}
			cursor.close();
			itemsAtLocation.put(name, item);
			catCursor.moveToNext();
		}
		
		catCursor.close();
		
		return itemsAtLocation;
	}
}