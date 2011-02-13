package finditnow.apk;

import java.util.List;
import com.google.android.maps.*;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.*;
import android.os.Bundle;
import android.widget.LinearLayout;

public class Map extends MapActivity {
	
	private MapView mapView;
	private MapController mapController;
	private MyLocationOverlay locOverlay;
	
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private UWOverlay itemizedOverlay;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	// Restore the saved instance and generate the primary (main) layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /*
        -- by Mai
        
        // To get the category type:
        Bundle extras = getIntent().getExtras(); 
        String category = extras.getString("category");
        
        // And to get the item name for buildings, supplies:
        String itemName = extras.getString("itemName");
        
        */
        
        createMap();
        locateUser();
    }
    
    private void createMap() {
        // Initialize our MapView and MapController
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        
        // Build up our overlays and initialize our "UWOverlay" class
        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.recycle_bin);
        itemizedOverlay = new UWOverlay(drawable, this);
        
        // Create a GeoPoint location on the Paul Allen Center and animate to it
        GeoPoint point = new GeoPoint(47653286,-122305850);
        mapController.animateTo(point);
        mapController.zoomToSpan(2500, 2500);
        OverlayItem overlayItem = new OverlayItem(point, "CSE Building", "Coffee Stand (Floor 1)");
        
        // Add our overlay to the list
        itemizedOverlay.addOverlay(overlayItem);
        mapOverlays.add(itemizedOverlay);
    }
    
    private void locateUser() {
    	// Define a new LocationOverlay and enable it
        locOverlay = new MyLocationOverlay(this, mapView);
        locOverlay.enableMyLocation();
        mapOverlays.add(locOverlay);
        
        // Run this ONLY once we get a fix on the location
		Runnable runnable = new Runnable() {
			public void run() {
				mapController.animateTo(locOverlay.getMyLocation());
				mapController.zoomToSpan(2500, 2500);
			}
		};
		locOverlay.runOnFirstFix(runnable);
    }
    
 
    @Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}