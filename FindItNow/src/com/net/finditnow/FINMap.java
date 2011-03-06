/***
 * FINMap.java by Eric Hare
 * This class handles the MapView activity of our application
 * It includes user location detection and overlay support
 * The Google Maps API is used to accomplish these tasks
 */

package com.net.finditnow;

// Java library imports
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;

// JSONArray import
import org.json.JSONArray;

// Google Maps for Android imports
import com.google.android.maps.*;

// Other Android library dependencies
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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
	private static HashMap<GeoPoint, CategoryItem> geoPointItem;
	
	// A constant representing the default location of the user
	// Change this the coordinates of another campus if desired (defaults to UW Seattle)
	public static final GeoPoint DEFAULT_LOCATION = new GeoPoint(47654799,-122307776);

	/** 
	 * Called when the activity is first created.
     * It initializes the map layout, detects the user's category, and builds the map
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	// Restore the saved instance and generate the primary (main) layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        // Get the item name for buildings, supplies:
        Bundle extras = getIntent().getExtras(); 
        category = extras.getString("category");
        itemName = extras.getString("itemName");
        
        // Set the Breadcrumb in the titlebar
        if (itemName == null) {
        	setTitle("FindItNow > " + FINUtil.capFirstChar(category));
        } else {
        	setTitle("FindItNow > " + FINUtil.capFirstChar(category) + " > " + FINUtil.capFirstChar(itemName));
        }
        
        // Create the map and the map view
        createMap();
        
        // Retrieve locations from the database and parse them
    	JSONArray listOfLocations = Get.requestFromDB(category, itemName, DEFAULT_LOCATION);
    	if (listOfLocations == null) {
    		geoPointItem = JsonParser.parseCategoryJson("");	
    	} else {
    		geoPointItem = JsonParser.parseCategoryJson(listOfLocations.toString());
    	}

    	// Add these locations to the map view and detect user location
    	placeOverlays();
        locateUser();
    }
    /**
     * Called when the activity is paused to disable the location services
     */
    public void onPause() {
    	super.onPause();
    	locOverlay.disableMyLocation();
    }
    
    /**
     * Create the Android options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    
    /**
     * Expand and define the Android options menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
    	// Handle the item selected
        switch (item.getItemId()) {
        
        	// Return to the categories screen
	        case R.id.categories_button:
                startActivity(new Intent(this, FINMenu.class));
	            return true;
	            
	        // Center the map on the user's location if it is possible
	        case R.id.my_location_button:
	        	if (location != null) {
	        		mapController.animateTo(location);
	        	} else {
	        		Toast.makeText(this, "Error: Could not detect your location", Toast.LENGTH_SHORT).show();
	        	}
	            return true;
	            
	        // Add a new location to the map
	        case R.id.add_new_button:
	        	startActivity(new Intent(this, FINAddNew.class));
	            return true;
	        
	        // Open up our help documentation
	        case R.id.help_button:
	        	startActivity(new Intent(this, FINHelp.class));
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
        }
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
    
    /**
     * This method locates the user and displays the user's location in an overlay icon
     */
    private void locateUser() {
    	
    	// Define a new LocationOverlay and enable it
        locOverlay = new MyLocationOverlay(this, mapView) {
        	
        	// Extend onLocationChanged() to add the result to the location variable
        	public void onLocationChanged(Location loc) {
        		super.onLocationChanged(loc);
        		location = new GeoPoint((int)(loc.getLatitude()*1E6), (int)(loc.getLongitude()*1E6));
        		mapOverlays.add(locOverlay);
        	}
        };
        locOverlay.enableMyLocation();
        
        // Attempt to detect the user's current location
    	Toast.makeText(this, "Detecting your location, please wait...", Toast.LENGTH_SHORT).show();
    	location = locOverlay.getMyLocation();
    	
    	// Define a handler to run the given commands after 3 seconds
    	Handler handler = new Handler();
    	handler.postDelayed(new Runnable() { 
    		
    		// Run this method after 3 seconds have elapsed
            public void run() { 
            	
            	// Try to get the current location, otherwise set a default
        		if (location == null) {
        			Toast.makeText(FINMap.this, "Error: Could not detect your location", Toast.LENGTH_SHORT).show();
        			mapController.animateTo(DEFAULT_LOCATION);
        		} else {
	        		mapOverlays.add(locOverlay);
	        		mapController.animateTo(location);
        		}
            } 
        }, 5000);
    }
    
    /**
     * This method places the locations retrieved from the database onto the map
     */
    private void placeOverlays() {
    	
    	// If the category is buildings, then we only put the single point on the map
    	if (getCategory().equals("buildings")) {
    		GeoPoint point = FINMenu.getGeoPointFromBuilding(itemName);
    		
    		CategoryItem item = new CategoryItem();
    		for(String flr:FINMenu.getBuilding(point).getFloorNames())
    			item.addFloor_names(flr);

    		geoPointItem.put(point, item);
    	}
    	
    	// Loop over the locations that we have retrieved and add them
        for (GeoPoint point : geoPointItem.keySet()) {
        	OverlayItem overlayItem = new OverlayItem(point, "", "");
        	itemizedOverlay.addOverlay(overlayItem);
        }
        
        // If we have retrieved items to display, add them to the overlay list
        if (!geoPointItem.keySet().isEmpty()) {
        	mapOverlays.add(itemizedOverlay);
        }
    }
 
    /**
     * Required for Android Maps API compatibility
     */
    @Override
	protected boolean isRouteDisplayed() {
		return false;
	}
    
    /**
     * This method returns the category selected by the user
     * @return A String representing the category chosen
     */
    public static String getCategory() {
    	return category;
    }
    
    /**
     * This method returns the item name selected by the user if supplies is chosen
     * @return A String representing the item name, null if supplies is not chosen
     */
    public static String getItemName() {
    	return itemName;
    }
    
    /**
     * This method returns an item located at the point p
     * @param p A GeoPoint representing the location to retrieve the category item
     * @return A CategoryItem object containing the list of locations
     */
    public static CategoryItem getCategoryItem(GeoPoint p){
    	return geoPointItem.get(p);
    }
    
    /**
     * This method returns the user's current location
     * @return GeoPoint representing the user's location
     */
    public static GeoPoint getLocation() {
    	return location;
    }
}