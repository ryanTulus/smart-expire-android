package com.poiseinteractive.smartexpire;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CardDetailActivity extends Activity {

	private static final String CARD_IMAGE_FILENAME = "cardimage";
	
	ImageButton imgButtonBack;
	TextView textViewCardLabel;
	TextView textViewCategory;
	TextView textViewNotification;
	TextView textViewExpiry;
	ImageView imgViewCardImage;
	
	String userID;
	String cardID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_detail);
		
		imgButtonBack = (ImageButton) findViewById(R.id.imagebutton_card_detail_go_back);
		imgButtonBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent returnIntent = new Intent(CardDetailActivity.this, HomeActivity.class);
				returnIntent.putExtra("user_id", userID);
				startActivity(returnIntent);
			}
		});
		
		userID = getIntent().getStringExtra("user_id");
		cardID = getIntent().getStringExtra("card_id");
		
		String str = getIntent().getStringExtra("card_label");
		textViewCardLabel = (TextView) findViewById(R.id.text_view_card_label);
		textViewCardLabel.setText(str);
		
		str = getIntent().getStringExtra("category");
		textViewCategory = (TextView) findViewById(R.id.text_view_card_category);
		textViewCategory.setText(str);
		
		str = getIntent().getStringExtra("notif_period");
		textViewNotification = (TextView) findViewById(R.id.text_view_card_notification);
		textViewNotification.setText(str);
		
		str = getIntent().getStringExtra("expiry");
		textViewExpiry = (TextView) findViewById(R.id.text_view_card_expiry);
		textViewExpiry.setText(str);
		
		imgViewCardImage = (ImageView) findViewById(R.id.image_view_card);
		Bitmap b = loadImage(userID, cardID);
		Log.d("userID", userID);
		Log.d("cardID", cardID);
		imgViewCardImage.setImageBitmap(b);
		
	}

	private Bitmap loadImage(String userID, String cardID) {
		File imageFile = getFileStreamPath(CARD_IMAGE_FILENAME + userID + "-" + cardID + ".jpeg");
		if (imageFile.exists()) {
			Log.d("image", "exists!");
			return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
		}
		Log.d("image", "null!");
		return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
	}
}
