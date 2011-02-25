package finditnow.apk;
/*
 * This custom dialog displays the details of the services with regards to the floors
 * of buildings.
 * It will display the floor name along with an icon next to the floor.
 * 
 */
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.os.Bundle;

public class FloorDetailDialog extends Dialog {
	
	//constants used for making the list
	private final int TEXT_LEFT = 10;
	private final int TEXT_TOP = 10;
	private final int TEXT_DIFF = 30;
	private final int IMG_LEFT = 150;
	private final int IMG_RIGHT = 20;
	private final int IMG_TOP = 0;
	private final int IMG_DIFF = 30;
	
	//the names of each floor already in order
	private String[] floor;
	//the name of the building
	private String name;
	
	//Creates a FloorDetailDialog
	public FloorDetailDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public FloorDetailDialog(Context context,String[] flr, String n)
	{
		super(context);
		floor = flr;
		name = n;
	}
	//Sets the information on this Dialog
	public void onCreate(Bundle savedInstanceState)
	{

    	super.setContentView(R.layout.floordialog);

    	//this is pre-defined TextView object that will be used to set
    	//the attribute of other TextView object
    	TextView smptext = (TextView) super.findViewById(R.id.floorDetailText);
    	
		RelativeLayout layout = new RelativeLayout(this.getContext());
		layout.setLayoutParams( ((RelativeLayout)findViewById(R.id.layout_rootflr)).getLayoutParams() );
		
		//text.setPadding(left, top, right, bottom)
		
		//Displays each floor name along with an icon
		for (int i = 0; i < floor.length; i++)
		{
			//sets the different property of the text display
			TextView text2 = new TextView(this.getContext());
			text2.setText(floor[i]);
			text2.setPadding(TEXT_LEFT, TEXT_TOP + TEXT_DIFF*i, 0, 0);
			text2.setTextColor(smptext.getTextColors());
			layout.addView(text2);
			
			//sets the different property of the icon display
			ImageView img2 = new ImageView(this.getContext());
			img2.setImageResource(Menu.getIcons().get(Map.getCategory()));
			img2.setAdjustViewBounds(true);
			img2.setMaxHeight(IMG_TOP+IMG_DIFF*(i+1));
			img2.setPadding(IMG_LEFT,IMG_TOP+IMG_DIFF*i , IMG_RIGHT,0);
			layout.addView(img2);
		}
		this.setContentView(layout);
		
		//sets the title of this dialog
		setTitle(name + " Floor Detail");
	}
}
