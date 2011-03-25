/**
 * This class holds useful, utility static methods
 * not specific to any class, but are shared among them.
 */

package com.net.finditnow;

import java.util.ArrayList;

public class FINUtil {
	
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
	 * Capitalizes the first character of the given string.
	 * @param str String to capitalize
	 * @return Copy of the string with the first character capitalized
	 */
	public static String capFirstChar(String str)
	{
		if (str.equals("")) {
			return str;
		} else if (str.equals("atms")) {
			return "ATMs";
		} else {
			StringBuffer buffer = new StringBuffer();
	    	buffer.append(str);    	
	    	buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
	    	if (buffer.indexOf("_") != -1) {
		    	buffer.setCharAt(buffer.indexOf("_") + 1, Character.toUpperCase(buffer.charAt(buffer.indexOf("_") + 1)));
	    	}
	    	return buffer.toString().replace('_', ' ');
		}
	}
	
	/**
	 * Undoes the above operation
	 * @param str The string to decap
	 * @return A new string with the above operations undone
	 */
	public static String deCapFirstChar(String str) {
		if (str != null) {
			return str.toLowerCase().replace(" ", "_");
		} else {
			return str;
		}
	}
	
	/**
	 * Returns the singular version of a plural string
	 * @param str The plural string
	 * @return A singular string
	 */
	public static String depluralize(String str) {
		if (str != null && str.endsWith("s")) {
			return str.substring(0, str.length() - 1);
		} else {
			return str;
		}
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
	 * Returns a String of the categories separated by "|"
	 * @param categories An ArrayList of Strings containing the categories
	 * @return A String of categories each separated by "|"
	 */
	public static String allCategories(ArrayList<String> categories) {
		String cats = "";
		for (String s : categories) {
			cats = cats + s + " ";
		}

		return cats.substring(0, cats.length() - 1);
	}
}
