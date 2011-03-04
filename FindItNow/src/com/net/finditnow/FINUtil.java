package com.net.finditnow;

import java.util.ArrayList;
import android.text.Html;


/*
 * This is a class that holds some util methods that is shared between classes.
 */
public class FINUtil {
	
	//precondition: str is not empty
	//post condition: str with first character capitalized
	public static String capFirstChar(String str)
	{
		if (str.equals("atms")) {
			return "ATMs";
		} else {
			StringBuffer buffer = new StringBuffer();
	    	buffer.append(str);    	
	    	//char cateName = Character.toUpperCase(buffer.charAt(0));
	    	buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
	    	return buffer.toString();
		}
	}
	
	public static ArrayList<String> capFirstChar(ArrayList<String> strs)
	{
		ArrayList<String> al = new ArrayList<String>();
		for (String s : strs) {
			capFirstChar(s);
		}
		return al;
	}
	
	// Get string from HTML
	public static String fromHtml(String str) {
		return Html.fromHtml(str).toString();
	}
	
	// pluralize like c-krazy
	public static String pluralize(String str, int num) {
		if (num == 1) {
			return str + "s";
		} else {
			return str;
		}
	}
}
