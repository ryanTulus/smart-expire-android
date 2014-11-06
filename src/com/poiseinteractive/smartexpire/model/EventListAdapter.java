package com.poiseinteractive.smartexpire.model;

import java.util.List;

import com.poiseinteractive.smartexpire.R;
import com.tyczj.extendedcalendarview.Event;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class EventListAdapter extends BaseAdapter {

	private List<Event> events;
	private LayoutInflater inflater;
	
	public EventListAdapter(Context context, List<Event> data) {
		events = data;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Object getItem(int position) {
		return events.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listview_event_row, parent, false);
		}
		Event event = events.get(position);
		
		TextView eventTitle = (TextView) convertView.findViewById(R.id.event_title);
		eventTitle.setText(event.getTitle());
		
		return convertView;
	}
	
}