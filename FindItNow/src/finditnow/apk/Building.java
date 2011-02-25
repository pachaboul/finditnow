package finditnow.apk;

import com.google.android.maps.GeoPoint;

public class Building {
    private GeoPoint point;
    private String name;
    
    public Building(GeoPoint point, String name) {
		this.point = point;
		this.name = name;
    }
    
    public GeoPoint getPoint() {
    	return point;
    }
    
    public String getName() {
    	return name;
    }
}