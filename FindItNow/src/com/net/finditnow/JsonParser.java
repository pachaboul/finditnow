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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

public class JsonParser {

	private static FINDatabase db;
	
	/*
	 * Design Principle: Information Hiding
	 * These two arrays are only visible to this class.  Other module do not
	 * ever need to know the exact names of each data coming from the
	 * back-end database via JSON objects.
	 */
	//This is a string to keep track of the names of each piece of information in the
	//JSON array.
	private static final String[] LOCATION_NAMES = { "lat",
		"long",
		"fid",
		"info",
		"id",
		"cat"};

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
		JsonArray arr = parser.next().getAsJsonArray();
		
		db = new FINDatabase(context);

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
								
				for (int j = 0; j < fids.size(); j++) {
					db.getWritableDatabase().execSQL("INSERT OR REPLACE INTO floors (fid, bid, fnum, name) VALUES (" + 
							  fids.get(j).getAsInt() + ", " + bid + ", " + fnums.get(j).getAsInt() + ", '" + fnames.get(j).getAsString() + "')");
				}
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				String rid = prefs.getInt("rid", 0)+"";

				db.getWritableDatabase().execSQL("INSERT OR REPLACE INTO buildings (bid, rid, name, latitude, longitude) VALUES (" + 
						  bid + ", " + rid + ", '" + name + "', " + latitude + ", " + longitude + ")");
			}
		}
		
		db.close();
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
		JsonArray arr = parser.next().getAsJsonArray();
		db = new FINDatabase(context);

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
				
				db.getWritableDatabase().execSQL("INSERT OR REPLACE INTO categories (cat_id, name, full_name, parent) VALUES (" + 
												  cat_id + ", '" + name + "', '" + full_name + "', " + parent + ")");
			}
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
	
	public static void parseRegionJson(String json, Context context) {
		//used for parsing the JSON object
		JsonStreamParser parser = new JsonStreamParser(json);
		JsonArray arr = parser.next().getAsJsonArray();
		db = new FINDatabase(context);

		for (int i = 0; i < arr.size(); i++)
		{
			if (arr.get(i).isJsonObject())
			{
				//Since the JsonArray contains whole bunch json array, we can get each one out
				JsonObject ob = arr.get(i).getAsJsonObject();

				// Grab the stuff
				String name = ob.get("name").getAsString();
				int rid = ob.get("rid").getAsInt();
				int lat = ob.get("lat").getAsInt();
				int lon = ob.get("lon").getAsInt();
				String color1 = ob.get("color1").getAsString();
				String color2 = ob.get("color2").getAsString();
				
				db.getWritableDatabase().execSQL("INSERT OR REPLACE INTO regions (rid, name, latitude, longitude) VALUES (" + 
												  rid + ", '" + name + "', " + lat + ", " + lon + ")");
				db.getWritableDatabase().execSQL("INSERT OR REPLACE INTO colors (rid, color1, color2) VALUES (" + 
						  rid + ", '" + color1 + "', '" + color2 + "')");
			}
		}
	}

	public static HashMap<GeoPoint,HashMap<String,CategoryItem>> parseCategoryJson(String json, String category, Context context){
		if (category.equals(""))
			return parseAllCategoryJson(json, context);
		else{
			HashMap<GeoPoint,HashMap<String,CategoryItem>> result = new HashMap<GeoPoint,HashMap<String,CategoryItem>>();

			HashMap<GeoPoint, CategoryItem> map = parseCategoryJson(json);

			for (GeoPoint key:map.keySet()){
				HashMap<String,CategoryItem> oneMap = new HashMap<String,CategoryItem>();

				oneMap.put(FINUtil.displayCategory(category, context), map.get(key));
				result.put(key,oneMap);
			}
			return result;
		}
	}
	/**
	 * parses a Json Array into a map of locations and its corresponding CategoryItem for one category
	 * 
	 * @param jsonArray jsonArray containing information
	 * @return HashMap<GeoPoint, CategoryItem> maps a location with its information
	 */
	public static HashMap<GeoPoint, CategoryItem> parseCategoryJson(String json)
	{
		//creates the map for information to be stored in
		HashMap<GeoPoint,CategoryItem> map = new HashMap<GeoPoint,CategoryItem>();

		if (json != null && !json.equals("")) {

			//String json = jsonArray.toString();
			//used for parsing the JSON object
			Gson gson = new Gson();
			JsonStreamParser parser = new JsonStreamParser(json);
			JsonArray arr = parser.next().getAsJsonArray();


			for (int i = 0; i < arr.size(); i++)
			{
				if (arr.get(i).isJsonObject())
				{
					//Since the JsonArray contains whole bunch json array, we can get each one out
					JsonObject ob = arr.get(i).getAsJsonObject();

					//place the information in the map with GeoPoint as key
					GeoPoint point = new GeoPoint( ob.get(LOCATION_NAMES[0]).getAsInt(),ob.get(LOCATION_NAMES[1]).getAsInt());
					CategoryItem item = new CategoryItem();

					//if the point is already in the map, get it out to add to it
					if (map.get(point) != null)
					{
						item = map.get(point);
					}


					if (ob.has(LOCATION_NAMES[2]))
					{
						int fid = ob.get(LOCATION_NAMES[2]).getAsInt();
						
						Cursor cursor = db.getReadableDatabase().query("floors", null, "fid = " + fid, null, null, null, null);
						cursor.moveToFirst();
						
						String floor_name = "";
						if (cursor.getCount() > 0) {
							floor_name = cursor.getString(cursor.getColumnIndex("name"));
						}
						
						item.addFloor_names(floor_name);
					}
					if (ob.has(LOCATION_NAMES[3]))
					{
						String s = ob.get(LOCATION_NAMES[3]).getAsString().replace("\\n", "<br />").replace("\n", "<br />");
						//the floor info associated with this point
						item.addInfo(s);
					}
					if (ob.has(LOCATION_NAMES[4]))
					{
						int id = ob.get(LOCATION_NAMES[4]).getAsInt();
						//the floor id associated with this point
						item.addId(id);
					}

					map.put(point, item);
				}
			}
		} 
		return map;
	}

	/**
	 * parses a Json Array into a map of locations and its corresponding CategoryItem for all category
	 * 
	 * @param jsonArray jsonArray containing information
	 * @return HashMap<GeoPoint,HashMap<String,CategoryItem>> maps a location with its information
	 */
	public static HashMap<GeoPoint,HashMap<String,CategoryItem>> parseAllCategoryJson(String json, Context context)
	{
		//creates the map for information to be stored in
		HashMap<GeoPoint,HashMap<String,CategoryItem>> map = new HashMap<GeoPoint,HashMap<String,CategoryItem>>();

		if (json != null && !json.equals("")) {

			//used for parsing the JSON object
			Gson gson = new Gson();
			JsonStreamParser parser = new JsonStreamParser(json);
			while (parser.hasNext()){
				JsonArray arr = parser.next().getAsJsonArray();

				for (int i = 0; i < arr.size(); i++)
				{
					if (arr.get(i).isJsonObject())
					{
						//Since the JsonArray contains whole bunch json array, we can get each one out
						JsonObject ob = arr.get(i).getAsJsonObject();					

						//place the information in the map with GeoPoint as key
						GeoPoint point = new GeoPoint( ob.get(LOCATION_NAMES[0]).getAsInt(),ob.get(LOCATION_NAMES[1]).getAsInt());
						HashMap<String,CategoryItem> oneMap;


						//Grab the Map of Location if it is in it or make a new one
						if (map.get(point) != null)
						{
							oneMap = map.get(point);
						}
						else{
							oneMap = new HashMap<String,CategoryItem>();
							map.put(point, oneMap);
						}

						//grab the category and its corresponding CategoryItem if it is in the map
						//else make a new one
						String cat = FINUtil.displayCategory(ob.get(LOCATION_NAMES[5]).getAsString(), context);
						CategoryItem item;
						if (oneMap.get(cat) != null){
							item = oneMap.get(cat);
						}else{
							item = new CategoryItem();
							oneMap.put(cat, item);
						}


						if (ob.has(LOCATION_NAMES[2]))
						{
							int fid = ob.get(LOCATION_NAMES[2]).getAsInt();
							
							Cursor cursor = db.getReadableDatabase().query("floors", null, "fid = " + fid, null, null, null, null);
							cursor.moveToFirst();
							
							String floor_name = cursor.getString(cursor.getColumnIndex("name"));
							item.addFloor_names(floor_name);
						}
						if (ob.has("info"))
						{
							String s = ob.get(LOCATION_NAMES[3]).getAsString().replace("\\n", "<br />").replace("\n", "<br />");
							//the floor info associated with this point
							item.addInfo(s);
						}
						if (ob.has(LOCATION_NAMES[4]))
						{
							int id = ob.get(LOCATION_NAMES[4]).getAsInt();
							//the floor id associated with this point
							item.addId(id);
						}
					}
				}
			}
		} 
		return map;
	}
}
