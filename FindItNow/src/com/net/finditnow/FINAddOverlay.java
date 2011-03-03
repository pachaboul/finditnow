package com.net.finditnow;

import android.view.MotionEvent;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


public class FINAddOverlay extends Overlay {
	
    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {   
        //---when user lifts his finger---
        if (event.getAction() == 1) {                
            FINAddNew.setTappedPoint(mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY()));
        }
        return false;
    }        

}
