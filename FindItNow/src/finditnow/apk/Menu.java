package finditnow.apk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

public class Menu extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
        GridView buttonGrid = (GridView) findViewById(R.id.gridview);
        buttonGrid.setAdapter(new ButtonAdapter(this));
	}

	// This class/list feeds into the grid view.
	public class ButtonAdapter extends BaseAdapter {
    	private Context mContext;
    	
    	public ButtonAdapter(Context c) {
    		setmContext(c);
    	}

    	public int getCount() {
    		return 8;
    	}

    	public Object getItem(int position) {
    		return null;
    	}

    	public long getItemId(int position) {
    		return 0;
    	}

    	public View getView(int position, View convertView, ViewGroup parent) {
    		View myView = convertView;
			// Define the view that appears in each grid cell.
    		if (convertView == null) {	
    			LayoutInflater li = getLayoutInflater();
    			myView = li.inflate(R.layout.grid_item, null);
    			
    			// add image button
    			ImageButton ib = (ImageButton) myView.findViewById(R.id.grid_item_button);
    			ib.setImageResource(R.drawable.android);
    			
    			 // for some reason it's gotta be 'final' to be passed to the listener ...
    			final String category = categories[position];
    			
    			if (position == 1 || position == 6) {
    				// jump to CategoryList
    				ib.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							Intent myIntent = new Intent(v.getContext(), CategoryList.class);
			                myIntent.putExtra("category", category.toLowerCase());
			                startActivity(myIntent);
						}
	    			});
    			} else {
    				// otherwise jump to map
	    			ib.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							Intent myIntent = new Intent(v.getContext(), Map.class);
			                myIntent.putExtra("category", category.toLowerCase());
			                startActivity(myIntent);
						}
	    			});
    			}
    			
    			// add text
    			TextView tv = (TextView) myView.findViewById(R.id.grid_item_text);
    			tv.setText(categories[position]);
    		}
    		return myView;
    	}

		public void setmContext(Context mContext) {
			this.mContext = mContext;
		}

		public Context getmContext() {
			return mContext;
		}
    }
	
    protected static String[] categories = {
        "ATMs",
        "Buildings",
        "Coffee",
        "Dining",
        "Mailboxes",
        "Restrooms",
        "Supplies",
        "Vending"
    };
}
