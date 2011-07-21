/***
 * UWOverlay.java by Eric Hare
 * This class defines an Overlay which extends ItemizedOverlay
 * It is used to display the icons on the map view
 */

package com.net.finditnow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
public class IconOverlay extends ItemizedOverlay<OverlayItem> {

	// Define a list of overlay items and the context for the overlay
	private ArrayList<OverlayItem> mapOverlays;
	private Context context;
	private String category;
	private HashMap<GeoPoint,HashMap<String,CategoryItem>> items;

	/**
	 * A custom constructor of the UWOverlay class which accepts lots of information
	 *
	 * @param defaultMarker The default icon to use on the overlay
	 * @param context The context in which the overlay is created
	 */
	public IconOverlay(Drawable defaultMarker, Context context, String category, HashMap<GeoPoint,HashMap<String,CategoryItem>> items) {
		super(boundCenterBottom(defaultMarker));
		mapOverlays = new ArrayList<OverlayItem>();
		this.context = context;
		this.category = category;
		this.items = items;
	}
	/**
	 * This method adds the given overlay to the map and then repopulates it
	 * @param overlay The overlay to add
	 */
	public void addOverlay(OverlayItem overlay) {
		mapOverlays.add(overlay);
		populate();
	}

	public void removeOverlay(int index) {
		mapOverlays.remove(index);
	}

	/**
	 * This method creates an item at the given index
	 */
	@Override
	protected OverlayItem createItem(int i) {
		return mapOverlays.get(i);
	}

	/**
	 * This method defines an onTap action at the particular overlay item
	 */
	@Override
	protected boolean onTap(int index) {

		// Retrieve the item that was tapped
		OverlayItem overlay = mapOverlays.get(index);
		GeoPoint itemLocation = overlay.getPoint();
		
		Log.v("Burke Museum", FINHome.getBuilding(itemLocation, context).toString());

		// Calculate the distance and the walking time to this location
		BigDecimal distance = FINMap.distanceBetween(FINMap.getLocation(), itemLocation);
		int walkingTime = FINMap.walkingTime(distance, 35);

		// Retrieve the floors, special info, and category of the location
		HashMap<String,CategoryItem> data = items.get(itemLocation);

		// Assume it is an outdoor location, but if it is not, grab the building name
		Building building = FINHome.getBuilding(itemLocation, context); 
		boolean isOutdoor = true;
		if (FINHome.getBuilding(itemLocation, context) != null) {
			isOutdoor = false;
		}

		// Building the pop-up dialog with this information and then show it
		Dialog popUp = new PopUpDialog(context, building, category, category, data, distance, walkingTime, isOutdoor);

		popUp.show();

		return true;
	}

	/**
	 * This method returns the current size of the overlays
	 */
	@Override
	public int size() {
		return mapOverlays.size();
	}
}