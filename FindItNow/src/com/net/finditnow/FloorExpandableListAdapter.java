package com.net.finditnow;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.view.Window;
import android.view.MotionEvent;
import android.text.Html;
import android.util.Log;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.view.LayoutInflater;

import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.StringBuffer;
import java.math.BigDecimal;



public class FloorExpandableListAdapter extends BaseExpandableListAdapter {
	private String[] floorNames;
	private String info;
	private Context context;
	private int iconId;
	
	public FloorExpandableListAdapter(Context context,String[] floorName, String info,
			int iconId) {
		super();
		this.context = context;
		this.floorNames = floorName;
		this.info = info;
		this.iconId = iconId;
	}

	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return info;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		View relative = LayoutInflater.from(context).inflate(R.layout.flrlist_child, parent,false);

		TextView text = (TextView) relative.findViewById(R.id.floorDetailText);
		
		String specialInfo = info.replace("\n", "<br />");
		//specialInfo += (": " + info);
		
		text.setText(Html.fromHtml(specialInfo));
		
		Button button = (Button) relative.findViewById(R.id.flrDetailButton);
		button.setOnClickListener( new View.OnClickListener()
    	{
    		public void onClick(View v)
    		{
    			AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
		
		return relative;
	}

	//every group (parent) only has 1 child, which is the information display.
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	public Object getGroup(int groupPosition) {
		return floorNames[groupPosition];
	}

	public int getGroupCount() {
		return floorNames.length;
	}


	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
			
		View relative = LayoutInflater.from(context).inflate(R.layout.flrlist_item, parent,false);
				
		TextView text = (TextView) relative.findViewById(R.id.flrName);
		Log.i("log", groupPosition+ "   "+ (text == null) );
		text.setText(floorNames[groupPosition]);
		
		ImageView img = (ImageView) relative.findViewById(R.id.flrIcon);
		img.setImageResource(iconId);
		
		return relative;
	}

	//The following methods are suppose to be override, but is not
	// of importance here, so they contain no meaningful results
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}



}
