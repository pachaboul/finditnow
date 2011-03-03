package com.net.finditnow;

import java.util.ArrayList;

/*
 * This is a class that holds some util methods that is shared between classes.
 */
public class FINUtil {
	//precondition: str is not empty
	//post condition: str with first character capitalized
	public static String capFirstChar(String str)
	{
		StringBuffer buffer = new StringBuffer();
    	buffer.append(str);    	
    	//char cateName = Character.toUpperCase(buffer.charAt(0));
    	buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
		
    	return buffer.toString();
	}
	
	public static ArrayList<String> capFirstChar(ArrayList<String> strs)
	{
		ArrayList<String> al = new ArrayList<String>();
		for (String s : strs) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(s);
			buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
			al.add(buffer.toString());
		}
		return al;
	}
}
