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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

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
	
	private JSONArray listOfLocations;
	
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
        listOfLocations = requestLocations();
        placeOverlays(listOfLocations);
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
  	        HttpPost httppost = new HttpPost("http://cubist.cs.washington.edu/~johnsj8/testJSON.php");
  			List nameValuePairs = new ArrayList();
  			
  			nameValuePairs.add(new BasicNameValuePair("cat", category));
  			GeoPoint location = locOverlay.getMyLocation();
  			nameValuePairs.add(new BasicNameValuePair("lat", location.getLatitudeE6()+""));
  			nameValuePairs.add(new BasicNameValuePair("lon", location.getLongitudeE6()+""));
  	        
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
	  	try {
			infoArray = new JSONArray(data);
		} catch (JSONException e) {
			Log.e("log_tag", "Error converting response to JSON "+e.toString());
		}
	  	return infoArray;
    }
    
    private void placeOverlays(JSONArray listOfLocations) {
    	// Create a GeoPoint location on the Paul Allen Center and animate to it
        GeoPoint cse = new GeoPoint(47653286,-122305850);
        mapController.animateTo(cse);
        mapController.zoomToSpan(2500, 2500);
        OverlayItem cseItem = new OverlayItem(cse, "CSE Building", "Coffee Stand (Floor 1)");
        
        /*java.util.Map<GeoPoint, String[]> geopointMap = JsonParser.parseJson(listOfLocations.toString());
        for (GeoPoint point : geopointMap.keySet()) {
        	OverlayItem overlayItem = new OverlayItem(point, "blah", "blah");
        	itemizedOverlay.addOverlay(overlayItem);
        }*/
        
        // Add our overlay to the list
        itemizedOverlay.addOverlay(cseItem);
        mapOverlays.add(itemizedOverlay);
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