/**
 * This class holds useful, utility static methods
 * not specific to any class, but are shared among them.
 */

package com.net.finditnow;

import java.util.ArrayList;

public class FINUtil {
	
	/**
	 * Capitalizes the first character of the given string.
	 * @param str String to capitalize
	 * @return Copy of the string with the first character capitalized
	 */
	public static String capFirstChar(String str)
	{
		if (str.equals("atms")) {
			return "ATMs";
		} else {
			StringBuffer buffer = new StringBuffer();
	    	buffer.append(str);    	
	    	buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
	    	if (buffer.indexOf(" ") != -1) {
		    	buffer.setCharAt(buffer.indexOf(" ") + 1, Character.toUpperCase(buffer.charAt(buffer.indexOf(" ") + 1)));
	    	}
	    	return buffer.toString();
		}
	}
	
	/**
	 * Capitalizes the first character of all strings in an ArrayList
	 * @param strs ArrayList of strings
	 * @return New ArrayList with copy of the strings that are in proper caps.
	 */
	public static ArrayList<String> capFirstChar(ArrayList<String> strs)
	{
		ArrayList<String> al = new ArrayList<String>();
		for (String s : strs) {
			al.add(capFirstChar(s));
		}
		return al;
	}
	
	/**
	 * Returns the proper pluralization of the given string noun
	 * @param str The string noun (singular)
	 * @param num The amount describing the noun
	 * @return The noun in singular if num is equal to 1; otherwise, appends an "s" to make it plural.
	 */
	public static String pluralize(String str, int num) {
		if (num == 1) {
			return str;
		} else {
			return str + "s";
		}
	}
	
	/**
	 * Returns a new string replacing occurrences of underscores with spaces
	 * @param str The old unformatted string
	 * @return A new string with underscores replaced by spaces
	 */
	public static String removeUnderscore(String str) {
		return str.replace('_', ' ');
	}
}
