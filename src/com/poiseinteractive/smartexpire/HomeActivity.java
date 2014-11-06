package com.poiseinteractive.smartexpire;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.poiseinteractive.smartexpire.model.Card.Category;
import com.poiseinteractive.smartexpire.model.Card.NotifPeriod;
import com.poiseinteractive.smartexpire.model.Card.Purpose;
import com.poiseinteractive.smartexpire.model.CardListAdapter;
import com.poiseinteractive.smartexpire.model.Card;
import com.tyczj.extendedcalendarview.CalendarProvider;
import com.tyczj.extendedcalendarview.Event;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	private int NEW_CARD_CODE = 200;
	private static final String CARD_FILENAME = "cards";

	String userID;

	EditText cardSearchEditText;
	TextWatcher cardSearchTextWatcher;
	TextView expiriesCountTextView;
	ListView cardListView;
	CardListAdapter cardListAdapter;

	ArrayList<Card> allCards;
	ArrayList<Card> cards;
	int expiriesCount = 0;

	ImageButton slideToScan;
	ImageButton slideToCalendar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		cardSearchEditText = (EditText) findViewById(R.id.search);
		expiriesCountTextView = (TextView) findViewById(R.id.expiries_count);
		cardListView = (ListView) findViewById(R.id.card_list);
		slideToScan = (ImageButton) findViewById(R.id.slide_to_scan);
		slideToCalendar = (ImageButton) findViewById(R.id.slide_to_calendar);

		// setting up empty listview
		allCards = new ArrayList<Card>();
		cards = new ArrayList<Card>();
		cardListAdapter = new CardListAdapter(HomeActivity.this, cards);
		cardListView.setAdapter(cardListAdapter);
		cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Card card = cards.get(position);
				Intent intent = new Intent(HomeActivity.this, CardDetailActivity.class);
				intent.putExtra("user_id", userID);
				intent.putExtra("card_id", card.getId());
				Log.d("cardID", card.getId());
				intent.putExtra("card_label", card.getLabel());
				intent.putExtra("category", card.getCategory().toString());
				intent.putExtra("notif_period", card.getNotifPeriod().toString());
				intent.putExtra("expiry", card.getExpiryString());
				startActivity(intent);
			}
		});

		// user id
		userID = getIntent().getStringExtra("user_id");

		// adding up cards data
		loadExistingCards();

		// text watcher for card search
		cardSearchTextWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				search(s.toString());
			}

			@Override
			public void afterTextChanged(Editable s) {
				search(s.toString());
			}
		};

		cardSearchEditText.addTextChangedListener(cardSearchTextWatcher);
		cardSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					search(v.getText().toString());
					hideKeyboard();
					handled = true;
				}
				return handled;
			}
		});

		search(cardSearchEditText.getText().toString());

		// button listeners
		slideToScan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this, ScanActivity.class);
				i.putExtra("user_id", userID);
				startActivityForResult(i, NEW_CARD_CODE);
			}
		});

		slideToCalendar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this, CalendarActivity.class);
				i.putExtra("user_id", userID);
				startActivity(i);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == NEW_CARD_CODE) {
			if (resultCode == RESULT_OK) {
				addNewCard(data);
			}
		}
	}

	private void addNewCard(Intent data) {
		String cardID = data.getStringExtra("card_id");
		String cardName = data.getStringExtra("card_name");
		Category category = (Category) data.getSerializableExtra("category");
		Purpose purpose = (Purpose) data.getSerializableExtra("purpose");
		NotifPeriod notifPeriod = (NotifPeriod) data.getSerializableExtra("notification");
		String expiry = data.getStringExtra("expiry");

		Card card = new Card(cardID);
		card.setLabel(cardName);
		card.setCategory(category);
		card.setPurpose(purpose);
		card.setNotifPeriod(notifPeriod);
		card.setExpiryDate(expiry);

		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTime(card.getExpiryDate());
//		if (card.getNotifPeriod() == NotifPeriod.ONE_WEEK) {
//			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)-7);
//			
//		} else if (card.getNotifPeriod() == NotifPeriod.TWO_WEEKS) {
//			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)-14);
//
//		} else if (card.getNotifPeriod() == NotifPeriod.ONE_MONTH) {
//			c.set(Calendar.MONTH, c.get(Calendar.MONTH)-1);
//
//		} else if (card.getNotifPeriod() == NotifPeriod.TWO_MONTHS) {
//			c.set(Calendar.MONTH, c.get(Calendar.MONTH)-2);
//
//		} else if (card.getNotifPeriod() == NotifPeriod.THREE_MONTHS) {
//			c.set(Calendar.MONTH, c.get(Calendar.MONTH)-3);
//			
//		}
		if (card.getNotifPeriod() != NotifPeriod.NO_NOTIFICATION) {
			pushAppointmentsToCalender(this, card.getLabel()+" expiry", "", c.getTimeInMillis(), card.getNotifPeriod());
		
		}
		// adding events to extended calendar view
		ContentValues values = new ContentValues();
		values.put(CalendarProvider.COLOR, Event.DEFAULT_EVENT_ICON);
		values.put(CalendarProvider.DESCRIPTION, card.getLabel() + " expiry");
		values.put(CalendarProvider.EVENT, card.getLabel()+" expiry");

		Calendar cal = Calendar.getInstance();
		TimeZone tz = TimeZone.getDefault();

		cal.setTime(card.getExpiryDate());
		values.put(CalendarProvider.START, cal.getTimeInMillis());
		values.put(CalendarProvider.START_DAY, Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis()))));

		cal.setTime(card.getExpiryDate());
		int endDayJulian = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));

		values.put(CalendarProvider.END, cal.getTimeInMillis());
		values.put(CalendarProvider.END_DAY, endDayJulian);

		this.getContentResolver().insert(CalendarProvider.CONTENT_URI, values);
		
		
		
		allCards.add(card);
		cardSearchEditText.setText("");
		search(cardSearchEditText.getText().toString());

		// append card to external file
		FileOutputStream fos;
		String out = cardID + ";" + cardName + ";" + category.toString() + ";" + purpose.toString() + ";" 
				+ notifPeriod.toString() + ";" + expiry + "\n";

		try {	
			if (isExternalStorageWritable()) {
				fos = openFileOutput(CARD_FILENAME + userID, MODE_APPEND);
				fos.write(out.getBytes());
				fos.flush();
				fos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadExistingCards() {
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
				for (String s : cards) {
					String[] cardDetails = s.split(";");
					Card card = new Card(cardDetails[0]);
					card.setLabel(cardDetails[1]);
					card.setCategory(Category.fromString(cardDetails[2]));
					card.setPurpose(Purpose.fromString(cardDetails[3]));
					card.setNotifPeriod(NotifPeriod.fromString(cardDetails[4]));
					card.setExpiryDate(cardDetails[5]);
					allCards.add(card);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		//check if no view has focus:
		View v=this.getCurrentFocus();
		if(v==null)
			return;

		inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@SuppressLint("DefaultLocale")
	private void search(String keyword) {
		cards.clear();
		if (keyword.isEmpty()) {
			cards.addAll(allCards);
		} else {
			for (Card c : allCards) {
				if (c.getLabel().toLowerCase().contains(keyword.toLowerCase())) {
					cards.add(c);
				}
			}
		}

		expiriesCount = 0;
		Date now = new Date();
		for (Card c : cards) {
			if (now.after(c.getExpiryDate())) {
				expiriesCount++;
			}
		}
		expiriesCountTextView.setText(Integer.toString(expiriesCount));
		cardListAdapter.notifyDataSetChanged();
	}

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

	public static long pushAppointmentsToCalender(Activity curActivity, String title, String addInfo, long startDate, NotifPeriod np) {
		/***************** Event: note(without alert) *******************/

		String eventUriString = "content://com.android.calendar/events";
		ContentValues eventValues = new ContentValues();

		eventValues.put("calendar_id", 1); // id, We need to choose from
		// our mobile for primary
		// its 1
		eventValues.put("title", title);
		eventValues.put("description", addInfo);
		//		    eventValues.put("eventLocation", place);

		//		long endDate = startDate + 1000 * 60 * 60 * 24 * 365; // For next 1year

		//		if (startDate == endDate)
		//		{
		//			endDate += 21600000;   
		//		}else if (endDate < startDate)
		//		{
		//			startDate += 21600000;
		//		}
		eventValues.put("dtstart", startDate);
		eventValues.put("dtend", startDate);

		// values.put("allDay", 1); //If it is bithday alarm or such
		// kind (which should remind me for whole day) 0 for false, 1
		// for true
		eventValues.put("eventStatus", 0); // This information is
		// sufficient for most
		// entries tentative (0),
		// confirmed (1) or canceled
		// (2):
		//		eventValues.put("visibility", 3); // visibility to default (0),   /// Not in new Api
		// confidential (1), private
		// (2), or public (3):
		//		eventValues.put("transparency", 0); // You can control whether    /// Not in new Api
		// an event consumes time
		// opaque (0) or transparent
		// (1).
		eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

		eventValues.put("eventTimezone", TimeZone.getDefault().getID());

		Uri eventUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
		long eventID = Long.parseLong(eventUri.getLastPathSegment());

		boolean needReminder = true;
		if (needReminder) {
			/***************** Event: Reminder(with alert) Adding reminder to event *******************/

			String reminderUriString = "content://com.android.calendar/reminders";

			ContentValues reminderValues = new ContentValues();

			reminderValues.put("event_id", eventID);
			
			int minutes = 0;
			if (np == NotifPeriod.ONE_WEEK) {
				minutes = 7*24*60;
			} else if (np == NotifPeriod.TWO_WEEKS) {
				minutes = 14*24*60;
			} else if (np == NotifPeriod.ONE_MONTH) {
				minutes = 30*24*60;
			} else if (np == NotifPeriod.TWO_MONTHS) {
				minutes = 60*24*60;
			} else if (np == NotifPeriod.THREE_MONTHS) {
				minutes = 90*24*60;
			} 
			
			reminderValues.put("minutes", minutes); // Default value of the
			// system. Minutes is a
			// integer
			reminderValues.put("method", 1); // Alert Methods: Default(0),
			// Alert(1), Email(2),
			// SMS(3)

			curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
		}

		return eventID;

	}


}
