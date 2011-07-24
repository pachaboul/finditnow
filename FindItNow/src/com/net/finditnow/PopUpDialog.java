/***
 * PopUpDialog.java by Chanel Huang
 * current version: 3.5
 * Log:
 * 	March 22 2011:
 * 		begin the work on version 4.0 while providing backward
 * 		compatiablity with the current version.
 * 
 * Version:
 *  version 3.5:
 *  	make the popUp behave differently for buildings category.
 *   	any function present in ver. 3.0 still works properly.
 * 	version 3.0:
 * 		Displays information differently for outdoor location and
 * 		location inside a building.
 * 		For location inside a building:
 * 		a button to show more details about each floor, where
 * 		more information about each floor will be displayed
 *  	as well as a button for reporting something to be not
 *  	there.
 * 		For out door location:
 * 		displays information and a button to report
 * 		something that is not there.
 * 
 * 	version 2.0:
 * 		Still displays information about the location.
 *		 However, unlike version 1, it displays detail about each floor
 * 		in the same dialog box.
 * 
 * 	version 1.0 (beta) :
 * 		A pop up dialog that displays information about the location,
 * 		where clicking on a button will pop up another dialog
 * 		with more detail information about each floor.
 * 
 */
package com.net.finditnow;

//Necessary for using certain methods
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Region;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
/*
 * Design Principle: inheritance
 *   inherit most of the methods from Dialog. We only override those methods that
 *   we need to produce a different behavior from regular Dialog as well as kept
 *   more information as global fields than Dialog.
 */
public class PopUpDialog extends Dialog{

	//Local variable for displaying
	private Building building;
	private double distance;
	private int walkTime;
	private ProgressDialog myDialog;
	private String category;
	private boolean isOutdoor;
	
	private String result;

