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

public class DBCommunicator {

	// A Constant representing the location of FIN
	private static final String FIN_ROOT = "http://yinnopiano.com/fin/";

	public static String create(String phone_id, String category, String fid, String special_info, String latitude, String longitude, String bb, String sc, String print, Context context) {
		// Initialize the array of name value pairs
		List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("phone_id", phone_id));
		nameValuePairs.add(new BasicNameValuePair("category", category));
		nameValuePairs.add(new BasicNameValuePair("fid", fid));
		nameValuePairs.add(new BasicNameValuePair("special_info", special_info));
		nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
		nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
		nameValuePairs.add(new BasicNameValuePair("bb", bb));
        nameValuePairs.add(new BasicNameValuePair("sc", sc));
        nameValuePairs.add(new BasicNameValuePair("print", print));
		
		return common("FINsert/create.php", nameValuePairs, context);
	}
	
	public static String delete(String phone_id, String category, String id, Context context) {
		// Initialize the array of name value pairs
		List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("phone_id", phone_id));
		nameValuePairs.add(new BasicNameValuePair("category", category));
		nameValuePairs.add(new BasicNameValuePair("id", id));
		
		return common("delete.php", nameValuePairs, context);
	}
	
	public static String getCategories(Context context) {
		return common("getCategories.php", new ArrayList<BasicNameValuePair>(), context);
	}
	
	public static String getBuildings(Context context) {
		return common("getBuildings.php", new ArrayList<BasicNameValuePair>(), context);
	}
	
	public static String getLocations(String cat, String item, String lat, String lon, Context context) {
		// Initialize the array of name value pairs
		List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("cat", cat));
		nameValuePairs.add(new BasicNameValuePair("item", item));
		nameValuePairs.add(new BasicNameValuePair("lat", lat));
		nameValuePairs.add(new BasicNameValuePair("long", lon));
		
		return common("getLocations.php", nameValuePairs, context);
	}
	
	public static String getAllLocations(String cat, String lat, String lon, Context context) {
		// Initialize the array of name value pairs
		List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		
		nameValuePairs.add(new BasicNameValuePair("cat", cat));
		nameValuePairs.add(new BasicNameValuePair("lat", lat));
		nameValuePairs.add(new BasicNameValuePair("long", lon));
		
		return common("getAllLocations.php", nameValuePairs, context);
	}
	
	public static String login(String phone_id, String username, String userpass, Context context) {
		// Initialize the array of name value pairs
		List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("userpass", userpass));
        nameValuePairs.add(new BasicNameValuePair("phone_id", phone_id));
        
        return common("login.php", nameValuePairs, context);
	}
	
	public static String loggedIn(String phone_id, Context context) {
		// Initialize the array of name value pairs
		List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		
		nameValuePairs.add(new BasicNameValuePair("phone_id", phone_id));
		
		return common("loggedIn.php", nameValuePairs, context);
	}
	
	public static String logout(String phone_id, Context context) {
		// Initialize the array of name value pairs
		List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		
		nameValuePairs.add(new BasicNameValuePair("phone_id", phone_id));
		
		return common("logout.php", nameValuePairs, context);
	}
	
	public static String update(String phone_id, String category, String id, Context context) {
		// Initialize the array of name value pairs
		List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("phone_id", phone_id));
		nameValuePairs.add(new BasicNameValuePair("category", category));
		nameValuePairs.add(new BasicNameValuePair("id", id));
		
		return common("update.php", nameValuePairs, context);
	}

	private static String common(String suffix, List<BasicNameValuePair> nameValuePairs, Context context) {

		// Initialize input stream and response variables
		InputStream iStream = null;
		String data = "";
		
		try {
			HttpPost httppost = new HttpPost(FIN_ROOT + suffix);

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

		return data.trim();
	}
}