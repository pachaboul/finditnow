package com.net.finditnow.test;

import junit.framework.TestCase;

import com.google.android.maps.GeoPoint;
import com.net.finditnow.FINMap;
import com.net.finditnow.Get;

public class GetTest extends TestCase {
	
	protected GeoPoint location;
	protected String category;
	protected String json1;
	protected String json2;
	protected String json3;
	
	protected void setUp() {
		location = FINMap.DEFAULT_LOCATION;
		category = "vending";
		json1 = Get.requestFromDB(category, "", location, null);
		json2 = Get.requestFromDB("dfljhadsf", "", location, null);
		json3 = Get.requestFromDB("", "", location, null);
	}

	public void test1() {
		assertTrue(!json1.startsWith("Error"));
	}
	
	public void test2() {
		assertTrue(json2.startsWith("Error"));
	}
	
	public void test3() {
		assertTrue(json3.startsWith("Error"));
	}
}