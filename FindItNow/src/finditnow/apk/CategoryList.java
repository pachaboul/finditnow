package finditnow.apk;

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
	
    public void onCreate(Bundle savedInstanceState) {
    	  super.onCreate(savedInstanceState);

    	  Bundle extras = getIntent().getExtras(); 
    	  final String category = extras.getString("category");
    	  String[] data;
    	  
    	  // Grab the correct list to show.
    	  if (category.equals("buildings")) {
    		  data = BUILDINGS;
    	  } else {
    		  data = SUPPLIES;
    	  }
    	  
    	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, data));

    	  ListView lv = getListView();
    	  lv.setTextFilterEnabled(true);

    	  lv.setOnItemClickListener(new OnItemClickListener() {
    		  public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    			  Intent myIntent = new Intent(v.getContext(), Map.class);
    			  myIntent.putExtra("category", category);
    			  myIntent.putExtra("itemName", ((TextView) v).getText());
    			  startActivity(myIntent);
    		  }
    	  });
    	}
    
    // Contains priority 1,2 buildings for now.
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
	 
	 private static final String[] SUPPLIES = {
		 "Blue books",
		 "Scantrons"
	 };
}
