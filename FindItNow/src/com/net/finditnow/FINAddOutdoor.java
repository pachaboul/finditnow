package com.net.finditnow;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class FINAddOutdoor extends MapActivity {
	
	// Map and Location Variables
	private static MapView mapView;
	private MapController mapController;
	private static FINAddOverlay mapOverlay;
	private static List<Overlay> mapOverlays;
	String selectedCategory;
	
	boolean[] supplyTypes;
	
	// Tapped Point
	private static GeoPoint tappedPoint;
	
    @Override
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
		supplyTypes = extras.getBooleanArray("supplyTypes");
        
        // Zoom out enough
        mapController.animateTo(FINMap.DEFAULT_LOCATION);
        mapController.setZoom(17);
        Toast.makeText(getBaseContext(), "Tap the location of your item", Toast.LENGTH_SHORT).show();
	}
	
	public class FINAddOverlay extends Overlay {
		
		 @Override
	     public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
	         super.draw(canvas, mapView, shadow);                   
	
	         //---translate the GeoPoint to screen pixels---
	         Point screenPts = new Point();
	         mapView.getProjection().toPixels(tappedPoint, screenPts);
	
	         //---add the marker---
	         Bitmap bmp = BitmapFactory.decodeResource(getResources(), FINMenu.getIcon(FINUtil.deCapFirstChar(selectedCategory)));            
	         canvas.drawBitmap(bmp, screenPts.x-12, screenPts.y-35, null);         
	         return true;
		 }
	
		@Override
	    public boolean onTap(GeoPoint p, MapView mapView) {   
        	tappedPoint = p;
        	
        	//Setup and show confirmation dialog for tapped point
        	AlertDialog.Builder builder = new AlertDialog.Builder(mapView.getContext());
			builder.setMessage("Is this Location Correct?")
			
				.setCancelable(false)
			    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			        	//Point is confirmed
			        	
						String bb = "", sc = "", pr = "";
						//Handle special case of school supplies
						if (selectedCategory.equals("School Supplies") && supplyTypes[0])
							bb = "bb"; 
						if (selectedCategory.equals("School Supplies") && supplyTypes[1])
							sc = "sc";
						if (selectedCategory.equals("School Supplies") && supplyTypes[2])
	 						pr = "print";
						
						//Send new item to database
						final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
			        	String result = Create.sendToDB(FINUtil.deCapFirstChar(selectedCategory), tappedPoint, 0, "",  bb,  sc,  pr, phone_id, getBaseContext());
			        	
			        	//Return to categories screen
				    	Intent myIntent = new Intent(getBaseContext(), FINHome.class);
				    	myIntent.putExtra("result", result);
				    	
			            startActivity(myIntent);
			        }
			    })
			    .setNegativeButton("No", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int id) {
			        	//New location not confirmed, try again
			        	Toast.makeText(getBaseContext(), "Tap the location of your item", Toast.LENGTH_SHORT).show();
			            dialog.cancel();
			        }
			    });
			
			AlertDialog alert = builder.create();
			alert.show();
			return true;
	    }
	}

	public static GeoPoint getTappedPoint() {
    	return tappedPoint;
    }
	
    @Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
