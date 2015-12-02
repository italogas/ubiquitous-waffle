package com.androidproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	public RefreshHandler Updater;
	private Activity activity; // to display Game Over dialog in GUI thread
	private boolean dialogIsDisplayed = false;
	// variables for the game loop and tracking statistics
	private boolean gameOver; // is the game over?
	private double timeLeft; // the amount of time left in seconds
	public int initialTime = 10; // the amount of time in seconds RAFA
	private float puckXVelocity; // blocker speed multiplier during game
	private float puckYVelocity; // blocker speed multiplier during game
	private float backVelocity; // blocker speed multiplier during game
	private float backInitialVelocity; // blocker speed multiplier during game
	private Position planePos;
	private Position infoPos;
	private Position backgPos;
	private int totalDistance;
	private int actualDistance;

	private int contentViewTop; // size of status bar
	private int screenWidth; // width of the screen
	private int screenHeight; // height of the screen
	// constants and variables for managing sounds
	private static final int FIRE = 0;
	private static final int MARIO = 1;
	private SoundPool soundPool; // plays sound effects
	private SparseIntArray soundMap; // maps IDs to SoundPool
	// Paint variables used when drawing each item on the screen
	private Paint textPaint; // Paint used to draw text
	private Paint backgroundPaint; // Paint used to clear the drawing area
	
	Bitmap thebmp = BitmapFactory.decodeResource(getResources(), R.drawable.grass);
	//int inc = 0;
	Bitmap tempPlaneBmp;
	Bitmap planeBmp;
	Bitmap FinishLineBmp = BitmapFactory.decodeResource(getResources(), R.drawable.finishline);
	Bitmap backg;
	Random r = new Random();
	ArrayList<Effect> Effects = new ArrayList<Effect>();
	SurfaceHolder holder;

	private boolean do321 = false;
	private double doing321 = 4000;
	private int doing321MaxSize;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs); // call super's constructor
		Log.v("init", "const");
		// setZOrderOnTop(true);
		activity = (Activity) context;
		// register SurfaceHolder.Callback listener
		holder = getHolder();
		holder.addCallback(this);
		// holder.setFormat(PixelFormat.TRANSPARENT);

		// initialize SoundPool to play the app's three sound effects
		soundPool = new SoundPool(99, AudioManager.STREAM_MUSIC, 0);
		// create Map of sounds and pre-load sounds (load returns a sound_ID)
		soundMap = new SparseIntArray();// <Integer, Integer>(); // create new
										// HashMap
		soundMap.put(FIRE, soundPool.load(context, R.raw.cannon_fire, 1));
		soundMap.put(MARIO, soundPool.load(context, R.raw.mario, 1));

		// construct Paints for drawing text, cannonball, cannon,
		// blocker and target; these are configured in method onSizeChanged
		textPaint = new Paint(); // Paint for drawing text
		backgroundPaint = new Paint(); // Paint for drawing the target
		planePos = new Position();
		infoPos= new Position();
		backgPos= new Position();
		
		
		
		
		
	} // end CannonView constructor

	// called by surfaceChanged when the size of the SurfaceView changes,
	// such as when it's first added to the View hierarchy
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.v("init", "onsizechanged");
		// size
		planePos.hw = w > h ? w / 10 : h / 10;
		// position and rotation
		planePos.fx = w / 2;
		planePos.fy = 3 * h / 4;
		planePos.fr = 0;
		backInitialVelocity = 25;//h / 100;
		
		Window window = activity.getWindow();
		contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		//Log.v("window", contentViewTop+"");
		
		infoPos.x=w/100;
		infoPos.y=contentViewTop==0?h/15:contentViewTop;


		backgPos.fy=h / 100;
		
		screenWidth = w; // store the width
		screenHeight = h; // store the height
		
		doing321MaxSize=(int)(w*0.5f);
		
		// lineWidth = w / 24; // target and blocker 1/24 screen width

		// configure Paint objects for drawing game elements
		textPaint.setTextSize(w / 20); // text size 1/20 of screen width
		textPaint.setARGB(255, 255, 255, 255);
		textPaint.setAntiAlias(true); // smoothes the text
		backgroundPaint.setColor(Color.WHITE); // set background color
		backgroundPaint.setColor(Color.TRANSPARENT); // set background color
		// backgroundPaint.set()Color(Color.WHITE); // set background color
		// newGame(); // set up and start a new game
		
		backg = Bitmap.createScaledBitmap(thebmp, screenWidth, screenHeight, false);
		planeBmp = BitmapFactory.decodeResource(getResources(), R.drawable.plane);

		planeBmp = Bitmap.createScaledBitmap(planeBmp, planePos.hw, planePos.hw, false);

		FinishLineBmp = Bitmap.createScaledBitmap(FinishLineBmp, screenWidth,
				FinishLineBmp.getHeight() * screenWidth / FinishLineBmp.getWidth(), false);
		
		
	} // end method onSizeChanged


	// reset all the screen elements and start a new game
	public void newGame() {
		backVelocity = backInitialVelocity;
		actualDistance = 0;
		totalDistance = 35000;
		
		
		int NumOfEffects = r.nextInt((int) (totalDistance <= 2500 ? 2 : 1 + totalDistance / 2500f));
		int NumOfBoosts = r.nextInt(NumOfEffects == 0 ? 1 : NumOfEffects);

		 for(int i=0;i<NumOfEffects;i++){
		 if(i<NumOfBoosts)
		 Effects.add(new Effect(Type.Boost,
		 r.nextInt(100),r.nextInt(totalDistance/1000)*1000));
		 else
		 Effects.add(new Effect(Type.Harm, r.nextInt(100),
		 r.nextInt(totalDistance/1000)*1000));
		 }
		 Collections.sort(Effects);

//		for (int i = 0; i < 35000; i += 3000)
//			Effects.add(new Effect(Type.Boost, 50, i));
		
		
		//inc = 0;
		backgPos.y=0;
		
		// puckPos.hw=w>h?w/20:h/20;
		planePos.x = planePos.fx;
		planePos.y = planePos.fy;
		planePos.r = 0;
		puckXVelocity = 0;// initialBlockerVelocity;
		puckYVelocity = 0;// initialBlockerVelocity;
		timeLeft = 10; // start the countdown at 10 seconds
		timeLeft = initialTime; // start the countdown at 10 seconds RAFA

		// if (gameOver) {
		gameOver = false; // the game is not over
		// cannonThread = new CannonThread(getHolder());
		// cannonThread.start();
		// } // end if
		for (Effect ef : Effects) {
			Log.v("ei", ef.X + "x" + ef.Y);
		}
		
		start();
	} // end method newGame

	public void start() {

		

		


	

		soundPool.play(soundMap.get(MARIO), 0.5f, 0.5f, 1, 9999, 1f);

		do321 = true;

		Updater = new RefreshHandler(holder);
		Updater.setRunning(true);

	}
	double sum=0;
	double qnts=0;
	

	// called repeatedly by the CannonThread to update game elements
	// private void updatePositions(double elapsedTimeMS) {
	private void update(Canvas canvas, double elapsedTimeMS) {
		// double interval = elapsedTimeMS / 1000.0; // convert to seconds
		if (!gameOver) {
			int offset = 25;
			qnts+=1;
			sum=elapsedTimeMS+sum;
			Log.v("time", (sum)/qnts+"q"+qnts);

			if (planePos.x - planePos.hw / 2 < 5 && puckXVelocity < 0
					|| planePos.x + planePos.hw / 2 > screenWidth - offset && puckXVelocity > 0) {
				// nothing
				planePos.r = 0;
				backVelocity = backInitialVelocity;
			} else {

				double acceleration = 1.15;

				planePos.x += (int) (puckXVelocity * acceleration);
				//planePos.y += (int) (puckYVelocity * acceleration);

				planePos.r = ((int) (360 + puckXVelocity)) % 360;

				backVelocity = (float) (backInitialVelocity * (0.5 + 0.5 * (45 - Math.abs(puckXVelocity)) / 45.0));

			}
			//inc += backVelocity;
			backgPos.y+=backgPos.fy;
			actualDistance += backVelocity;
			if (backgPos.y >= backg.getHeight())
				backgPos.y = 0;
			// if (puckPos.y - puckPos.hw / 2 < 5
			// || puckPos.y + puckPos.hw / 2 > screenHeight - offset
			// || puckPos.x - puckPos.hw / 2 < 5
			// || puckPos.x + puckPos.hw / 2 > screenWidth - offset) {
			//
			// // soundPool.play(soundMap.get(CANNON_SOUND_ID), 1, 1, 1, 0, 1f);
			//
			// }
			//Log.v("pq", totalDistance + "a" + actualDistance);
			if (totalDistance - actualDistance < 0) {
				Updater.setRunning(false);
				gameOver = true;
				//Log.v("pq", totalDistance + "aaa" + actualDistance);
			}
		}

		drawGameElements(canvas);
	} // end method updatePositions

	// Draw Game elements on canvas

	public void drawGameElements(Canvas canvas) {

		canvas.drawBitmap(backg, 0, (float) backgPos.y, null);
		canvas.drawBitmap(backg, 0, (float) -backg.getHeight() + backgPos.y, null);

		Matrix matrix = new Matrix();
		matrix.setRotate(planePos.r);
		tempPlaneBmp = Bitmap.createBitmap(planeBmp, 0, 0, planePos.hw, planePos.hw, matrix, false);

		int poisonedDistance = actualDistance + planePos.y - (tempPlaneBmp.getHeight() / 2);

		// if (totalDistance - actualDistance < puckPos.y) {
		if (totalDistance < poisonedDistance) { // FinishLine must appear in the
												// screen
			// tempPlaneBmp = Bitmap.createBitmap(FinishLineBmp, 0, 0,
			// screenWidth-20,
			// screenWidth/FinishLineBmp.getWidth()*FinishLineBmp.getHeight());

			canvas.drawBitmap(FinishLineBmp, 0,
					// (float) (puckPos.y - totalDistance + actualDistance),
					(float) (poisonedDistance - totalDistance), null);
			// Log.v("fim","A"+actualDistance+"y"+puckPos.y);
		}

		//////////

		for (Iterator<Effect> iterator = Effects.iterator(); iterator.hasNext();) {
			Effect ef = iterator.next();
			if (ef.Y < (poisonedDistance - screenHeight)) {// remove if it's no
															// longer visible
															// TODO include size
															// of image here (or
															// not)
				iterator.remove();
				// Log.v("eit","rem"+ef.X+"x"+ef.Y+"a"+actualDistance);
			} else if (ef.Y < poisonedDistance) {
				canvas.drawBitmap(tempPlaneBmp, (float) (ef.X * (screenWidth - tempPlaneBmp.getWidth()) / 100f),
						(float) ((poisonedDistance - ef.Y)), null);
				// Log.v("eit","des"+ef.X+"x"+ef.Y+"a"+actualDistance);
			}
		}

		canvas.drawBitmap(tempPlaneBmp, (float) (planePos.x - tempPlaneBmp.getWidth() / 2),
				(float) (planePos.y - tempPlaneBmp.getHeight() / 2), null);

		canvas.drawText(getResources().getString(R.string.distance, actualDistance / 1000.0, totalDistance / 1000.0),
				infoPos.x, infoPos.y, textPaint);
		//30, contentViewTop==0?50:contentViewTop, textPaint);
		

	} // end method drawGameElements

	private void doing321(Canvas canvas, double elapsedTimeMS) {
		drawGameElements(canvas);
		doing321 -= elapsedTimeMS * 2;
		String text = ((Integer) ((int) (doing321 / 1000))).toString();
		text = text.equals("0") ? "GO!" : text;
		Paint textp = new Paint();
		textp.setARGB(255, 0, 0, 255);
		textp.setTextSize((float) ((doing321 / 1000) % 1) * doing321MaxSize);
		textp.setAntiAlias(true); // smoothes the text
		Rect bounds = new Rect();
		textp.getTextBounds(text, 0, text.length(), bounds);

		canvas.drawText(text, screenWidth / 2 - bounds.width() / 2, screenHeight / 2 + bounds.height() / 2, textp);
		if (doing321 < elapsedTimeMS * 2) {
			do321 = false;
			doing321 = 4000;
		}
	}

