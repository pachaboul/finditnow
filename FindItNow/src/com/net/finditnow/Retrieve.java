/***
 * Retrieve.java by Eric Hare
 * This class defines our Retrieve Locations interface with the PHP layer
 * It is called to retrieve items from the database, including categories and buildings
 */

package com.net.finditnow;

// Java imports for parsing strings and lists
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// Apache and Json necessary imports
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;

// Log import to store error messages
import android.util.Log;

// GeoPoint for location services
import com.google.android.maps.GeoPoint;

public class Retrieve {
	
	// A Constant representing the location of the root of the get files
	private static final String REQUEST_LOCATIONS_ROOT = "http://cubist.cs.washington.edu/projects/11wi/cse403/RecycleLocator/";
	
	/**
	 * This method processes a request to retrieve locations from the database
	 * It is used for plotting the locations properly on the map
	 * 
	 * @param category The category of items to retrieve
	 * @param itemName If the category is supplies, the type of supplies to retrieve
	 * @param location The location of the user making the request
	 * 
	 * @return A JSONArray of locations, categories, or buildings from the database
	 */
	public static JSONArray requestFromDB(String category, String itemName, GeoPoint location) {

		// Initialize input stream and response variables
		String data = "";
	  	InputStream iStream = null;
	  	JSONArray infoArray = null;
	  	
	  	// Attempt to make the HTTPPOST to the given location
	  	try {
		        HttpClient httpclient = new DefaultHttpClient();
		        String suffix = (location == null? (category == null? "getCategories.php" : "getBuildings.php") : "getLocations.php");
		        HttpPost httppost = new HttpPost(REQUEST_LOCATIONS_ROOT + suffix);
		        
		        // If the location is not null, this is a request for items in a category
		        if (location != null) {
		        	
		  			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();		  			
		  			nameValuePairs.add(new BasicNameValuePair("cat", category));
		  			
		  			// If the itemName is not null, this is a request for supplies
		  			if (itemName != null) {
		  				String item = itemName;
		  				if (item.equals("Blue books")) {
		  					item = "blue_book";
		  				} else if (item.equals("Scantrons")) {
		  					item = "scantron";
		  				}
		  				nameValuePairs.add(new BasicNameValuePair("item", item));
		  			} 
		  			
		  			// Add the lat and long of the user's location
		  			nameValuePairs.add(new BasicNameValuePair("lat", location.getLatitudeE6()+""));
		  			nameValuePairs.add(new BasicNameValuePair("long", location.getLongitudeE6()+""));
		  	        
		  			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        }
		        
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        iStream = entity.getContent();
	  	} catch(Exception e) {
	  	    Log.e("log_tag", "Error in http connection " + e.toString());
	  	}
	  	
	  	// Attempt to convert the response into a string
	  	try {
		        BufferedReader reader = new BufferedReader(new InputStreamReader(iStream,"iso-8859-1"),8);
		        StringBuilder sb = new StringBuilder();
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		        	sb.append(line + "\n");
		        }
		        iStream.close();
		 
		        data = sb.toString();
	  	} catch(Exception e) {
	  	    Log.e("log_tag", "Error converting result " + e.toString());
	  	}
	  	
	  	// Now try to convert it to a JSONArray
	  	try {
	  		
	  		// If the category is null, we need to retrieve a comma separated list
		  	if (category == null) {
		  		JSONTokener jsontok = new JSONTokener(data);
		  		infoArray = CDL.rowToJSONArray(jsontok);
		  	} else {
		  		infoArray = new JSONArray(data);
		  	}
		} catch (JSONException e) {
			Log.e("log_tag", "Error converting response to JSON " + e.toString());
		}
		
	  	return infoArray;
	}
}
