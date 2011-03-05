/**
 * Building.java by Eric Hare
 * This class defines a "Building" object for use in FindItNow
 * It allows convenient storage of pertinent building information
 */

package com.net.finditnow;

import java.util.HashMap; // For map from floor to IDs

public class Building {
	
	private String name; // Store the name of the building
	private int building_id; // Store the building ID
	private int[] floor_ids; // Store the floor IDs
	private String[] floor_names; // Store the floor names

	// This no-argument constructor is for the use of Gson
	public Building() {

	}
	
	// This constructor defines a building object
	public Building(int building_id, String name, int[] floor_ids, String[] floor_names) {
		this.building_id = building_id;
		this.name = name;
		this.floor_ids = floor_ids;
		this.floor_names = floor_names;
	}

	// Returns the building ID of the building object
	public int getBuildingID() {
		return building_id;
	}

	// Returns the name of the building
	public String getName() {
		return name;
	}

	// Returns the floor names of the building
	public String[] getFloorNames(){
		return floor_names;
	}
	
	// Returns the floor IDs of the building
	public int[] getFloorIDs(){
		return floor_ids;
	}

	// This method defines and returns a map from floor names to floor IDs
	public HashMap<String, Integer> floorMap() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < getFloorNames().length; i++) {
			map.put(getFloorNames()[i], getFloorIDs()[i]);
		}
		return map;
	}

	// Returns a string representation of the given building object
	public String toString(){
		String result = "Name: " + name + ", Building ID: " + getBuildingID() + 
						", Floor IDs: " + getFloorIDs().toString() + 
						", Floor Names: " + getFloorNames().toString();
		return result;
	}
}