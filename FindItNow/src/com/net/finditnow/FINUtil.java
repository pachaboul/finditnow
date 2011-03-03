package com.net.finditnow;

import java.lang.StringBuffer;

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
}
