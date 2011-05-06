
package com.net.finditnow;

import com.google.android.maps.GeoPoint;

public class Region implements Comparable<Region> {

	private String name;
	private int uni_id;
	private GeoPoint loc;
	private int order;

	/**
	 * An empty constructor for use by GSON
	 */
	public Region() {

	}

	public Region(String name, int uni_id, GeoPoint loc, int order) {
		this.name = name;
		this.uni_id = uni_id;
		this.loc = loc;
		this.order = order;
	}

	public String getName() {
		return name;
	}
	
	public int getUniID() {
		return uni_id;
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