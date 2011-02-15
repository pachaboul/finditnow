package finditnow.apk;
/*
 * This class provides methods to parse a Json string into a HashMap
 * 
 * Json String:
 * [ {"lat": int, "long":int, "floor_names":[strings]} ,...]
 * 
 * TODO: changes the map so that it contains more information for a GeoPoint
 * 		 such as the building name, radius of the overlay for adding,
 * 			or the coffe place name...etc.
 */
import com.google.gson.*;
import com.google.android.maps.GeoPoint;
import java.util.Map;
import java.util.HashMap;//	{\"buildingName\":\"MEB\",\"category\":\"BLAH\",\"floorNum\":100}]"
public class JsonParser {
	private static final String[] names = { "lat",
								   "long",
								   "floor_names"};
	//maybe?
	/*
	private static final String[] types = { "int",
											"int",
											"String[]"};
	*/
	/*
	 		String testB = "[{\"lat\":47653631,\"long\":-122305025,\"floor_names\":[\"1\",\"2\",\"3\",\"3M\",\"4\"]},"+
		 "{\"lat\":47653725,\"long\":-122306313,\"floor_names\":[\"B\",\"1\",\"2\",\"2M\",\"3\",\"3M\",\"4\",\"4M\"]}]";
		
	 */
	
	public static Map<GeoPoint,String[]> parseJson(String json)
	{
		Gson gson = new Gson();
		JsonStreamParser parser = new JsonStreamParser(json);
		JsonArray arr = parser.next().getAsJsonArray();
		
		
		Map<GeoPoint,String[]> map = new HashMap<GeoPoint,String[]>();
		
		//System.out.println("obj.length: "+arr.size());
		for (int i = 0; i < arr.size(); i++)
		{
			JsonObject ob = arr.get(i).getAsJsonObject();
			//System.out.println("obj: " + ob);
			
			/*System.out.println("Building Name: "+ob.get("buildingName").getAsString());
			System.out.println("category Name: "+ob.get("category").getAsString());
			System.out.println("floor Num: "+ob.get("floorNum").getAsInt());
			System.out.println();*/
			
			//TO DO: build a container with GeoPoints as key for the map.
			GeoPoint point = new GeoPoint( ob.get(names[0]).getAsInt(),ob.get(names[1]).getAsInt());
			JsonArray s = ob.get(names[2]).getAsJsonArray();
			map.put(point,gson.fromJson(s,String[].class));
		}
		
		return map;
	}
}
