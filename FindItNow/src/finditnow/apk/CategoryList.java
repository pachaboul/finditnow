/*
 * This class provides the intermediate list screen
 * that is displayed for Buildings and Supplies.
 * Selection of any option will launch the map class.
 * 
 * Auto-completion suggestions are enabled.
 */

package finditnow.apk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.android.maps.GeoPoint;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class CategoryList extends ListActivity {
	
	// On launch, determine which category type was passed
	// and display the appropriate list.
	// Defaults to supplies if category is unrecognized.
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	Bundle extras = getIntent().getExtras(); 
    	final String category = extras.getString("category");
    	List<String> list = new ArrayList<String>();
    	
    	// Grab the correct list to show.
    	if (category.equals("buildings")) {
    		Map<GeoPoint, Building> map = Menu.getBuildings();
    		for (GeoPoint point : map.keySet()) {
    			list.add(map.get(point).getName());
    		}
    		Collections.sort(list);
    	} else {
    		list = supplies();
    	}
    	
    	setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, list));
    	
    	ListView lv = getListView();
    	lv.setTextFilterEnabled(true);
    	
    	lv.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    			Intent myIntent = new Intent(v.getContext(), finditnow.apk.Map.class);
    			myIntent.putExtra("category", category);
    			myIntent.putExtra("itemName", ((TextView) v).getText());
    			startActivity(myIntent);
    		}
    	});
    }
    
    // List of buildings we have data collection for.
    private static final String[] BUILDINGS = new String[] {
    	"Electrical Engineering",
    	"Guggenheim Hall",
    	"Kane Hall",
    	"Mary Gates Hall",
    	"Odegaard Undergraduate Library",
    	"Savery Hall",
    	"Sieg Hall",
    	"Smith Hall"
	};
	 
    // List of supplies item types.
	private static ArrayList<String> supplies() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("Blue books");
		list.add("Scantrons");
		return list;
	}
}
