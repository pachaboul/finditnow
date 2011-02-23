package finditnow.apk;


import junit.framework.TestCase;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import com.google.android.maps.GeoPoint;

import finditnow.apk.JsonParser;

public class JsonParserTest extends TestCase {
	protected String json1;
	protected String json2;
	protected String json3;
	protected String json4;
	
	protected void setUp()
	{
		json1 = "[{\"lat\":1,\"long\":-1,\"floor_names\":[\"Hello, World!\"], \"name\":\"Test Name\"}]";
		json2 = "[{\"lat\":47653631,\"long\":-122305025,\"floor_names\":[\"1\",\"2\",\"3\",\"3M\",\"4\"], \"name\":\"A\"},"+
				"{\"lat\":47653725,\"long\":-122306313,\"floor_names\":[\"B\",\"1\",\"2\",\"2M\",\"3\",\"3M\",\"4\",\"4M\"], \"name\":\"B\"}]";
		json3 = "[{\"lat\":0,\"long\":0,\"floor_names\":[\"ABCDEFGHIJK\",\"ABCDEFGHIJK\",\"ABCDEFGHIJK\",\"ABCDEFGHIJK\",\"ABCDEFGHIJK\"], \"name\":\"A test on the names that could be long\"}]";
		json4 = "[{\"lat\":1,\"long\":-1,\"floor_names\":[\"Hello, World!\"], \"name\":\"Test Name\"},"+
				"{\"lat\":47653631,\"long\":-122305025,\"floor_names\":[\"1\",\"2\",\"3\",\"3M\",\"4\"], \"name\":\"A\"},"+
				"{\"lat\":47653725,\"long\":-122306313,\"floor_names\":[\"B\",\"1\",\"2\",\"2M\",\"3\",\"3M\",\"4\",\"4M\"], \"name\":\"B\"},"+
				"{\"lat\":0,\"long\":0,\"floor_names\":[\"ABCDEFGHIJK\",\"ABCDEFGHIJK\",\"ABCDEFGHIJK\",\"ABCDEFGHIJK\",\"ABCDEFGHIJK\"], \"name\":\"A test on the names that could be long\"},"+
				"{\"lat\":1,\"long\":-1,\"floor_names\":[\"Hello, World!\"], \"name\":\"Test Name\"},"+
				"{\"lat\":47653631,\"long\":-122305025,\"floor_names\":[\"1\",\"2\",\"3\",\"3M\",\"4\"], \"name\":\"A\"},"+
				"{\"lat\":47653725,\"long\":-122306313,\"floor_names\":[\"B\",\"1\",\"2\",\"2M\",\"3\",\"3M\",\"4\",\"4M\"], \"name\":\"B\"},"+
				"{\"lat\":0,\"long\":0,\"floor_names\":[\"ABCDEFGHIJK\",\"ABCDEFGHIJK\",\"ABCDEFGHIJK\",\"ABCDEFGHIJK\",\"ABCDEFGHIJK\"], \"name\":\"A test on the names that could be long\"}]";
	}
	
	public void testSimpleItem()
	{
		Map<GeoPoint,String[]> expected = new HashMap<GeoPoint,String[]>();
		expected.put(new GeoPoint( 1,-1), new String[]{"Hello, World!"} );
		Map<GeoPoint,String[]> result = JsonParser.parseJson(json1);
		assertTrue( equals(expected,result));
	}

