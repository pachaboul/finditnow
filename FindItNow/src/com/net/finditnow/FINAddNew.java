package com.net.finditnow;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class FINAddNew extends Activity {
	
	private RadioButton rs;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnew_popup);
		
		
		rs = (RadioButton) findViewById(R.id.addnew_in);
		final RadioButton radio_in = (RadioButton) findViewById(R.id.addnew_in);
		final RadioButton radio_out = (RadioButton) findViewById(R.id.addnew_out);
		radio_in.setOnClickListener(radio_listener);
		radio_out.setOnClickListener(radio_listener);
		
		final Button next = (Button) findViewById(R.id.addnew_next);
		final Button cancel1 = (Button) findViewById(R.id.addnew_cancel1);
		next.setOnClickListener(next_listener);
		cancel1.setOnClickListener(cancel_listener);
	}

	private OnClickListener next_listener = new OnClickListener() {
	    public void onClick(View v) {    	
	    	if (rs.getId() == R.id.addnew_in) { //Adding indoor location
	    		Toast.makeText(FINAddNew.this, rs.getText(), Toast.LENGTH_SHORT).show();
	    		setContentView(R.layout.addnew_indoor);
	    	} else if (rs.getId() == R.id.addnew_out) { //Adding outdoor location
	    		Toast.makeText(FINAddNew.this, rs.getText(), Toast.LENGTH_SHORT).show();	
	    	}
	    }
	};
	

	private OnClickListener cancel_listener = new OnClickListener() {
	    public void onClick(View v) {
	    	startActivity(new Intent(v.getContext(), FINMenu.class));
	    }
	};
	
	
	private OnClickListener radio_listener = new OnClickListener() {
	    public void onClick(View v) {
	        rs = (RadioButton) v;
	    }
	};
	
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
