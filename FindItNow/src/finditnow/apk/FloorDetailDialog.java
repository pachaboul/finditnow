package finditnow.apk;

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
	
	private String[] floor;
	private String category;
	
	//Creates a FloorDetailDialog
	public FloorDetailDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public FloorDetailDialog(Context context,String[] flr, String cat)
	{
		super(context);
		floor = flr;
		category = cat;
	}
	//Sets the information on this Dialog
	public void onCreate(Bundle savedInstanceState)
	{

    	super.setContentView(R.layout.floordialog);

    	TextView smptext = (TextView) super.findViewById(R.id.floorDetailText);
    	
		RelativeLayout layout = new RelativeLayout(this.getContext());
		layout.setLayoutParams( ((RelativeLayout)findViewById(R.id.layout_rootflr)).getLayoutParams() );
		
		//text.setPadding(left, top, right, bottom)
		

		for (int i = 0; i < floor.length; i++)
		{
			TextView text2 = new TextView(this.getContext());
			text2.setText(floor[i]);
			text2.setPadding(TEXT_LEFT, TEXT_TOP + TEXT_DIFF*i, 0, 0);
			text2.setTextColor(smptext.getTextColors());
			layout.addView(text2);
			
			ImageView img2 = new ImageView(this.getContext());
			setImgResource(img2,category);
			img2.setPadding(IMG_LEFT, IMG_TOP+IMG_DIFF*i, IMG_RIGHT,0);
			layout.addView(img2);
		}
		this.setContentView(layout);
		setTitle("Floor Dialog");
	}
	//This will contain a list of hard coded image file resource
	// and set it depending on the category being displayed.
	private void setImgResource(ImageView img, String imgName)
	{
		if (imgName.equals("recycle"))
			img.setImageResource(R.drawable.recycle_bin);
	}
}
