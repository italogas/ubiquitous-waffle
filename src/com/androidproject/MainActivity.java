package com.androidproject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.androidproject.bd.GameRoundData;
import com.androidproject.bd.GameRoundsDataSource;
import com.androidproject.bd.GameRoundDBSQLiteHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

	private GestureDetector gestureDetector; // listens for double taps
	private GameView gameView = null; // custom view to display the game

	AlertDialog ad;

	private SensorManager sensorManager;
	private Sensor accelerometer;

	private float deltaX = 0;

	private float vibrateThreshold = 0;

	public Vibrator v;

	ImageView background, background2, background3;

	Animation upfront, downfront, frontup, frontdown;

	RelativeLayout LayGame;
	RelativeLayout LayMainMenu;
	boolean gameLoaded = false;
	ArrayList<Screen> Screens = new ArrayList<Screen>();
	
	private GameRoundsDataSource gameRoundsDataSource;
	private ListView listView;
	private Button btnDB;
	private ArrayAdapter<GameRoundData> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		LayGame = (RelativeLayout) findViewById(R.id.LayGame);
		LayMainMenu = (RelativeLayout) findViewById(R.id.LayMainMenu);
		gameView = (GameView) findViewById(R.id.gameView);
		Screens.add(new Screen(Name.MainMenu, LayMainMenu));

		frontdown = AnimationUtils.loadAnimation(this, R.anim.frontdown);
		downfront = AnimationUtils.loadAnimation(this, R.anim.downfront);
		frontup = AnimationUtils.loadAnimation(this, R.anim.frontup);
		upfront = AnimationUtils.loadAnimation(this, R.anim.upfront);
		frontdown.setInterpolator(interp);
		downfront.setInterpolator(interp);
		frontup.setInterpolator(interp);
		upfront.setInterpolator(interp);
		frontdown.setFillAfter(true);
		downfront.setFillAfter(true);
		frontup.setFillAfter(true);
		upfront.setFillAfter(true);

		// setContentView(R.layout.activity_main);
		// get the CannonView
		// gameView = (GameView) findViewById(R.id.gameView);
		// initialize the GestureDetector
		gestureDetector = new GestureDetector(this, gestureListener);
		// allow volume keys to set game volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
			// success! we have an accelerometer

			accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);// SENSOR_DELAY_NORMAL
			vibrateThreshold = accelerometer.getMaximumRange() / 2;
		} else {
			// fail! we dont have an accelerometer!
		}

		Log.v("aa", "oncreate");

		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// LayGame.setVisibility(View.VISIBLE);
				// LayMainMenu.setVisibility(View.INVISIBLE);

				GoTo(Name.Game);
			}
		});
		
		//get database access point
		gameRoundsDataSource = new GameRoundsDataSource(getApplicationContext());
		try {
			gameRoundsDataSource.open();
		} catch (SQLException e) {
			Log.e("DATABASE ERROR", e.getMessage());
		}
		
		//get all registered records in example file 
		List<GameRoundData> allRecords = gameRoundsDataSource.getAllRecords();
		
		adapter = new ArrayAdapter<GameRoundData>(
				this, android.R.layout.simple_list_item_1, allRecords);
		
		listView = (ListView) findViewById(R.id.listView);
		
		btnDB = (Button) findViewById(R.id.btnDBQuery);
		btnDB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listView.setAdapter(adapter);
				listView.setVisibility(ListView.VISIBLE);
			}
		});

	}

	public void GoTo(Name whichScreen) {

		switch (whichScreen) {
		case Game:
			LayGame.startAnimation(downfront);
			LayMainMenu.startAnimation(frontup);
			Screens.add(new Screen(whichScreen, LayGame));

			// gameLoaded = true;
			gameView.newGame();
			break;
		case MainMenu:
			LayMainMenu.startAnimation(downfront);
			LayGame.startAnimation(frontup);
			break;
		case Back:
			if (Screens.size() < 2) {
				finish(); // Or ask if want to finish
			} else {
				// GoingBack
				RelativeLayout actual = Screens.get(Screens.size() - 1).Layout;
				if (Screens.get(Screens.size() - 1).name == Name.Game && GoingBack == 0) {

					GoingBack += 1;

					Toast.makeText(getApplicationContext(), "Press again to exit the game", Toast.LENGTH_SHORT).show();
					(new Thread() {
						@Override
						public void run() {
							try {
								Thread.sleep(2000);
								GoingBack--;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
				} else {
					if (Screens.get(Screens.size() - 1).name == Name.Game) {
						if (gameView != null)
							if (gameView.Updater != null)
								// if
								// (gameView.surfaceWorking&&gameView.cannonThread.getRunning())
								gameView.stopGame();
					}

					actual.startAnimation(frontdown);
					Screens.remove(Screens.size() - 1);
					RelativeLayout before = Screens.get(Screens.size() - 1).Layout;
					before.startAnimation(upfront);

				}

			}

			break;
		default:
			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	int GoingBack = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do something on back.
			GoTo(Name.Back);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	// onResume() register the accelerometer for listening the events
	protected void onResume() {
		super.onResume();
		// sensorManager = (SensorManager)
		// getSystemService(Context.SENSOR_SERVICE);
		//
		// sensorManager.registerListener(this, accelerometer,
		// SensorManager.SENSOR_DELAY_GAME);
		if (gameView != null)
			if (gameView.Updater != null)
				// if
				// (gameView.surfaceWorking&&gameView.cannonThread.getRunning())
				gameView.resumeGame(); // resumes the game

		Log.v("aa", "onresume");
	}

	// when the app is pushed to the background, pause it
	@Override
	public void onPause() {
		super.onPause(); // call the super method
		Log.v("aa", "onpause");

		if (gameView != null)
			if (gameView.Updater != null)
				// if
				// (gameView.surfaceWorking&&gameView.cannonThread.getRunning())
				gameView.stopGame(); // terminates the game

		// sensorManager.unregisterListener(this);
	} // end method onPause
		// release resources

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("aa", "ondestroy");
		if (gameView != null)
			if (gameView.Updater != null)
				// if
				// (gameView.surfaceWorking&&gameView.cannonThread.getRunning())
				// {
				gameView.releaseResources();
	} // end method onDestroy

	// listens for touch events sent to the GestureDetector
	SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// Log.v("ae", velocityX + "aqui" + velocityY);
			// gameView.movePuck(e1, e2, velocityX, velocityY);
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
		if (gameView != null) {
			if (gameView.Updater != null)
				if (gameView.surfaceWorking && gameView.Updater.getRunning()) {
					deltaX = -event.values[0];
					if (Math.abs(deltaX) > 10.0)
						deltaX = deltaX > 0 ? 9 : -9;
					gameView.movePlane(deltaX);

				}
		}
	}


	Interpolator interp = new Interpolator() {

		@Override
		public float getInterpolation(float arg0) {
			return arg0;
		}
	};

	enum Name {
		MainMenu, Game, Back
	}

	class Screen {
		Name name;
		RelativeLayout Layout;

		public Screen(Name name, RelativeLayout Layout) {
			this.name = name;
			this.Layout = Layout;
		}
	}

}
