/**
 * Note:
 * At the moment there doesn't seem to be a way to
 * test the accessor methods in FINMenu.java because 
 * they rely on internal variables.  
 * @author Mai Dang
 */

package com.net.finditnow.test;

import java.util.ArrayList;
import java.util.HashMap;

import android.test.AndroidTestCase;

import com.google.android.maps.GeoPoint;
import com.net.finditnow.Building;
import com.net.finditnow.FINMenu;
import com.net.finditnow.Get;
import com.net.finditnow.JsonParser;

public class FINMenuTest extends AndroidTestCase {

	private static HashMap<GeoPoint, Building> buildingsMap;
	private static HashMap<String, Integer> iconsMap;
	private static ArrayList<String> categories;
	private static GeoPoint gp;
	private static Building building;
	
	protected void setUp() {
		
		// category stuff
		categories = FINMenu.getCategoriesList();
	
		// icons stuff
		iconsMap = FINMenu.createIconsList(categories, getContext());

		// buildings stuff
		String listOfBuildings = Get.requestFromDB("", null, null);
		buildingsMap = JsonParser.parseBuildingJson(listOfBuildings);
		
		gp = new GeoPoint(47657186, -122306194);
		building = new Building(15, "Miller Hall", null, null);
		buildingsMap.put(gp, building);
	}
	
	/********************************************
	 * 				getCategoriesList()
	 ********************************************/
	
	public void testNullCategories() {
		assertTrue(categories != null);
	}
	
	public void testEmptyCategories() {
		assertTrue(categories.size() > 0);
	}
	
	
	/********************************************
	 * 				createIconsList()
	 ********************************************/
	
	public void testNullIcons() {
		assertTrue(iconsMap != null);
	}
	
	public void testEmptyIconMap() {
		assertTrue(iconsMap.size() > 0);
	}

	/********************************************
	 * 				createBuildingList()
	 ********************************************/
	
	public void testNullBuildings() {
		assertTrue(buildingsMap != null);
	}
	
	public void testEmptyBuildingMap() {
		assertTrue(buildingsMap.size() > 0);
	}
}
