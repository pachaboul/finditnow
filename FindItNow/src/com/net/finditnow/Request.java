package com.net.finditnow;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class Request {
	
	/**This method makes a request across the network to the database sending
	the current location and category
	@return: a JSONArray if item locations sent from the database */
	public static JSONArray requestFromDB(String category, String itemName, GeoPoint location) {
		/*
		   * HTTP Post request
		   */
		String data = "";
	  	InputStream iStream = null;
	  	JSONArray infoArray = null;
	  	try{
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = (location != null)? new HttpPost("http://cubist.cs.washington.edu/~johnsj8/getLocations.php") : 
		        										new HttpPost("http://cubist.cs.washington.edu/~johnsj8/getBuildings.php");
		        if (location != null) {
		  			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		  			
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
		        }
		        
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
}
