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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class FINAddOutdoor extends MapActivity {
	
	// Map and Location Variables
	private static MapView mapView;
	private MapController mapController;
	private static FINAddOverlay mapOverlay;
	private static List<Overlay> mapOverlays;
	String selectedCategory;
	
	// Tapped Point
	private static GeoPoint tappedPoint;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew_outdoor);
		
		tappedPoint = new GeoPoint(0, 0);
		
        // Initialize our MapView and MapController
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        
        mapOverlay = new FINAddOverlay();
        
        mapOverlays = mapView.getOverlays();
        mapOverlays.add(mapOverlay);
        
        Bundle extras = getIntent().getExtras(); 
		selectedCategory = extras.getString("selectedCategory");
		Log.v("test", selectedCategory);
        
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
	         Bitmap bmp = BitmapFactory.decodeResource(getResources(), FINMenu.getIcon(FINUtil.reverseCapFirstChar(selectedCategory)));            
	         canvas.drawBitmap(bmp, screenPts.x-12, screenPts.y-35, null);         
	         return true;
		 }
	
		@Override
	    public boolean onTap(GeoPoint p, MapView mapView) {   
        	tappedPoint = p;
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(mapView.getContext());
			builder.setMessage("Is this Location Correct?")
			
				.setCancelable(false)
			    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			        	Create.sendToDB(FINUtil.reverseCapFirstChar(selectedCategory), tappedPoint, 0, "",  "",  "",  "");
				    	Intent myIntent = new Intent(getBaseContext(), FINMenu.class);
			            startActivity(myIntent);
			            Toast.makeText(getBaseContext(), "New item added successfully!", Toast.LENGTH_LONG).show();
			        }
			    })
			    .setNegativeButton("No", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			             dialog.cancel();
			        }
			    });
			
			AlertDialog alert = builder.create();
			alert.show();
			return true;
	    }
	}
}
