package com.net.finditnow;
/***
 * FloorExpandableListAdapter by Chanel Huang
 * Version 1.0
 * 
 * This is a customer adapter for the building a scrollabel list
 * to display the floor details.
 * 
 */
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Design Principle: inheritance
 *   Inherit most of the methods from BaseExpandableListAdapter. 
 *   BaseExpandableListAdapter is actually implements the interface, ExpandableListAdapter.
 *   However, since not all method defined in ExpandableListAdapter need to be different from
 *   BaseExpandableListAdapter, we decide to inherit the from BaseExpandableListAdapter and only
 *   override those that should have different behavior.
 */
public class FloorExpandableListAdapter extends BaseExpandableListAdapter {

	//data for populating the list
	private CategoryItem catItem;		//information associated w/ this location
	private Context context;
	private ProgressDialog myDialog;
	private String category;		//category
	private String[] parentText;
	private String parentMode;

	private int pos;
	
	private String result;

	/**
	 * Creates a new FloorExpandableListAdapter with each variable initialized
	 * 
	 * @param context - the context in which the list will be displayed
	 * @param floorName - the list of floor names
	 * @param info - the additional information associated
	 * @param iconId - the resource id of the icon of the category
	 * @param category - the category currently displaying
	 */
	public FloorExpandableListAdapter(Context context,CategoryItem catItem,
			String category, String[] parentText, String parentMode) {
		super();
		this.context = context;
		this.catItem = catItem;
		this.category = category;
		this.parentText = parentText;
		this.parentMode = parentMode;
	}

	/**
	 * Gets the data associated with the given child within the given group.
	 * 
	 * @param groupPosition - the position of the parent view
	 * @param childPosition - the position of the child view
	 * @return the data of the child 
	 */
	public Object getChild(int groupPosition, int childPosition) {
		return catItem.getInfo().get(catItem.getFloor_names().indexOf(parentText[groupPosition]));
	}
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}
	/**
	 * Gets the number of children in a specified group.
	 * 
	 * @param groupPosition - the position of the group that contains the child
	 * @return the children count in the specified group 
	 */
	public int getChildrenCount(int groupPosition) {
		//every group (parent) only has 1 child, which is the information display.
		return (category.equals("") ? 0 : 
			(catItem.getFloor_names().contains(parentText[groupPosition]) ? 1 :0  ) );
	}

	/**
	 * Gets a View that displays the data for the given child within the given group.
	 * 
	 * @param groupPosition - the position of the group that contains the child
	 * @param childPosition - the position of the child (for which the View is returned) within the group
	 * @param isLastChild - Whether the child is the last child within the group
	 * @param convertView - the old view to reuse, if possible
	 * @param parent - the parent that this view will eventually be attached to
	 * 
	 * @return the View corresponding to the child at the specified position 
	 */
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		if (catItem.getFloor_names().contains(parentText[groupPosition])){
			pos = catItem.getFloor_names().indexOf(parentText[groupPosition]);

			//the layout/view which is defined by a layout XML
			View relative = LayoutInflater.from(context).inflate(R.layout.flrlist_child, parent,false);
			setChildThemeColors(relative);
			
			//This is the text for any additional information associated with this
			// particular object
			TextView text = (TextView) relative.findViewById(R.id.floorDetailText);
			String specialInfo = (catItem.getInfo().get(pos) != null? catItem.getInfo().get(pos) : "");

			//sets it to the text field
			text.setText(Html.fromHtml(specialInfo));


			//This is the button for reporting something to be missing
			TextView button = (TextView) relative.findViewById(R.id.flrDetailButton);
			button.setOnClickListener( new View.OnClickListener()
			{
				public void onClick(View v)
				{
					//pops a Dialog to confirm the user's intent
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage("Are you sure that this " + category.replace("_", " ").toLowerCase() + " location is not here?");
					builder.setCancelable(false);
					//confirms the action and perform the update accordingly 
					builder.setPositiveButton("Yes! I am sure.", new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog, int id) {
							myDialog = ProgressDialog.show(context, "" , "Reporting as not found...", true);
							Thread thread = new Thread() {
								@Override
								public void run() {
									final String phone_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
									result = DBCommunicator.update(phone_id, catItem.getId().get(pos)+"", context);							
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
								myDialog = ProgressDialog.show(context, "" , "Deleting " + category + "...", true);
								Thread thread = new Thread() {
									@Override
									public void run() {
										final String phone_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
										String result = DBCommunicator.delete(phone_id, catItem.getId().get(0)+"", context);

										Intent myIntent = new Intent(context, FINMap.class);
										myIntent.putExtra("result", result);
										myIntent.putExtra("category", category);
										myIntent.putExtra("building", "");
										
										SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
										String rid = prefs.getInt("rid", 0)+"";

										String locations = DBCommunicator.getLocations(category, rid, 0+"", context);
										myIntent.putExtra("locations", locations);

										context.startActivity(myIntent);

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

			return relative;
		}
		return null;
	}
	/**
	 * Gets the data associated with the given group.
	 * 
	 * @param groupPosition - the position of the group that contains the child
	 * @return the data child for the specified group 
	 */
	public Object getGroup(int groupPosition) {
		return catItem.getFloor_names().get(groupPosition);
	}

	/**
	 * Gets the number of groups.
	 * 
	 * @return the number of groups
	 */
	public int getGroupCount() {
		return parentText.length;
	}

	//The following methods are suppose to be override, but is not
	// of importance here, so they contain no meaningful results
	public long getGroupId(int groupPosition) {
		return 0;
	}
	/**
	 * Gets a View that displays the given group.
	 * @param groupPosition - the position of the group for which the View is returned
	 * @param isExpanded - whether the group is expanded or collapsed
	 * @param convertView - the old view to reuse, if possible. 
	 * @param parent - the parent that this view will eventually be attached to
	 * 
	 * @return the View corresponding to the group at the specified position 
	 */
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		//the layout/view which is defined by a layout XML
		View relative= LayoutInflater.from(context).inflate(R.layout.flrlist_item, parent,false);;
		setItemThemeColors(relative);
		
		ImageView img = (ImageView) relative.findViewById(R.id.flrIcon);

		//Text for displaying the floor name
		TextView text = (TextView) relative.findViewById(R.id.flrName);


		if (parentMode.equals("categoryView")){
			relative.setBackgroundResource(R.color.FIN_secondary);
			text.setText(category);
			text.getLayoutParams().height = DoubleExpandableListAdapter.HEIGHT;
			img.setImageResource(FINHome.getIcon(category, context));
		}
		else{
			text.setText(parentText[groupPosition]);

			//the icon associated with the category
			if (catItem.getFloor_names().contains(parentText[groupPosition])){
				img.setImageResource(FINHome.getIcon(category, context));
			}
		}
		return relative;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(context, result, Toast.LENGTH_LONG).show();
		}
	};
	
	private void setItemThemeColors(View parent) {
		parent.setBackgroundResource(FINTheme.getBrightColor());
	}
	
	private void setChildThemeColors(View parent) {
		View bg = (View) parent.findViewById(R.id.layout_rootflr);
		bg.setBackgroundResource(FINTheme.getLightColor());
		
		TextView button = (TextView) parent.findViewById(R.id.flrDetailButton);
		button.setTextColor(FINTheme.getFontColor());
		button.setBackgroundDrawable(context.getResources().getDrawable(FINTheme.getButtonSelector()));
	}
}
