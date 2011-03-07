// This section here is under construction.  It'll be cleaned up soon.

package com.net.finditnow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FINHelp extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		setTitle("FindItNow > Help Guide");
		
		LinearLayout helpBody = (LinearLayout) findViewById(R.id.help_contents);
		
		// Instructions.
		View instructions = addTextHelpSection("QUESTIONS", String.format(getString(R.string.help_questions)));
		helpBody.addView(instructions);
		
		// Tips
		View tips = addTextHelpSection("TROUBLESHOOTING TIPS", String.format(getString(R.string.help_troubleshooting)));
		helpBody.addView(tips);
		
		// Disclaimers
		View disclaimers = addTextHelpSection("DISCLAIMERS", String.format(getString(R.string.help_disclaimers)));
		helpBody.addView(disclaimers);
		
		// About
		View about = addTextHelpSection("ABOUT", String.format(getString(R.string.help_about)));
		helpBody.addView(about);
	}
	
	/**
	 * Adds a help item to the FINHelp page (a header title and text body)
	 */
	private View addTextHelpSection(String title, String info) {
		LayoutInflater li = getLayoutInflater();
		View section = li.inflate(R.layout.help_item, null);
		
		TextView banner = (TextView) section.findViewById(R.id.help_section_header);
		banner.setText(title);
		
		TextView body = (TextView) section.findViewById(R.id.help_section_body);
		body.setText(Html.fromHtml(info));
		return section;
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.findItem(R.id.add_new_button).setVisible(false);
    	menu.findItem(R.id.my_location_button).setVisible(false);
    	menu.findItem(R.id.help_button).setVisible(false);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.categories_button:
                startActivity(new Intent(this, FINMenu.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
}
