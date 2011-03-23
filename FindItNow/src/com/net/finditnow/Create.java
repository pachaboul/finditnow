/***
 * Create.java by Eric Hare
 * This class defines our Create/Add interface with the PHP layer
 * It is called to add items to the database
 */

package com.net.finditnow;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class Create {
	
	// A Constant representing the location of the Create.php file
	public static final String CREATE_LOCATION = "http://yinnopiano.com/fin/FINsert/create.php";
	
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
								  String special_info, String bb, String sc, String print,
								  String phone_id, Context context) {

		// Initialize input stream and response variables
	  	InputStream iStream = null;
		String data = "";
	  	
		// Attempt to make the HTTPPOST to the given location
		// DESIGN PATTERN: Exceptions.  In Get/Update/Create, we catch any exception in PHP communication
	  	//				   This also allows us to localize errors that occur during the process
	  	try {
		        HttpPost httppost = new HttpPost(CREATE_LOCATION);
		  		
		        // Initialize the array of name value pairs
		        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		        
		        // Special case for supplies.  Handle the optional parameters as needed
		        nameValuePairs.add(new BasicNameValuePair("category", category));
		        if (category.equals("school_supplies")) {
		        	nameValuePairs.add(new BasicNameValuePair("bb", bb));
		        	nameValuePairs.add(new BasicNameValuePair("sc", sc));
		        	nameValuePairs.add(new BasicNameValuePair("print", print));
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
	  			HttpParams httpParameters = new BasicHttpParams();
				
				// Set the timeout in milliseconds until a connection is established.
				int timeoutConnection = 5000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				
				// Set the default socket timeout (SO_TIMEOUT) 
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 5000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
				HttpResponse httpResponse = httpClient.execute(httppost);
				
				HttpEntity entity = httpResponse.getEntity();
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
	  	    return context.getString(R.string.timeout);
	  	}
	  	
	  	return data;
	}
}
