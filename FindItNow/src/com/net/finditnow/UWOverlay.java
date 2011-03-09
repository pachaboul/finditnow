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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class UWOverlay extends ItemizedOverlay<OverlayItem> {

	// Define a list of overlay items and the context for the overlay
	private ArrayList<OverlayItem> mapOverlays;
	private Context context;
	private String category;
	private String itemName;
	private HashMap<GeoPoint, CategoryItem> items;

	/**
	 * A constructor of the UWOverlay class
	 *
	 * @param defaultMarker The default icon to use on the overlay
	 */
	public UWOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		mapOverlays = new ArrayList<OverlayItem>();
	}

	/**
	 * A constructor of the UWOverlay class which accepts a context
	 *
	 * @param defaultMarker The default icon to use on the overlay
	 * @param context The context in which the overlay is created
	 */
	public UWOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mapOverlays = new ArrayList<OverlayItem>();
		this.context = context;
	}
	/**
	 * A custom constructor of the UWOverlay class which accepts lots of information
	 *
	 * @param defaultMarker The default icon to use on the overlay
	 * @param context The context in which the overlay is created
	 */
	public UWOverlay(Drawable defaultMarker, Context context, String category, String itemName, HashMap<GeoPoint, CategoryItem> items) {
		super(boundCenterBottom(defaultMarker));
		mapOverlays = new ArrayList<OverlayItem>();
		this.context = context;
		this.category = category;
		this.itemName = itemName;
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

		// Calculate the distance and the walking time to this location
		BigDecimal distance = FINMap.distanceBetween(FINMap.getLocation(), itemLocation);
		int walkingTime = FINMap.walkingTime(distance, 35);

		// Retrieve the floors, special info, and category of the location
		int iconId = FINMenu.getIcon(category);
		String item = category;
		if (category.equals("school_supplies")) {
			item = itemName;
		}
		CategoryItem catItem = items.get(itemLocation);

		// Assume it is an outdoor location, but if it is not, grab the building name
		String buildingName = "Outdoor Location"; 
		String[] allFlrName = new String[0];
		boolean isOutdoor = true;
		if (FINMenu.getBuilding(itemLocation) != null) {
			buildingName = FINMenu.getBuilding(itemLocation).getName();
			isOutdoor = false;
			allFlrName = FINMenu.getBuilding(itemLocation).getFloorNames();
		}

		// Building the pop-up dialog with this information and then show it
		Dialog popUp = new PopUpDialog(context, buildingName, item, category, catItem, distance, walkingTime, iconId, isOutdoor, allFlrName);
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