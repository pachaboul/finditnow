package com.net.finditnow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FINLogin extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// load up the layout
		setContentView(R.layout.login);

		// get the button resource in the xml file and assign it to a local variable of type Button
		Button launch = (Button)findViewById(R.id.login_button);

		// this is the action listener
		OnClickListener listener = new OnClickListener() {

			public void onClick(View viewParam) {
				// this gets the resources in the xml file and assigns it to a local variable of type EditText
				EditText usernameEditText = (EditText) findViewById(R.id.txt_username);
				EditText passwordEditText = (EditText) findViewById(R.id.txt_password);

				// the getText() gets the current value of the text box
				// the toString() converts the value to String data type
				// then assigns it to a variable of type String
				String sUserName = usernameEditText.getText().toString();
				String sPassword = passwordEditText.getText().toString();

				// this just catches the error if the program cant locate the GUI stuff
				AlertDialog.Builder builder = new AlertDialog.Builder(FINLogin.this);

				if (usernameEditText == null || passwordEditText == null) {
					builder.setMessage("Oops, fail");
				} else {
					// display the username and the password in string format
					builder.setMessage("Yay!");
				}

				AlertDialog alert = builder.create();
				alert.show();
			}
		};

		launch.setOnClickListener(listener);
	}
}