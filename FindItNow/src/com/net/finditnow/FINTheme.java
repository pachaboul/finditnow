package com.net.finditnow;

import android.content.Context;

public class FINTheme {

	public static String theme; // color
	public static Context context;
	public static final String RED = "red"; //unimplemented
	public static final String ORANGE = "orange"; // unimplemented
	public static final String GREEN = "green";
	public static final String BLUE = "blue";
	public static final String PURPLE = "purple";
	
	/**
	 * Needs to be called in launch class, after campus is selected.
	 */
	public static void setTheme(String _theme, Context _context) {
		theme = _theme;
		context = _context;
	}
	
	/**
	 * Returns light color for theme
	 * Used for menu squares
	 */
	public static int getLightColor() {
		return context.getResources().getIdentifier("color/light_" + theme, null, context.getPackageName());
	}
	
	/**
	 * Returns bright color for theme
	 * Used in focused tab, tab bottom strip, page headers
	 */
	public static int getBrightColor() {
		return context.getResources().getIdentifier("color/bright_" + theme, null, context.getPackageName());
	}
	
	/**
	 * Returns main/dark color for theme
	 * Used for unfocused tab, colored text
	 */
	public static int getMainColor() {
		return context.getResources().getIdentifier("color/main_" + theme, null, context.getPackageName());
	}
	
	/**
	 * Returns appropriate colored tab selector for menu.
	 */
	public static int getTabSelector() {
		return context.getResources().getIdentifier("drawable/tab_selector_" + theme, null, context.getPackageName());
	}

}
