package com.net.finditnow;

import java.util.List;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

public class FINAddMap extends MapActivity {
	
	// Map and Location Variables
	private MapView mapView;
	private MapController mapController;
	private FINAddOverlay itemizedOverlay;
	private List<Overlay> mapOverlays;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew_map);
		
        // Initialize our MapView and MapController
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        
        // Build up our overlays and initialize our "UWOverlay" class
        mapOverlays = mapView.getOverlays();
        itemizedOverlay = new FINAddOverlay();
        mapOverlays.add(itemizedOverlay);
        
        // Zoom out enough
        mapController.setZoom(17);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
