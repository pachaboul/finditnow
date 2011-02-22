package finditnow.apk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.google.android.maps.*;


public class UWOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	
	public UWOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
	}
	
	public UWOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		GeoPoint itemLocation = item.getPoint();
	    //This is how you use a PopUpDialog
	    String[] floor = Map.getFloors(itemLocation);
	    
	    if (floor == null)
	    {
	    	floor = new String[]{"Level 6", "Level 5", "Level 4", "Level 3","Level 2","Level 1","Level B"};
	    }
	    
		String buildingName = Map.getBuilding(itemLocation);
	    if (buildingName == null) {
	  	    buildingName = "Unknown Building";
	    }
	    PopUpDialog popUp = new PopUpDialog(mContext, floor, buildingName);
	  
	    popUp.show();
	    return true;
	}

}
