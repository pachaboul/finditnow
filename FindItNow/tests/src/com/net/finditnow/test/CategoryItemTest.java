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
	
	public void testAddId(){
		item.addId(4);
		item.addId(4);
		item.addId(4);
		item.addId(5);
		
		assertTrue(item.getId().get(4) == 4);
		assertTrue(item.getId().get(6) == 5);
	}
	public void testAddInfo(){
		item.addInfo("");
		item.addInfo("Test!");
		item.addInfo("");
		item.addInfo("abc");
		
		assertTrue(item.getInfo().get(4).equals("Test!"));
		assertTrue(!item.getInfo().get(5).equals("abc"));
	}
	public void testAddFloorName(){
		item.addFloor_names("Floor B");
		item.addFloor_names("");
		item.addFloor_names("");
		item.addFloor_names("base");
		
		assertTrue(item.getFloor_names().get(4).equals(""));
		assertTrue(item.getFloor_names().get(6).equals("base"));
	}
	public void testgetID() {
		assertTrue(item.getId().get(0) == 1);
		assertTrue(item.getId().get(2) == 3);
	}
	
	public void testgetFloorName() {
		assertTrue(item.getFloor_names().get(0).equals("Floor One"));
		assertTrue(!item.getFloor_names().get(2).equals("Floor Two"));
	}
	
	public void testgetInfo() {
		assertTrue(item.getInfo().get(0).equals("a"));
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
