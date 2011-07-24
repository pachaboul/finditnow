
package com.net.finditnow;

import com.google.android.maps.GeoPoint;

public class Region implements Comparable<Region> {

	private String name;
	private int rid;
	private GeoPoint loc;
	private int order;

	/**
	 * An empty constructor for use by GSON
	 */
	public Region() {

	}

	public Region(String name, int rid, GeoPoint loc, int order) {
		this.name = name;
		this.rid = rid;
		this.loc = loc;
		this.order = order;
	}

	public String getName() {
		return name;
	}
	
	public int getRID() {
		return rid;
	}
	
	public GeoPoint getLocation() {
		return loc;
	}
	
	public int getOrder() {
		return order;
	}

	public int compareTo(Region arg0) {
		return arg0.order - this.order;
	}
}