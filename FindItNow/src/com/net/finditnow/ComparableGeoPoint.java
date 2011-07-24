package com.net.finditnow;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class ComparableGeoPoint implements Comparator<GeoPoint> {

	public double distanceTo(GeoPoint one, GeoPoint another) {
		// Define a math context and two location variables to process
		Location loc1 = new Location("");
		Location loc2 = new Location("");

		// This method is valid so long as the location is not the default and not null
		if (another != null) {

			// Compute the latitude and longitude of the two points
			// Add these values to our location variable
			loc1.setLatitude((float)(one.getLatitudeE6()*1E-6));
			loc1.setLongitude((float)(one.getLongitudeE6()*1E-6));
			loc2.setLatitude((float)(another.getLatitudeE6()*1E-6));
			loc2.setLongitude((float)(another.getLongitudeE6()*1E-6));

			// Return this value in miles rounded
			return loc1.distanceTo(loc2) * 0.000621371192;
		} else {

			// Return -1 if the location was not valid
			return -1;
		}
	}

	public int compare(GeoPoint object1, GeoPoint object2) {
		GeoPoint point = FINMap.getLocation();
		if (point != null) {
			double distance1 = distanceTo(point, object1);
			double distance2 = distanceTo(point, object2);
			double diff = distance1 - distance2;
			if (diff < 0) {
				return -1;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

}
