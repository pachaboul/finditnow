/**
 * This class holds useful, utility static methods
 * not specific to any class, but are shared among them.
 */

package com.net.finditnow;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class FINUtil {

	/**
	 * Capitalizes the first character of the given string.
	 * @param str String to capitalize
	 * @return Copy of the string with the first character capitalized
	 */
	public static String displayCategory(String cat, Context context)
	{
		FINDatabase db = new FINDatabase(context);
		Cursor cursor = db.getReadableDatabase().query("categories", null, "name = '" + cat + "'", null, null, null, null);
		cursor.moveToFirst();
		
		String category = cursor.getString(cursor.getColumnIndex("full_name"));
		
		cursor.close();
		db.close();
		
		return category;
	}

	/**
	 * Capitalizes the first character of all strings in an ArrayList
	 * @param strs ArrayList of strings
	 * @return New ArrayList with copy of the strings that are in proper caps.
	 */
	public static ArrayList<String> displayAllCategories(ArrayList<String> strs, Context context)
	{
		ArrayList<String> al = new ArrayList<String>();
		for (String s : strs) {
			al.add(displayCategory(s, context));
		}
		return al;
	}

	public static String pluralize(String str, int num) {
		if (num == 1) {
			return str;
		} else {
			return str + "s";
		}
	}

	/**
	 * Undoes the above operation
	 * @param str The string to decap
	 * @return A new string with the above operations undone
	 */
	public static String sendCategory(String cat, Context context) {
		FINDatabase db = new FINDatabase(context);
		Cursor cursor = db.getReadableDatabase().query("categories", null, "full_name = '" + cat + "'", null, null, null, null);
		cursor.moveToFirst();
		
		String category = cursor.getString(cursor.getColumnIndex("name"));
		
		db.close();
				
		return category;
	}

	/**
	 * Capitalizes the first character of all strings in an ArrayList
	 * @param strs ArrayList of strings
	 * @return New ArrayList with copy of the strings that are in proper caps.
	 */
	public static ArrayList<String> sendAllCategories(ArrayList<String> strs, Context context)
	{
		ArrayList<String> al = new ArrayList<String>();
		for (String s : strs) {
			al.add(sendCategory(s, context));
		}
		return al;
	}

	/**
	 * Returns a String of the categories separated by "|"
	 * @param categories An ArrayList of Strings containing the categories
	 * @return A String of categories each separated by "|"
	 */
	public static String allCategories(ArrayList<String> categories, Context context) {
		String cats = "";
		for (String s : categories) {
			cats = cats + sendCategory(s, context) + " ";
		}

		return cats.substring(0, cats.length() - 1);
	}
}
