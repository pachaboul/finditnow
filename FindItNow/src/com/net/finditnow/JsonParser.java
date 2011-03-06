package com.net.finditnow;

/*
 * This class provides methods to parse a Json string into a HashMap
 * 
 * Json String:
 * [ {"lat": int, "long":int, "floor_names":[strings], "name":string} ,...]
 * 
 * TODO: need to have a map for the radius of a building for final release.
 * 
 */

import com.google.gson.*;
import com.google.android.maps.GeoPoint;
import java.util.HashMap;
import org.json.JSONArray;

import android.util.Log;

public class JsonParser {
	//This is a string to keep track of the names of each piece of information in the
	//JSON array.
	private static final String[] LOCATION_NAMES = { "lat",
		"long",
		"floor_names",
	"info"};

	private static final String[] BUILDING_NAMES = { "bid",
		"lat",
		"long",
		"name",
		"fid",
	"floor_names"};

	//parses a Json Array into a map of locations and its floor names
	public static HashMap<GeoPoint, String[]> parseJson(JSONArray jsonArray)
	{
		//creates the map for information to be stored in
		HashMap<GeoPoint,String[]> map = new HashMap<GeoPoint,String[]>();
		
		if (jsonArray != null) {
			
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
	
					//some ways to get things out of a Json Object
					/*System.out.println("Building Name: "+ob.get("buildingName").getAsString());
						System.out.println("category Name: "+ob.get("category").getAsString());
						System.out.println("floor Num: "+ob.get("floorNum").getAsInt());
						System.out.println();*/
	
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

	//a Json Array into a map of locations and its corresponding names
	public static HashMap<GeoPoint, String> parseNameJson(JSONArray jsonArray)
	{
		//create the map with GeoPoint as key and string as name
		HashMap<GeoPoint,String> map = new HashMap<GeoPoint,String>();
		
		if (jsonArray != null) {
			String json = jsonArray.toString();
			//	Gson gson = new Gson();
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
	
				//some ways to get things out of a Json Object
				/*System.out.println("Building Name: "+ob.get("buildingName").getAsString());
					System.out.println("category Name: "+ob.get("category").getAsString());
					System.out.println("floor Num: "+ob.get("floorNum").getAsInt());
					System.out.println();*/
	
				//place the information in the map with BuildingID as key
				//int bid = ob.get(BUILDING_NAMES[0]).getAsInt();
				GeoPoint point = new GeoPoint( ob.get(BUILDING_NAMES[1]).getAsInt(),ob.get(BUILDING_NAMES[2]).getAsInt());
				//	String name = ob.get(BUILDING_NAMES[3]).getAsString();
				ob.remove(BUILDING_NAMES[1]);
				ob.remove(BUILDING_NAMES[2]);
	
				//Log.i("log_tag", ob.toString());
	
				Building build = gson.fromJson(ob,Building.class);
				//JsonArray floor_ids_raw = ob.get(BUILDING_NAMES[4]).getAsJsonArray();
				//JsonArray floor_names = ob.get(BUILDING_NAMES[5]).getAsJsonArray();
				//Building build = new Building(bid, name);
				//Log.i("log_tag", point.toString() + "   " + build.toString());
				map.put(point, build);
			}
		}

		return map;
	}
}
