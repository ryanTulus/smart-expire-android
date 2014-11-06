package com.poiseinteractive.smartexpire;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends Activity {

	private enum LoginMethod {
		PIN,
		PASSWORD
	}

	private static final int REGISTRATION_REQUEST_CODE = 300;
	private static final String CREDENTIAL_FILENAME = "credential";

	LoginMethod loginMethod;
	String fullPath;
	File appDir;
	
	String userID;
	String userName;
	
	// register button
	ImageButton buttonRegister;

	// password
	ImageButton imgButtonPassword;
	EditText inputBarPassword;

	// pin
	ImageButton imgButtonPin;
	LinearLayout inputBarPin;
	TableLayout keypad;

	String userEnteredPin;

	final int PIN_LENGTH = 4;

	TextView pinBox0;
	TextView pinBox1;
	TextView pinBox2;
	TextView pinBox3;

	TextView [] pinBoxArray;

	Button button0;
	Button button1;
	Button button2;
	Button button3;
	Button button4;
	Button button5;
	Button button6;
	Button button7;
	Button button8;
	Button button9;
	Button button10;
	Button buttonExit;
	Button buttonDelete;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		// views
		buttonRegister = (ImageButton) findViewById(R.id.button_register);

		inputBarPin = (LinearLayout) findViewById(R.id.input_bar_pin);
		keypad = (TableLayout) findViewById(R.id.numericPad);
		imgButtonPin = (ImageButton) findViewById(R.id.imagebutton_pin);

		inputBarPassword = (EditText) findViewById(R.id.input_bar_password);
		imgButtonPassword = (ImageButton) findViewById(R.id.imagebutton_password);

		// register button
		buttonRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				executeAccountRegistration();
			}
		});

		// password input setup
		imgButtonPassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				imgButtonPin.setImageResource(R.drawable.pin_button);
				inputBarPin.setVisibility(View.GONE);
				keypad.setVisibility(View.GONE);

				imgButtonPassword.setImageResource(R.drawable.password_button_select);
				inputBarPassword.setVisibility(View.VISIBLE);
				loginMethod = LoginMethod.PASSWORD;
			}
		});
		
		inputBarPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_GO) {
					hideKeyboard();
					authenticatePassword(v.getText().toString());
					handled = true;
				}
				return handled;
			}
		});

		
		// pin input setup
		imgButtonPin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideKeyboard();
				imgButtonPassword.setImageResource(R.drawable.password_button);
				inputBarPassword.setVisibility(View.GONE);
				
				imgButtonPin.setImageResource(R.drawable.pin_button_select);
				inputBarPin.setVisibility(View.VISIBLE);
				loginMethod = LoginMethod.PIN;
				
			}
		});
		
		userEnteredPin = "";
		pinBox0 = (TextView)findViewById(R.id.pinBox0);
		pinBox1 = (TextView)findViewById(R.id.pinBox1);
		pinBox2 = (TextView)findViewById(R.id.pinBox2);
		pinBox3 = (TextView)findViewById(R.id.pinBox3);

		pinBoxArray = new TextView[PIN_LENGTH];
		pinBoxArray[0] = pinBox0;
		pinBoxArray[1] = pinBox1;
		pinBoxArray[2] = pinBox2;
		pinBoxArray[3] = pinBox3;

		View.OnClickListener pinBoxHandler = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				keypad.setVisibility(View.VISIBLE);
			}
		};
		
		for (int i = 0; i < PIN_LENGTH; i++) {
			pinBoxArray[i].setOnClickListener(pinBoxHandler);
		}
		
		View.OnClickListener pinButtonHandler = new View.OnClickListener() {
			public void onClick(View v) {

				Button pressedButton = (Button) v;

				if (userEnteredPin.length()<PIN_LENGTH) {
					userEnteredPin = userEnteredPin + pressedButton.getText();
					Log.v("PinView", "User entered="+userEnteredPin);

					//Update pin boxes
					pinBoxArray[userEnteredPin.length()-1].setText("8");

					if (userEnteredPin.length()== PIN_LENGTH) {
						//Check if entered PIN is correct
						authenticatePIN(userEnteredPin);
					}
				}
			}
		};

		button0 = (Button)findViewById(R.id.button0);
		button0.setOnClickListener(pinButtonHandler);

		button1 = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(pinButtonHandler);

		button2 = (Button)findViewById(R.id.button2);
		button2.setOnClickListener(pinButtonHandler);

		button3 = (Button)findViewById(R.id.button3);
		button3.setOnClickListener(pinButtonHandler);

		button4 = (Button)findViewById(R.id.button4);
		button4.setOnClickListener(pinButtonHandler);

		button5 = (Button)findViewById(R.id.button5);
		button5.setOnClickListener(pinButtonHandler);

		button6 = (Button)findViewById(R.id.button6);
		button6.setOnClickListener(pinButtonHandler);

		button7 = (Button)findViewById(R.id.button7);
		button7.setOnClickListener(pinButtonHandler);

		button8 = (Button)findViewById(R.id.button8);
		button8.setOnClickListener(pinButtonHandler);

		button9 = (Button)findViewById(R.id.button9);
		button9.setOnClickListener(pinButtonHandler);

		buttonDelete = (Button) findViewById(R.id.buttonDeleteBack);
		buttonDelete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (userEnteredPin.length()>0) {
					userEnteredPin = userEnteredPin.substring(0,userEnteredPin.length()-1);
					pinBoxArray[userEnteredPin.length()].setText("");
				}
			}
		});
		

		// set initial states
		setInitialState();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REGISTRATION_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String id = data.getStringExtra("id");
				String name = data.getStringExtra("name");
				String pin = data.getStringExtra("pin");
				String password = data.getStringExtra("password");

				storeLoginCredentials(id, name, pin, password);
				Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		//check if no view has focus:
		View v = this.getCurrentFocus();
		if(v == null)
			return;

		inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	protected void executeAccountRegistration() {
		Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
		startActivityForResult(i, REGISTRATION_REQUEST_CODE);
	}

	private void storeLoginCredentials(String id, String name, String pin, String password) {
		String str = id + ";" + name + ";" + pin + ";" + password + "\n";
		try {
			if (isExternalStorageWritable()) {
				FileOutputStream fos = openFileOutput(CREDENTIAL_FILENAME, Context.MODE_APPEND);
				fos.write(str.getBytes());
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Checks if external storage is available for read and write */
	private boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	private boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}

	private boolean isRegistered() {
		
		if (isExternalStorageReadable()) {
			File file = getFileStreamPath(CREDENTIAL_FILENAME);
			if (file.exists()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	private void authenticatePIN(String pin) {
		if (isRegistered()) {
			try {
				FileInputStream fis = openFileInput(CREDENTIAL_FILENAME);
				StringBuffer fileContent = new StringBuffer("");
				byte[] buffer = new byte[fis.available()];
				int n;
				while ((n = fis.read(buffer)) != -1) {
					fileContent.append(new String(buffer, 0, n));
				}
				String str = fileContent.toString();
				String[] credentials = str.split("\n");
				for (String user : credentials) {
					String[] details = user.split(";");
					if (details[2].equals(pin)) {
						userID = details[0];
						userName = details[1];
						
						loadHomeScreen(userID);
						return;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			Toast.makeText(getApplicationContext(), "Wrong PIN, please try again.", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(), "No login credentials found. Please register.", Toast.LENGTH_LONG).show();
		}
		userEnteredPin = "";
		
		for (int i = 0; i < PIN_LENGTH; i++) {
			pinBoxArray[i].setText("");
		}
	}
	

	private void authenticatePassword(String password) {
		if (isRegistered()) {
			try {
				FileInputStream fis = openFileInput(CREDENTIAL_FILENAME);
				StringBuffer fileContent = new StringBuffer("");
				byte[] buffer = new byte[fis.available()];
				int n;
				while ((n = fis.read(buffer)) != -1) {
					fileContent.append(new String(buffer, 0, n));
				}
				String str = fileContent.toString();
				String[] credentials = str.split("\n");
				for (String user : credentials) {
					String[] details = user.split(";");
					if (details[3].equals(password)) {
						userID = details[0];
						userName = details[1];
						
						loadHomeScreen(userID);
						return;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			Toast.makeText(getApplicationContext(), "Wrong password, please try again.", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(), "No login credentials found. Please register.", Toast.LENGTH_LONG).show();
		}
		inputBarPassword.setText("");
	}
	
	
	
	private void loadHomeScreen(String id) {
		Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
		intent.putExtra("user_id", id);
		startActivity(intent);
		finish();
	}

	private void setInitialState() {
		userEnteredPin = "";
		userID = "";
		userName = "";
		
		for (int i = 0; i < PIN_LENGTH; i++) {
			pinBoxArray[i].setText("");
		}
		
		inputBarPassword.setText("");
		
		loginMethod = LoginMethod.PIN;
		inputBarPassword.setVisibility(View.GONE);
		imgButtonPassword.setImageResource(R.drawable.password_button);

		imgButtonPin.setImageResource(R.drawable.pin_button_select);
		inputBarPin.setVisibility(View.VISIBLE);
		keypad.setVisibility(View.GONE);
	}

	
	
}