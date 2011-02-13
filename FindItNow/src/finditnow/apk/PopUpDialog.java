package finditnow.apk;

import android.widget.ImageView;
import android.widget.TextView;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;


public class PopUpDialog extends Dialog{

	private String[] floor;
	private String category;
	private String buildName;
	
	//creates a PopUpDialog
	public PopUpDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public PopUpDialog(Context context,String[] floor, String category, String building)
	{
		super(context);
		this.floor = floor;
		this.category = category;
		this.buildName = building;
	}
	
	//Sets the information on this dialog
	protected void onCreate(Bundle savedInstanceState)
	{

    	setContentView(R.layout.infopopdialog);
    	setTitle(buildName);

    	TextView cate = (TextView) findViewById(R.id.category);
    	cate.setText(category);

    	/*TextView seeflr = (TextView) findViewById(R.id.seefloor);
    	seeflr.on*/
    	
    	Button butt = (Button) findViewById(R.id.flrButton);
    	
    	butt.setOnClickListener( new View.OnClickListener()
    	{
    		public void onClick(View v)
    		{
    		//	PopUpDialog.this.dismiss();
        		Dialog dialog = new FloorDetailDialog(PopUpDialog.this.getContext(),floor,category);
        		dialog.show();
    		}
    	});
    	
	}
}
