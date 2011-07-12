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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

public class FINMenu extends FINActivity {

	private int cellSize = -1;
	private Context mContext;
	private ProgressDialog myDialog;

	/**
	 * Check for a connection, generate our categories and buildings list
	 * from the database, and set up the grid layout of buttons.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		mContext = this;

		// Populate the grid with category buttons.
		final GridView buttonGrid = (GridView) findViewById(R.id.gridview);

		// Add a listener catch the first instance where the 
		// area alloted to the grid is nonzero.  Once we know,
		// we roughly calculate the imagebutton size.
		ViewTreeObserver observer = buttonGrid.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				int height = buttonGrid.getHeight();
				if (height != 0) {
					int contentSize = height - buttonGrid.getPaddingTop() - buttonGrid.getPaddingBottom();
					int numRows = (FINHome.getCategoriesList().size() + 1) / 2;
					int innerPadding = (numRows - 1) * 8;
					cellSize = (contentSize - innerPadding) / numRows;
					buttonGrid.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					buttonGrid.setAdapter(new ButtonAdapter(mContext));
				}
			}
		});
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
				myView.setBackgroundResource(FINTheme.getLightColor());
			} else {
				myView = convertView;
			}

			if (position < FINHome.getCategoriesList().size()) {
				// Add image button
				ImageButton ib = (ImageButton) myView.findViewById(R.id.grid_item_button);

				final String category = FINHome.getCategoriesList().get(position);
				ib.setImageResource(FINHome.getBigIcon(category));
				
				// Get the DPI of the screen and generate the minimum cell size based on that
				int minCellSize = 55;	
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);
				switch(metrics.densityDpi){
				     case DisplayMetrics.DENSITY_LOW:
				    	 		minCellSize *= 1; // no need to have this, just a formality
				                break;
				     case DisplayMetrics.DENSITY_MEDIUM:
				    	 		 minCellSize *= 1.5;
				                 break;
				     case DisplayMetrics.DENSITY_HIGH:
				    	 		 minCellSize *= 2;
				                 break;
				}
				
				// Math.max() effectively sets a minimum cell size so that the buttons don't become too small
				// TODO: Make not terrible
				ib.getLayoutParams().height = Math.max((int) (cellSize * .74), (int) (minCellSize * .95));
				ib.getLayoutParams().width = Math.max((int) (cellSize * .74), (int) (minCellSize * .95));

				// Otherwise, jump to map
				ib.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (!FINHome.hasItems(category)) {
							myDialog = ProgressDialog.show(FINMenu.this, "" , "Loading " + category + "...", true);
						}
						Thread menuThread = new Thread() {

							@Override
							public void run() {
								Class<? extends Activity> nextClass = (FINHome.hasItems(category)? CategoryList.class : FINMap.class);
								Intent myIntent = new Intent(getBaseContext(), nextClass);

								myIntent.putExtra("category", category);
								myIntent.putExtra("building", "");
								myIntent.putExtra("itemName", "");
								
								if (!FINHome.hasItems(category)) {
									SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
									
									String locations = DBCommunicator.getLocations(category, "", prefs.getInt("campusLat", 0)+"", prefs.getInt("campusLon", 0)+"", getBaseContext());
									myIntent.putExtra("locations", locations);
								}

								startActivity(myIntent);
								if (!FINHome.hasItems(category)) {
									myDialog.dismiss();
								}
							}

						};
						menuThread.start();
					}
				});

				// Add text above button.
				TextView tv = (TextView) myView.findViewById(R.id.grid_item_text);
				tv.setText(category);
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