	//version 3.5 added stuff.
	private HashMap<String,CategoryItem> dataMap;

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
			Building building, String category, String item, HashMap<String,CategoryItem> dataMap, double distance, int walkingTime,
			boolean isOutdoor)
	{
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.building = building;
		this.dataMap = (dataMap == null? new HashMap<String,CategoryItem>() : dataMap);
		this.distance = distance;
		this.walkTime = walkingTime;
		this.category = category;
		this.isOutdoor = isOutdoor;
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
		setThemeColors();
		
		//sets the title of this dialog
		TextView title = (TextView) findViewById(R.id.dialogTitle);

		//the button that 1) displays the list of floor
		//            or  2) unconfirms an outdoor location
		TextView butt = (TextView) findViewById(R.id.showFlrButt);

		// If we're in building mode, hide the unnecessary
		// special info and category fields.
		if (category.equals("")) {
			// Hide outdoor/special info area.
			TextView outDoor = (TextView) findViewById(R.id.outDoorText);
			TextView category = (TextView) findViewById(R.id.categoryName);
			outDoor.setVisibility(View.INVISIBLE);
			outDoor.getLayoutParams().height = 0;
			category.setVisibility(View.INVISIBLE);
			category.getLayoutParams().height = 0;

			// If we're logged in, show the extra "Add Item" button.
			if (FINHome.isLoggedIn()) {
				TextView addItem = (TextView) findViewById(R.id.add_item_button);
				addItem.setVisibility(View.VISIBLE);
				addItem.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent myIntent = new Intent(getContext(), FINAddNew.class);
						myIntent.putExtra("building", building.getName());
						getContext().startActivity(myIntent);
					}
				});
			}
		}

		//the text for displaying information for outdoor
		// has no info is it is indoor
		TextView outDoor = (TextView) findViewById(R.id.outDoorText);

		if ( !isOutdoor) {
			title.setText(building.getName());

			//This location is indoor
			//the button will show/hide the floor list when clicked on
			butt.setOnClickListener( new View.OnClickListener()
			{
				public void onClick(View v)
				{
					ExpandableListView lv = (ExpandableListView) findViewById(R.id.flrList);
					//Button toggle = (Button) findViewById(R.id.showFlrButt);
					TextView toggle = (TextView) findViewById(R.id.showFlrButt);

					// Show all the floor info.
					if (lv.getCount() == 0)
					{
						toggle.setText("Hide Floors");
						lv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;

						String[] floorsWithCategories = new String[0];
						if (dataMap.get(category) != null) {
							floorsWithCategories = dataMap.get(category).getFloor_names().toArray (new String[0]);
						}

						if (floorsWithCategories.length > 3)
							lv.getLayoutParams().height = 250;

						// If we're displaying information by building, and not category
						if (category.equals("")) {

							String[] categories =dataMap.keySet().toArray(new String[0]);

							lv.setAdapter(new DoubleExpandableListAdapter(lv.getContext(),building,categories,dataMap));
							lv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener(){
								public void onGroupExpand(int groupPosition) {
									if (!DoubleExpandableListAdapter.set){
										ExpandableListView lv = (ExpandableListView) findViewById(R.id.flrList);
										DoubleExpandableListAdapter.HEIGHT=lv.getChildAt(0).getHeight()- lv.getDividerHeight();
										DoubleExpandableListAdapter.set = true;
									}
								}
							});
						}
						else{
							//auto scrolls to the item in view into the screen zone
							lv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
								public void onGroupExpand(int groupPosition) {
									ExpandableListView lv = (ExpandableListView) findViewById(R.id.flrList);
									lv.setSelectedGroup(groupPosition);		
								}
							});
							lv.setAdapter(new FloorExpandableListAdapter(lv.getContext(),dataMap.get(category),
									category, floorsWithCategories,"flrName"));
						}
					}
					// Hide all the floor info.
					else {
						toggle.setText("Show Floors");
						lv.getLayoutParams().height = 0;
						lv.setAdapter(new FloorExpandableListAdapter(lv.getContext(),new CategoryItem(),
								category, new String[0],"flrName"));

					}

				}
			});
			//outdoor information is not needed in this case, make
			// it disappear
			outDoor.setVisibility(View.INVISIBLE);
			outDoor.getLayoutParams().height = 0;
		} else {
			title.setText("Outdoor Location");

			String spInfo = dataMap.get(category).getInfo().get(0);

			// If there's no special info, hide the outdoor info section
			// (it would have added unnecessary padding)
			if (spInfo == null || spInfo.equals("")) {
				outDoor.setVisibility(View.INVISIBLE);
				outDoor.getLayoutParams().height = 0;
			} else {
				outDoor.setText(Html.fromHtml(spInfo));
			}

			butt.setText("Nothing Here?");

			butt.setOnClickListener( new View.OnClickListener()
			{
				public void onClick(View v)
				{
					//pops a Dialog to confirm the user's intent
					AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
					builder.setMessage("Are you sure that this " + category.replace("_", " ").toLowerCase() + " location is not here?");
					builder.setCancelable(false);
					//confirms the action and perform the update accordingly 
					builder.setPositiveButton("Yes! I am sure.", new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, int id) {
							myDialog = ProgressDialog.show(getContext(), "" , "Reporting as not found...", true);
							Thread thread = new Thread() {
								@Override
								public void run() {
									final String phone_id = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);
									result = DBCommunicator.update(phone_id, dataMap.get(category).getId().get(0)+"", getContext());	    		        	   
									myDialog.dismiss();
									handler.sendEmptyMessage(0);
								}
							};
							thread.start();
							dialog.dismiss();
						}
					});
					//cancels the action if the user didn't mean to do it
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					if (FINHome.isLoggedIn()) {
						builder.setNeutralButton("Delete", new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface dialog, int id) {
								myDialog = ProgressDialog.show(getContext(), "" , "Deleting " + category + "...", true);
								Thread thread = new Thread() {
									@Override
									public void run() {
										final String phone_id = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);
										String result = DBCommunicator.delete(phone_id, dataMap.get(category).getId().get(0)+"", getContext());

										Intent myIntent = new Intent(getContext(), FINMap.class);
										myIntent.putExtra("result", result);
										myIntent.putExtra("category", category);
										myIntent.putExtra("building", "");
										
										SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
										String rid = prefs.getInt("rid", 0)+"";

										String locations = DBCommunicator.getLocations(category, rid, 0+"", getContext());
										myIntent.putExtra("locations", locations);

										getContext().startActivity(myIntent);

										myDialog.dismiss();
									}
								};
								thread.start();
							}
						});
					}
					AlertDialog dailog = builder.create();
					dailog.show();
				}
			});
		}

		TextView cate = (TextView) findViewById(R.id.categoryName);

		//Converts the first letter of category to upper case and
		//adds the name of the service provided if it exist
		String specialInfo = "<b>" + category + "</b>";

		//sets the text into the textView for category name
		cate.setText(Html.fromHtml(specialInfo));

		//sets the text for displaying the distance and walkingTime
		TextView distText = (TextView) findViewById(R.id.distanceText);
		TextView timeToText = (TextView) findViewById(R.id.timeReachText);
		if (distance == -1)
		{
			//if location of the user is not known, indicated by -1
			//then show that it cannot be calculated.
			distText.setText(Html.fromHtml("<b>Distance to here:</b> Cannot calculate"));
			timeToText.setText(Html.fromHtml("<b>Walking time:</b> Cannot calculate"));
		}
		else
		{
			String dist = String.format("%.1f", distance);
			distText.setText(Html.fromHtml("<b>Distance to here:</b> " + dist + " mi."));
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
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
		}
	};
	
	private void setThemeColors() {
		TextView title = (TextView) findViewById(R.id.dialogTitle);
		title.setTextColor(FINTheme.getFontColor());
		
		TextView button = (TextView) findViewById(R.id.showFlrButt);
		button.setTextColor(FINTheme.getFontColor());
		button.setBackgroundDrawable(getContext().getResources().getDrawable(FINTheme.getButtonSelector()));
		
		button = (TextView) findViewById(R.id.add_item_button);
		button.setTextColor(FINTheme.getFontColor());
		button.setBackgroundDrawable(getContext().getResources().getDrawable(FINTheme.getButtonSelector()));
	}
}  
