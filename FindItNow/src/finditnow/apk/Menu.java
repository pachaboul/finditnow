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
    	
        private String[] mButtonText = {
            "ATMs",
            "Buildings",
            "Coffee",
            "Dining",
            "Recycling",
            "Restrooms",
            "Supplies",
            "Vending"
        };
    	
    	public ButtonAdapter(Context c) {
    		mContext = c;
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
    			// inflate the layout
    			LayoutInflater li = getLayoutInflater();
    			myView = li.inflate(R.layout.grid_item, null);
    			
    			// add image button
    			ImageButton ib = (ImageButton) myView.findViewById(R.id.grid_item_button);
    			ib.setImageResource(R.drawable.android);
    			ib.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent myIntent = new Intent(v.getContext(), Map.class);
		                // myIntent.putExtra("category", category);
		                startActivity(myIntent);
					}
    			});
    			
    			// add text
    			TextView tv = (TextView) myView.findViewById(R.id.grid_item_text);
    			tv.setText(mButtonText[position]);
    		}
    		return myView;
    	}
    }
}
