package com.net.finditnow.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.net.finditnow.FINUtil;

public class FINUtilTest extends TestCase {
        
	protected String string1;
	protected String string2;
	protected String string3;
	protected ArrayList<String> stringArray1;
	
	protected void setUp() {
		string1 = "cat";
		string2 = "Cat";
		string3 = "c";
		
		stringArray1 = new ArrayList<String>();		
		stringArray1.add("chanel");
		stringArray1.add("mai");
	}
	
	public void test1() {
	    assertTrue(FINUtil.capFirstChar(string1).equals("Cat"));
	}
	
	public void test2() {
	    assertTrue(FINUtil.capFirstChar(string2).equals("Cat"));
	}
	
	public void test3() {
	    assertTrue(FINUtil.capFirstChar(string3).equals("C"));
	}
	
	public void test4() {
		ArrayList<String> result4 = new ArrayList<String>();
		result4.add("Chanel");
		result4.add("Mai");
	    assertTrue(FINUtil.capFirstChar(stringArray1).equals(result4));
	}
}