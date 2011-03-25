/**
 * This class displays the menu of buttons
 * each corresponding each category.  Simple options will launch the Map;
 * options with sub-categories will launch CategoryList.
 * 
 * Initial database retrieving routines are also executed before
 * the button grid is drawn.  An Internet connection is also checked.
 * 
 * This is the class that is first shown when FIN is
 * launched, after the splash screen.
 */
package com.net.finditnow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

public class FINMenu extends FINActivity {

	private ProgressDialog myDialog;

	/**
	 * Check for a connection, generate our categories and buildings list
	 * from the database, and set up the grid layout of buttons.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);

		// Populate the grid with category buttons.
		GridView buttonGrid = (GridView) findViewById(R.id.gridview);
		buttonGrid.setAdapter(new ButtonAdapter(this));
	}

	@Override
	public void onResume() {
		super.onResume();
		if (myDialog != null) {
			myDialog.dismiss();
		}
	}

	/**
	 * This class populates the grid view.
	 * It is a list of image buttons
	 */
	public class ButtonAdapter extends BaseAdapter {
		private Context mContext;

		/**
		 * Constructor for ButtonAdapter
		 * @param c The context of the client class.
		 */
		public ButtonAdapter(Context c) {
			setmContext(c);
		}

		/**
		 * Returns the amount of items in the list.
		 * Note: if there's an odd amount, we add 1 to make
		 * the bottom row appears to have two colored cells.
		 */
		public int getCount() {
			int size = FINHome.getCategoriesList().size();
			if (size % 2 == 0) {
				return size;
			} else {
				return size + 1;
			}
		}

		/**
		 * Stub method; it doesn't matter what we return.
		 * @param position
		 * @return null (default)
		 */
		public Object getItem(int position) {
			return null;
		}

		/**
		 * Stub method; it doesn't matter what we return.
		 * @param position
		 * @return 0 (default)
		 */
		public long getItemId(int position) {
			return 0;
		}

		/**
		 * Returns the class's own context
		 * @return Returns the class's private context
		 */
		public Context getmContext() {
			return mContext;
		}

		/**
		 * Sets up the view shown in each grid cell:
		 * The image button and the text displayed on top
		 * @param position The index of the button
		 * @param convertView The view to be returned
		 * @param parent The parent ViewGroup that will house the view.
		 * @return The generated view for this position.
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			View myView;

			// If not created yet, initialize it.
			if (convertView == null) {	
				LayoutInflater li = getLayoutInflater();
				myView = li.inflate(R.layout.grid_item, null);
			} else {
				myView = convertView;
			}

			if (position < FINHome.getCategoriesList().size()) {
				// Add image button
				ImageButton ib = (ImageButton) myView.findViewById(R.id.grid_item_button);

				final String category = FINHome.getCategoriesList().get(position);
				ib.setImageResource(FINHome.getBigIcon(category));
				// Otherwise, jump to map
				ib.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (!category.equals("school_supplies")) {
							myDialog = ProgressDialog.show(FINMenu.this, "Category Loading" , "Loading " + FINUtil.capFirstChar(category) + "...", true);
						}

						Class<? extends Activity> nextClass = (category.equals("school_supplies")? CategoryList.class : FINMap.class);
						Intent myIntent = new Intent(v.getContext(), nextClass);
						myIntent.putExtra("category", category);
						myIntent.putExtra("building", "");
						myIntent.putExtra("itemName", "");
						startActivity(myIntent);
					}
				});

				// Add text above button.
				TextView tv = (TextView) myView.findViewById(R.id.grid_item_text);
				tv.setText(FINUtil.capFirstChar(category));
			} else {
				// This is just a blank placeholder cell, so hide the button.
				ImageButton ib = (ImageButton) myView.findViewById(R.id.grid_item_button);
				ib.setVisibility(View.INVISIBLE);
			}
			return myView;
		}

		/**
		 * Sets the passed context to be our own context
		 * @param mContext
		 */
		public void setmContext(Context mContext) {
			this.mContext = mContext;
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);		
		menu.findItem(R.id.home_button).setVisible(false);

		return true;
	}
}