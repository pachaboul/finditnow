/***
 * PopUpDialog.java by Chanel Huang
 * version 3.0:
 * Dispalys information differently for outdoor location and
 * location inside a building.
 * For location inside a building:
 * 	a button to show more details about each floor, where
 * 	more information about each floor will be displayed
 *  as well as a button for reporting something to be not
 *  there.
 * For out door location:
 * 	displays information and a button to report
 * 	something that is not there.
 * 
 * version 2.0:
 * Still displays information about the location.
 * However, unlike version 1, it displays detail about each floor
 * in the same dialog box.
 * 
 * version 1.0 (beta) :
 * A pop up dialog that displays information about the location,
 * where clicking on a button will pop up another dialog
 * with more detail information about each floor.
 * 
 */
package com.net.finditnow;

//Necessary for using certain methods
import java.math.BigDecimal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Region;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.TextView;
/*
 * Design Principle: inheritance
 *   inherit most of the methods from Dialog. We only override those methods that
 *   we need to produce a different behavior from regular Dialog as well as kept
 *   more information as global fields than Dialog.
 */
public class PopUpDialog extends Dialog{

	//Local variable for displaying
	private String buildName;
	private CategoryItem catItem;	
	private BigDecimal distance;
	private int walkTime;
	private String category;
	private int iconId;
	private boolean isOutdoor;
	private String[] allFlrName;

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
	public PopUpDialog(Context context,
				String buildName, String category, CategoryItem catItem, BigDecimal distance, int walkingTime,
				int iconId, boolean isOutdoor, String[] allFlrName)
	{
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.buildName = buildName;
		this.catItem = catItem;
		this.distance = distance;
		this.walkTime = walkingTime;
		this.category = category;
		this.iconId = iconId;
		this.isOutdoor = isOutdoor;
		this.allFlrName = allFlrName;
	}
	
	/**
	 * called when PopUpDialog is first created
	 * It places all the information on the dialog, 
	 * 	creates a list for the floor details if there should be one
	 *  and any button that is necessary
	 *  
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.popupdialog);

		//sets the title of this dialog
    	TextView title = (TextView) findViewById(R.id.dialogTitle);
		title.setText(buildName);

		//the button that 1) displays the list of floor
		//            or  2) unconfirms an outdoor location
		TextView butt = (TextView) findViewById(R.id.showFlrButt);
    	
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
	    			//Button toggle = (Button) findViewById(R.id.showFlrButt);
	    			TextView toggle = (TextView) findViewById(R.id.showFlrButt);
	    			
	    			CategoryItem item = new CategoryItem();
	    			String[] flrNames = new String[0];
	    			// Show all the floor info.
	    			if (lv.getCount() == 0)
	    			{
	    				toggle.setText("Hide Floors");
	    				lv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
	    				if (allFlrName.length > 3)
	    					lv.getLayoutParams().height = 150;

	    				item = catItem;
	    				flrNames = allFlrName;
	    				lv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
							public void onGroupExpand(int groupPosition) {
				    			ExpandableListView lv = (ExpandableListView) findViewById(R.id.flrList);
				    			
				    			if (lv.getExpandableListAdapter().getChildrenCount(groupPosition) != 0)
				    				lv.scrollBy(0, (groupPosition+1)*lv.getDividerHeight());			
							}
						});
	    			}
	    			// Hide all the floor info.
	    			else {
	    				toggle.setText("Show Floors");
	    				lv.getLayoutParams().height = 0;
	    			}
	    			lv.setAdapter(new FloorExpandableListAdapter(lv.getContext(),item,
	    					iconId, category, flrNames));
	    		}
	    	});
	    	//outdoor information is not needed in this case, make
	    	// it disappear
	    	outDoor.setVisibility(View.INVISIBLE);
	    	outDoor.getLayoutParams().height = 0;
    	} else {
    		String spInfo = catItem.getInfo().get(0).replace("\n", "<br />");
			outDoor.setText(Html.fromHtml(spInfo));

			butt.setText("Can't find it?");
			
			butt.setOnClickListener( new View.OnClickListener()
	    	{
	    		public void onClick(View v)
	    		{
	    			//pops a Dialog to confirm the user's intent
	    			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
	    			builder.setMessage("Are you sure that this " + FINMap.getCategory().replace("_", " ") + " location is not here?");
	    			builder.setCancelable(false);
	    			//confirms the action and perform the update accordingly 
	    			builder.setPositiveButton("Yes! I am sure.", new DialogInterface.OnClickListener() {
	    		           public void onClick(DialogInterface dialog, int id) {
	    		        	   Update.updateDB(FINUtil.deCapFirstChar(FINMap.getCategory()), catItem.getId().get(0));
	    		        	   dialog.dismiss();
	    		           }
	    		       });
	    			//cancels the action if the user didn't mean to do it
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
    		timeToText.setText(Html.fromHtml("<b>Walking Time:</b> " + walkTime + " " + FINUtil.pluralize("minute", walkTime)));
    	}
	}
	/**
	 * call when user touches on the screen
	 * closes the dialog if the user tap anywhere on screen
	 * but the lists or buttons
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		//dismisses this dialog (close it)
		int bottom = this.findViewById(R.id.popupLayout).getBottom();
		int top = this.findViewById(R.id.popupLayout).getTop();
		int left =this.findViewById(R.id.popupLayout).getLeft();
		int right =this.findViewById(R.id.popupLayout).getRight();
		
		Region dialogRegion = new Region (left,top,right,bottom );
		
		float x = e.getX();
		float y = e.getY();
		
		if (!dialogRegion.contains(Math.round(x),Math.round(y)))
			dismiss();
		return true;
	}
}  
