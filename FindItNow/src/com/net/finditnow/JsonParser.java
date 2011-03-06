package com.net.finditnow;

/***
 * This class provides methods to parse a Json string into a HashMap
 * 
 * Json String:
 * [ {"lat": int, "long":int, "floor_names":[strings], "name":string} ,...]
 * 
 * 
 */

//packages for handling JSON
import com.google.gson.*;
import org.json.JSONArray;

//Android reporting log
import android.util.Log;

//utils to build the results
import com.google.android.maps.GeoPoint;
import java.util.HashMap;

public class JsonParser {
	//This is a string to keep track of the names of each piece of information in the
	//JSON array.
	private static final String[] LOCATION_NAMES = { "lat",
		"long",
		"floor_names",
		"info",
		"id"};
	private static final String[] BUILDING_NAMES = { "bid",
		"lat",
		"long",
		"name",
		"fid",
	"floor_names"};

	/**
	 * DEPRECIATED
	 * parses a Json Array into a map of locations and its corresponding CategoryItem
	 * 
	 * @param jsonArray jsonArray containing information
	 * @return HashMap<GeoPoint, CategoryItem> maps a location with its information
	 */
	public static HashMap<GeoPoint, CategoryItem> parseCategoryJson(JSONArray jsonArray)
	{
		//creates the map for information to be stored in
		HashMap<GeoPoint,CategoryItem> map = new HashMap<GeoPoint,CategoryItem>();
		
		if (jsonArray != null) {
			//Log.i("log", jsonArray.toString());
			
			String json = jsonArray.toString();
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
						JsonArray s = ob.get(LOCATION_NAMES[2]).getAsJsonArray();
						//the floor names associated with this point
						String[] flrNames = gson.fromJson(s,String[].class);
						for (String flr: flrNames)
							item.addFloor_names(flr);
					}
					if (ob.has(LOCATION_NAMES[3]))
					{
						JsonArray s = ob.get(LOCATION_NAMES[3]).getAsJsonArray();
						//the floor names associated with this point
						String[] infos = gson.fromJson(s,String[].class);
						for (String info: infos)
							item.addInfo(info);
					}
					if (ob.has(LOCATION_NAMES[4]))
					{
						JsonArray s = ob.get(LOCATION_NAMES[4]).getAsJsonArray();
						//the floor names associated with this point
						int[] ids = gson.fromJson(s,int[].class);
						for (int id: ids)
							item.addId(id);
					}
				}
			}
		} 
		return map;
	}

	
	
	/**
	 * DEPRECIATED
	 * parses a Json Array into a map of locations and its floor names
	 * 
	 * @param jsonArray jsonArray containing information
	 * @return HashMap<GeoPoint, String[]> maps a location with its floor names
	 */
	public static HashMap<GeoPoint, String[]> parseJson(JSONArray jsonArray)
	{
		//creates the map for information to be stored in
		HashMap<GeoPoint,String[]> map = new HashMap<GeoPoint,String[]>();
		
		if (jsonArray != null) {
			//Log.i("log", jsonArray.toString());
			
			String json = jsonArray.toString();
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
					if (ob.has(LOCATION_NAMES[2]))
					{
						JsonArray s = ob.get(LOCATION_NAMES[2]).getAsJsonArray();
						//the floor names associated with this point
						String[] flrNames = gson.fromJson(s,String[].class);
	
						//if the point is not added before, we add it
						if (map.get(point) == null)
							map.put(point,flrNames);
						else
						{
							//if the point already has entries in map, we append it to the end.
							String[] temp = map.get(point);
							String[] newS = new String[temp.length+flrNames.length];
							int c = 0;
							for (; c < temp.length; c++)
								newS[c] = temp[c];
	
							for (int k = 0; k < flrNames.length; k++, c++)
							{
								newS[c] = flrNames[k];
	
							}
							map.put(point, newS);
						}
					}
					else
						map.put(point, new String[]{"n/a"});
				}
			}
		} 
		return map;
	}

	/**
	 * DEPRECIATED
	 * parses a Json Array into a map of locations and its corresponding names
	 * 
	 * @param jsonArray jsonArray containing information
	 * @return HashMap<GeoPoint, String> maps a location with its names
	 */
	public static HashMap<GeoPoint, String> parseNameJson(JSONArray jsonArray)
	{
		//create the map with GeoPoint as key and string as name
		HashMap<GeoPoint,String> map = new HashMap<GeoPoint,String>();
		
		if (jsonArray != null) {
			String json = jsonArray.toString();
			//used to parse a json
			JsonStreamParser parser = new JsonStreamParser(json);
			JsonArray arr = parser.next().getAsJsonArray();
	
			for (int i = 0; i < arr.size(); i++)
			{
				if (arr.get(i).isJsonObject())
				{
					//have JsonObjects in the JsonArray, so get it out to process
					JsonObject ob = arr.get(i).getAsJsonObject();
		
					//get the Geopoint and the name to put in map.
					GeoPoint point = new GeoPoint( ob.get(LOCATION_NAMES[0]).getAsInt(),ob.get(LOCATION_NAMES[1]).getAsInt());
					map.put(point,ob.get(LOCATION_NAMES[3]).getAsString());
				}
			}

		} 
		return map;
	}

	/**
	 *  parse a json string into a map of GeoPoint to Building
	 *  
	 * @param json the json string representation of an array of building objects
	 * @return a map of location to its corresponding building object
	 */
	public static HashMap<GeoPoint, Building> parseBuildingJson(String json)
	{
		//used for parsing the JSON object
		Gson gson = new Gson();
		JsonStreamParser parser = new JsonStreamParser(json);
		JsonArray arr = parser.next().getAsJsonArray();

		//creates the map for information to be stored in
		HashMap<GeoPoint,Building> map = new HashMap<GeoPoint,Building>();


		for (int i = 0; i < arr.size(); i++)
		{
			if (arr.get(i).isJsonObject())
			{
				//Since the JsonArray contains whole bunch json array, we can get each one out
				JsonObject ob = arr.get(i).getAsJsonObject();
	
				//place the information in the map with BuildingID as key
				GeoPoint point = new GeoPoint( ob.get(BUILDING_NAMES[1]).getAsInt(),ob.get(BUILDING_NAMES[2]).getAsInt());
	
				//remove lat, long so it can be used for the Gson.fromJson
				ob.remove(BUILDING_NAMES[1]);
				ob.remove(BUILDING_NAMES[2]);
	
				//Log.i("log_tag", ob.toString());
	
				//converts a Json string directly to a building object
				Building build = gson.fromJson(ob,Building.class);
				
				//puts it in the map
				map.put(point, build);
			}
		}

		return map;
	}
}
