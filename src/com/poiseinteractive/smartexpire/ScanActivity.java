package com.poiseinteractive.smartexpire;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.poiseinteractive.smartexpire.model.Card.Category;
import com.poiseinteractive.smartexpire.model.Card.NotifPeriod;
import com.poiseinteractive.smartexpire.model.Card.Purpose;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class ScanActivity extends Activity {

	private static final int REQUEST_IMAGE_CAPTURE = 100;
	private static final int DATE_DIALOG_ID = 1111;
	private static final String CARD_FILENAME = "cards";

	String userID;
	String cardID;

	ImageButton imgButtonBack;
	EditText editTextCardName;
	Spinner spinnerCategory;
	ImageButton imgButtonPurpose;
	Spinner spinnerNotifPeriod;
	Button buttonExpiry;
	ImageButton imgButtonSave;

	List<String> catArray;
	List<String> notifArray;

	Category chosenCategory;
	Purpose chosenPurpose;
	NotifPeriod chosenNotifPeriod;
	EditText etPickADate;
	
	Calendar cal;
	int mYear;
	int mMonth;
	int mDay;

    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			updateDate();
		}

	};

	// function that is executed when activity is created
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);

		imgButtonBack = (ImageButton) findViewById(R.id.imagebutton_scan_go_back);
		spinnerCategory = (Spinner) findViewById(R.id.spinner_category);
		spinnerNotifPeriod = (Spinner) findViewById(R.id.spinner_notif_period);
		imgButtonPurpose = (ImageButton) findViewById(R.id.imagebutton_purpose);
		editTextCardName = (EditText) findViewById(R.id.textview_card_name);
		buttonExpiry = (Button) findViewById(R.id.button_expiry);
		imgButtonSave = (ImageButton) findViewById(R.id.imagebutton_save);

		// back button
		imgButtonBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				backToHomeScreen();
			}
		});

		userID = getIntent().getStringExtra("user_id");

		// category spinner
		catArray = new ArrayList<String>();
		for (Category cat : Category.values()) {
			catArray.add(cat.toString());
		}
		settingUpSpinner(spinnerCategory, catArray);
		chosenCategory = Category.NONE_OF_THE_ABOVE;

		spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				chosenCategory = Category.values()[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		// notification spinner
		notifArray = new ArrayList<String>();
		for (NotifPeriod notif : NotifPeriod.values()) {
			notifArray.add(notif.toString());
		}
		settingUpSpinner(spinnerNotifPeriod, notifArray);
		chosenNotifPeriod = NotifPeriod.NO_NOTIFICATION;

		spinnerNotifPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				chosenNotifPeriod = NotifPeriod.values()[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		// image button purpose
		imgButtonPurpose.setImageResource(R.drawable.forwork_bool);
		chosenPurpose = Purpose.FOR_WORK;

		imgButtonPurpose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (chosenPurpose == Purpose.FOR_ME) {
					chosenPurpose = Purpose.FOR_WORK;
					imgButtonPurpose.setImageResource(R.drawable.forwork_bool);
				} else {
					chosenPurpose = Purpose.FOR_ME;
					imgButtonPurpose.setImageResource(R.drawable.for_me_bool);
				}
			}
		});

		Calendar cal = Calendar.getInstance();
		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH);
		mDay = cal.get(Calendar.DAY_OF_MONTH);

		// expiry button
		buttonExpiry.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

	}


	// function to create a date picker dialog.
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			/*
			 * return new DatePickerDialog(this, mDateSetListner, mYear, mMonth,
			 * mDay);
			 */
			DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateListener, mYear, mMonth, mDay);
			return datePickerDialog;
		}
		return null;
	}

	// function to set up spinner list with string array
	private void settingUpSpinner(Spinner spinner, List<String> array) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
	}

	// function to go back to the home screen
	private void backToHomeScreen() {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("user_id", userID);
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}

	// function to update date on expiry button after date is picked by user
	protected void updateDate() {
//		int localDay = mDay;
		int localMonth = (mMonth + 1);
//		String dayString = localDay < 10 ? "0" + localDay : Integer
//				.toString(localMonth);
		String monthString = localMonth < 10 ? "0" + localMonth : Integer
				.toString(localMonth);
		String localYear = Integer.toString(mYear).substring(2);
		buttonExpiry.setText(new StringBuilder()
		// Month is 0 based so add 1
		.append(monthString).append("/").append(localYear));
	}

	// function to be executed after user presses imgButtonSave
	public void onPhotoPress(View v) {
		if (editTextCardName.getText().toString().isEmpty()) {
			Toast.makeText(getApplicationContext(), "Please fill in card name", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (buttonExpiry.getText().equals("Expiry")) {
			Toast.makeText(getApplicationContext(), "Please specify the expiry date", Toast.LENGTH_LONG).show();
			return;
		}
		
		cardID = Integer.toString(nextCardImageFile());
		Intent takePictureIntent = new Intent(this, CameraActivity.class);
		takePictureIntent.putExtra("user_id", userID);
		takePictureIntent.putExtra("card_id", cardID);
		startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	}

	//Êfunction to be executed after camera activity finishes its task
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_IMAGE_CAPTURE) {
			if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
				Intent returnIntent = new Intent();
				returnIntent.putExtra("card_id", cardID);
				returnIntent.putExtra("card_name", editTextCardName.getText().toString());
				returnIntent.putExtra("category", chosenCategory);
				returnIntent.putExtra("purpose", chosenPurpose);
				returnIntent.putExtra("notification", chosenNotifPeriod);
				returnIntent.putExtra("expiry", buttonExpiry.getText());
				setResult(RESULT_OK, returnIntent);
				finish();
			}
		}
	}

	// function to determine the next sequence of card image file name.
	private int nextCardImageFile() {

		if (cardFileExists()) {
			try {
				FileInputStream fis = openFileInput(CARD_FILENAME + userID);
				StringBuffer fileContent = new StringBuffer("");
				byte[] buffer = new byte[1024];
				int n;
				while ((n = fis.read(buffer)) != -1) {
					fileContent.append(new String(buffer, 0, n));
				}
				fis.close();
				String str = fileContent.toString();
				String[] cards = str.split("\n");
				return cards.length + 1;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 1;
	}

	// function to determine whether a file that stores data of cards has been created or not
	private boolean cardFileExists() {

		if (isExternalStorageReadable()) {
			File file = getFileStreamPath(CARD_FILENAME + userID);
			if (file.exists()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

//	/* Checks if external storage is available for read and write */
//	private boolean isExternalStorageWritable() {
//		String state = Environment.getExternalStorageState();
//		if (Environment.MEDIA_MOUNTED.equals(state)) {
//			return true;
//		}
//		return false;
//	}

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
