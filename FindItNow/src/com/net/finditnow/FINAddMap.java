package com.net.finditnow;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class FINAddMap extends MapActivity {
	
	// Map and Location Variables
	private static MapView mapView;
	private MapController mapController;
	private static FINAddOverlay mapOverlay;
	private static List<Overlay> mapOverlays;
	
	// Tapped Point
	private static GeoPoint tappedPoint;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew_map);
		
		tappedPoint = new GeoPoint(0, 0);
		
        // Initialize our MapView and MapController
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        
        mapOverlay = new FINAddOverlay();
        
        mapOverlays = mapView.getOverlays();
        mapOverlays.add(mapOverlay);
        
        // Build up our overlays and initialize our "UWOverlay" class
        
        // Zoom out enough
        mapController.animateTo(FINMap.DEFAULT_LOCATION);
        mapController.setZoom(17);
        Toast.makeText(getBaseContext(), "Tap the Location of your Item", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
    public static GeoPoint getTappedPoint() {
    	return tappedPoint;
    }
    
    public class FINAddOverlay extends Overlay {
		
		 @Override
	     public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
	         super.draw(canvas, mapView, shadow);                   
	
	         //---translate the GeoPoint to screen pixels---
	         Point screenPts = new Point();
	         mapView.getProjection().toPixels(tappedPoint, screenPts);
	
	         //---add the marker---
	         Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.androidmarker);            
	         canvas.drawBitmap(bmp, screenPts.x, screenPts.y, null);         
	         return true;
	     }
	
		@Override
	    public boolean onTouchEvent(MotionEvent event, MapView mapView) {   
	        //---when user lifts his finger---...
	        if (event.getAction() == 1) {
	        	tappedPoint = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
	        	Log.v("Blah", tappedPoint.toString());
	        	Toast.makeText(getBaseContext(), tappedPoint.getLatitudeE6() / 1E6 + "," +  tappedPoint.getLongitudeE6() /1E6 , Toast.LENGTH_SHORT).show();
	        	
	        	AlertDialog.Builder builder = new AlertDialog.Builder(mapView.getContext());
				builder.setMessage("Is this Location Correct?")
				
					.setCancelable(false)
				    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int id) {
				             FINAddMap.this.finish();
				        }
				    })
				    .setNegativeButton("No", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int id) {
				             dialog.cancel();
				        }
				    });
				
				AlertDialog alert = builder.create();
				alert.show();
	        }
	        return false;
	    }
	}
}
