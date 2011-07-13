/***
 * FINMap.java by Eric Hare
 * This class handles the MapView activity of our application
 * It includes user location detection and overlay support
 * The Google Maps API is used to accomplish these tasks
 */

package com.net.finditnow;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
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
	private String itemName;
	private String listOfLocations;

	// Location and GeoPoint Variables
	private static GeoPoint location;    
	private static HashMap<GeoPoint,HashMap<String,CategoryItem>> geoPointItem;

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

		// Get the category, building, and potentially item name
		Bundle extras = getIntent().getExtras(); 
		category = extras.getString("category");
		building = extras.getString("building");
		itemName = extras.getString("itemName");

		listOfLocations = extras.getString("locations");	

		// Set the Breadcrumb in the titlebar
		String title = (!building.equals("")? building : category + (!itemName.equals("")? " > " + itemName : ""));
		setTitle(getString(R.string.app_name) + " > " + title);

		// Check connection of Android
		ConnectionChecker conCheck = new ConnectionChecker(this, FINMap.this);

		// Parse retrieved locations from the database
		if (listOfLocations.equals(getString(R.string.timeout))) {
			conCheck.connectionError();
		} else {
			geoPointItem = JsonParser.parseCategoryJson(listOfLocations, category);

			// Create the map and the map view and detect user location
			createMap();
			locateUser();

			// Add these locations to the map view
			placeOverlays();
		}
	}

	/**
	 * Called when the activity is paused to disable the location services
	 */
	@Override
	public void onPause() {
		super.onPause();

		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();

		editor.putInt("centerLat", mapView.getMapCenter().getLatitudeE6());
		editor.putInt("centerLon", mapView.getMapCenter().getLongitudeE6());
		editor.putInt("zoomLevel", mapView.getZoomLevel());

		// Commit the edits!
		editor.commit();

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

	/**
	 * This method returns the distance from the user's current location to a given point
	 * It returns this in BigDecimal format for ease of processing
	 * 
	 * @param point A GeoPoint corresponding to the location under consideration
	 * 
	 * @return A BigDecimal representing the distance to this point in miles
	 */
	public static BigDecimal distanceBetween(GeoPoint point1, GeoPoint point2) {

		// Define a math context and two location variables to process
		MathContext mc = new MathContext(2);
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
			return new BigDecimal(loc1.distanceTo(loc2) * 0.000621371192, mc);
		} else {

			// Return -1 if the location was not valid
			return new BigDecimal(-1);
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
			appData.putString("itemName", itemName);
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			
			appData.putString("lat", prefs.getInt("campusLat", 0)+"");
			appData.putString("lon", prefs.getInt("campusLon", 0)+"");
			
			appData.putString("locations", listOfLocations);
			
			startSearch(null, false, appData, false);
		}
		return true;
	}

	public static CategoryItem getCategoryItem(GeoPoint point, String category) {
		if (geoPointItem != null && geoPointItem.get(point) != null) {
			return geoPointItem.get(point).get(category);
		} else {
			return null;
		}
	}

	/**
	 * This method computes the walking time for a given distance based on the mile time
	 * 
	 * @param distance The distance to calculate walking time over
	 * @param mile_time The amount of time in minutes to walk a mile
	 * 
	 * @return The walking time in minutes that it takes to walk the given distance rounded
	 */
	public static int walkingTime(BigDecimal distance, int mile_time) {
		BigDecimal dec = new BigDecimal(mile_time * distance.doubleValue(), new MathContext(2));
		return dec.intValue();
	}

	/**
	 * This method creates the map and displays the overlays on top of it 
	 */
	private void createMap() {

		// Restore preferences
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		int latitude = prefs.getInt("campusLat", 0);
		int longitude = prefs.getInt("campusLon", 0);
		int zoomLevel = prefs.getInt("zoomLevel", 18);

		// Initialize our MapView and MapController
		mapView = (FINMapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		mapController = mapView.getController();
		mapController.setZoom(zoomLevel);
		mapController.setCenter(new GeoPoint(latitude, longitude));

		Bundle extras = getIntent().getExtras();
		if (getIntent().hasExtra("centerLat") && getIntent().hasExtra("centerLon")) {
			mapController.animateTo(new GeoPoint(extras.getInt("centerLat"), extras.getInt("centerLon")));
		}

		// Build up our overlays and initialize our "UWOverlay" class
		mapOverlays = mapView.getOverlays();
		drawable = getResources().getDrawable(building.equals("")? FINHome.getIcon(category) : R.drawable.buildings);
		itemizedOverlay = new IconOverlay(drawable, this, category, itemName, geoPointItem);

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
					appData.putString("itemName", itemName);
					appData.putString("lat", prefs.getInt("campusLat", 0)+"");
					appData.putString("lon", prefs.getInt("campusLon", 0)+"");
					appData.putString("locations", listOfLocations);

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
				mapController.animateTo(new GeoPoint(prefs.getInt("campusLat", 0), prefs.getInt("campusLon", 0)));
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
			GeoPoint point = FINHome.getGeoPointFromBuilding(building);
			mapController.animateTo(point);
			itemizedOverlay.addOverlay(new OverlayItem(point, "", ""));

			// Otherwise, we must loop over all the overlays
		} else {
			for (GeoPoint point : geoPointItem.keySet()) {
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
}