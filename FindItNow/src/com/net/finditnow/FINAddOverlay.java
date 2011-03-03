package com.net.finditnow;

import java.util.ArrayList;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.*;
import android.app.Dialog;


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
