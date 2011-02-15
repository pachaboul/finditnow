package finditnow.apk;

import junit.framework.TestCase;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import com.google.android.maps.GeoPoint;

public class JsonParserTest extends TestCase {
	protected String json;
	
	protected void setUp()
	{
		json = "[{\"lat\":47653631,\"long\":-122305025,\"floor_names\":[\"1\",\"2\",\"3\",\"3M\",\"4\"]},"+
		 "{\"lat\":47653725,\"long\":-122306313,\"floor_names\":[\"B\",\"1\",\"2\",\"2M\",\"3\",\"3M\",\"4\",\"4M\"]}]";
		
	}

	public void testTwoItem()
	{
		Map<GeoPoint,String[]> expected = new HashMap<GeoPoint,String[]>();
		expected.put(new GeoPoint( 47653631,-122305025), new String[]{"1","2","3","3M","4"} );
		expected.put(new GeoPoint( 47653725,-122306313), new String[]{"B","1","2","2M","3","3M","4","4M"} );

		Map<GeoPoint,String[]> result = JsonParser.parseJson(json);
		assertTrue( equals(expected,result));
	}
	
	private boolean equals(Map<GeoPoint,String[]> a, Map<GeoPoint,String[]> b)
	{
		Iterator<Map.Entry<GeoPoint, String[]>> iterA = a.entrySet().iterator();
		Iterator<Map.Entry<GeoPoint, String[]>> iterB = b.entrySet().iterator();
		
		boolean result = true;
		
		while (iterA.hasNext() && iterB.hasNext())
		{
			Map.Entry<GeoPoint, String[]> entryA = iterA.next();
			Map.Entry<GeoPoint, String[]> entryB = iterB.next();
			
			result = result && ( entryA.getKey().equals(entryB.getKey()) );
			String[] strA = entryA.getValue();
			String[] strB = entryB.getValue();
			result = result && ( strA.length == strB.length);
			for (int i = 0; i < Math.min(strA.length, strB.length); i++)
			{
				result = result && ( strA[i].compareTo(strB[i]) == 0 );
			}
			
		}
		return result;
	}
}
