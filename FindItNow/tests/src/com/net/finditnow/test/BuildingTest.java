package com.net.finditnow.test;

import junit.framework.TestCase;

import com.net.finditnow.Building;

public class BuildingTest extends TestCase {
        
	protected Building build;
	
	protected void setUp() {
		int[] fid = new int[3];
		fid[0] = 1;
		fid[1] = 2;
		fid[2] = 3;
		
		String[] fnames = new String[3];
		fnames[0] = "Floor One";
		fnames[1] = "Floor Two";
		fnames[2] = "Floor Three";
		
		build = new Building(15, "Test Building", fid, fnames);
	}
	
	public void testBID() {
		assertTrue(build.getBuildingID() == 15);
	}
	
	public void testName() {
		assertTrue(build.getName().equals("Test Building"));
	}
	
	public void testFID() {
		int[] fid = new int[3];
		fid[0] = 1;
		fid[1] = 2;
		fid[2] = 3;
		
		assertTrue(build.getFloorIDs()[1] == fid[1]);
	}
	
	public void testFloorNames() {
		String[] fnames = new String[3];
		fnames[0] = "Floor One";
		fnames[1] = "Floor Two";
		fnames[2] = "Floor Three";
		assertTrue(build.getFloorNames()[0].equals("Floor One"));
	}
	
	public void testMap() {
		assertTrue(build.floorMap().get("Floor One") == 1);
	}
}