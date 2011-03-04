package com.net.finditnow;

import java.math.BigDecimal;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.google.android.maps.*;
import android.app.Dialog;


public class UWOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	
	public UWOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
	}
	
	public UWOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		GeoPoint itemLocation = item.getPoint();
		
		BigDecimal distance = FINMap.distanceTo(itemLocation);
		
		// Returns the time to walk there in minutes
		// Only valid if distance is not BigDecimal(-1)
		int walkingTime = FINMap.walkingTime(distance);

	    String[] floor = FINMap.getFloors(itemLocation);
	    String name = FINMap.getSpecialInfo(itemLocation);	 
	    String buildingName = "Outdoor Location";
	    
	    if (FINMenu.getBuilding(itemLocation) != null)
	    	buildingName = FINMenu.getBuilding(itemLocation).getName();
	    
		String category = FINMap.getCategory();
		
	    Dialog popUp = new PopUpDialogVer2(mContext, floor, buildingName,category, name, distance, walkingTime);
	  
	    popUp.show();
	    return true;
	}

}
