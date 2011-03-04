package com.net.finditnow.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Iterator;
import com.google.android.maps.GeoPoint;
import com.net.finditnow.JsonParser;

public class JsonParserTest extends TestCase {
        
        protected double fValue1;
        protected double fValue2;
        
        protected void setUp() {
        	fValue1= 2.0;
        	fValue2= 3.0;
        }
        
        public void test1() {
                double result= fValue1 + fValue2;
                assertTrue(result == 5.0);
        }
        
        private boolean equals(HashMap<GeoPoint,String[]> a, HashMap<GeoPoint,String[]> b) {
                Iterator<HashMap.Entry<GeoPoint, String[]>> iterA = a.entrySet().iterator();
                Iterator<HashMap.Entry<GeoPoint, String[]>> iterB = b.entrySet().iterator();
                
                boolean result = true;
                
                while (iterA.hasNext() && iterB.hasNext()) {
                        HashMap.Entry<GeoPoint, String[]> entryA = iterA.next();
                        HashMap.Entry<GeoPoint, String[]> entryB = iterB.next();
                        
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

        private boolean nameEquals(HashMap<GeoPoint,String> a, HashMap<GeoPoint,String> b) {
                Iterator<HashMap.Entry<GeoPoint, String>> iterA = a.entrySet().iterator();
                Iterator<HashMap.Entry<GeoPoint, String>> iterB = b.entrySet().iterator();
                
                boolean result = true;
                
                while (iterA.hasNext() && iterB.hasNext())
                {
                        HashMap.Entry<GeoPoint, String> entryA = iterA.next();
                        HashMap.Entry<GeoPoint, String> entryB = iterB.next();
                        
                        result = result && ( entryA.getKey().equals(entryB.getKey()) );
                        String strA = entryA.getValue();
                        String strB = entryB.getValue();
                        result = result && ( strA.compareTo(strB) == 0);
                        
                }
                return result;
        }
}