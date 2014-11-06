package com.poiseinteractive.smartexpire;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class RegistrationActivity extends Activity {

	private static final String CREDENTIAL_FILENAME = "credential";
	private static final boolean PIN_OKAY = true;
	private static final boolean PIN_NOT_OKAY = false;
	private static final boolean PASSWORD_OKAY = true;
	private static final boolean PASSWORD_NOT_OKAY = false;

	String userID;

	ImageButton imgButtonBack;
	EditText editTextUserName;
	EditText editTextPin;
	EditText editTextPassword;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);

		editTextUserName = (EditText) findViewById(R.id.edittext_user_name);
		editTextPin = (EditText) findViewById(R.id.edittext_pin);
		editTextPassword = (EditText) findViewById(R.id.edittext_password);

		imgButtonBack = (ImageButton) findViewById(R.id.imagebutton_register_go_back);
		imgButtonBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				setResult(RESULT_CANCELED, i);
				finish();
			}
		});
	}

	public void onRegisterPress(View v) {
		String name = editTextUserName.getText().toString();
		String pin = editTextPin.getText().toString();
		String password = editTextPassword.getText().toString();

		if (name.isEmpty() || pin.isEmpty() || password.isEmpty()) {
			Toast.makeText(getApplicationContext(), "Please fill in all your details.", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (pin.length() != 4) {
			Toast.makeText(getApplicationContext(), "PIN number has to be 4-digit.", Toast.LENGTH_LONG).show();
			return;
		}

		if (passwordCheck(password) == PASSWORD_NOT_OKAY) {
			Toast.makeText(getApplicationContext(), "Your password has been used by another user.\nPlease enter a different password.", Toast.LENGTH_LONG).show();
			return;
		}

		if (pinCheck(pin) == PIN_NOT_OKAY) {
			Toast.makeText(getApplicationContext(), "Your PIN number has been used by another user.\nPlease enter a different PIN number.", Toast.LENGTH_LONG).show();
			return;
		}

		Intent i = new Intent();
		i.putExtra("id", userID);
		i.putExtra("name", name);
		i.putExtra("pin", pin);
		i.putExtra("password", password);
		setResult(RESULT_OK, i);
		finish();
	}

	private boolean passwordCheck(String password) {

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
				if (!password.isEmpty()) {
					for (String user : credentials) {
						String[] details = user.split(";");
						if (details[3].equals(password)) {
							return PASSWORD_NOT_OKAY;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return PASSWORD_OKAY;
	}

	private boolean pinCheck(String pin) {

		userID = "1";

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
				userID = String.valueOf(credentials.length+1);
				if (!pin.isEmpty()) {
					for (String user : credentials) {
						String[] details = user.split(";");
						if (details[2].equals(pin)) {
							return PIN_NOT_OKAY;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return PIN_OKAY;
	}

	private boolean isRegistered() {

		if (isExternalStorageReadable()) {
			File file = getFileStreamPath(CREDENTIAL_FILENAME);
			if (file.exists()) {
				return true;
			}
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
}
