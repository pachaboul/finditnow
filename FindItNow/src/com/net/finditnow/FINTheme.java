/**
 * This class has useful static theming methods.  It can retrieve
 * the correct main/bright/light colors and the correct assets 
 * (tabs, buttons, etc.) for the current theme.
 */
package com.net.finditnow;

import android.content.Context;

public class FINTheme {
	
	/**
	 * Returns light color for theme
	 * Used for menu squares
	 */
	public static int getLightColor(String theme, Context context) {
		return context.getResources().getIdentifier("color/light_" + theme, null, context.getPackageName());
	}
	
	/**
	 * Returns bright color for theme
	 * Used in focused tab, tab bottom strip, page headers
	 */
	public static int getBrightColor(String theme, Context context) {
		return context.getResources().getIdentifier("color/bright_" + theme, null, context.getPackageName());
	}
	
	/**
	 * Returns main/dark color for theme
	 * Used for unfocused tab.
	 */
	public static int getMainColor(String theme, Context context) {
		return context.getResources().getIdentifier("color/main_" + theme, null, context.getPackageName());
	}
	
	/**
	 * Returns appropriate colored tab selector for menu.
	 */
	public static int getTabSelector(String theme, Context context) {
		return context.getResources().getIdentifier("drawable/tab_selector_" + theme, null, context.getPackageName());
	}
	
	/**
	 * Returns the appropriate colored button selector.
	 */
	public static int getButtonSelector(String theme, Context context) {
		return context.getResources().getIdentifier("drawable/fin_clickable_button_" + theme, null, context.getPackageName());
	}
	
	/**
	 * Font color is the same as the main color, but unfortunately
	 * setTextColor doesn't work when passed an identifier (instead of a color).
	 */
	public static int getFontColor(String theme, Context context) {
		if (theme.equals("blue")) {
			return context.getResources().getColor(R.color.main_blue);
		} else if (theme.equals("purple")) {
			return context.getResources().getColor(R.color.main_purple);
		} else if (theme.equals("red")) {
			return context.getResources().getColor(R.color.main_red);
		} else if (theme.equals("orange")) {
			return context.getResources().getColor(R.color.main_orange);
		} else {
			return context.getResources().getColor(R.color.main_green);
		} 
	}
}
