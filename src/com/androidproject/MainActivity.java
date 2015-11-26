package com.androidproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.widget.ImageView;

public class MainActivity extends Activity implements SensorEventListener {

	private GestureDetector gestureDetector; // listens for double taps
	private GameView gameView; // custom view to display the game

	AlertDialog ad;

	private SensorManager sensorManager;
	private Sensor accelerometer;

	private float deltaX = 0;

	private float vibrateThreshold = 0;

	public Vibrator v;

	ImageView background,background2,background3;
	
	Animation anim,anim2,anim3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// get the CannonView
		gameView = (GameView) findViewById(R.id.gameView);
		// initialize the GestureDetector
		gestureDetector = new GestureDetector(this, gestureListener);
		// allow volume keys to set game volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
			// success! we have an accelerometer

			accelerometer = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(this, accelerometer,
					SensorManager.SENSOR_DELAY_GAME);// SENSOR_DELAY_NORMAL
			vibrateThreshold = accelerometer.getMaximumRange() / 2;
		} else {
			// fail! we dont have an accelerometer!
		}

		Log.v("aa","oncreate");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// onResume() register the accelerometer for listening the events
	protected void onResume() {
		super.onResume();
//		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//		
//		sensorManager.registerListener(this, accelerometer,
//				SensorManager.SENSOR_DELAY_GAME);
		gameView.resumeGame(); // terminates the game
		Log.v("aa","onresume");
	}

	// when the app is pushed to the background, pause it
	@Override
	public void onPause() {
		super.onPause(); // call the super method
		Log.v("aa","onpause");
		
		
		gameView.stopGame(); // terminates the game
//		sensorManager.unregisterListener(this);
	} // end method onPause
		// release resources

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("aa","ondestroy");
		gameView.releaseResources();
	} // end method onDestroy

	
	
		// listens for touch events sent to the GestureDetector
	SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
//			Log.v("ae", velocityX + "aqui" + velocityY);
//			gameView.movePuck(e1, e2, velocityX, velocityY);
			return true; // the event was handled
		};

		// called when the user double taps the screen
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// puckView.fireCannonball(e); // fire the cannonball
			return true; // the event was handled
		} // end method onDoubleTap
	}; // end gestureListener

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		deltaX = -event.values[0];
		if (Math.abs(deltaX) > 10.0)
			deltaX = deltaX > 0 ? 9 : -9;
			gameView.movePuck(deltaX);

	}

    

}
