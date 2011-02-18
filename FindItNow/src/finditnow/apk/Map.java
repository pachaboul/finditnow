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
	
	private MapView mapView;
	private MapController mapController;
	private MyLocationOverlay locOverlay;
	
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private UWOverlay itemizedOverlay;
	private static HashMap<String, Integer> icons;
	private static String category;
	private JSONArray listOfLocations;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// Restore the saved instance and generate the primary (main) layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // And to get the item name for buildings, supplies:
        // String itemName = extras.getString("itemName");
        Bundle extras = getIntent().getExtras(); 
        category = extras.getString("category");
        
        icons = createIconsList();
        
        createMap();
        locateUser();
        listOfLocations = requestLocations();
    }
    
    private HashMap<String, Integer> createIconsList() {
    	HashMap<String, Integer> iconsMap = new HashMap<String, Integer>();
    	for (String str : Menu.categories) {
			iconsMap.put(str.toLowerCase(), getResources().getIdentifier("drawable/"+str.toLowerCase(), null, getPackageName()));
		}
		return iconsMap;
    }
    
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
    
    //This method makes a request across the network to the database sending
    //the current location and category
    //Returns: a JSONArray if item locations sent from the database
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
 
    @Override
	protected boolean isRouteDisplayed() {
		return false;
	}
    
    public static HashMap<String, Integer> getIcons() {
    	return icons;
    }
    
    public static String getCategory() {
    	return category;
    }
}