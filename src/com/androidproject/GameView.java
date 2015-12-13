package com.androidproject;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import com.androidproject.bd.GameRoundData;
import com.androidproject.bd.GameRoundsDataSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	// Color of ship
	public static final String BLUE = "0";
	public static final String GREEN = "1";
	public static final String ORANGE = "2";
	public static final String RED = "3";
	// Ship
	public static final String FIRST = "1";
	public static final String SECOND = "2";
	public static final String THIRD = "3";

	public RefreshHandler Updater;
	private Activity activity; // to display Game Over dialog in GUI thread
	// variables for the game loop and tracking statistics
	public boolean gameOver; // is the game over?
	public long initialTime = 0; // the amount of time in seconds RAFA
	private float puckXVelocity; // blocker speed multiplier during game
	private float backVelocity; // blocker speed multiplier during game
	private float backInitialVelocity; // blocker speed multiplier during game
	private Position planePos;
	private Position infoPos;
	private Position backgPos;
	private Position finishLinePos;
	public int totalDistance = 0;
	private int actualDistance;

	private int contentViewTop; // size of status bar
	private int screenWidth; // width of the screen
	private int screenHeight; // height of the screen
	// constants and variables for managing sounds
	private static final int FIRE = 0;
	private SoundPool soundPool; // plays sound effects
	private SparseIntArray soundMap; // maps IDs to SoundPool
	// Paint variables used when drawing each item on the screen
	private Paint textPaint; // Paint used to draw text
	private Paint backgroundPaint; // Paint used to clear the drawing area

	Bitmap thebmp = BitmapFactory.decodeResource(getResources(), R.drawable.farback);
	Bitmap tempPlaneBmp;
	Bitmap planeBmp;
	Bitmap FinishLineBmp = BitmapFactory.decodeResource(getResources(), R.drawable.finishline);
	Bitmap backg;
	Bitmap harmBmp;
	Bitmap boostBmp;
	Bitmap[] damages = new Bitmap[3];
	Random r = new Random();
	ArrayList<Effect> Effects = new ArrayList<Effect>();
	SurfaceHolder holder;

	private boolean do321 = false;
	private double doing321 = 4000;
	private float doing321MaxSize;
	// a distance that was poisoned by the position of the plane
	private int poisonedDistance;
	private long initialtime;

	ArrayList<Player> Players = new ArrayList<Player>();
	ArrayList<Integer> shipsandcolors = new ArrayList<Integer>();

	MediaPlayer mPlayer;

	boolean playerLose;
	boolean playerLose2=false;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs); // call super's constructor
		activity = (Activity) context;
		// register SurfaceHolder.Callback listener
		holder = getHolder();
		holder.addCallback(this);
		// initialize SoundPool to play the app's three sound effects
		soundPool = new SoundPool(99, AudioManager.STREAM_MUSIC, 0);
		// create Map of sounds and pre-load sounds (load returns a sound_ID)
		soundMap = new SparseIntArray();// <Integer, Integer>(); // create new HashMap
		mPlayer = MediaPlayer.create(context, R.raw.music);
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer.setLooping(true);
		soundMap.put(FIRE, soundPool.load(context, R.raw.cannon_fire, 1));
		// construct Paints for drawing text, cannonball, cannon,
		// blocker and target; these are configured in method onSizeChanged
		textPaint = new Paint(); // Paint for drawing text
		backgroundPaint = new Paint(); // Paint for drawing the target
		planePos = new Position();
		infoPos = new Position();
		backgPos = new Position();
		finishLinePos = new Position();

		shipsandcolors.add(R.drawable.playership1_blue);
		shipsandcolors.add(R.drawable.playership1_green);
		shipsandcolors.add(R.drawable.playership1_orange);
		shipsandcolors.add(R.drawable.playership1_red);

		shipsandcolors.add(R.drawable.playership2_blue);
		shipsandcolors.add(R.drawable.playership2_green);
		shipsandcolors.add(R.drawable.playership2_orange);
		shipsandcolors.add(R.drawable.playership2_red);

		shipsandcolors.add(R.drawable.playership3_blue);
		shipsandcolors.add(R.drawable.playership3_green);
		shipsandcolors.add(R.drawable.playership3_orange);
		shipsandcolors.add(R.drawable.playership3_red);

	} // end CannonView constructor

	// called by surfaceChanged when the size of the SurfaceView changes,
	// such as when it's first added to the View hierarchy
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.v("init", "onsizechanged" + w + " " + h);
		// size
		planePos.hw = w > h ? w / 10 : h / 10;
		// position and rotation
		planePos.fx = w / 2;
		planePos.fy = 3 * h / 4;
		planePos.fr = 0;
		backInitialVelocity = 25;// h / 100;
		// Get statusbar size
		Window window = activity.getWindow();
		contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();

		infoPos.x = w / 100;
		infoPos.y = contentViewTop == 0 ? h / 15 : contentViewTop;
		infoPos.fy = h / 15;

		backgPos.fy = h / 100;

		finishLinePos.y = 0;

		screenWidth = w; // store the width
		screenHeight = h; // store the height

		doing321MaxSize = (0.25f);

		// configure Paint objects for drawing game elements
		textPaint.setTextSize(w / 20); // text size 1/20 of screen width
		textPaint.setARGB(255, 255, 255, 255);
		textPaint.setAntiAlias(true); // smoothes the text
		backgroundPaint.setColor(Color.WHITE); // set background color
		backgroundPaint.setColor(Color.TRANSPARENT); // set background color

		Rect bounds = new Rect();
		String str = "qwertyuiop´[asdfghjklç~]\\zxcvbnm,.;QWERTYUIOP`{ASDFGHJKLÇ^}|ZXCVBNM<>:'1234567890-=\"!@#$%¨&*()_+";
		textPaint.getTextBounds(str, 0, str.length(), bounds);
		infoPos.hw = bounds.height();

		harmBmp = BitmapFactory.decodeResource(getResources(), R.drawable.meteorbrown_big3);
		harmBmp = Bitmap.createScaledBitmap(harmBmp, planePos.hw,
				harmBmp.getHeight() * planePos.hw / harmBmp.getWidth(), false);
		boostBmp = BitmapFactory.decodeResource(getResources(), R.drawable.meteorgrey_big3);
		boostBmp = Bitmap.createScaledBitmap(boostBmp, planePos.hw,
				boostBmp.getHeight() * planePos.hw / boostBmp.getWidth(), false);

		backg = Bitmap.createScaledBitmap(thebmp, screenWidth, screenHeight, false);

	} // end method onSizeChanged

	// reset all the screen elements and start a new game
	public void newGame() {
		// From sizechanged, when players had to be created

		// DEPENDING ON SELECTED SHIP
		switch (Integer.parseInt(Players.get(0).ship)) {
		case 1:
			damages[0] = BitmapFactory.decodeResource(getResources(), R.drawable.playership1_damage1);
			damages[1] = BitmapFactory.decodeResource(getResources(), R.drawable.playership1_damage2);
			damages[2] = BitmapFactory.decodeResource(getResources(), R.drawable.playership1_damage3);
			break;
		case 2:
			damages[0] = BitmapFactory.decodeResource(getResources(), R.drawable.playership2_damage1);
			damages[1] = BitmapFactory.decodeResource(getResources(), R.drawable.playership2_damage2);
			damages[2] = BitmapFactory.decodeResource(getResources(), R.drawable.playership2_damage3);
			break;
		case 3:
			damages[0] = BitmapFactory.decodeResource(getResources(), R.drawable.playership3_damage1);
			damages[1] = BitmapFactory.decodeResource(getResources(), R.drawable.playership3_damage2);
			damages[2] = BitmapFactory.decodeResource(getResources(), R.drawable.playership3_damage3);
			break;

		default:
			break;
		}

		planeBmp = BitmapFactory.decodeResource(getResources(), shipsandcolors
				.get((Integer.parseInt(Players.get(0).ship) - 1) * (4) + Integer.parseInt(Players.get(0).color)));

		/////
		Bitmap fire = BitmapFactory.decodeResource(getResources(), R.drawable.fire03);
		Bitmap temp = Bitmap.createBitmap(planeBmp.getWidth(),
				(int) (planeBmp.getHeight() + 17 * (fire.getHeight() / 34f)), Bitmap.Config.ARGB_8888);

		Canvas canvas1 = new Canvas(temp);
		canvas1.drawBitmap(fire, (fire.getWidth() / 14f) * 17, (fire.getHeight() / 34f) * 57, null);
		canvas1.drawBitmap(fire, (fire.getWidth() / 14f) * 67, (fire.getHeight() / 34f) * 57, null);
		canvas1.drawBitmap(planeBmp, 0, 0, null);
		planeBmp = temp;

		/////
		planeBmp = Bitmap.createScaledBitmap(planeBmp, planePos.hw,
				planeBmp.getHeight() * planePos.hw / planeBmp.getWidth(), false);

		FinishLineBmp = Bitmap.createScaledBitmap(FinishLineBmp, screenWidth,
				FinishLineBmp.getHeight() * screenWidth / FinishLineBmp.getWidth(), false);

		// Adjust sizes of damage images to fit on plane
		for (int i = 0; i < 3; i++) {
			Bitmap temp2 = Bitmap.createBitmap(damages[i].getWidth(),
					(int) (damages[i].getHeight() + 17 * (fire.getHeight() / 34f)), Bitmap.Config.ARGB_8888);
			Canvas canvas2 = new Canvas(temp2);
			canvas2.drawBitmap(damages[i], 0, 0, null);
			damages[i] = temp2;
			damages[i] = Bitmap.createScaledBitmap(damages[i], planePos.hw,
					damages[i].getHeight() * planePos.hw / damages[i].getWidth(), false);
		}

		////

		initialtime = 0;
		backVelocity = backInitialVelocity;
		actualDistance = 0;
		totalDistance = totalDistance == 0 ? 3500 : totalDistance;

		int NumOfEffects = r.nextInt((int) (totalDistance <= 1000 ? 1 : 1 + totalDistance / 1000f)) + 2;
		int NumOfBoosts = r.nextInt(NumOfEffects == 0 ? 1 : NumOfEffects);

		for (int i = 0; i < NumOfEffects; i++) {
			if (i < NumOfBoosts)
				Effects.add(new Effect(Type.Boost, r.nextInt(100), r.nextInt(totalDistance), planePos.hw));
			else
				Effects.add(new Effect(Type.Harm, r.nextInt(100), r.nextInt(totalDistance), planePos.hw));
		}
		Collections.sort(Effects);
		backgPos.y = 0;
		planePos.x = planePos.fx;
		planePos.y = planePos.fy;
		planePos.r = 0;
		planePos.damaged = -1;
		puckXVelocity = 0;
		gameOver = false; // the game is not over
		playerLose=false;
		start();
	} // end method newGame

	public void start() {
		mPlayer.start();
		do321 = true;
		Updater = new RefreshHandler(holder);
		Updater.setRunning(true);
	}

	int qnts = 0;

	// called repeatedly to update game elements
	// private void updatePositions(double elapsedTimeMS) {
	private void update(Canvas canvas, double elapsedTimeMS) {
		if (initialtime == 0) {
			initialtime = System.currentTimeMillis();
			initialTime = initialtime;
		}
		if (!gameOver) {
			int offset = 5;
			qnts += 1;

			if (planePos.x - planePos.hw / 2 < 5 && puckXVelocity < 0
					|| planePos.x + planePos.hw / 2 > screenWidth - offset && puckXVelocity > 0) {
				planePos.r = 0;
				backVelocity = backInitialVelocity;

			} else {

				double acceleration = 1.15;

				planePos.x += (int) (puckXVelocity * acceleration);
				planePos.r = ((int) (360 + puckXVelocity)) % 360;

				backVelocity = (float) (backInitialVelocity * (0.5 + 0.5 * (45 - Math.abs(puckXVelocity)) / 45.0));

			}

			backgPos.y += backgPos.fy;

			// actualDistance += backVelocity;
			actualDistance = (int) (System.currentTimeMillis() - initialtime);

			// Just to fake a real-time actualization
			// for (Player p : Players) {
			// p.distance+= p.distance>=totalDistance?0:backVelocity;
			//
			// }
			Players.get(0).distance = actualDistance;
			if (backgPos.y >= backg.getHeight())
				backgPos.y = 0;

			Matrix matrix = new Matrix();
			matrix.setRotate(planePos.r);
			tempPlaneBmp = Bitmap.createBitmap(planeBmp, 0, 0, planePos.hw,
					planeBmp.getHeight() * planePos.hw / planeBmp.getWidth(), matrix, false);

			poisonedDistance = actualDistance + planePos.y - (tempPlaneBmp.getHeight() / 2);

			double tempMed = (-initialtime + System.currentTimeMillis()) / qnts;
			if (totalDistance < poisonedDistance - planePos.y + tempMed * planePos.y / backgPos.fy) {
				finishLinePos.y += backgPos.fy;
			}

			for (Iterator<Effect> iterator = Effects.iterator(); iterator.hasNext();) {
				Effect ef = iterator.next();
				if (ef.dy > 0) { // If it's already show on screen
					ef.dy += backgPos.fy;
					if (!ef.hit) {
						if ((ef.x * screenWidth / 100 >= planePos.x
								&& ef.x * screenWidth / 100 <= planePos.x + planePos.hw
								|| ef.x * screenWidth / 100 + ef.hw >= planePos.x
										&& ef.x * screenWidth / 100 + ef.hw <= planePos.x + planePos.hw)
								&& (ef.dy >= planePos.y && ef.dy <= planePos.y + planePos.hw
										|| ef.dy + ef.hw >= planePos.y && ef.dy + ef.hw <= planePos.y + planePos.hw)) {
							// We have a collision
							soundPool.play(soundMap.get(FIRE), 1, 1, 1, 0, 1f);
							ef.hit = true;
							if (ef.Type == Type.Harm) {
								planePos.damaged += 1;
								initialtime += 1000;
							} else {
								planePos.damaged -= 1;
								initialtime -= 1000;
							}
							if (planePos.damaged > 2) {
								// Lose the game
								planePos.damaged = 2;
								playerLose=true;
								gameOver = true;
								endOfGame();
							} else if (planePos.damaged < -1)
								planePos.damaged = -1;

						}

					}
					if (ef.dy > screenHeight) {// remove if it's no longer visible
						iterator.remove();
					}

				} else if (ef.y < poisonedDistance) {
					ef.dy += backgPos.fy;
				}
			}

			if (totalDistance - actualDistance < 0 || gameOver) {
				Updater.setRunning(false);
			}
		}

		drawGameElements(canvas);
	} // end method updatePositions

	// Draw Game elements on canvas

	public void drawGameElements(Canvas canvas) {

		canvas.drawBitmap(backg, 0, (float) backgPos.y, null);
		canvas.drawBitmap(backg, 0, (float) -backg.getHeight() + backgPos.y, null);

		// FinishLine must appear in the screen
		if (finishLinePos.y > 0) {
			canvas.drawBitmap(FinishLineBmp, 0, (float) finishLinePos.y, null);
		}

		for (Iterator<Effect> iterator = Effects.iterator(); iterator.hasNext();) {
			Effect ef = iterator.next();
			if (ef.dy > 0) { // If it's already show on screen
				Matrix matrix = new Matrix();
				matrix.setRotate((720 * ef.dy / (float) screenHeight) % 360);
				Bitmap tempEf;
				if (ef.Type == Type.Harm)
					tempEf = Bitmap.createBitmap(harmBmp, 0, 0, planePos.hw,
							harmBmp.getHeight() * planePos.hw / harmBmp.getWidth(), matrix, false);

				else
					tempEf = Bitmap.createBitmap(boostBmp, 0, 0, planePos.hw,
							boostBmp.getHeight() * planePos.hw / boostBmp.getWidth(), matrix, false);

				canvas.drawBitmap(tempEf, (float) (ef.x * (screenWidth - tempPlaneBmp.getWidth()) / 100f),
						(float) (ef.dy), null);
			}
		}
		//////////

		if (planePos.damaged > -1) {
			Bitmap temp = Bitmap.createBitmap(tempPlaneBmp.getWidth(), tempPlaneBmp.getHeight(),
					Bitmap.Config.ARGB_8888);
			Canvas canvas1 = new Canvas(temp);
			canvas1.drawBitmap(tempPlaneBmp, 0, 0, null);
			Bitmap tempDamage;
			Matrix matrix = new Matrix();
			matrix.setRotate(planePos.r);
			tempDamage = Bitmap.createBitmap(damages[planePos.damaged], 0, 0, planePos.hw,
					damages[planePos.damaged].getHeight() * planePos.hw / damages[planePos.damaged].getWidth(), matrix,
					false);

			canvas1.drawBitmap(tempDamage, 0, 0, null);
			tempPlaneBmp = temp;
		}

		////// q
		canvas.drawBitmap(tempPlaneBmp, (float) (planePos.x - tempPlaneBmp.getWidth() / 2),
				(float) (planePos.y - tempPlaneBmp.getHeight() / 2), null);

		for (int i = 0; i < Players.size(); i++) {
			String str = Players.get(i).name + ": " + new DecimalFormat("0.0").format(Players.get(i).distance / 1000.0)
					+ "/" + new DecimalFormat("0.0").format(totalDistance / 1000.0);
			canvas.drawText(str, infoPos.x, infoPos.y + (infoPos.hw * 1.15f) * (i), textPaint);
		}

	} // end method drawGameElements

	Bitmap bitmapCountdown;
	String textCountdown = "";
	long initialtimecountdown = 0;

	private void doing321(Canvas canvas, double elapsedTimeMS) {
		if (initialtimecountdown == 0)
			initialtimecountdown = System.currentTimeMillis();
		Matrix matrix = new Matrix();
		matrix.setRotate(planePos.r);
		tempPlaneBmp = Bitmap.createBitmap(planeBmp, 0, 0, planePos.hw,
				planeBmp.getHeight() * planePos.hw / planeBmp.getWidth(), matrix, false);
		drawGameElements(canvas);
		elapsedTimeMS = System.currentTimeMillis() - initialtimecountdown;

		doing321 = doing321 < 1000 ? 4000 - elapsedTimeMS + 3000 : 4000 - elapsedTimeMS / 2;
		if (!((Integer) ((int) (doing321 / 1000))).toString().equals(textCountdown) || !textCountdown.equals("GO!")) {
			textCountdown = ((Integer) ((int) (doing321 / 1000))).toString();
			textCountdown = textCountdown.equals("0") ? "GO!" : ((Integer) ((int) (doing321 / 1000))).toString();
			Paint textp = new Paint();
			textp.setARGB(255, 0, 0, 255);
			textp.setTextSize((float) screenWidth / 2);
			textp.setAntiAlias(true); // smoothes the text
			Rect bounds = new Rect();
			textp.getTextBounds(textCountdown, 0, textCountdown.length(), bounds);
			bitmapCountdown = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
			Canvas canv = new Canvas(bitmapCountdown);
			canv.drawText(textCountdown, -bounds.left, -bounds.top, textp);

		}
		if (doing321 < 50) {// elapsedTimeMS * 2) {
			do321 = false;
			doing321 = 4000;
			return;
		}
		Bitmap temp = Bitmap.createScaledBitmap(bitmapCountdown,
				(int) (bitmapCountdown.getWidth() * ((doing321 / 1000) % 1) * screenHeight * doing321MaxSize
						/ bitmapCountdown.getHeight()),
				(int) (((doing321 / 1000) % 1) * screenHeight * doing321MaxSize), false);

		canvas.drawBitmap(temp, (float) screenWidth / 2 - temp.getWidth() / 2,
				(float) screenHeight / 2 - temp.getHeight() / 2, null);

	}

	public void movePlane(float accel) {
		puckXVelocity = (float) (accel * 25.0 * 0.2);
	}

	public void endOfGame() {
		if (!gameOver) {
			gameOver = true;
			Updater.setRunning(false);
		}

		// get database access point
		GameRoundsDataSource gameRoundsDataSource = new GameRoundsDataSource(getContext());
		try {
			gameRoundsDataSource.open();
		} catch (SQLException e) {
			Log.e("DATABASE ERROR", e.getMessage());
		}

		GameRoundData grd = new GameRoundData();
		grd.setTime_stamp(System.currentTimeMillis() + "");
		grd.setPlayer1(Players.get(0).name);
		grd.setPlayer1_color(Players.get(0).color);
		grd.setPlayer1_distance(totalDistance);
		grd.setPlayer1_score(Players.get(0).score);
		grd.setPlayer1_ship(Players.get(0).ship);
		if (Players.size() > 1) {
			grd.setPlayer2(Players.get(1).name);
			grd.setPlayer2_color(Players.get(1).color);
			grd.setPlayer2_distance(Players.get(1).distance);
			grd.setPlayer2_score(Players.get(1).score);
			grd.setPlayer2_ship(Players.get(1).ship);

		}
		if (Players.size() > 2) {
			grd.setPlayer3(Players.get(2).name);
			grd.setPlayer3_color(Players.get(2).color);
			grd.setPlayer3_distance(Players.get(2).distance);
			grd.setPlayer3_score(Players.get(2).score);
			grd.setPlayer3_ship(Players.get(2).ship);

		}
		if (Players.size() > 3) {
			grd.setPlayer4(Players.get(3).name);
			grd.setPlayer4_color(Players.get(3).color);
			grd.setPlayer4_distance(Players.get(3).distance);
			grd.setPlayer4_score(Players.get(3).score);
			grd.setPlayer4_ship(Players.get(3).ship);
		}

		gameRoundsDataSource.insertRecord(grd);
		gameRoundsDataSource.close();

		showGameOverDialog();
	}

	// display an AlertDialog when the game ends
	private void showGameOverDialog() {
		// create a dialog
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setTitle("End of the game!");
		dialogBuilder.setCancelable(false);
		String me = Players.get(0).device.toString();
		Collections.sort(Players);
		if(playerLose&&!playerLose2){
			if(Players.size()>1){
			dialogBuilder.setMessage("You lost the game! Waiting for other players.");// getResources().getString(R.string.results_format, shotsFired, totalElapsedTime));
			playerLose2=true;
			}else
				dialogBuilder.setMessage("You lost the game!");// getResources().getString(R.string.results_format, shotsFired, totalElapsedTime));
			
		}else{
		if (me.equals(Players.get(0).device))
			dialogBuilder.setMessage("You won the game with a score of " + Players.get(0).score + "!!!");// getResources().getString(R.string.results_format, shotsFired, totalElapsedTime));
		else
			dialogBuilder
					.setMessage(Players.get(0).name + " won the game with a score of " + Players.get(0).score + "!!!");// getResources().getString(R.string.results_format, shotsFired, totalElapsedTime));
		}
		dialogBuilder.setPositiveButton("Ok", // R.string.reset_game,
				new DialogInterface.OnClickListener() {
					// called when "Reset Game" Button is pressed
					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.finish(); // set up and start a new game
					} // end method onClick
				} // end anonymous inner class
		); // end call to setPositiveButton
		activity.runOnUiThread(new Runnable() {
			public void run() {
				// dialogIsDisplayed = true;
				dialogBuilder.show(); // display the dialog
			} // end method run
		} // end Runnable
		); // end call to runOnUiThread
	} // end method showGameOverDialog

	// stops the game
	public void stopGame() {

		if (Updater != null && getHolder().getSurface().isValid())
			Updater.setRunning(false);
		Log.v("where", Updater.getRunning() + "stopGame");
		// soundPool.autoPause();
		mPlayer.pause();
	} // end method stopGame
		// resumes the game

	public void resumeGame() {

		Log.v("where", "resumeGame");
		// if (cannonThread != null) {
		// while(holder.getSurface().isValid())
		soundPool.autoResume();
		mPlayer.start();
		Updater = new RefreshHandler(getHolder());
		Updater.setRunning(true);
		// }

	} // end method stopGame

	// releases resources; called by CannonGame's onDestroy method

	public void releaseResources() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
		soundPool.release(); // release all resources used by the SoundPool
		soundPool = null;
	} // end method releaseResources

	// called when surface changes size
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	} // end method surfaceChanged
		// called when surface is first created

	boolean surfaceWorking = false;

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v("where", "surfaceCreated");
		surfaceWorking = true;
	} // end method surfaceCreated
		// called when the surface is destroyed

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// ensure that thread terminates properly
		surfaceWorking = false;
		Log.v("where", "surfaceDestroyed");
	} // end method surfaceDestroyed

	class RefreshHandler extends Handler {
		public RefreshHandler(SurfaceHolder holder) {
			surfaceHolder = holder;
		}

		private SurfaceHolder surfaceHolder; // for manipulating canvas
		private boolean threadIsRunning = false; // running by default

		public void setRunning(boolean running) {
			if (running && !threadIsRunning) {
				threadIsRunning = running;
				this.sleep(0);

			}
			threadIsRunning = running;

		} // end method setRunning
			// controls the game loop

		public boolean getRunning() {
			return threadIsRunning;
		} // end method getRunning

		@Override
		public void handleMessage(Message msg) {
			Canvas canvas = null; // used for drawing
			long previousFrameTime = System.currentTimeMillis();
			if (surfaceWorking) {
				try {
					canvas = surfaceHolder.lockCanvas(null);
					// lock the surfaceHolder for drawing
					synchronized (surfaceHolder) {
						if (threadIsRunning) {
							long currentTime = System.currentTimeMillis();
							double elapsedTimeMS = currentTime - previousFrameTime;
							if (!do321)
								update(canvas, elapsedTimeMS);
							else
								doing321(canvas, elapsedTimeMS);
							previousFrameTime = currentTime;
						}
					} // end synchronized block
				} // end try
				catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (canvas != null)
						surfaceHolder.unlockCanvasAndPost(canvas);
				} // end finally

			}
			Updater.sleep(0);
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			if (threadIsRunning)
				sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	class Position {
		public int fx, fy, fr; // first values
		public int x, y, r; // actual values
		public int hw; // size
		public int damaged = -1; // (for the plane only) level of damage (0~3)

		public Position() {
		}
	}

	enum Type {
		Boost, Harm
	}

	class Effect implements Comparable<Effect> {
		public Type Type;
		/**
		 * X is a value between 0 and 100, which is a position between the left and right of the screen
		 */
		public int x;
		public int y;
		public int dy = 0; // the actual position in screen
		public int hw;
		public boolean hit = false;

		public Effect(Type type, int x, int y, int hw) {
			Type = type;
			this.x = x > 100 ? 100 : x < 0 ? 0 : x;
			this.y = y;
			this.hw = hw;
		}

		@Override
		public int compareTo(Effect arg0) {
			return ((Integer) y).compareTo(arg0.y);
		}
	}

}
