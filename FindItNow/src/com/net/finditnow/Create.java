/***
 * Create.java by Eric Hare
 * This class defines our Create/Add interface with the PHP layer
 * It is called to add items to the database
 */

package com.net.finditnow;

// Java imports for parsing strings and lists
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// Apache necessary imports
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

// Log import to store error messages
import android.util.Log;

// GeoPoint for location services
import com.google.android.maps.GeoPoint;

public class Create {
	
	// A Constant representing the location of the Create.php file
	public static final String CREATE_LOCATION = "http://cubist.cs.washington.edu/projects/11wi/cse403/RecycleLocator/FINsert/create.php";
	
	/**
	 * This method processes a request to add an item to the database
	 * It calls the Create.php file with the given parameters
	 * 
	 * @param category The category selected by the user
	 * @param location The location of the object if it is not in a building
	 * @param fid The floor ID of the object if it IS in a building
	 * @param special_info Currently ignored: Include any special info
	 * @param bb If category is school supplies, "bb" indicates blue books at location
	 * @param sc If category is school supplies, "sc" indicates scantrons at location
	 * @param print If category is school supplies, "print" indicates printing at location
	 * 
	 * @return A String indicating if the request was successful
	 */
	public static String sendToDB(String category, GeoPoint location, int fid, 
								  String special_info, String bb, String sc, String print) {

		// Initialize input stream and response variables
	  	InputStream iStream = null;
		String data = "";
	  	
		// Attempt to make the HTTPPOST to the given location
	  	try {
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost(CREATE_LOCATION);
		  		
		        // Initialize the array of name value pairs
		        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		        
		        // Special case for supplies.  Handle the optional parameters as needed
		        if (category.toLowerCase() == "supplies") {
		        	nameValuePairs.add(new BasicNameValuePair("category", "school_supplies"));
		        	nameValuePairs.add(new BasicNameValuePair("bb", bb));
		        	nameValuePairs.add(new BasicNameValuePair("sc", sc));
		        	nameValuePairs.add(new BasicNameValuePair("print", print));
		        } else {
		        	nameValuePairs.add(new BasicNameValuePair("category", category.toLowerCase()));
		        }
		        
		        // Add the floor ID, and if not in a building, the lat and long
	  			nameValuePairs.add(new BasicNameValuePair("fid", fid + ""));
	  			if (fid == 0) {
		  			nameValuePairs.add(new BasicNameValuePair("latitude", location.getLatitudeE6()+""));
		  			nameValuePairs.add(new BasicNameValuePair("longitude", location.getLongitudeE6()+""));
	  			}
	  			
	  			// Add any special information that we desire
	  			nameValuePairs.add(new BasicNameValuePair("special_info", special_info));
	  			
	  			// Process the response from the server
	  			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));		        
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        iStream = entity.getContent();
	  	} catch(Exception e) {
	  	    Log.e("log_tag", "Error in http connection " + e.toString());
	  	}
	  	
	  	// Convert server's response to a String
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
	  	
	  	return data;
	}
}
