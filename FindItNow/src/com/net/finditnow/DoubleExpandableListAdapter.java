package com.net.finditnow;
/***
 * DoubleExpandableListAdapter by Chanel Huang
 * Version 1.0
 * 
 * This is a customer adapter for the building a scrollabel expandable list 
 * of expandalbe to display the floor details of multiple category.
 * 
 * TODO: all the comments in this class need to be updated
 * 
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings.Secure;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView;


import java.util.HashMap;

public class DoubleExpandableListAdapter extends BaseExpandableListAdapter {

	//data for populating the list
	private Context context;			
	private Building building;				
	private String[] categories;
	private HashMap<String,CategoryItem> dataMap;

	/**
	 * Creates a new FloorExpandableListAdapter with each variable initialized
	 * 
	 * @param context - the context in which the list will be displayed
	 * @param floorName - the list of floor names
	 * @param info - the additional information associated
	 * @param iconId - the resource id of the icon of the category
	 * @param category - the category currently displaying
	 */
	public DoubleExpandableListAdapter(Context context,Building building,  String[] categories,HashMap<String,CategoryItem> dataMap) {
		super();
		this.context = context;
		this.building = building;
		this.categories = categories;
		this.dataMap = dataMap;
	}

	/**
	 * Gets the data associated with the given child within the given group.
	 * 
	 * @param groupPosition - the position of the parent view
	 * @param childPosition - the position of the child view
	 * @return the data of the child 
	 */
	public Object getChild(int groupPosition, int childPosition) {
		return dataMap.get(categories[groupPosition]).getInfo().get(childPosition);
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
		return categories.length;
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

		//the layout/view which is defined by a layout XML
		View relative = LayoutInflater.from(context).inflate(R.layout.multi_category_list, parent,false);

		//This is the text for any additional information associated with this
		// particular object
		ExpandableListView lv = (ExpandableListView) relative.findViewById(R.id.cateList);
		
		int iconId = 0;
		String category = categories[childPosition];
		String dbCategory = category;
		String[] parentText = building.getFloorNames();
		String parentMode = "categoryView";

		lv.setAdapter(new FloorExpandableListAdapter( context, dataMap.get(category),
				 iconId,  category,  dbCategory,  parentText,  parentMode) );

		return relative;
	}
	/**
	 * Gets the data associated with the given group.
	 * 
	 * @param groupPosition - the position of the group that contains the child
	 * @return the data child for the specified group 
	 */
	public Object getGroup(int groupPosition) {
		return building.getFloorNames()[groupPosition];
	}

	/**
	 * Gets the number of groups.
	 * 
	 * @return the number of groups
	 */
	public int getGroupCount() {
		return building.getFloorNames().length;
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
		View  relative = LayoutInflater.from(context).inflate(R.layout.flrlist_item, parent,false);

			//Text for displaying the floor name
			TextView text = (TextView) relative.findViewById(R.id.flrName);
			text.setText(building.getFloorNames()[groupPosition]);

			//the icon comes later :)
		
		return relative;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}
