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
import org.json.*;

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class Create {
	
	/**This method makes a request across the network to the database sending
	the current location and category
	@return: a JSONArray if item locations sent from the database */
	public static JSONArray sendToDB(String category, GeoPoint location, int fid, String special_info, String bb, String sc, String print) {
		/*
		   * HTTP Post request
		   */
		String data = "";
	  	InputStream iStream = null;
	  	JSONArray infoArray = null;
	  	try{
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost("http://cubist.cs.washington.edu/projects/11wi/cse403/RecycleLocator/FINsert/create.php");
		  			
		        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		        
		        if (category.toLowerCase() == "supplies") {
		        	nameValuePairs.add(new BasicNameValuePair("category", "school_supplies"));
		        	nameValuePairs.add(new BasicNameValuePair("bb", bb));
		        	nameValuePairs.add(new BasicNameValuePair("sc", sc));
		        	nameValuePairs.add(new BasicNameValuePair("print", print));
		        } else {
		        	nameValuePairs.add(new BasicNameValuePair("category", category.toLowerCase()));
		        }
		        
	  			nameValuePairs.add(new BasicNameValuePair("fid", fid+""));
	  			if (fid == 0) {
		  			nameValuePairs.add(new BasicNameValuePair("latitude", location.getLatitudeE6()+""));
		  			nameValuePairs.add(new BasicNameValuePair("longitude", location.getLongitudeE6()+""));
	  			}
	  			nameValuePairs.add(new BasicNameValuePair("special_info", special_info));
	  			
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
		        Log.v("test", data);
	  	}catch(Exception e){
	  	    Log.e("log_tag", "Error converting result "+e.toString());
	  	}
	  	//Log.i("log_tag", "the output of request is : "+data);
	  	try {
		  	if (category == null) {
		  		JSONTokener jsontok = new JSONTokener(data);
		  		infoArray = CDL.rowToJSONArray(jsontok);
		  	} else {
		  		infoArray = new JSONArray(data);
		  	}
		} catch (JSONException e) {
			Log.e("log_tag", "Error converting response to JSON "+e.toString());
		}
		
	  	return infoArray;
	}
}
