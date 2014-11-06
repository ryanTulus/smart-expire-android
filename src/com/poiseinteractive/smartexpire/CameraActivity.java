package com.poiseinteractive.smartexpire;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CameraActivity extends Activity implements SurfaceHolder.Callback{

	private static final String CARD_IMAGE_FILENAME = "cardimage";

	RelativeLayout background;
	RelativeLayout cameraOverlay;


	Camera camera;
	Camera.Parameters camParams;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	LayoutInflater controlInflater = null;
	ImageButton imageButtonCapture;
	String cardID;
	String userID;

	ShutterCallback cameraShutterCallback;
	PictureCallback cameraPictureCallbackRaw;
	PictureCallback cameraPictureCallbackJpeg;
	AutoFocusCallback cameraAutoFocusCallback;

	private static int wid = 0, hgt = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		cardID = getIntent().getStringExtra("card_id");
		userID = getIntent().getStringExtra("user_id");

		background = (RelativeLayout) findViewById(R.id.camera_layout);

		Drawable drawableCameraOverlay = getResources().getDrawable(R.drawable.camera_overlay);
		int width = drawableCameraOverlay.getIntrinsicWidth();
		int height = drawableCameraOverlay.getIntrinsicHeight();

		cameraOverlay = new RelativeLayout(this);
		RelativeLayout.LayoutParams cameraOverlayParam = new RelativeLayout.LayoutParams(width, height);
		cameraOverlayParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

		ImageView imageBg = new ImageView(this);
		RelativeLayout.LayoutParams imageBgParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		imageBg.setBackgroundResource(R.drawable.camera_overlay);
		
		surfaceView = new SurfaceView(this);
		int svWidth = Math.round(((float)17)*imageBgParam.width/24);
		int svHeight = Math.round(((float)10.5)*imageBgParam.height/((float)13.5));
		
		RelativeLayout.LayoutParams surfaceViewParam = new RelativeLayout.LayoutParams(svWidth, svHeight);
		surfaceViewParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		surfaceViewParam.setMargins(22, 0, 0, 0);
		cameraOverlay.addView(surfaceView, surfaceViewParam);

		cameraOverlay.addView(imageBg, imageBgParam);

		imageButtonCapture = new ImageButton(this);
		imageButtonCapture.setImageResource(R.drawable.cam_button);
		imageButtonCapture.setBackgroundColor(Color.TRANSPARENT);
		RelativeLayout.LayoutParams imgButtonParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		imgButtonParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		imgButtonParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		imgButtonParam.setMargins(0, 0, 10, 75);
		cameraOverlay.addView(imageButtonCapture, imgButtonParam);

		background.addView(cameraOverlay, cameraOverlayParam);

		getWindow().setFormat(PixelFormat.UNKNOWN);

		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);

		imageButtonCapture.setOnClickListener(new View.OnClickListener() 
		{
			//@Override
			public void onClick(View v) 
			{
				camera.takePicture(cameraShutterCallback,cameraPictureCallbackRaw,cameraPictureCallbackJpeg);
				setResult(RESULT_OK);
				finish();
			}
		});


		cameraShutterCallback = new ShutterCallback() 
		{
			@Override
			public void onShutter() 
			{
				// TODO Auto-generated method stub
			}
		};

		cameraPictureCallbackRaw = new PictureCallback() 
		{
			@Override
			public void onPictureTaken(byte[] data, Camera camera) 
			{
				// TODO Auto-generated method stub
			}
		};

		cameraPictureCallbackJpeg = new PictureCallback() 
		{
			@Override
			public void onPictureTaken(byte[] data, Camera camera) 
			{
				// TODO Auto-generated method stub
				Bitmap cameraBitmap = BitmapFactory.decodeByteArray
						(data, 0, data.length);



				wid = cameraBitmap.getWidth();
				hgt = cameraBitmap.getHeight();

				Bitmap newImage = Bitmap.createBitmap
						(wid, hgt, Bitmap.Config.ARGB_8888);

				Canvas canvas = new Canvas(newImage);

				canvas.drawBitmap(cameraBitmap, 0f, 0f, null);

				try
				{
					FileOutputStream out = openFileOutput(CARD_IMAGE_FILENAME + userID + "-" + cardID + ".jpeg", MODE_APPEND);
					newImage.compress(Bitmap.CompressFormat.JPEG, 80, out);
					out.flush();
					out.close();
				}
				catch(FileNotFoundException e)
				{
					Log.d("In Saving File", e + "");
				}
				catch(IOException e)
				{
					Log.d("In Saving File", e + "");
				}

				camera.startPreview();

				newImage.recycle();
				newImage = null;

				cameraBitmap.recycle();
				cameraBitmap = null;
			}
		};


		cameraAutoFocusCallback = new AutoFocusCallback(){

			@Override
			public void onAutoFocus(boolean arg0, Camera arg1) {
				imageButtonCapture.setEnabled(true);
			}
		};

		surfaceView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				imageButtonCapture.setEnabled(false);
				camera.autoFocus(cameraAutoFocusCallback);
			}
		});
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		if (camera != null) {
			Camera.Parameters params = camera.getParameters();
			List<Camera.Size> sizes = params.getSupportedPreviewSizes();
			Camera.Size selected = getOptimalPreviewSize(sizes, width, height);
			ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
			lp.width = selected.width;
			lp.height = selected.height;
			surfaceView.setLayoutParams(lp);
			params.setPreviewSize(selected.width, selected.height);
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			camera.setParameters(params);


			try {
				camera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
			}

			camera.startPreview();
		} else {
			Toast.makeText(this, "showing error.", Toast.LENGTH_LONG).show();
		}


		if(previewing){
			camera.stopPreview();
			previewing = false;
		}

		if (camera != null){
			try {
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
				imageButtonCapture.setEnabled(false);
				camera.autoFocus(cameraAutoFocusCallback);
				previewing = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		camera = null;
		previewing = false;
	}

	private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.2;
		double targetRatio=(double)h / w;

		if (sizes == null) return null;

		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		for (Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}


}
