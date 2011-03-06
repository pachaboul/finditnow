package com.net.finditnow;

/***
 * CategoryItem.java by Chanel Huang
 * This class defines a "CategoryItem" object which is a 
 * convenient storage of information about a certain 
 * category in regards with a location.
 */

import java.util.ArrayList;


public class CategoryItem {

	private ArrayList<String> floor_names;
	private ArrayList<String> info;
	private ArrayList<Integer> id;
	
	public CategoryItem(){
		floor_names = new ArrayList<String>();
		info = new ArrayList<String>();
		id = new ArrayList<Integer>();
	}

	public ArrayList<String> getFloor_names() {
		return floor_names;
	}

	public void addFloor_names(String floorNames) {
		this.floor_names.add(floorNames);
	}

	public ArrayList<String> getInfo() {
		return info;
	}

	public void addInfo(String info) {
		this.info.add(info);
	}

	
	public ArrayList<Integer> getId() {
		return id;
	}

	public void addId(int id) {
		this.id.add(id);
	}
	
	
}
