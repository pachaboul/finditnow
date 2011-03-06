package com.net.finditnow;
/***
 * Updata.java by Chanel Huang
 * 
 * This class defines the interface with the PHP layer, update.php
 *  which updates the not found count in the database. This updates
 *  the count every time a user report a particular item is not at
 *  the location location reported by the App.
 * 
 */
//for parsing strings
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//Apache necessary imports
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

//to report error using Log
import android.util.Log;

public class Update {
	/**
	 * Communicate with the database for updating the counts
	 * for things that is not present at the supposed location.
	 * 
	 * @param category - category of the object
	 * @param id - id of the object to be update
	 * @return String that represents the db response
	 */
	public static String updateDB(String category,int id) {
		/*
		   * HTTP Post request
		   */
		String data = "";
	  	InputStream iStream = null;
	  	try{
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost("http://cubist.cs.washington.edu/projects/11wi/cse403/RecycleLocator/update.php");
		  			
		        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		        
	  			nameValuePairs.add(new BasicNameValuePair("category", category));
	  			nameValuePairs.add(new BasicNameValuePair("id", id+""));
	  			
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
		
	  	return data;
	}
}