//	public void movePlane(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
//
//		Point touchPoint1 = new Point((int) event1.getX(), (int) event1.getY());
//		Point touchPoint2 = new Point((int) event2.getX(), (int) event2.getY());
//		Log.v("ui", Math.abs(touchPoint1.x - planePos.x) + " " + planePos.hw / 2 + " "
//				+ Math.abs(touchPoint1.y - planePos.y) + " " + planePos.hw / 2);
//
//		if (Math.abs(touchPoint1.x - planePos.x) < planePos.hw
//				&& Math.abs(touchPoint1.y - planePos.y) < planePos.hw / 2 * 3
//				|| Math.abs(touchPoint2.x - planePos.x) < planePos.hw
//						&& Math.abs(touchPoint2.y - planePos.y) < planePos.hw / 2 * 3) {
//			puckXVelocity = (float) (velocityX / 30.0);
//			puckYVelocity = (float) (velocityY / 30.0);
//
//		}
//
//	}

	public void movePlane(float accel) {

		puckXVelocity = (float) (accel * 25.0 * 0.2);

	}
	// display an AlertDialog when the game ends
	// private void showGameOverDialog(int messageId) {
	// // create a dialog displaying the given String
	// final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
	// getContext());
	// dialogBuilder.setTitle(getResources().getString(messageId));
	// dialogBuilder.setCancelable(false);
	// // display number of shots fired and total time elapsed
	// dialogBuilder.setMessage("tt");//getResources().getString(R.string.results_format,
	// shotsFired, totalElapsedTime));
	// dialogBuilder.setPositiveButton("uu",//R.string.reset_game,
	// new DialogInterface.OnClickListener() {
	// // called when "Reset Game" Button is pressed
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialogIsDisplayed = false;
	// newGame(); // set up and start a new game
	// } // end method onClick
	// } // end anonymous inner class
	// ); // end call to setPositiveButton
	// activity.runOnUiThread(new Runnable() {
	// public void run() {
	// dialogIsDisplayed = true;
	// dialogBuilder.show(); // display the dialog
	// } // end method run
	// } // end Runnable
	// ); // end call to runOnUiThread
	// } // end method showGameOverDialog

	// stops the game
	public void stopGame() {

		if (Updater != null && getHolder().getSurface().isValid())
			Updater.setRunning(false);
		Log.v("aa", Updater.getRunning() + "stopGame");
		soundPool.autoPause();
	} // end method stopGame
		// resumes the game

	public void resumeGame() {

		Log.v("aa", "resumeGame");
		// if (cannonThread != null) {
		// while(holder.getSurface().isValid())
		soundPool.autoResume();
		Updater = new RefreshHandler(getHolder());
		Updater.setRunning(true);
		// }

	} // end method stopGame

	// releases resources; called by CannonGame's onDestroy method

	public void releaseResources() {
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
		Log.v("aa", "surfaceCreated");
		surfaceWorking = true;
		// if (!dialogIsDisplayed) {
		// Log.v("aa", "surfaceCreated2");
		// cannonThread = new CannonThread(holder);
		// cannonThread.setRunning(true);
		// cannonThread.start(); // start the game loop thread
		// } // end if
	} // end method surfaceCreated
		// called when the surface is destroyed

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// ensure that thread terminates properly
		surfaceWorking = false;
		Log.v("aa", "surfaceDestroyed");
		// Updater.setRunning(false);
		// end while
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
			// boolean go = true;
			if (surfaceWorking) {
				// go = false;
				try {
					canvas = surfaceHolder.lockCanvas(null);
					// lock the surfaceHolder for drawing
					synchronized (surfaceHolder) {

						// Log.v("aa", "TA" + threadIsRunning + "" + go);
						if (threadIsRunning) {
							long currentTime = System.currentTimeMillis();
							double elapsedTimeMS = currentTime - previousFrameTime;
							// totalElapsedTime += elapsedTimeMS / 1000.00;
							// updatePositions(elapsedTimeMS); // update game
							// state
							if (!do321)
								update(canvas, elapsedTimeMS);
							else
								doing321(canvas, elapsedTimeMS);
							// drawGameElements(canvas); // draw
							previousFrameTime = currentTime; // update
																// previous
																// time
						}
						// go = true;
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
		public int fx, fy, fr, x, y, hw, r; // r = rotation

		public Position() {
		}
	}

	enum Type {
		Boost, Harm
	}

	class Effect implements Comparable<Effect> {
		public Type Type;
		/**
		 * X is a value between 0 and 100, which is a position between the left
		 * and right of the screen
		 */
		public int X;
		public int Y;

		public Effect(Type type, int x, int y) {
			Type = type;
			X = x > 100 ? 100 : x < 0 ? 0 : x;
			Y = y;
		}

		@Override
		public int compareTo(Effect arg0) {
			return ((Integer) Y).compareTo(arg0.Y);
		}
	}

}
