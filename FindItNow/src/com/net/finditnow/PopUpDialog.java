package com.net.finditnow;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.MotionEvent;

import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import android.app.AlertDialog;
import android.app.Dialog;

import android.text.Html;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class PopUpDialog extends Dialog{

	//Local variable for displaying
	private String[] floorName;
	private String buildName;
	private String info;	
	private BigDecimal distance;
	private int walkTime;
	private String category;
	private int iconId;
	private boolean listExpanded;
	private boolean isOutdoor;

	//creates a PopUpDialog with the given fields, should use this one
	/**
	 * Constructor that intializes all the information to be displayed in
	 * this dialog
	 * 
	 * @param context - the context which calls this dialog
	 * @param floorName - the names of the floors where the category is located
	 * @param buildName - the name of the building/location
	 * @param category - the name of the current category
	 * @param info - any additional information that goes along the category
	 * @param distance - the distance from current location to location of building
	 * @param walkingTime - the time it take from current location to this location
	 * @param iconId - the id of the drawable Icon for this category
	 * @param isOutdoor - indicates this popUpDialog is for outdoor location or not
	 * 
	 */
	public PopUpDialog(Context context,String[] floorName, 
				String building, String category, String info, BigDecimal distance, int walkingTime,
				int iconId, boolean isOutdoor)
	{
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.floorName = floorName;
		this.buildName = building;
		this.info = info;
		this.distance = distance;
		this.walkTime = walkingTime;
		this.category = category;
		this.iconId = iconId;
		this.isOutdoor = isOutdoor;
		this.listExpanded = true;

	}
	
	/**
	 * called when PopUpDialog is first created
	 * It places all the information on the dialog, 
	 * 	creates a list for the floor details if there should be one
	 *  and any button that is necessary
	 *  
	 */
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.popupdialog);

		//sets the title of this dialog
    	TextView title = (TextView) findViewById(R.id.dialogTitle);
		title.setText(buildName);

		//the button that 1) displays the list of floor
		//            or  2) unconfirms an outdoor location
    	Button butt = (Button) findViewById(R.id.showFlrButt);
    	
    	//the text for displaying information for outdoor
    	// has no info is it is indoor
    	TextView outDoor = (TextView) findViewById(R.id.outDoorText);
    	
    	if ( !isOutdoor) {
    		//This location is indoor
    		//the button will show/hide the floor list when clicked on
	    	butt.setOnClickListener( new View.OnClickListener()
	    	{
	    		public void onClick(View v)
	    		{
	    			
	    			ExpandableListView lv = (ExpandableListView) findViewById(R.id.flrList);
	    			Button toggle = (Button) findViewById(R.id.showFlrButt);
	    			
	    			String[] flr = new String[0];
	    			// Show all the floor info.
	    			if (listExpanded)
	    			{
	    				toggle.setText("Hide Floors");
		    			lv.getLayoutParams().height = Math.min(200, LinearLayout.LayoutParams.WRAP_CONTENT);
		    			flr = floorName;
	    			}
	    			// Hide all the floor info.
	    			else {
	    				toggle.setText("Show Floors");
	    				lv.getLayoutParams().height = 0;
	    			}
	    			lv.setAdapter(new FloorExpandableListAdapter(lv.getContext(),flr, info, iconId));
	    			listExpanded = !listExpanded;
	    		}
	    	});
	    	//outdoor information is not needed in this case, make
	    	// it disappear
	    	outDoor.setVisibility(outDoor.INVISIBLE);
	    	outDoor.getLayoutParams().height = 0;
    	} else {
    		String spInfo = info.replace("\n", "<br />");
			outDoor.setText(Html.fromHtml(spInfo));

			butt.setText("Clcik to report this is not here");
			
			butt.setOnClickListener( new View.OnClickListener()
	    	{
	    		public void onClick(View v)
	    		{
	    			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
	    			builder.setMessage("This is not actually connect to update and don't have"
	    						+ " the object id yet, but!!\n"
	    						+ "Are you sure that this is not here?");
	    			builder.setCancelable(false);
	    			builder.setPositiveButton("Yes! I am sure.", new DialogInterface.OnClickListener() {
	    		           public void onClick(DialogInterface dialog, int id) {
	   		                //sents info to update.php here
	    		                dialog.dismiss();
	    		           }
	    		       });
	    			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
	    		           public void onClick(DialogInterface dialog, int id) {
	    		                dialog.cancel();
	    		           }
	    		       });

	    			AlertDialog dailog = builder.create();
	    			dailog.show();
	    		}
	    	});
    	}
    	
    	TextView cate = (TextView) findViewById(R.id.categoryName);
 
    	//Converts the first letter of category to upper case and
    	//adds the name of the service provided if it exist
    	String specialInfo = "<b>" + FINUtil.capFirstChar(category) + "</b>";

    	//sets the text into the textView for category name
    	cate.setText(Html.fromHtml(specialInfo));
    	
    	//sets the text for displaying the distance and walkingTime
    	TextView distText = (TextView) findViewById(R.id.distanceText);
    	TextView timeToText = (TextView) findViewById(R.id.timeReachText);
      	if (distance.equals(new BigDecimal(-1)))
    	{
    		//if location of the user is not known, indicated by -1
      		//then show that it cannot be calculated.
      		distText.setText(Html.fromHtml("<b>Distance to here:</b> Cannot calculate"));
    		timeToText.setText(Html.fromHtml("<b>Walking time:</b> Cannot calculate"));
    	}
    	else
    	{
    		distText.setText(Html.fromHtml("<b>Distance to here:</b> " + distance + " mi."));
    		timeToText.setText(Html.fromHtml("<b>Walking Time:</b> " + walkTime + FINUtil.pluralize("minute", walkTime)));
    	}
	}
	/**
	 * call when user touches on the screen
	 * closes the dialog if the user tap anywhere on screen
	 * but the lists or buttons
	 */
	public boolean onTouchEvent(MotionEvent e)
	{
		//dismisses this dialog (close it)
		dismiss();
		return true;
	}
	
	public static String updateDB(String category,int id) {
		/*
		   * HTTP Post request
		   */
		String data = "";
	  	InputStream iStream = null;
	  	try{
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpPost httppost = new HttpPost("http://cubist.cs.washington.edu/projects/11wi/cse403/RecycleLocator/update.php");
		  			
		        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
		        
	  			nameValuePairs.add(new BasicNameValuePair("category", category));
	  			nameValuePairs.add(new BasicNameValuePair("id", id+""));
	  			
	  			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        
		        HttpResponse response = httpclient.execute(httppost);
		        HttpEntity entity = response.getEntity();
		        iStream = entity.getContent();
	  	}catch(Exception e){
	  	    Log.e("log_tag", "Error in http connection "+e.toString());
	  	}
	  	//convert response to string
	  	try{
		        BufferedReader reader = new BufferedReader(new InputStreamReader(iStream,"iso-8859-1"),8);
		        StringBuilder sb = new StringBuilder();
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		        	sb.append(line + "\n");
		        }
		        iStream.close();
		 
		        data = sb.toString();
		        Log.v("test", data);
	  	}catch(Exception e){
	  	    Log.e("log_tag", "Error converting result "+e.toString());
	  	}
		
	  	return data;
	}
}  
