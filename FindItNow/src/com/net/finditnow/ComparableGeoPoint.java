package com.net.finditnow;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class ComparableGeoPoint implements Comparator<GeoPoint> {

	public BigDecimal distanceTo(GeoPoint one, GeoPoint another) {
		// Define a math context and two location variables to process
		MathContext mc = new MathContext(2);
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
			return new BigDecimal(loc1.distanceTo(loc2) * 0.000621371192, mc);
		} else {

			// Return -1 if the location was not valid
			return new BigDecimal(-1);
		}
	}

	public int compare(GeoPoint object1, GeoPoint object2) {
		GeoPoint point = FINMap.getLocation();
		if (point != null) {
			BigDecimal distance1 = distanceTo(point, object1);
			BigDecimal distance2 = distanceTo(point, object2);
			double diff = distance1.doubleValue() - distance2.doubleValue();
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
