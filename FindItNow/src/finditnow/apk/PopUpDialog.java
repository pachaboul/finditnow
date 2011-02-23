package finditnow.apk;
/* 
 * This class is a custom dialog for displaying more information regarding a location
 * and the service it provides
 * 
 * TODO: to implement the button for confirming or not confirming a certain location
 * 
 */
import android.widget.TextView;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import java.lang.StringBuffer;


public class PopUpDialog extends Dialog{

	//Local variable for displaying
	private String[] floor;
	private String buildName;
	private String name;
	
	//creates a PopUpDialog
	public PopUpDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	//creates a PopUpDialog with the given fields, should use this one
	public PopUpDialog(Context context,String[] floor, String building, String name)
	{
		super(context);
		this.floor = floor;
		this.buildName = building;
		this.name = name;
	}
	
	//Sets the information on this dialog
	protected void onCreate(Bundle savedInstanceState)
	{

    	setContentView(R.layout.infopopdialog);
    	setTitle(buildName);
    	
    	TextView cate = (TextView) findViewById(R.id.category);
 
    	//Converts the first letter of category to upper case and
    	//adds the name of the service provided if it exist
    	StringBuffer buffer = new StringBuffer();
    	buffer.append(Map.getCategory());    	
    	//char cateName = Character.toUpperCase(buffer.charAt(0));
    	buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
    	if (!(name == null) && !name.equals(""))
    		buffer.append(" : "+ name);
    	
    	//sets the text into the textView
    	cate.setText(buffer.toString());

    	/*TextView seeflr = (TextView) findViewById(R.id.seefloor);
    	seeflr.on*/
    	
    	//there is a button on this dialog, we need it to be clickable
    	Button butt = (Button) findViewById(R.id.flrButton);
    	//so when the user press it, it'll show the detail display
    	butt.setOnClickListener( new View.OnClickListener()
    	{
    		public void onClick(View v)
    		{
    		//	PopUpDialog.this.dismiss();
        		Dialog dialog = new FloorDetailDialog(PopUpDialog.this.getContext(),floor, buildName);
        		dialog.show();
    		}
    	});
    	
	}
}
