package com.net.finditnow;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class FINAddOutdoor extends FINMapActivity {

	// Map and Location Variables
	private static FINMapView mapView;
	private MapController mapController;
	private static FINAddOverlay mapOverlay;
	private static List<Overlay> mapOverlays;
	String selectedCategory;
	String special_info;
	ProgressDialog myDialog;
	
	// Tapped Point
	private static GeoPoint tappedPoint;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew_outdoor);

		// Set the text in the titlebar
		setTitle(getString(R.string.app_name) + " > Add New Item > Outdoor Item");

		tappedPoint = new GeoPoint(0, 0);

		// Initialize our MapView and MapController
		mapView = (FINMapView)findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();

		mapOverlay = new FINAddOverlay();

		mapOverlays = mapView.getOverlays();
		mapOverlays.add(mapOverlay);

		Bundle extras = getIntent().getExtras(); 
		selectedCategory = extras.getString("selectedCategory");
		special_info = extras.getString("special_info");

		// Zoom out enough		
		mapController.animateTo(FINMap.getRegionCenter(getBaseContext()));
		mapController.setZoom(18);
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
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), FINHome.getIcon(selectedCategory, getBaseContext()));            
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
					myDialog = ProgressDialog.show(FINAddOutdoor.this, "" , "Adding " + selectedCategory + "...", true);
					Thread thread = new Thread() {
						@Override
						public void run() {
							//Send new item to database
							final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
							
							SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
							String rid = prefs.getInt("rid", 0)+"";

							String result = DBCommunicator.createItem(phone_id, selectedCategory, rid, 0+"", special_info, tappedPoint.getLatitudeE6()+"", tappedPoint.getLongitudeE6()+"", getBaseContext());

							// Load the map with the new item
							Intent myIntent = new Intent(getBaseContext(), FINMap.class);
							myIntent.putExtra("result", result);
							myIntent.putExtra("category", selectedCategory);
							myIntent.putExtra("building", "");
							myIntent.putExtra("centerLat", tappedPoint.getLatitudeE6());
							myIntent.putExtra("centerLon", tappedPoint.getLongitudeE6());

							String locations = DBCommunicator.getLocations(selectedCategory, rid, 0+"", getBaseContext());
							myIntent.putExtra("locations", locations);

							startActivity(myIntent);

							myDialog.dismiss();
						}
					};
					thread.start();
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

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);		
		menu.findItem(R.id.add_new_button).setVisible(false);

		return true;
	}
}
