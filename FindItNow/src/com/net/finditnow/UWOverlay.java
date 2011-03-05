/***
 * UWOverlay.java by Eric Hare
 * This class defines an Overlay which extends ItemizedOverlay
 * It is used to display the icons on the map view
 */

package com.net.finditnow;

// Java library imports
import java.math.BigDecimal;
import java.util.ArrayList;

// Google Maps for Android imports
import com.google.android.maps.*;

// Other Android library dependencies
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.app.Dialog;

public class UWOverlay extends ItemizedOverlay<OverlayItem> {
	
	// Define a list of overlay items and the context for the overlay
	private ArrayList<OverlayItem> mapOverlays;
	private Context context;
	
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
	 * This method creates an item at the given index
	 */
	@Override
	protected OverlayItem createItem(int i) {
	  return mapOverlays.get(i);
	}

	/**
	 * This method returns the current size of the overlays
	 */
	@Override
	public int size() {
		return mapOverlays.size();
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
	 * This method defines an onTap action at the particular overlay item
	 */
	@Override
	protected boolean onTap(int index) {
		
		// Retrieve the item that was tapped
		OverlayItem item = mapOverlays.get(index);
		GeoPoint itemLocation = item.getPoint();
		
		// Calculate the distance and the walking time to this location
		BigDecimal distance = FINMap.distanceTo(itemLocation);
		int walkingTime = FINMap.walkingTime(distance, 35);

		// Retrieve the floors, special info, and category of the location
	    String[] floors = FINMap.getLocationFloors(itemLocation);
	    String specialInfo = FINMap.getSpecialInfo(itemLocation);
		String category = FINMap.getCategory();
		int iconId = FINMenu.getIcon(category);
		if (category.equals("supplies")) {
			category = FINMap.getItemName();
		}
		
		// Assume it is an outdoor location, but if it is not, grab the building name
	    String buildingName = "Outdoor Location";	    
	    if (FINMenu.getBuilding(itemLocation) != null) {
	    	buildingName = FINMenu.getBuilding(itemLocation).getName();
	    }
		
	    // Building the pop-up dialog with this information and then show it
	    Dialog popUp = new PopUpDialogV3(context, floors, buildingName, category,
	    			specialInfo, distance, walkingTime, iconId);
	    popUp.show();
	    
	    return true;
	}
}
