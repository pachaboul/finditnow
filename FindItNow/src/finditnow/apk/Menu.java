package finditnow.apk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
		// They all jump to the map for now
		Button buildings = (Button) findViewById(R.id.BuildingsButton);
		onClickToCategory(buildings, "building");
		
		Button vending = (Button) findViewById(R.id.VendingButton);
		onClickToCategory(vending, "vending");
		
		Button restrooms = (Button) findViewById(R.id.RestroomsButton);
		onClickToCategory(restrooms, "restrooms");
		
		Button coffee = (Button) findViewById(R.id.CoffeeButton);
		onClickToCategory(coffee, "coffee");
		
		Button recycling = (Button) findViewById(R.id.RecyclingButton);
		onClickToCategory(recycling, "recycling");
		
		Button dining = (Button) findViewById(R.id.DiningButton);
		onClickToCategory(dining, "dining");
		
		Button supplies = (Button) findViewById(R.id.SuppliesButton);
		onClickToCategory(supplies, "supplies");
		
		Button atms = (Button) findViewById(R.id.ATMButton);
		onClickToCategory(atms, "ATMs");

	}
	
	// Adds a click listener to a button, jumps to Map class.
	// (maybe there's a better way to pass the category string within the inner function.
	// Write a different listener?)
	private void onClickToCategory(Button b, String category) {
		b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Map.class);
                // myIntent.putExtra("category", category);
                startActivityForResult(myIntent, 0);
            }
        });
	}
	/*
	private void onClickToList(Button b, String category) {
		b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TypeList.class);
                // myIntent.putExtra("category", category);
                startActivityForResult(myIntent, 0);
            }
        });  
	}
	*/
}
