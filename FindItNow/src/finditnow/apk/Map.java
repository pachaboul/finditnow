/** Map.java
 *  This is MapView class which uses the Google Maps API
 *  It draws the overlay, communicates with the database, and detects user location
 */

package finditnow.apk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.maps.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * This is the Map class, which integrates the Google Maps API and itemized overlays
 * It also includes location-awareness and houses most of the shared data in the front-end
 * @author EricHare
 *
 */
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
	private static HashMap<GeoPoint, String> buildings;
	private static String category;
	private static String itemName;
	
	// Location and GeoPoint Variables
	private JSONArray listOfLocations;
	private static GeoPoint location;
	private static java.util.Map<GeoPoint, String[]> geopointMap;
	private static java.util.Map<GeoPoint,String> geopointNameMap;

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
        
        // Store a map from categories to icons so that other modules can use it
        icons = createIconsList();
        buildings = createBuildingsList();
        
        // Create the map and detect the user's location
        createMap();
        locateUser();
        if (getCategory().equals("buildings")) {
        	for (GeoPoint p : buildings.keySet()) {
        		if (buildings.get(p).equals(itemName)) {
        			itemizedOverlay.addOverlay(new OverlayItem(p, "blah", "blah"));
        	        mapOverlays.add(itemizedOverlay);
        		}
        	}
        } else {
	        listOfLocations = requestLocations();
	        geopointMap = JsonParser.parseJson(listOfLocations.toString());
	        placeOverlays();
	        geopointNameMap = JsonParser.parseNameJson(listOfLocations.toString());
        }
    }
    
    public void onPause() {
    	super.onPause();
    	locOverlay.disableMyLocation();
    }
    
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.android_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.my_location:
        	centerOnLocation();
            return true;
        case R.id.help:
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
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
    
    /** This method creates a map from GeoPoint coordinates to building names */
    private HashMap<GeoPoint, String> createBuildingsList() {
    	HashMap<GeoPoint, String> buildingsMap = new HashMap<GeoPoint, String>();
    	
    	buildingsMap.put(new GeoPoint(47654799, -122307776), "Mary Gates Hall");
    	buildingsMap.put(new GeoPoint(47653613, -122306380), "Electrical Engineering");
    	buildingsMap.put(new GeoPoint(47654244, -122306348), "Guggenheim Hall");
    	buildingsMap.put(new GeoPoint(47654860, -122306558), "Sieg Hall");
    	buildingsMap.put(new GeoPoint(47657123, -122308101), "Savery Hall");
    	buildingsMap.put(new GeoPoint(47656629, -122307181), "Smith Hall");
    	buildingsMap.put(new GeoPoint(47656582, -122309098), "Kane Hall");
    	buildingsMap.put(new GeoPoint(47656465, -122310287), "Odegaard Undergraduate Library");
    	
    	return buildingsMap;
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
        
        // Zoom out enough
        mapController.setZoom(17);
    }
    
    /** This method locates the user and displays the user's location in an overlay icon */
    private void locateUser() {
    	
    	// Define a new LocationOverlay and enable it
        locOverlay = new MyLocationOverlay(this, mapView);
        
        // Enable the overlay
        locOverlay.enableMyLocation();
        mapOverlays.add(locOverlay);
        
        Runnable runnable = new Runnable() {
			public void run() {
				centerOnLocation();
			}
        };
        
        locOverlay.runOnFirstFix(runnable);
    }
    
    private void centerOnLocation() {
    	// Try to get the current location, otherwise set a default
		location = locOverlay.getMyLocation();
		if (location == null) {
			location = new GeoPoint(47654799,-122307776);
		}
		mapController.animateTo(location);
    }
    
    /**This method makes a request across the network to the database sending
    	the current location and category
    	@return: a JSONArray if item locations sent from the database */
    private JSONArray requestLocations() {
    	/*
  	   * HTTP Post request
  	   */
    	String data = "";
	  	InputStream iStream = null;
	  	JSONArray infoArray = null;
	  	try{
  	        HttpClient httpclient = new DefaultHttpClient();
  	        HttpPost httppost = new HttpPost("http://cubist.cs.washington.edu/~johnsj8/getLocations.php");
  			List nameValuePairs = new ArrayList();
  			
  			nameValuePairs.add(new BasicNameValuePair("cat", category));
  			if (itemName != null) {
  				String item = itemName;
  				if (item.equals("Blue books")) {
  					item = "blue_book";
  				} else if (item.equals("Scantrons")) {
  					item = "scantron";
  				}
  				nameValuePairs.add(new BasicNameValuePair("item", item));
  			}
  			nameValuePairs.add(new BasicNameValuePair("lat", location.getLatitudeE6()+""));
  			nameValuePairs.add(new BasicNameValuePair("long", location.getLongitudeE6()+""));
  	        
  			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
  	        HttpResponse response = httpclient.execute(httppost);
  	        HttpEntity entity = response.getEntity();
  	        iStream = entity.getContent();
	  	}catch(Exception e){
	  	    Log.e("log_tag", "Error in http connection "+e.toString());
	  	}
	  	//convert response to string
	  	try{
  	        BufferedReader reader = new BufferedReader(new InputStreamReader(iStream,"iso-8859-1"),8);
  	        StringBuilder sb = new StringBuilder();
  	        String line = null;
  	        while ((line = reader.readLine()) != null) {
  	        	sb.append(line + "\n");
  	        }
  	        iStream.close();
  	 
  	        data = sb.toString();
	  	}catch(Exception e){
	  	    Log.e("log_tag", "Error converting result "+e.toString());
	  	}
	  	
	  	//Log.i("log_tag", "the output of request is : "+data);
	  	try {
			infoArray = new JSONArray(data);
		} catch (JSONException e) {
			Log.e("log_tag", "Error converting response to JSON "+e.toString());
		}
	  	return infoArray;
    }
    
    /** This method places the locations retrieved from the database onto the map */
    private void placeOverlays() {
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
    
    /** This method returns the icons map */
    public static HashMap<String, Integer> getIcons() {
    	return icons;
    }
    
    /** This method returns the current category */
    public static String getCategory() {
    	return category;
    }
	
    /** This method returns the building associated with the location p */
	public static String getBuilding(GeoPoint p) {
		return buildings.get(p);
	}
	
	/** This method returns the floors associated with the location p */
	public static String[] getFloors(GeoPoint p)
	{
		return geopointMap.get(p);
	}
	
	/** This method returns the location name associated with the location p */
	public static String getLocationName(GeoPoint p){
		return geopointNameMap.get(p);
	}
}