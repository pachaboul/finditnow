package finditnow.apk;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

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
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  
	  /*
	   * HTTP Post request
	   */
	String result = "";
	InputStream is = null;
	try{
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost("http://cubist.cs.washington.edu/~dustinab/index.php");
			//httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	}catch(Exception e){
	        Log.e("log_tag", "Error in http connection "+e.toString());
	}
	//convert response to string
	try{
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	        }
	        is.close();
	 
	        result=sb.toString();
	}catch(Exception e){
	        Log.e("log_tag", "Error converting result "+e.toString());
	}
	 
	//reference for converting to and parsing json data
	/*
	try{
        JSONArray jArray = new JSONArray(result);
        for(int i=0;i<jArray.length();i++){
            JSONObject json_data = jArray.getJSONObject(i);
            Log.i("log_tag","id: "+json_data.getInt("id")+
                ", name: "+json_data.getString("name")+
                ", sex: "+json_data.getInt("sex")+
                ", birthyear: "+json_data.getInt("birthyear")
            );
        }
	} catch(JSONException e){
	        Log.e("log_tag", "Error parsing data "+e.toString());
	}
	*/	  
	  
	  dialog.setTitle(item.getTitle());
	  //dialog.setMessage(item.getSnippet());
	  dialog.setMessage(result);
	  dialog.show();
	  
	  //This is how you use a PopUpDialog
	  String[] floor = {"Level 6", "Level 5", "Level 4", "Level 3","Level 2","Level 1","Levev B"};
	  String buildingName = "CSE Building";
      String categoryType = "recycle";
      PopUpDialog popUp = new PopUpDialog(mContext, floor, categoryType, buildingName);
	  
	  popUp.show();
	  
	  return true;
	}

}
