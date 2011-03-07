package com.net.finditnow.test;

import junit.framework.TestCase;

import com.net.finditnow.CategoryItem;

public class CategoryItemTest extends TestCase {

	protected CategoryItem item;
	
	protected void setUp() {
		int[] id = new int[3];
		id[0] = 1;
		id[1] = 2;
		id[2] = 3;
		
		String[] fnames = new String[3];
		fnames[0] = "Floor One";
		fnames[1] = "Floor Two";
		fnames[2] = "Floor Three";
		
		String[] info = {"a", "b", "blah"};
		item = new CategoryItem();
		
		for (String f: fnames)
		{
			item.addFloor_names(f);
		}
		for (String i: info)
			item.addInfo(i);
		
		for (int i: id)
			item.addId(i);
		
	}
	
	public void testOneID() {
		assertTrue(item.getId().get(0) == 1);
	}
	
	public void testOneFloorName() {
		assertTrue(item.getFloor_names().get(0).equals("Floor One"));
	}
	
	public void testOneInfo() {
		assertTrue(item.getInfo().get(2).equals("blah"));
	}
	public void testEquals(){
		int[] id = new int[3];
		id[0] = 1;
		id[1] = 2;
		id[2] = 3;
		
		String[] fnames = new String[3];
		fnames[0] = "Floor One";
		fnames[1] = "Floor Two";
		fnames[2] = "Floor Three";
		
		String[] info = {"a", "b", "blah"};
		CategoryItem test = new CategoryItem();
		
		for (String f: fnames)
		{
			test.addFloor_names(f);
		}
		for (String i: info)
			test.addInfo(i);
		
		for (int i: id)
			test.addId(i);
		
		assertTrue(item.equals(test));
	}
}
