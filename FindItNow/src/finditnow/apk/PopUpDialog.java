package finditnow.apk;

import android.widget.TextView;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import java.lang.StringBuffer;


public class PopUpDialog extends Dialog{

	private String[] floor;
	private String buildName;
	private String name;
	
	//creates a PopUpDialog
	public PopUpDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
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
    	
    	StringBuffer buffer = new StringBuffer();
    	buffer.append(Map.getCategory());    	
    	//char cateName = Character.toUpperCase(buffer.charAt(0));
    	buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
    	if (!(name == null) && !name.equals(""))
    		buffer.append(" : "+ name);
    	
    	cate.setText(buffer.toString());

    	/*TextView seeflr = (TextView) findViewById(R.id.seefloor);
    	seeflr.on*/
    	
    	Button butt = (Button) findViewById(R.id.flrButton);
    	
    	butt.setOnClickListener( new View.OnClickListener()
    	{
    		public void onClick(View v)
    		{
    		//	PopUpDialog.this.dismiss();
        		Dialog dialog = new FloorDetailDialog(PopUpDialog.this.getContext(),floor);
        		dialog.show();
    		}
    	});
    	
	}
}
