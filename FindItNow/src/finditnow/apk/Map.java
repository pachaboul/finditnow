/** Map.java
 *  This is MapView class which uses the Google Maps API
 *  It draws the overlay, communicates with the database, and detects user location
 */

package finditnow.apk;

import java.util.HashMap;
import java.util.List;
import com.google.android.maps.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class Map extends MapActivity {
	
	// Map and Location Variables
	private MapView mapView;
	private MapController mapController;
	private MyLocationOverlay locOverlay;
	
	// Overlay Variables
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private UWOverlay itemizedOverlay;
	
	// Shared static variables that the other modules can access
	private static HashMap<String, Integer> icons;
	private static String category;
	
    /** Called when the activity is first created.
     * 	It initializes the map layout, detects the user's category, and builds the map
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	// Restore the saved instance and generate the primary (main) layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        // And to get the item name for buildings, supplies:
        // String itemName = extras.getString("itemName");
        Bundle extras = getIntent().getExtras(); 
        category = extras.getString("category");
        
        // Store a map from categories to icons so that other modules can use it
        icons = createIconsList();
        
        // Create the map and detect the user's location
        createMap();
        locateUser();
    }
    
    /** This method returns a map from categories to icons (icons must be the same name as the category, in lowercase */
    private HashMap<String, Integer> createIconsList() {
    	
    	HashMap<String, Integer> iconsMap = new HashMap<String, Integer>();
    	
    	// Loop over each category and map it to the icon file associated with it
    	for (String str : Menu.categories) {
			iconsMap.put(str.toLowerCase(), getResources().getIdentifier("drawable/"+str.toLowerCase(), null, getPackageName()));
		}
    	
		return iconsMap;
    }
    
    /** This method creates the map and displays the overlays on top of it */
    private void createMap() {
    	
        // Initialize our MapView and MapController
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        
        // Build up our overlays and initialize our "UWOverlay" class
        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(getIcons().get(getCategory()));
        itemizedOverlay = new UWOverlay(drawable, this);
        
        // Create a GeoPoint location on the Paul Allen Center and animate to it
        GeoPoint point = new GeoPoint(47653286,-122305850);
        mapController.animateTo(point);
        mapController.zoomToSpan(2500, 2500);
        OverlayItem overlayItem = new OverlayItem(point, "CSE Building", "Coffee Stand (Floor 1)");
        
        // Add our overlay to the list
        itemizedOverlay.addOverlay(overlayItem);
        mapOverlays.add(itemizedOverlay);
    }
    
    /** This method locates the user and displays the user's location in an overlay icon */
    private void locateUser() {
    	
    	// Define a new LocationOverlay and enable it
        locOverlay = new MyLocationOverlay(this, mapView);
        locOverlay.enableMyLocation();
        mapOverlays.add(locOverlay);
        
        // Run this ONLY once we get a fix on the location
		Runnable runnable = new Runnable() {
			public void run() {
				mapController.animateTo(locOverlay.getMyLocation());
				mapController.zoomToSpan(2500, 2500);
			}
		};
		
		locOverlay.runOnFirstFix(runnable);
    }
    
    /** Required for Android Maps API compatibility */
    @Override
	protected boolean isRouteDisplayed() {
		return false;
	}
    
    /** This method returns the icons map */
    public static HashMap<String, Integer> getIcons() {
    	return icons;
    }
    
    /** This method returns the current category */
    public static String getCategory() {
    	return category;
    }
}