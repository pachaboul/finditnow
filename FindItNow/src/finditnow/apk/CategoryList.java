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
    	"Allen Library",
    	"Anderson Hall",
    	"Architecture Hall",
    	"Art Bldg",
    	"Atmos Sciences - Geophysics",
    	"Bagley Hall",
    	"Balmer Hall",
    	"Bloedel Hall",
    	"Chemistry Bldg",
    	"Clark Hall",
    	"Communications Bldg",
    	"Denny Hall",
    	"Electrical Engineering",
    	"Engineering Annex",
    	"Engineering Library",
    	"Gerberding Hall",
    	"Gowen Hall",
    	"Guggenheim Hall",
    	"Guthrie Hall",
    	"Haggett Hall",
    	"Hansee Hall",
    	"Hutchinson Hall",
    	"Johnson Hall",
    	"Kane Hall",
    	"Loew Hall",
    	"Mary Gates Hall",
    	"McCarty Hall",
    	"McMahon Hall",
    	"Meany Hall",
    	"Mechanical Engineering",
    	"Miller Hall",
    	"More Hall",
    	"Mueller Hall",
    	"Music Bldg",
    	"Odegaard Undergrad. Library",
    	"Padelford Hall",
    	"Parrington Hall",
    	"Paul Allen Center for Comp Sci & Eng",
    	"Physics/Astronomy Auditorium",
    	"Physics/Astronomy Bldg",
    	"Raitt Hall",
    	"Red Square",
    	"Roberts Hall",
    	"Savery Hall",
    	"Sieg Hall",
    	"Smith Hall",
    	"Suzzallo Library",
    	"Thomson Hall",
    	"Wilcox Hall",
    	"William H. Gates Hall",
    	"Winkenwerder Forest Lab"
	};
	 
	 private static final String[] SUPPLIES = {
		 "Blue books",
		 "Scantrons"
	 };
}
