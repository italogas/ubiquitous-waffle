package com.androidproject;

import java.sql.SQLException;

import com.androidproject.bd.PreferencesData;
import com.androidproject.bd.PreferencesDataSource;
import com.androidproject.bluetooth.Connection;
import com.androidproject.bluetooth.Connection.OnConnectionLostListener;
import com.androidproject.bluetooth.Connection.OnConnectionServiceReadyListener;
import com.androidproject.bluetooth.Connection.OnIncomingConnectionListener;
import com.androidproject.bluetooth.Connection.OnMaxConnectionsReachedListener;
import com.androidproject.bluetooth.Connection.OnMessageReceivedListener;
import com.androidproject.bluetooth.ServerList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements SensorEventListener {

	private GameView gameView; // custom view to display the game
	private SensorManager sensorManager;
	private Sensor accelerometer;

	private float deltaX = 0;
	int GoingBack = 0;
	RelativeLayout gameLay;
	RelativeLayout newgameLay;
	LinearLayout chooseLay;
	LinearLayout createLay;
	LinearLayout enterLay;
	GameActivity self;
	SeekBar seekBarNumOfPlayers;
	TextView textViewSeek;
	TextView textViewWaiting;
	EditText editTextDistance;
	ProgressBar waitingBar;
	private int mType; // 0 = server, 1 = client
	private static final int SERVER = 0;
	private static final int CLIENT = 1;

	private Connection mConnection;

	private String rivalDevice = "";

	ListView lv, lv2;// lv for list of clients connecting, and lv2 for list of servers to connect
	PreferencesData preferencesData;

	private ArrayAdapter<String> arrayAdapter1;

	PowerManager powerManager;
	WakeLock wakeLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
		getWindow().getDecorView().setBackgroundColor(Color.GRAY);
		gameView = (GameView) findViewById(R.id.gameView);
		gameLay = (RelativeLayout) findViewById(R.id.gameLay);
		newgameLay = (RelativeLayout) findViewById(R.id.newgameLay);
		chooseLay = (LinearLayout) findViewById(R.id.chooseLay);
		createLay = (LinearLayout) findViewById(R.id.createLay);
		enterLay = (LinearLayout) findViewById(R.id.enterLay);
		Button create = (Button) findViewById(R.id.button1);
		Button enter = (Button) findViewById(R.id.button2);
		Button createfinal = (Button) findViewById(R.id.button3);
		seekBarNumOfPlayers = (SeekBar) findViewById(R.id.seekBar1);
		textViewSeek = (TextView) findViewById(R.id.textViewSeek);
		textViewWaiting = (TextView) findViewById(R.id.textView1);
		lv = (ListView) findViewById(R.id.listView);
		lv2 = (ListView) findViewById(R.id.listView2);
		editTextDistance = (EditText) findViewById(R.id.editText1);
		waitingBar = (ProgressBar) findViewById(R.id.progressBar1);

		// get database access point
		PreferencesDataSource preferencesDataSource = new PreferencesDataSource(getApplicationContext());
		try {
			preferencesDataSource.open();
		} catch (SQLException e) {
			Log.e("DATABASE ERROR", e.getMessage());
		}

		// get all registered records in example file
		preferencesData = preferencesDataSource.getAllRecords();
		preferencesDataSource.close();
		editTextDistance.setText("" + preferencesData.getPlayer1_distance());
		arrayAdapter1 = new ArrayAdapter<String>(this, R.layout.text);

		lv.setAdapter(arrayAdapter1);
		// allow volume keys to set game volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
			// success! we have an accelerometer
			accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);// SENSOR_DELAY_NORMAL
		} else {
			// fail! we dont have an accelerometer!
		}

		gameLay.setVisibility(View.INVISIBLE);
		self = this;

		create.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				chooseLay.setVisibility(View.GONE);
				enterLay.setVisibility(View.GONE);
				createLay.setVisibility(View.VISIBLE);
				mType = SERVER;
				mConnection = new Connection(self, serviceReadyListener);
			}
		});
		enter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				chooseLay.setVisibility(View.GONE);
				createLay.setVisibility(View.GONE);
				enterLay.setVisibility(View.VISIBLE);
				mType = CLIENT;
				mConnection = new Connection(self, serviceReadyListener);
			}
		});

		createfinal.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Check if no view has focus:
				View view = self.getCurrentFocus();
				if (view != null) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}

				if (gameView.Players.size() > 1) {
					// mConnection = new Connection(self, serviceReadyListener);
					// newgameLay.setVisibility(View.INVISIBLE);
					// SHIPCOLOR= SHIP*10+COLOR
					// BEGIN:SIZE OF PATH:NUMBER OF PLAYERS:IDP1:IDP2:...:SHIPCOLORP1:SHIPCOLORP2:...:NAME OF PLAYER1:NAME OF PLAYER2:...
					// CONFIG:YOUR ID:NAME OF CREATOR
					StringBuilder beginning = new StringBuilder(
							"BEGIN:" + editTextDistance.getText().toString() + ":" + gameView.Players.size() + ":");

					for (Player p : gameView.Players) {
						beginning.append(p.device);
						beginning.append(":");
					}
					for (Player p : gameView.Players) {
						beginning.append(p.getShipColor());
						beginning.append(":");
					}
					for (Player p : gameView.Players) {
						beginning.append(p.name);
						beginning.append(":");
					}
					for (int i = 1; i < gameView.Players.size(); i++) {

						mConnection.sendMessage(StringToMAC(gameView.Players.get(i).device), beginning.toString());
					}

				} else if (seekBarNumOfPlayers.getProgress() == 1) {

					gameView.Players.add(new Player(preferencesData.getPlayer1(), preferencesData.getPlayer1_ship(),
							preferencesData.getPlayer1_color(), 0, 0, "me"));

				} else {
					Toast.makeText(self, "Select just one player", Toast.LENGTH_SHORT).show();
					return;
				}
				gameView.totalDistance = Integer.parseInt(editTextDistance.getText().toString());

				beginGame();
			}
		});

		seekBarNumOfPlayers.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Check();

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				Check();

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				Check();

			}

			void Check() {
				if (seekBarNumOfPlayers.getProgress() < 2)
					seekBarNumOfPlayers.setProgress(1);
				textViewSeek.setText(
						getResources().getString(R.string.number_of_players, seekBarNumOfPlayers.getProgress()));
			}
		});
		seekBarNumOfPlayers.setProgress(2);

	}

	private void beginGame() {
		Animation anim = AnimationUtils.loadAnimation(self, R.anim.frontdown);
		anim.setFillAfter(true);

		newgameLay.startAnimation(anim);
		newgameLay.setVisibility(View.GONE);
		chooseLay.setVisibility(View.GONE);
		createLay.setVisibility(View.GONE);
		enterLay.setVisibility(View.GONE);
		newgameLay.setEnabled(false);
		gameView.newGame();
		gaming.start();
		gameLay.setVisibility(View.VISIBLE);

	}

	Thread gaming = new Thread(new Runnable() {
		public void run() {
			while (true)
				while (gameView != null)
					while (gameView.Updater != null)
						while (gameView.Updater.getRunning() || !gameView.gameOver) {
							try {
								Thread.sleep(200);
							} catch (Exception e) {
								e.printStackTrace();
							}
							boolean end = false;
							// CHECK IF SOMEONE WON
							if (mType == SERVER)
								for (Player p : gameView.Players) {
									if (p.distance >= gameView.totalDistance) {
										end = true;

									}

								}

							if (mType == SERVER) {
								// ALLPOS:IDP1:IDP2:...:DISTP1:DISTP2:...
								// MYPOS:DISTANCEP1
								StringBuilder sending = new StringBuilder();
								if (!end) {
									sending.append("ALLPOS:");
									for (Player p : gameView.Players) {
										sending.append(p.device);
										sending.append(":");
									}
									for (Player p : gameView.Players) {
										sending.append(p.distance);
										sending.append(":");
									}

								} else {
									sending.append("THEEND:");

									for (Player p : gameView.Players) {
										p.score = (int) (p.distance * (0.77 + (((float) p.distance/(float)gameView.totalDistance) * 1.1234f) * 0.85));
									}
									for (Player p : gameView.Players) {
										sending.append(p.device);
										sending.append(":");
									}
									for (Player p : gameView.Players) {
										sending.append(p.score);
										sending.append(":");
									}

								}

								for (int i = 1; i < gameView.Players.size(); i++) {

									mConnection.sendMessage(StringToMAC(gameView.Players.get(i).device),
											sending.toString());
								}
								if (end)
									gameView.endOfGame();
							} else {
								mConnection.sendMessage(rivalDevice, "MYPOS:" + gameView.Players.get(0).distance);
							}
						}
		}
	});

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			if (GoingBack == 0) {

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
				return true;
			} else {

				if (gameView != null)
					if (gameView.Updater != null)
						gameView.stopGame();
			}

		}
		overridePendingTransition(R.anim.downfront, R.anim.frontup);
		return super.onKeyDown(keyCode, event);
	}

	// onResume() register the accelerometer for listening the events
	protected void onResume() {
		super.onResume();
		wakeLock.acquire();

		if (gameView != null)
			if (gameView.Updater != null)
				gameView.resumeGame(); // resumes the game
		Log.v("where", "onresume");
	}

	// when the app is pushed to the background, pause it
	@Override
	public void onPause() {
		wakeLock.release();
		super.onPause(); // call the super method
		Log.v("where", "onpause");
		if (gameView != null)
			if (gameView.Updater != null)
				gameView.stopGame(); // terminates the game
	} // end method onPause
		// release resources

	@Override
	protected void onDestroy() {
		if (serverList != null) {
			serverList.finish();
			serverList = null;
		}
		if (mConnection != null) {
			mConnection.shutdown();
		}
		super.onDestroy();
		if (gameView != null)
			if (gameView.Updater != null)
				gameView.releaseResources();

		Log.v("where", "ondestroy");
	} // end method onDestroy

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.upfront, R.anim.frontdown);
	}

	ServerList serverList;

	private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

	private OnConnectionServiceReadyListener serviceReadyListener = new OnConnectionServiceReadyListener() {
		@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
		@SuppressLint("NewApi")
		@Override
		public void OnConnectionServiceReady() {
			Log.w("OnConnectionServiceReady", "");
			if (mType == SERVER) {
				mConnection.startServer(1, connectedListener, maxConnectionsListener, dataReceivedListener,
						disconnectedListener);
				// self.setTitle("Air Hockey: " + mConnection.getName() + "-" + mConnection.getAddress());
			} else {
				serverList = new ServerList(self, lv2);
				waitForChoose.start();

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					// Android M Permission check
					if (self.checkSelfPermission(
							Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						final AlertDialog.Builder builder = new AlertDialog.Builder(self);
						builder.setTitle("This app needs location access");
						builder.setMessage("Please grant location access so this app can detect other users.");
						builder.setPositiveButton(android.R.string.ok, null);
						builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
							@SuppressLint("NewApi")
							@Override
							public void onDismiss(DialogInterface dialog) {
								requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
										PERMISSION_REQUEST_COARSE_LOCATION);
							}
						});
						builder.show();
					} else {
						serverList.Resume();
					}
				} else {
					serverList.Resume();
				}
			}
		}
	};

	@SuppressLint("NewApi")
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
		case PERMISSION_REQUEST_COARSE_LOCATION: {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.d("TAG", "coarse location permission granted");
				serverList.Resume();
			} else {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Functionality limited");
				builder.setMessage(
						"Since location access has not been granted, this app will not be able to discover other devices.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
					}

				});
				builder.show();
			}
			return;
		}
		}

	}

	private String StringToMAC(String str) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length() - 1; i += 2) {
			sb.append(str.substring(i, i + 2));
			sb.append(":");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public Thread waitForChoose = new Thread(new Runnable() {

		@Override
		public void run() {

			if (serverList != null) {
				while (serverList.Result == 0) {
					try {
						Thread.sleep(100);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				if ((serverList.Result == Activity.RESULT_OK) && (serverList.ResultIntent != null)) {
					String device = serverList.device;

					Log.v("connected", device);
					int connectionStatus = mConnection.connect(device, dataReceivedListener, disconnectedListener);
					if (connectionStatus != Connection.SUCCESS) {
						Looper.prepare();
						Toast.makeText(self, "Unable to connect; please try again.", Toast.LENGTH_SHORT).show();
					} else {
						rivalDevice = device;
						runOnUiThread(new Runnable() {
							public void run() {
								waitingBar.setVisibility(View.VISIBLE);
								textViewWaiting.setText("Waiting for player");
								textViewWaiting.setVisibility(View.VISIBLE);
							}
						});

					}
				}

			}
		}
	});

	private OnConnectionLostListener disconnectedListener = new OnConnectionLostListener() {
		@Override
		public void OnConnectionLost(String device) {

			Log.w("OnConnectionLostListener", device);
			class displayConnectionLostAlert implements Runnable {
				@Override
				public void run() {
					Builder connectionLostAlert = new Builder(self);

					connectionLostAlert.setTitle("Connection lost");
					connectionLostAlert.setMessage("Your connection with the other player has been lost.");

					connectionLostAlert.setPositiveButton("Ok", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// finish();
						}
					});
					connectionLostAlert.setCancelable(false);
					try {
						connectionLostAlert.show();
					} catch (BadTokenException e) {
						// Something really bad happened here;
						// seems like the Activity itself went away before
						// the runnable finished.
						// Bail out gracefully here and do nothing.
					}
				}
			}
			self.runOnUiThread(new displayConnectionLostAlert());
		}
	};

	// When I'm the server, and someone connects
	private OnIncomingConnectionListener connectedListener = new OnIncomingConnectionListener() {
		@Override
		public void OnIncomingConnection(final String device) {
			Log.w("OnIncomingConnectionListener", device);

			runOnUiThread(new Runnable() {
				public void run() {
					// arrayAdapter1.add(device);
					mConnection.sendMessage(device,
							"CONFIG:" + device.replace(":", "") + ":" + preferencesData.getPlayer1());

				}
			});

		}
	};

	private OnMaxConnectionsReachedListener maxConnectionsListener = new OnMaxConnectionsReachedListener() {
		@Override
		public void OnMaxConnectionsReached() {

			Log.w("OnMaxConnectionsReached", "");
		}
	};
	// ALLPOS:IDP1:IDP2:...:DISTP1:DISTP2:... (SERVER->CLIENT)
	// MYPOS:DISTANCEP1 (FROM CLIENT TO SERVER)
	// BEGIN:SIZE OF PATH:NUMBER OF PLAYERS:IDP1:IDP2:...:SHIPCOLORP1:SHIPCOLORP2:...:NAME OF PLAYER1:NAME OF PLAYER2:... (SERVER->CLIENT)
	// CONFIG:YOUR ID:NAME OF CREATOR (SERVER->CLIENT)
	// REPLY:SERVER DEVICE:CLIENTID:SHIP COLOR:NAME (FROM CLIENT TO SERVER)
	// SERVER WILL ONLY KNOW HIS DEVICE AFTER SOMEONE REPLY HIM
	private OnMessageReceivedListener dataReceivedListener = new OnMessageReceivedListener() {
		@Override
		public void OnMessageReceived(String device, String message) {
			Log.w("dataReceivedListener", device + "~" + message);

			final String[] scoreMessageSplit = message.split(":");
			if (message.indexOf("CONFIG") == 0) {
				String myid = scoreMessageSplit[1];
				// Game will begin, so I put my user on game
				gameView.Players.add(new Player(preferencesData.getPlayer1(), preferencesData.getPlayer1_ship(),
						preferencesData.getPlayer1_color(), 0, 0, myid));

				runOnUiThread(new Runnable() {
					public void run() {
						textViewWaiting.setText("Waiting for " + scoreMessageSplit[2]);
					}
				});

				mConnection.sendMessage(rivalDevice, "REPLY:" + device.replace(":", "") + ":" + myid + ":"

				+ Player.convertfromShipAndColorToShipColor(preferencesData.getPlayer1_ship(),
						preferencesData.getPlayer1_color()) + ":" + preferencesData.getPlayer1());
			} else if (message.indexOf("BEGIN") == 0) {
				// Client receiving message from server
				gameView.totalDistance = Integer.parseInt(scoreMessageSplit[1]);
				int nOfPlayers = Integer.parseInt(scoreMessageSplit[2]);
				for (int i = 0; i < nOfPlayers; i++) {
					if (!scoreMessageSplit[i + 3].equals(gameView.Players.get(0).device))
						gameView.Players.add(new Player(scoreMessageSplit[i + 3 + nOfPlayers * 2],
								Player.convertfromShipColortoShip(
										Integer.parseInt(scoreMessageSplit[i + 3 + nOfPlayers])),
								Player.convertfromShipColortoColor(
										Integer.parseInt(scoreMessageSplit[i + 3 + nOfPlayers]) % 10),
								0, 0, scoreMessageSplit[i + 3]));
				}

				runOnUiThread(new Runnable() {
					public void run() {

						beginGame();
					}
				});

			} else if (message.indexOf("REPLY") == 0) {
				// REPLY:SERVER DEVICE:CLIENTID:SHIP COLOR:NAME (FROM CLIENT TO SERVER)
				String serverid = scoreMessageSplit[1];
				// If it's the first client to come, the server puts himself on the list first
				if (gameView.Players.size() == 0) {
					gameView.Players.add(new Player(preferencesData.getPlayer1(), preferencesData.getPlayer1_ship(),
							preferencesData.getPlayer1_color(), 0, 0, serverid));

				}
				gameView.Players.add(new Player(scoreMessageSplit[4],
						Player.convertfromShipColortoShip(Integer.parseInt(scoreMessageSplit[3])),
						Player.convertfromShipColortoColor(Integer.parseInt(scoreMessageSplit[3])), 0, 0,
						scoreMessageSplit[2]));
				runOnUiThread(new Runnable() {
					public void run() {
						arrayAdapter1.add(scoreMessageSplit[4]);
					}
				});

			} else if (message.indexOf("MYPOS") == 0) { // Server receiving and updating his database
				for (Player p : gameView.Players) {
					if (p.device.equals(device.replace(":", ""))) {
						p.distance = Integer.parseInt(scoreMessageSplit[1]);
						break;
					}

				}

			} else if (message.indexOf("ALLPOS") == 0) {
				int nOfPlayers = (scoreMessageSplit.length - 1) / 2;
				for (int i = 0; i < nOfPlayers; i++) {
					if (!scoreMessageSplit[i + 1].equals(gameView.Players.get(0).device))
						for (Player p : gameView.Players) {
							if (p.device.equals(scoreMessageSplit[i + 1])) {
								p.distance = Integer.parseInt(scoreMessageSplit[i + nOfPlayers + 1]);
								break;
							}

						}

				}

			} else if (message.indexOf("THEEND") == 0) {
				int nOfPlayers = (scoreMessageSplit.length - 1) / 2;
				for (int i = 0; i < nOfPlayers; i++) {
					// if (!scoreMessageSplit[i + 1].equals(gameView.Players.get(0).device))
					for (Player p : gameView.Players) {
						if (p.device.equals(scoreMessageSplit[i + 1])) {
							p.score = Integer.parseInt(scoreMessageSplit[i + nOfPlayers + 1]);
							break;
						}

					}

				}
				gameView.endOfGame();

			}
		}
	};

}
