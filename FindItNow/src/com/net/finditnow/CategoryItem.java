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

	public void addFloor_names(String floorNames) {
		this.floor_names.add(floorNames);
	}

	public void addId(int id) {
		this.id.add(id);
	}

	public void addInfo(String info) {
		this.info.add(info);
	}

	/**
	 * auto-generated equals method
	 * return true if two are equal, otherwise false
	 * 
	 * @param obj oth other to be compare to
	 * @return boolean whether two object are equal or not
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		CategoryItem other = (CategoryItem) obj;
		if (this.floor_names.size() != other.floor_names.size())
			return false;
		for(int c= 0; c < floor_names.size(); c++)
		{
			if (! this.floor_names.get(c).equals(other.floor_names.get(c)) )
				return false;
		}
		if (this.info.size() != other.floor_names.size())
			return false;
		for(int c= 0; c < floor_names.size(); c++)
		{
			if (! this.info.get(c).equals(other.info.get(c)) )
				return false;
		}
		if (this.id.size() != other.id.size())
			return false;
		for(int c= 0; c < id.size(); c++)
		{
			if (this.id.get(c) != other.id.get(c) )
				return false;
		}
		return true;
	}

	
	public ArrayList<String> getFloor_names() {
		return floor_names;
	}

	public ArrayList<Integer> getId() {
		return id;
	}


	public ArrayList<String> getInfo() {
		return info;
	}

	@Override
	public String toString() {
		return "CategoryItem [floor_names=" + floor_names + ", id=" + id
				+ ", info=" + info + "]";
	}
	
	
	
}
