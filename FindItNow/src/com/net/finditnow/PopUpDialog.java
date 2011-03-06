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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.MotionEvent;

//different type of views used in this dialog
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.app.AlertDialog;
import android.app.Dialog;

//formating
import android.text.Html;
//special Deciaml Object which is used for formatting
import java.math.BigDecimal;

public class PopUpDialog extends Dialog{

	//Local variable for displaying
	private String[] floorName;
	private String buildName;
	private String info;	
	private BigDecimal distance;
	private int walkTime;
	private String category;
	private int iconId;
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
				String buildName, String category, String info, BigDecimal distance, int walkingTime,
				int iconId, boolean isOutdoor)
	{
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.floorName = floorName;
		this.buildName = buildName;
		this.info = info;
		this.distance = distance;
		this.walkTime = walkingTime;
		this.category = category;
		this.iconId = iconId;
		this.isOutdoor = isOutdoor;
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
	    			
	    			String[] flr = new String[0];
	    			// Show all the floor info.
	    			if (lv.getCount() == 0)
	    			{
	    				toggle.setText("[ - Hide Floors ]");
	    				lv.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
	    				if (floorName.length > 3)
	    					lv.getLayoutParams().height = 200;

	    				flr = floorName;
	    			}
	    			// Hide all the floor info.
	    			else {
	    				toggle.setText("[ + Show Floors ]");
	    				lv.getLayoutParams().height = 0;
	    			}
	    			lv.setAdapter(new FloorExpandableListAdapter(lv.getContext(),flr, info, 
	    					iconId, category));
	    		}
	    	});
	    	//outdoor information is not needed in this case, make
	    	// it disappear
	    	outDoor.setVisibility(outDoor.INVISIBLE);
	    	outDoor.getLayoutParams().height = 0;
    	} else {
    		String spInfo = info.replace("\n", "<br />");
			outDoor.setText(Html.fromHtml(spInfo));

			butt.setText("[ Report not found ]");
			
			butt.setOnClickListener( new View.OnClickListener()
	    	{
	    		public void onClick(View v)
	    		{
	    			//pops a Dialog to confirm the user's intent
	    			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
	    			builder.setMessage("This is not actually connect to update and don't have"
	    						+ " the object id yet, but!!\n"
	    						+ "Are you sure that this is not here?");
	    			builder.setCancelable(false);
	    			//confirms the action and perform the update accordingly 
	    			builder.setPositiveButton("Yes! I am sure.", new DialogInterface.OnClickListener() {
	    		           public void onClick(DialogInterface dialog, int id) {
	   		                //sents info to update.php here
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
}  