	public void testTwoItem()
	{
		Map<GeoPoint,String[]> expected = new HashMap<GeoPoint,String[]>();
		expected.put(new GeoPoint( 47653631,-122305025), new String[]{"1","2","3","3M","4"} );
		expected.put(new GeoPoint( 47653725,-122306313), new String[]{"B","1","2","2M","3","3M","4","4M"} );

		Map<GeoPoint,String[]> result = JsonParser.parseJson(json2);
		assertTrue( equals(expected,result));
	}

	
	public void testLongStrings()
	{
		Map<GeoPoint,String[]> expected = new HashMap<GeoPoint,String[]>();
		expected.put(new GeoPoint( 0,0), new String[]{"ABCDEFGHIJK","ABCDEFGHIJK","ABCDEFGHIJK","ABCDEFGHIJK","ABCDEFGHIJK"} );

		Map<GeoPoint,String[]> result = JsonParser.parseJson(json3);
		assertTrue( equals(expected,result));
	}

	
	public void testManyItems()
	{
		Map<GeoPoint,String[]> expected = new HashMap<GeoPoint,String[]>();
		expected.put(new GeoPoint( 1,-1), new String[]{"Hello, World!"} );
		expected.put(new GeoPoint( 47653631,-122305025), new String[]{"1","2","3","3M","4"} );
		expected.put(new GeoPoint( 47653725,-122306313), new String[]{"B","1","2","2M","3","3M","4","4M"} );
		expected.put(new GeoPoint( 0,0), new String[]{"ABCDEFGHIJK","ABCDEFGHIJK","ABCDEFGHIJK","ABCDEFGHIJK","ABCDEFGHIJK"} );
		
		expected.put(new GeoPoint( 1,-1), new String[]{"Hello, World!"} );
		expected.put(new GeoPoint( 47653631,-122305025), new String[]{"1","2","3","3M","4"} );
		expected.put(new GeoPoint( 47653725,-122306313), new String[]{"B","1","2","2M","3","3M","4","4M"} );
		expected.put(new GeoPoint( 0,0), new String[]{"ABCDEFGHIJK","ABCDEFGHIJK","ABCDEFGHIJK","ABCDEFGHIJK","ABCDEFGHIJK"} );
		Map<GeoPoint,String[]> result = JsonParser.parseJson(json4);
		assertTrue( equals(expected,result));
	}
	

	public void testSimpleName()
	{
		Map<GeoPoint,String> expected = new HashMap<GeoPoint,String>();
		expected.put(new GeoPoint( 1,-1),"Test Name" );
		Map<GeoPoint,String> result = JsonParser.parseNameJson(json1);
		assertTrue( nameEquals(expected,result));	
	}

	public void testTwoName()
	{
		Map<GeoPoint,String> expected = new HashMap<GeoPoint,String>();
		expected.put(new GeoPoint( 47653631,-122305025), "A" );
		expected.put(new GeoPoint( 47653725,-122306313), "B" );
		
		Map<GeoPoint,String> result = JsonParser.parseNameJson(json2);
		assertTrue( nameEquals(expected,result));	
	}

	public void testLongName()
	{
		Map<GeoPoint,String> expected = new HashMap<GeoPoint,String>();
		expected.put(new GeoPoint( 0,0), "A test on the names that could be long" );
		
		Map<GeoPoint,String> result = JsonParser.parseNameJson(json3);
		assertTrue( nameEquals(expected,result));	
	}
	

	public void testManyNames()
	{
		Map<GeoPoint,String> expected = new HashMap<GeoPoint,String>();
		expected.put(new GeoPoint( 1,-1),"Test Name" );
		expected.put(new GeoPoint( 47653631,-122305025), "A" );
		expected.put(new GeoPoint( 47653725,-122306313), "B" );
		expected.put(new GeoPoint( 0,0), "A test on the names that could be long" );
		
		expected.put(new GeoPoint( 1,-1),"Test Name" );
		expected.put(new GeoPoint( 47653631,-122305025), "A" );
		expected.put(new GeoPoint( 47653725,-122306313), "B" );
		expected.put(new GeoPoint( 0,0), "A test on the names that could be long" );

		Map<GeoPoint,String> result = JsonParser.parseNameJson(json4);
		assertTrue( nameEquals(expected,result));	
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

	private boolean nameEquals(Map<GeoPoint,String> a, Map<GeoPoint,String> b)
	{
		Iterator<Map.Entry<GeoPoint, String>> iterA = a.entrySet().iterator();
		Iterator<Map.Entry<GeoPoint, String>> iterB = b.entrySet().iterator();
		
		boolean result = true;
		
		while (iterA.hasNext() && iterB.hasNext())
		{
			Map.Entry<GeoPoint, String> entryA = iterA.next();
			Map.Entry<GeoPoint, String> entryB = iterB.next();
			
			result = result && ( entryA.getKey().equals(entryB.getKey()) );
			String strA = entryA.getValue();
			String strB = entryB.getValue();
			result = result && ( strA.compareTo(strB) == 0);
			
		}
		return result;
	}
}
