package com.poiseinteractive.smartexpire;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.poiseinteractive.smartexpire.model.Card;
import com.poiseinteractive.smartexpire.model.Card.Category;
import com.poiseinteractive.smartexpire.model.Card.NotifPeriod;
import com.poiseinteractive.smartexpire.model.Card.Purpose;
import com.poiseinteractive.smartexpire.model.EventListAdapter;
import com.tyczj.extendedcalendarview.Day;
import com.tyczj.extendedcalendarview.Event;
import com.tyczj.extendedcalendarview.ExtendedCalendarView;
import com.tyczj.extendedcalendarview.ExtendedCalendarView.OnDayClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

public class CalendarActivity extends Activity implements OnDayClickListener{

	private static final String CARD_FILENAME = "cards";
	
	String userID;
	ImageButton imgButtonBack;
	ExtendedCalendarView calendar;
	ArrayList<Card> allCards;
	
	ListView eventList;
	EventListAdapter adapter;
	ArrayList<Event> events;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);

		userID = getIntent().getStringExtra("user_id");
		
		imgButtonBack = (ImageButton) findViewById(R.id.imagebutton_calendar_go_back);
		imgButtonBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(CalendarActivity.this, HomeActivity.class);
				i.putExtra("user_id", userID);
				startActivity(i);
			}
		});

		allCards = new ArrayList<Card>();
		loadExistingCards();
		
		calendar = (ExtendedCalendarView) findViewById(R.id.calendar);
		calendar.setOnDayClickListener(this);

		eventList = (ListView) findViewById(R.id.listview_events);
		events = new ArrayList<Event>();
		adapter = new EventListAdapter(CalendarActivity.this, events);
		eventList.setAdapter(adapter);
		
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

	@Override
	public void onDayClicked(AdapterView<?> adapt, View view, int position,
			long id, Day day) {
		Log.d("day", "day clicked!");
		events.clear();
		events.addAll(day.getEvents());
		adapter.notifyDataSetChanged();
	}
}
