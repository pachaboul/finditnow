package com.net.finditnow.test;

import junit.framework.TestCase;

import org.json.JSONArray;

import com.google.android.maps.GeoPoint;
import com.net.finditnow.FINMap;
import com.net.finditnow.Get;

public class GetTest extends TestCase {
	
	protected GeoPoint location;
	protected String category;
	protected JSONArray json1;
	protected JSONArray json2;
	protected JSONArray json3;
	
	protected void setUp() {
		location = FINMap.DEFAULT_LOCATION;
		category = "vending";
		json1 = Get.requestFromDB(category, "", location);
		json2 = Get.requestFromDB("dfljhadsf", "", location);
		json3 = Get.requestFromDB("", "", location);
	}

	public void test1() {
		assertTrue(!(json1 == null));
	}
	
	public void test2() {
		assertTrue(json2 == null);
	}
	
	public void test3() {
		assertTrue(json3 == null);
	}
}