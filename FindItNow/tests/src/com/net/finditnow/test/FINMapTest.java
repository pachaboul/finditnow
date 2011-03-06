package com.net.finditnow.test;

import junit.framework.TestCase;

import com.google.android.maps.GeoPoint;
import com.net.finditnow.FINMap;

public class FINMapTest extends TestCase {
	
	protected GeoPoint point1;
	protected GeoPoint point2;
	protected GeoPoint point3;
	
	protected void setUp() {
		point1 = new GeoPoint(47654799,-122307776);
		point2 = FINMap.DEFAULT_LOCATION;
		point3 = new GeoPoint(47651799,-122300776);
	}
	
	public void testPoint1() {
		assertEquals(point1.getLatitudeE6(), point2.getLatitudeE6());
	}
	
	public void testPoint2() {
		assertEquals(point1.getLongitudeE6(), point2.getLongitudeE6());
	}
	
	public void testDistance() {
		assertEquals(0, FINMap.distanceBetween(point1, point2).intValue());
	}
	
	public void distanceTest2() {
		assertEquals(0.39, FINMap.distanceBetween(point1, point3).doubleValue());
	}
	
	public void testWalkingTime1() {
		assertEquals(0, FINMap.walkingTime(FINMap.distanceBetween(point1, point2), 35));
	}
	
	public void testWalkingTime2() {
		assertEquals(7, FINMap.walkingTime(FINMap.distanceBetween(point1, point3), 20));
	}
	
	public void testWalkingTime3() {
		assertEquals(7, FINMap.walkingTime(FINMap.distanceBetween(point1, point3), 20));
	}
	
	public void testWalkingTime4() {
		assertEquals(15, FINMap.walkingTime(FINMap.distanceBetween(point1, point3), 40));
	}
	
	public void testWalkingTime5() {
		assertEquals(0, FINMap.walkingTime(FINMap.distanceBetween(point1, point3), 0));
	}
}