package recyclelocator.apk;

import java.util.List;

import com.google.android.maps.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.*;
import android.os.Bundle;
import android.widget.LinearLayout;

public class Map extends MapActivity {
	
	LinearLayout linearLayout;
	MapView mapView;
	
	List<Overlay> mapOverlays;
	Drawable drawable;
	UWOverlay itemizedOverlay;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.recyclebin);
        itemizedOverlay = new UWOverlay(drawable);
        
        GeoPoint point = new GeoPoint(19240000,-99120000);
        OverlayItem overlayItem = new OverlayItem(point, "", "");
        
        itemizedOverlay.addOverlay(overlayItem);
        mapOverlays.add(itemizedOverlay);
        
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {}

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
          };
        
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
 

    @Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}