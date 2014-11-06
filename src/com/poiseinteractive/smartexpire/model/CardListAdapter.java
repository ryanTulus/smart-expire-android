package com.poiseinteractive.smartexpire.model;

import java.util.Date;
import java.util.List;

import com.poiseinteractive.smartexpire.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CardListAdapter extends BaseAdapter {

	private List<Card> cards;
	private LayoutInflater inflater;
	
	public CardListAdapter(Context context, List<Card> data) {
		cards = data;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return cards.size();
	}

	@Override
	public Object getItem(int position) {
		return cards.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listview_card_row, parent, false);
		}
		Card card = cards.get(position);
		
		TextView cardSequence = (TextView) convertView.findViewById(R.id.sequence);
		cardSequence.setText(getCharForNumber(position));
		
		Date now  = new Date();
		Date cardExpiry = card.getExpiryDate();
		
		if (now.after(cardExpiry)) {
			cardSequence.setBackgroundResource(R.drawable.red);
		}
		
		TextView cardLabel = (TextView) convertView.findViewById(R.id.card_label);
		cardLabel.setText(card.getLabel());
		
		return convertView;
	}
	
	private String getCharForNumber(int i) {
		return i >= 0 && i < 26 ? String.valueOf((char)(i + 'A')) : "";
	}

}