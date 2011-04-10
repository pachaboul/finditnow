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
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
		
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		int height = display.getHeight();
		Log.v("screen width", width + "px");
		Log.v("screen height", height + "px");
		
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
			Log.v("getView() call", position + "");
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
				ib.getLayoutParams().height = (int) (cellSize * .74);
				ib.getLayoutParams().width = (int) (cellSize * .74);
				
				// Otherwise, jump to map
				ib.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (!FINHome.hasItems(category)) {
							myDialog = ProgressDialog.show(FINMenu.this, "" , "Loading " + category + " ...", true);
						}
						Thread menuThread = new Thread() {
							
							@Override
							public void run() {
								Class<? extends Activity> nextClass = (FINHome.hasItems(category)? CategoryList.class : FINMap.class);
								Intent myIntent = new Intent(getBaseContext(), nextClass);
								
								myIntent.putExtra("category", category);
								myIntent.putExtra("building", "");
								myIntent.putExtra("itemName", "");
								
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