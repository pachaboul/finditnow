package com.net.finditnow;


import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import java.lang.StringBuffer;
import java.math.BigDecimal;

import android.widget.SimpleAdapter;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import android.widget.RelativeLayout;
import android.util.Log;


import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.BaseExpandableListAdapter;
import java.util.List;


public class PopUpDialogV3 extends Dialog{

	//Local variable for displaying
	private String[] floor;
	private String buildName;
	private String name;	
	private BigDecimal distance;
	private int walkTime;
	private String category;
	
	public PopUpDialogV3(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	//creates a PopUpDialog with the given fields, should use this one
	public PopUpDialogV3(Context context,String[] floor, 
				String building, String category, String name, BigDecimal distance, int walkingTime)
	{
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.floor = floor;
		this.buildName = building;
		this.name = name;
		this.distance = distance;
		this.walkTime = walkingTime;
		this.category = category;
	}
	
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.popupdialog3);

    	TextView title = (TextView) findViewById(R.id.dialogTitle);
    	if ( buildName != null && !buildName.equals("")) {
			title.setText(buildName);
    	} else {
			title.setText("Outdoor Location");
    	}
    	
    	TextView cate = (TextView) findViewById(R.id.categoryName);
 
    	//Converts the first letter of category to upper case and
    	//adds the name of the service provided if it exist
    	StringBuffer buffer = new StringBuffer(FINUtil.capFirstChar(category));
    	
    	buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
    	if (!(name == null) && !name.equals(""))
    		buffer.append(": "+ name);
    	
    	//sets the text into the textView
    	cate.setText(buffer.toString());

    	/*TextView seeflr = (TextView) findViewById(R.id.seefloor);
    	seeflr.on*/
    	
    	TextView distText = (TextView) findViewById(R.id.distanceText);
    	
    	TextView timeToText = (TextView) findViewById(R.id.timeReachText);
      	if (distance.equals(new BigDecimal(-1)))
    	{
    		distText.setText("Distance to here: Cannot Calculate");
    		timeToText.setText("Walking time: Cannot Calculate");
    	}
    	else
    	{
    		distText.setText("Distance to here: " + distance + " mi.");
    		timeToText.setText("Walking Time: " + walkTime + " minutes");
    	}

    	
    	//there is a button on this dialog, we need it to be clickable
    	Button butt = (Button) findViewById(R.id.showFlrButt);
    	//so when the user press it, it'll show the detail display
    	butt.setOnClickListener( new View.OnClickListener()
    	{
    		public void onClick(View v)
    		{
    		/*
    		 * 			//sets the different property of the text display
			TextView text2 = new TextView(this.getContext());
			text2.setText(floor[i]);
			text2.setPadding(TEXT_LEFT, TEXT_TOP + TEXT_DIFF*i, 0, 0);
			text2.setTextColor(smptext.getTextColors());
			layout.addView(text2);
			
			//sets the different property of the icon display
			ImageView img2 = new ImageView(this.getContext());
			img2.setImageResource(FINMenu.getIcon(FINMap.getCategory()));
			img2.setAdjustViewBounds(true);
			img2.setMaxHeight(IMG_TOP+IMG_DIFF*(i+1));
			img2.setPadding(IMG_LEFT,IMG_TOP+IMG_DIFF*i , IMG_RIGHT,0);
			layout.addView(img2);
    		 */
    			ExpandableListView lv = (ExpandableListView) findViewById(R.id.flrList);
    			ArrayList<HashMap<String,Object>> hashMapListForListView = new ArrayList<HashMap<String,Object>>();
    			List<List<HashMap<String,Object>>> childMapForView = new ArrayList<List<HashMap<String,Object>>>();
    		
    			Button toggle = (Button) findViewById(R.id.showFlrButt);
    			
    			// Show all the floor info.
    			if (lv.getCount() == 0)
    			{
    				toggle.setText("Hide Floors");
	    			HashMap<String,Object> map = new HashMap<String,Object>();
	    			for (String s: floor)
	    			{
	    				map.put("name",s);
	    				map.put("icon",FINMenu.getIcon(category));
	    				hashMapListForListView.add(map);
	    				map = new HashMap<String,Object>();
	    			}
	    			
	    			ArrayList<HashMap<String,Object>> childList = new ArrayList<HashMap<String,Object>>();
	    			for (String s: floor)
	    			{
	    				map.put("name",s);
	    				map.put("text", s + " ...blahblablaba info");
	    				childList.add(map);
	    				childMapForView.add(childList);
	    				map = new HashMap<String,Object>();
	    				childList = new ArrayList<HashMap<String,Object>>();
	    			}
	    			
	    			
	    			
	    			
	    			lv.getLayoutParams().height = Math.min(200, LinearLayout.LayoutParams.WRAP_CONTENT);
    			}
    			
    			// Hide all the floor info.
    			else {
    				toggle.setText("Show Floors");
    				lv.getLayoutParams().height = 0;
    			}

    	    	lv.setAdapter(
    	    			new SimpleExpandableListAdapter(lv.getContext(),
                                hashMapListForListView, 
                                R.layout.flrlist_item,
                                new String[] {"name"},//, "icon"},
                                new int[] { R.id.flrName},//, R.id.flrIcon}, 

                                childMapForView,
                                R.layout.flrlist_child,
                                new String[]{"text"},
                                new int[] {R.id.floorDetailText}        
    	    			) 
    	    	);
    		}
    	});
    	
	}
	public boolean onTouchEvent(MotionEvent e)
	{
		dismiss();
		return true;
	}
}  
