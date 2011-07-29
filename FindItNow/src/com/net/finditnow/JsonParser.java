package com.net.finditnow;

/***
 * JsonParser.java by Chanel Huang
 * This class provides methods to parse a Json string into a HashMap
 * 
 * Json String:
 * [ {"lat": int, "long":int, "floor_names":[strings], "name":string} ,...]
 * 
 * 
 */

//packages for handling JSON
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.google.android.maps.GeoPoint;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class JsonParser {

	/**
	 * parse a json string into a map of GeoPoint to Building
	 *  
	 * @param json the json string representation of an array of building objects
	 * @return a map of location to its corresponding building object
	 */
	public static void parseBuildingJson(String json, Context context)
	{
		//used for parsing the JSON object
		JsonStreamParser parser = new JsonStreamParser(json);
		if (!json.equals("")) {
			JsonArray arr = parser.next().getAsJsonArray();
			
			SQLiteDatabase db = new FINDatabase(context).getWritableDatabase();
	
			for (int i = 0; i < arr.size(); i++)
			{
				if (arr.get(i).isJsonObject())
				{
					//Since the JsonArray contains whole bunch json array, we can get each one out
					JsonObject ob = arr.get(i).getAsJsonObject();
					
					// Grab the stuff
					int bid = ob.get("bid").getAsInt();
					String name = ob.get("name").getAsString();
					int latitude = ob.get("latitude").getAsInt();
					int longitude = ob.get("longitude").getAsInt();
					JsonArray fids = ob.get("fid").getAsJsonArray();
					JsonArray fnums = ob.get("fnum").getAsJsonArray();
					JsonArray fnames = ob.get("floor_names").getAsJsonArray();
					JsonArray deletedFloors = ob.get("deletedFloors").getAsJsonArray();
					int deleted = ob.get("deleted").getAsInt();
									
					for (int j = 0; j < fids.size(); j++) {
						db.execSQL("INSERT OR REPLACE INTO floors (fid, bid, fnum, name, deleted) VALUES (" + 
								  fids.get(j).getAsInt() + ", " + bid + ", " + fnums.get(j).getAsInt() + ", '" + fnames.get(j).getAsString() + "', " + deletedFloors.get(j).getAsInt() + ")");
					}
					
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
					String rid = prefs.getInt("rid", 0)+"";
	
					db.execSQL("INSERT OR REPLACE INTO buildings (bid, rid, name, latitude, longitude, deleted) VALUES (" + 
							  bid + ", " + rid + ", '" + name + "', " + latitude + ", " + longitude + ", " + deleted + ")");
				}
			}
			
			db.close();
		}

	}

	/**
	 *  parses a String of category names and parse it into ArrayList
	 *  
	 * @param json the string representation of an array of categories
	 * @return an ArrayList of category names
	 */
	public static void parseCategoriesList(String json, Context context) {
		//used for parsing the JSON object
		JsonStreamParser parser = new JsonStreamParser(json);
		if (!json.equals("null")) {
			JsonArray arr = parser.next().getAsJsonArray();
			SQLiteDatabase db = new FINDatabase(context).getWritableDatabase();
	
			for (int i = 0; i < arr.size(); i++)
			{
				if (arr.get(i).isJsonObject())
				{
					//Since the JsonArray contains whole bunch json array, we can get each one out
					JsonObject ob = arr.get(i).getAsJsonObject();
	
					// Grab the stuff
					int cat_id = ob.get("cat_id").getAsInt();
					String name = ob.get("name").getAsString();
					String full_name = ob.get("full_name").getAsString();
					int parent = ob.get("parent").getAsInt();
					int deleted = ob.get("deleted").getAsInt();
					
					db.execSQL("INSERT OR REPLACE INTO categories (cat_id, name, full_name, parent, deleted) VALUES (" + 
													  cat_id + ", '" + name + "', '" + full_name + "', " + parent + ", " + deleted + ")");
				}
			}
			
			db.close();
		}
	}

	public static HashMap<Integer, GeoPoint> parseSearchJson(String json) {
		String[] arr = json.split(",");
		HashMap<Integer, GeoPoint> result = new HashMap<Integer, GeoPoint>();
		if (arr.length >= 3) {
			for (int i = 0; i < arr.length; i=i+3) {
				result.put(i / 3, new GeoPoint(Integer.parseInt(arr[i+1]), Integer.parseInt(arr[i+2])));
			}
		} 
		return result;
	}
	
	public static void parseItemJson(String json, Context context) {
		//used for parsing the JSON object
		JsonStreamParser parser = new JsonStreamParser(json);
		if (!json.equals("[]")) {
			JsonArray arr = parser.next().getAsJsonArray();
			SQLiteDatabase db = new FINDatabase(context).getWritableDatabase();
	
			for (int i = 0; i < arr.size(); i++)
			{
				if (arr.get(i).isJsonObject())
				{
					//Since the JsonArray contains whole bunch json array, we can get each one out
					JsonObject ob = arr.get(i).getAsJsonObject();
	
					// Grab the stuff
					int item_id = ob.get("item_id").getAsInt();
					int rid = ob.get("rid").getAsInt();
					int latitude = ob.get("latitude").getAsInt();
					int longitude = ob.get("longitude").getAsInt();
					String special_info = ob.get("special_info").getAsString().replace("'", "''"); // SQLITE single quotes
					int fid = ob.get("fid").getAsInt();
					int not_found_count = ob.get("not_found_count").getAsInt();
					String username = ob.get("username").getAsString();
					int cat_id = ob.get("cat_id").getAsInt();
					int deleted = ob.get("deleted").getAsInt();
					
					db.execSQL("INSERT OR REPLACE INTO items (item_id, rid, latitude, longitude, special_info, fid, not_found_count, username, cat_id, deleted) VALUES (" + 
													  item_id + ", " + rid + ", " + latitude + ", " + longitude + ", '" + special_info.replace("\\n", "<br />").replace("\n", "<br />") + "', " +
													  fid + ", " + not_found_count + ", '" + username + "', " + cat_id + ", " + deleted + ")");
				}
			}
			
			db.close();
		}
	}

	public static void parseRegionJson(String json, Context context) {
		//used for parsing the JSON object
		JsonStreamParser parser = new JsonStreamParser(json);
		if (!json.equals("null")) {
			JsonArray arr = parser.next().getAsJsonArray();
			SQLiteDatabase db = new FINDatabase(context).getWritableDatabase();
	
			for (int i = 0; i < arr.size(); i++)
			{
				if (arr.get(i).isJsonObject())
				{
					//Since the JsonArray contains whole bunch json array, we can get each one out
					JsonObject ob = arr.get(i).getAsJsonObject();
	
					// Grab the stuff
					String name = ob.get("name").getAsString();
					String full_name = ob.get("full_name").getAsString();
					int rid = ob.get("rid").getAsInt();
					int lat = ob.get("lat").getAsInt();
					int lon = ob.get("lon").getAsInt();
					String color1 = ob.get("color1").getAsString();
					String color2 = ob.get("color2").getAsString();
					int deleted = ob.get("deleted").getAsInt();
					
					db.execSQL("INSERT OR REPLACE INTO regions (rid, name, full_name, latitude, longitude, deleted) VALUES (" + 
													  rid + ", '" + name + "', '" + full_name + "', " + lat + ", " + lon + ", " + deleted + ")");
					db.execSQL("INSERT OR REPLACE INTO colors (rid, color1, color2) VALUES (" + 
							  rid + ", '" + color1 + "', '" + color2 + "')");
				}
			}
			
			db.close();
		}
	}
}
