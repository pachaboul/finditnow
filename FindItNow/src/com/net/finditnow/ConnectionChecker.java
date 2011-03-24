/***
 * ConnectionChecker.java by Eric Hare
 * This class checks the Android device for internet connectivity
 */

package com.net.finditnow;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectionChecker {

	private Context context;
	private Activity activity;

	/**
	 * Constructor for the ConnectionChecker class
	 * 
	 * @param context The context in which the class was defined
	 * @param cm A connection manager defined in the calling class
	 * @param activity The activity of the calling class
	 */
	public ConnectionChecker(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
	}

	/**
	 * This method returns whether the user's internet connection is functioning
	 * 
	 * @param context The context with which to do the check
	 * 
	 * @return True if the internet connection is functional
	 * @throws IOException 
	 */
	public boolean isOnline() {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected()) {
				URL url = new URL("http://www.google.com");
				HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
				urlc.setConnectTimeout(4000); // mTimeout is in seconds
	
				urlc.connect();
	
				if (urlc.getResponseCode() == 200) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch(IOException e) {
			return false;
		}
	}

	/**
	 * Checks for an Internet connection.
	 * If there is no connection, or we are unable to retrieve information about our connection,
	 * display a message alerting the user about lack of connection.
	 * 
	 * @param context The context with which to do the check
	 * 
	 * @return True if the internet connection is functional
	 */
	public void connectionError() {	
		Toast.makeText(context, context.getString(R.string.timeout), Toast.LENGTH_LONG).show();
		activity.finish();
	}

	/**
	 * Looks up the host address for the given hostname
	 * @param hostname A string representing the hostname to lookup
	 * @return An integer for the hostname
	 */
	public int lookupHost(String hostname) {
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(hostname);
		} catch (UnknownHostException e) {
			return -1;
		}
		byte[] addrBytes;
		int addr;
		addrBytes = inetAddress.getAddress();
		addr = ((addrBytes[3] & 0xff) << 24)
		| ((addrBytes[2] & 0xff) << 16)
		| ((addrBytes[1] & 0xff) << 8)
		|  (addrBytes[0] & 0xff);
		return addr;
	}
}
