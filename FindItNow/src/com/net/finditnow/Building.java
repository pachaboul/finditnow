/***
 * Building.java by Eric Hare
 * This class defines a "Building" object for use in FindItNow
 * It allows convenient storage of pertinent building information
 */

package com.net.finditnow;

import java.util.HashMap; // For map from floor to IDs

public class Building {
	
	private String name; // Store the name of the building
	private int bid; // Store the building ID
	private int[] fid; // Store the floor IDs
	private String[] floor_names; // Store the floor names

	/**
	 * An empty constructor for use by GSON
	 */
	public Building() {

	}
	
	/**
	 * Defines a Building object
	 * 
	 * @param building_id - ID of the building
	 * @param name - Name of the building
	 * @param floor_ids - Floor IDs of the building
	 * @param floor_names = Floor names of the building
	 */
	public Building(int building_id, String name, int[] floor_ids, String[] floor_names) {
		this.bid = building_id;
		this.name = name;
		this.fid = floor_ids;
		this.floor_names = floor_names;
	}

	/**
	 * Method to return the building ID of the building object
	 * 
	 * @return An integer representing the building ID
	 */
	public int getBuildingID() {
		return bid;
	}

	/**
	 * Method to return the building name of the building object
	 * 
	 * @return A string of the building name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method to return the floor names of the building object
	 * 
	 * @return An array of strings representing the floor names
	 */
	public String[] getFloorNames(){
		return floor_names;
	}
	
	/**
	 * Method to return the floor IDs of the building object
	 * 
	 * @return An array of integers representing the floor IDs
	 */
	public int[] getFloorIDs(){
		return fid;
	}

	/**
	 * This method defines and returns a map from floor names to floor IDs
	 * 
	 * @return A HashMap from strings to integers of floor names to floor IDs
	 */
	public HashMap<String, Integer> floorMap() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < getFloorNames().length; i++) {
			map.put(getFloorNames()[i], getFloorIDs()[i]);
		}
		return map;
	}

	/**
	 * Returns a string representation of the given building object
	 * 
	 * @return String corresponding to the building object
	 */
	public String toString(){
		String result = "Name: " + name + ", Building ID: " + getBuildingID() + 
						", Floor IDs: " + getFloorIDs()+ 
						", Floor Names: " + getFloorNames().toString();
		return result;
	}
}