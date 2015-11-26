package com.androidproject;

import com.androidproject.GameView.CannonThread;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	private float lastX, lastY, lastZ;
	private GestureDetector gestureDetector; // listens for double taps
	private GameView gameView; // custom view to display the game

	AlertDialog ad;

	private SensorManager sensorManager;
	private Sensor accelerometer;

	private float deltaX = 0;
	private float deltaY = 0;
	private float deltaZ = 0;

	private float vibrateThreshold = 0;

	public Vibrator v;

	//CloudThread cloudThread;

	ImageView background,background2,background3;
	
	Animation anim,anim2,anim3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		background = (ImageView) findViewById(R.id.imageView1);
//		background2 = (ImageView) findViewById(R.id.imageView2);
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

//		anim = AnimationUtils.loadAnimation(this, R.anim.updown);
//		//background.setAnimation(anim);
//
//		anim3 = AnimationUtils.loadAnimation(this, R.anim.updown);
//		anim2 = AnimationUtils.loadAnimation(this, R.anim.updown2);
//		anim2.setInterpolator(new Interpolator() {
//			
//			@Override
//			public float getInterpolation(float arg0) {
//				return arg0;
//			}
//		});
//		anim3.setInterpolator(new Interpolator() {
//			
//			@Override
//			public float getInterpolation(float arg0) {
//				return arg0;
//			}
//		});
//		anim.setInterpolator(new Interpolator() {
//			
//			@Override
//			public float getInterpolation(float arg0) {
//				return arg0;
//			}
//		});
//		
//		
//		anim.setRepeatCount(Animation.INFINITE);//
//		anim.setRepeatMode(Animation.RESTART);
		

		//anim.start();

    	//mRedrawHandler.sleep(400);
    	
//    	
//		cloudThread = new CloudThread();
//		cloudThread.setRunning(true);
//		cloudThread.start(); // start the game loop thread

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
		gameView.releaseResources();
	} // end method onDestroy

	// called when the user touches the screen in this Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		// get int representing the type of action causing the event
//		int action = event.getAction();
//		Log.v("1", event.getX() + " " + event.getY());
//		DisplayMetrics displaymetrics = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//		int height = displaymetrics.heightPixels;
//		int width = displaymetrics.widthPixels;
//		Log.v("2", height + " " + width);
//		if (event.getX() < width / 8 && event.getY() < height / 8) {
//			// cannonView.cannonThread.setRunning(!cannonView.cannonThread.getRunning());
//		} else
//		// the user user touched the screen or dragged along the screen
//		if (action == MotionEvent.ACTION_DOWN
//				|| action == MotionEvent.ACTION_MOVE) {
//			// puckView.alignCannon(event); // align the cannon
//		} // end if
//			// call the GestureDetector's onTouchEvent method
		return gestureDetector.onTouchEvent(event);
	} // end method onTouchEvent
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
		// TODO Auto-generated method stub

		// get the change of the x,y,z values of the accelerometer
		deltaX = -event.values[0];
		deltaY = Math.abs(lastY - event.values[1]);
		deltaZ = Math.abs(lastZ - event.values[2]);

		//Log.v("lo", Math.abs(deltaX) + " " + deltaX);
		// if the change is below 2, it is just plain noise
//		if (Math.abs(deltaX) < 0.5)
//			deltaX = 0;
		if (Math.abs(deltaX) > 10.0)
			deltaX = deltaX > 0 ? 9 : -9;
		// if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) ||
		// (deltaZ > vibrateThreshold)) {
		// v.vibrate(50);
		// }
		//if (Math.abs(deltaX) > 0.5)
			gameView.movePuck(deltaX);

	}

//	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//	public class CloudThread extends Thread {
//		private SurfaceHolder surfaceHolder; // for manipulating canvas
//		private boolean threadIsRunning = true; // running by default
//
//		public CloudThread() {
//			// TODO Auto-generated constructor stub
//		}
//
//		public void setRunning(boolean running) {
//			threadIsRunning = running;
//		} // end method setRunning
//			// controls the game loop
//
//		public boolean getRunning() {
//			return threadIsRunning;
//		} // end method getRunning
//			// controls the game loop
//
//		@Override
//		public void run() {
//			while (threadIsRunning) {
//				synchronized (background) {
//					background.setAlpha((float) ((i++ % 1000) / 1000.0));
//					Log.v("aa", "alo" + i);
//				}
//				
//			}
//		}
//	}

	int i = 0;
	
	
	private RefreshHandler mRedrawHandler = new RefreshHandler();

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
        	background.setAlpha((float) ((i++ % 100) / 100.0));
		Log.v("aa", "alo" + i);
        	background.invalidate();
        	mRedrawHandler.sleep(400);
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };
    
    

}
