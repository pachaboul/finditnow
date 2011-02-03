package recyclelocator.apk;

import java.util.List;

import com.google.android.maps.*;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;

public class Map extends MapActivity {
	
	LinearLayout linearLayout;
	MapView mapView;
	MapController mapController;
	
	List<Overlay> mapOverlays;
	Drawable drawable;
	UWOverlay itemizedOverlay;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	// Restore the saved instance and generate the primary (main) layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
    
 
    @Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}