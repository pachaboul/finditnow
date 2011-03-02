/** FINMap.java
 *  This is MapView class which uses the Google Maps API
 *  It draws the overlay, communicates with the database, and detects user location
 */

package com.net.finditnow;

import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import com.google.android.maps.*;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * This is the Map class, which integrates the Google Maps API and itemized overlays
 * It also includes location-awareness and houses most of the shared data in the front-end
 * @author EricHare
 *
 */
public class FINMap extends MapActivity {
	
	// Map and Location Variables
	private MapView mapView;
	private MapController mapController;
	private MyLocationOverlay locOverlay;
	
	// Overlay Variables
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private UWOverlay itemizedOverlay;
	
	// Shared static variables that the other modules can access
	private static String category;
	private static String itemName;
	
	// Location and GeoPoint Variables
	private static GeoPoint location;
	private static HashMap<GeoPoint, String[]> geopointMap;
	private static HashMap<GeoPoint,String> geopointNameMap;	
	private static final GeoPoint DEFAULT_LOCATION = new GeoPoint(47654799,-122307776);

	/** Called when the activity is first created.
     * 	It initializes the map layout, detects the user's category, and builds the map
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	// Restore the saved instance and generate the primary (main) layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        // And to get the item name for buildings, supplies:
        Bundle extras = getIntent().getExtras(); 
        category = extras.getString("category");
        itemName = extras.getString("itemName");
        
        // Create the map and detect the user's location
        createMap();
        locateUser();
        
    	JSONArray listOfLocations = Request.requestFromDB(category, itemName, DEFAULT_LOCATION);
    	geopointMap = JsonParser.parseJson(listOfLocations);
    	geopointNameMap = JsonParser.parseNameJson(listOfLocations);
            
    	placeOverlays();
    }
    
    public void onPause() {
    	super.onPause();
    	locOverlay.disableMyLocation();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    
    public static double distanceTo(GeoPoint point) {
    	Location dest = new Location("Hi");
    	Location curr = new Location("Bye");
    	
    	if (location != null && !location.equals(DEFAULT_LOCATION)) {
	    	float latitude_curr = (float) (location.getLatitudeE6()*1E-6);
	    	float longitude_curr = (float) (location.getLongitudeE6()*1E-6);
	    	float latitude_dest = (float) (point.getLatitudeE6()*1E-6);
	    	float longitude_dest = (float) (point.getLongitudeE6()*1E-6);
	    	
	    	curr.setLatitude(latitude_curr);
	    	curr.setLongitude(longitude_curr);
	    	dest.setLatitude(latitude_dest);
	    	dest.setLongitude(longitude_dest);
	    	
	    	return (curr.distanceTo(dest) * 0.000621371192);
    	} else {
    		return -1;
    	}
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.categories_button:
                startActivity(new Intent(this, FINMenu.class));
	            return true;
	        case R.id.my_location_button:
	        	if (location != null) {
	        		mapController.animateTo(location);
	        	} else {
	        		Toast.makeText(this, "Error: Could Not Detect Location", Toast.LENGTH_SHORT).show();
	        	}
	            return true;
	        case R.id.add_new_button:
	        	startActivity(new Intent(this, FINAddNew.class));
	            return true;
	        case R.id.help_button:
	        	startActivity(new Intent(this, FINHelp.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    /** This method creates the map and displays the overlays on top of it */
    private void createMap() {
    	
        // Initialize our MapView and MapController
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        
        // Build up our overlays and initialize our "UWOverlay" class
        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(FINMenu.getIcon(getCategory()));
        itemizedOverlay = new UWOverlay(drawable, this);
        
        // Zoom out enough
        mapController.setZoom(17);
    }
    
    /** This method locates the user and displays the user's location in an overlay icon */
    private void locateUser() {
    	// Define a new LocationOverlay and enable it
        locOverlay = new MyLocationOverlay(this, mapView) {
        	public void onLocationChanged(Location loc) {
        		super.onLocationChanged(loc);
        		location = new GeoPoint((int)(loc.getLatitude()*1E6), (int)(loc.getLongitude()*1E6));
        	}
        };
        
        // Enable the overlay
        locOverlay.enableMyLocation();
        
    	Toast.makeText(this, "Detecting Location, Please Wait...", Toast.LENGTH_SHORT).show();
    	location = locOverlay.getMyLocation();
    	
    	Handler handler = new Handler();
    	handler.postDelayed(new Runnable() { 
            public void run() { 
            	// Try to get the current location, otherwise set a default
        		if (location == null) {
        			Toast.makeText(FINMap.this, "Error: Could Not Detect Location", Toast.LENGTH_SHORT).show();
        			mapController.animateTo(DEFAULT_LOCATION);
        		} else {
	        		mapOverlays.add(locOverlay);
	        		mapController.animateTo(location);
        		}
            } 
        }, 3000);
    }
    
    /** This method places the locations retrieved from the database onto the map */
    private void placeOverlays() {
    	
    	if (getCategory().equals("buildings")) {
    		GeoPoint point = CategoryList.getGeoPointFromBuilding(itemName);
    		geopointMap.put(point, FINMenu.getBuildings().get(point).getFloorName());
    	}
    	
        for (GeoPoint point : geopointMap.keySet()) {
        	OverlayItem overlayItem = new OverlayItem(point, "blah", "blah");
        	itemizedOverlay.addOverlay(overlayItem);
        }
        
        // Add our overlay to the list
        if (!geopointMap.keySet().isEmpty()) {
        	mapOverlays.add(itemizedOverlay);
        }
    }
 
    /** Required for Android Maps API compatibility */
    @Override
	protected boolean isRouteDisplayed() {
		return false;
	}
    
    /** This method returns the current category */
    public static String getCategory() {
    	return category;
    }
	
	/** This method returns the floors associated with the location p */
	public static String[] getFloors(GeoPoint p) {
		return geopointMap.get(p);
	}
	
	/** This method returns the location name associated with the location p */
	public static String getLocationName(GeoPoint p) {
		return geopointNameMap.get(p);
	}
}