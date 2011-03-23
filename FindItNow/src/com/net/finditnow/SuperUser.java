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

public class SuperUser {
	
	// A Constant representing the location of the Update.php file
	public static final String LOGIN_LOCATION = "http://yinnopiano.com/fin/login.php";
	public static final String LOGOUT_LOCATION = "http://yinnopiano.com/fin/logout.php";
	public static final String LOGGEDIN_LOCATION = "http://yinnopiano.com/fin/loggedin.php";
	
	public static String login(String phone_id, String username, String userpass, Context context) {
		/*
		   * HTTP Post request
		   */
		String data = "";
	  	InputStream iStream = null;
	  	
	  	// DESIGN PATTERN: Exceptions.  In Get/Update/Create, we catch any exception in PHP communication
	  	//				   This also allows us to localize errors that occur during the process
	  	try{
		        HttpPost httppost = new HttpPost(LOGIN_LOCATION);
		        
		        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("userpass", userpass));
                nameValuePairs.add(new BasicNameValuePair("phone_id", phone_id));
                
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
	  	    return context.getString(R.string.timeout);
	  	}

  		return data.trim();
	}
	
	public static String logout(String phone_id, Context context) {
		/*
		   * HTTP Post request
		   */
		String data = "";
	  	InputStream iStream = null;
	  	
	  	// DESIGN PATTERN: Exceptions.  In Get/Update/Create, we catch any exception in PHP communication
	  	//				   This also allows us to localize errors that occur during the process
	  	try{
		        HttpPost httppost = new HttpPost(LOGOUT_LOCATION);
		        
		        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("phone_id", phone_id));
                
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
	  	    return context.getString(R.string.timeout);
	  	}

  		return data.trim();
	}
	
	public static String loggedin(String phone_id, Context context) {
		/*
		   * HTTP Post request
		   */
		String data = "";
	  	InputStream iStream = null;
	  	
	  	// DESIGN PATTERN: Exceptions.  In Get/Update/Create, we catch any exception in PHP communication
	  	//				   This also allows us to localize errors that occur during the process
	  	try{
		        HttpPost httppost = new HttpPost(LOGGEDIN_LOCATION);
		        
		        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("phone_id", phone_id));
                
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
	  	    return context.getString(R.string.timeout);
	  	}

  		return data.trim();
	}
	
}
