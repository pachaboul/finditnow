package com.net.finditnow.test;

import junit.framework.TestCase;
import java.util.HashMap;
import java.util.Iterator;

import com.google.android.maps.GeoPoint;
import com.net.finditnow.JsonParser;
import com.net.finditnow.CategoryItem;

public class JsonParserTest extends TestCase {
        
        protected String json1;
        protected String json2;
        protected String json3;
        protected String json4;
        
        protected void setUp() {
        	json1 ="[{\"id\":13,\"long\":-122309098,\"floor_names\":[],\"lat\":47656582,\"info\":\"Curbside 8, Siganos \n Mon to Fri, 10:30am to 3pm\"}," +
			"{\"id\":14,\"long\":-122309098,\"floor_names\":[],\"lat\":47656582,\"info\":\"Hot Dawgs, Motosurf, Red Square BBQ \n Mon to Fri, 10:30am to 3pm\"}]";
        	json2 = "[{\"id\":17,\"long\":-122309098,\"floor_names\":[\"Basement\"],\"lat\":47656582,\"info\":\"\"}]";       	
        	json3="";
        	json4=null;
        }
        
        public void testCategory1() {
                HashMap<GeoPoint, CategoryItem> map = JsonParser.parseCategoryJson(json1);
                HashMap<GeoPoint, CategoryItem> expected = new  HashMap<GeoPoint, CategoryItem>();
                
                CategoryItem value= new CategoryItem();
                value.addFloor_names("");
                value.addInfo("Curbside 8, Siganos \n Mon to Fri, 10:30am to 3pm");
                value.addId(13);
                value.addFloor_names("");
                value.addInfo("Hot Dawgs, Motosurf, Red Square BBQ \n Mon to Fri, 10:30am to 3pm");
                value.addId(14);

                expected.put(new GeoPoint(47656582,-122309098), value);

                assertTrue(categoryEquals(map,expected));
        }
        
        public void testCategory2() {
            HashMap<GeoPoint, CategoryItem> map = JsonParser.parseCategoryJson(json2);
            HashMap<GeoPoint, CategoryItem> expected = new  HashMap<GeoPoint, CategoryItem>();
            
            CategoryItem value= new CategoryItem();
            value.addFloor_names("Basement");
            value.addInfo("");
            value.addId(17);
            expected.put(new GeoPoint(47656582,-122309098), value);
            
            assertTrue(categoryEquals(map,expected));
        }
    
        public void testEmptyString(){
            HashMap<GeoPoint, CategoryItem> map = JsonParser.parseCategoryJson(json3);
            HashMap<GeoPoint, CategoryItem> expected = new  HashMap<GeoPoint, CategoryItem>();
            
            assertTrue(categoryEquals(map,expected));
        }
        public void testNullString(){
            HashMap<GeoPoint, CategoryItem> map = JsonParser.parseCategoryJson(json4);
            HashMap<GeoPoint, CategoryItem> expected = new  HashMap<GeoPoint, CategoryItem>();
            
            assertTrue(categoryEquals(map,expected));
        }
        private boolean categoryEquals(HashMap<GeoPoint,CategoryItem> a, HashMap<GeoPoint,CategoryItem> b) {
                Iterator<HashMap.Entry<GeoPoint, CategoryItem>> iterA = a.entrySet().iterator();
                Iterator<HashMap.Entry<GeoPoint, CategoryItem>> iterB = b.entrySet().iterator();
                boolean result = a.size() == b.size();

                while (iterA.hasNext() && iterB.hasNext()) {
                    HashMap.Entry<GeoPoint,CategoryItem> entryA = iterA.next();
                    HashMap.Entry<GeoPoint, CategoryItem> entryB = iterB.next();
                    
                    result = result && ( entryA.getKey().equals(entryB.getKey()) );
                    CategoryItem strA = entryA.getValue();
                    CategoryItem strB = entryB.getValue();

                    result = result && ( strA.equals(strB)) ;                        
                }
                
                return result;
        }
}