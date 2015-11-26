package com.androidproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	public CannonThread cannonThread; // controls the game loop
	private Activity activity; // to display Game Over dialog in GUI thread
	private boolean dialogIsDisplayed = false;
	// variables for the game loop and tracking statistics
	//private boolean gameOver; // is the game over?
	private double timeLeft; // the amount of time left in seconds
	public int initialTime = 10; // the amount of time in seconds RAFA
	private float puckXVelocity; // blocker speed multiplier during game
	private float puckYVelocity; // blocker speed multiplier during game
	private float backVelocity; // blocker speed multiplier during game
	private float backInitialVelocity; // blocker speed multiplier during game
	private Position puckPos;
	private int totalDistance;
	private int actualDistance;

	private int contentViewTop;
	private int lineWidth; // width of the target and blocker
	private int screenWidth; // width of the screen
	private int screenHeight; // height of the screen
	// constants and variables for managing sounds
	private static final int FIRE = 0;
	private static final int MARIO = 1;
	private SoundPool soundPool; // plays sound effects
	private SparseIntArray soundMap; // maps IDs to SoundPool
	// Paint variables used when drawing each item on the screen
	private Paint textPaint; // Paint used to draw text
	private Paint cannonPaint; // Paint used to draw the cannon
	private Paint blockerPaint; // Paint used to draw the blocker
	private Paint targetPaint; // Paint used to draw the target
	private Paint backgroundPaint; // Paint used to clear the drawing area

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs); // call super's constructor
		// setZOrderOnTop(true);
		activity = (Activity) context;
		// register SurfaceHolder.Callback listener
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		// holder.setFormat(PixelFormat.TRANSPARENT);

		// initialize SoundPool to play the app's three sound effects
		soundPool = new SoundPool(99, AudioManager.STREAM_MUSIC, 0);
		// create Map of sounds and pre-load sounds (load returns a sound_ID)
		soundMap = new SparseIntArray();// <Integer, Integer>(); // create new
										// HashMap
		soundMap.put(FIRE, soundPool.load(context, R.raw.cannon_fire, 1));
		soundMap.put(MARIO,	soundPool.load(context, R.raw.mario, 1));

		// construct Paints for drawing text, cannonball, cannon,
		// blocker and target; these are configured in method onSizeChanged
		textPaint = new Paint(); // Paint for drawing text
		cannonPaint = new Paint(); // Paint for drawing the cannon
		blockerPaint = new Paint(); // Paint for drawing the blocker
		targetPaint = new Paint(); // Paint for drawing the target
		backgroundPaint = new Paint(); // Paint for drawing the target
		puckPos = new Position();
	} // end CannonView constructor

	// called by surfaceChanged when the size of the SurfaceView changes,
	// such as when it's first added to the View hierarchy
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// size
		puckPos.hw = w > h ? w / 10 : h / 10;
		// position and rotation
		puckPos.fx = w / 2;
		puckPos.fy = h / 2;
		puckPos.fr = 0;
		backInitialVelocity = h / 100;
		backVelocity = backInitialVelocity;
		actualDistance = 0;
		totalDistance = 30000;

		screenWidth = w; // store the width
		screenHeight = h; // store the height
		lineWidth = w / 24; // target and blocker 1/24 screen width

		// configure Paint objects for drawing game elements
		textPaint.setTextSize(w / 20); // text size 1/20 of screen width
		textPaint.setARGB(255, 255, 255, 255);
		textPaint.setAntiAlias(true); // smoothes the text
		cannonPaint.setStrokeWidth(lineWidth * 1.5f); // set line thickness
		blockerPaint.setStrokeWidth(lineWidth); // set line thickness
		// targetPaint.setStrokeWidth(lineWidth); // set line thickness
		targetPaint.setStrokeWidth(lineWidth * 4); // set line thickness //RAFA
		backgroundPaint.setColor(Color.WHITE); // set background color
		backgroundPaint.setColor(Color.TRANSPARENT); // set background color
		// backgroundPaint.set()Color(Color.WHITE); // set background color
		newGame(); // set up and start a new game
	} // end method onSizeChanged

	Bitmap backg;

	// reset all the screen elements and start a new game
	public void newGame() {

		 start();
	} // end method newGame
	
	public void start(){
		
		Window window = activity.getWindow();
		contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT)
				.getTop();

		backg = Bitmap.createScaledBitmap(thebmp, screenWidth, screenHeight,
				false);
		planeBmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.plane);

		planeBmp = Bitmap.createScaledBitmap(planeBmp, puckPos.hw, puckPos.hw,
				false);
		// puckPos.hw=w>h?w/20:h/20;
		puckPos.x = puckPos.fx;
		puckPos.y = puckPos.fy;
		puckPos.r = 0;
		puckXVelocity = 0;// initialBlockerVelocity;
		puckYVelocity = 0;// initialBlockerVelocity;

		timeLeft = 10; // start the countdown at 10 seconds
		timeLeft = initialTime; // start the countdown at 10 seconds RAFA

		// if (gameOver) {
		// gameOver = false; // the game is not over
		// cannonThread = new CannonThread(getHolder());
		// cannonThread.start();
		// } // end if

		soundPool.play(soundMap.get(MARIO), 0.5f, 0.5f, 1, 9999, 1f);
	}

	// called repeatedly by the CannonThread to update game elements
	private void updatePositions(double elapsedTimeMS) {
		// double interval = elapsedTimeMS / 1000.0; // convert to seconds

		// puckXVelocity *= 0.991;
		// puckYVelocity *= 0.991;
		// puckPos.x += (int) (puckXVelocity * 0.2);
		// puckPos.y += (int) (puckYVelocity * 0.2);
		// puckPos.r += Math.abs(puckXVelocity) >= Math.abs(puckYVelocity) ?
		// Math
		// .abs(puckXVelocity) : Math.abs(puckYVelocity);

		int offset = 25;

		//
		// if (puckPos.y - puckPos.hw / 2 < 5)
		// puckYVelocity *= puckYVelocity > 0 ? 1 : -1;
		// if (puckPos.y + puckPos.hw / 2 > screenHeight - offset)
		// puckYVelocity *= puckYVelocity > 0 ? -1 : 1;

		if (puckPos.x - puckPos.hw / 2 < 5 && puckXVelocity < 0
				|| puckPos.x + puckPos.hw / 2 > screenWidth - offset
				&& puckXVelocity > 0) {
			// nothing
			puckPos.r = 0;
			backVelocity = backInitialVelocity;
		} else {

			// puckPos.x += (int) (puckXVelocity * 0.2);
			// puckPos.y += (int) (puckYVelocity * 0.2);

			double acceleration = 1.15;

			puckPos.x += (int) (puckXVelocity * acceleration);
			puckPos.y += (int) (puckYVelocity * acceleration);

			puckPos.r = (int) (360 + puckXVelocity);// > 0 ? 45 :
													// (Math.abs(puckXVelocity)<0)?0:315;
			puckPos.r %= 360;

			backVelocity = (float) (backInitialVelocity * (0.5 + 0.5 * (45 - Math
					.abs(puckXVelocity)) / 45.0));

			// puckPos.r += Math.abs(puckXVelocity) >= Math.abs(puckYVelocity) ?
			// Math
			// .abs(puckXVelocity) : Math.abs(puckYVelocity);
			// puckPos.r %= 360;
		}

		// puckXVelocity *= puckXVelocity > 0 ? 1 : -1;
		// if (puckPos.x + puckPos.hw / 2 > screenWidth - offset)
		// puckXVelocity *= puckXVelocity > 0 ? -1 : 1;

		if (puckPos.y - puckPos.hw / 2 < 5
				|| puckPos.y + puckPos.hw / 2 > screenHeight - offset
				|| puckPos.x - puckPos.hw / 2 < 5
				|| puckPos.x + puckPos.hw / 2 > screenWidth - offset) {

			// soundPool.play(soundMap.get(CANNON_SOUND_ID), 1, 1, 1, 0, 1f);

		}

		// Log.v("pos", puckPos.y + "y" + puckPos.x + "x" + puckPos.hw + "hw");

		// // if the target hit the top or bottom, reverse direction
		// if (target.start.y < 0 || target.end.y > screenHeight)
		// targetVelocity *= -1;
		// timeLeft -= interval; // subtract from time left
		// if the timer reached zero
		if (timeLeft <= 0.0) {
			timeLeft = 0.0;
			//gameOver = true; // the game is over
			// cannonThread.setRunning(false);
			// showGameOverDialog(R.string.lose); // show the losing dialog
		} // end if
	} // end method updatePositions

	public void movePuck(MotionEvent event1, MotionEvent event2,
			float velocityX, float velocityY) {

		Point touchPoint1 = new Point((int) event1.getX(), (int) event1.getY());
		Point touchPoint2 = new Point((int) event2.getX(), (int) event2.getY());
		Log.v("ui", Math.abs(touchPoint1.x - puckPos.x) + " " + puckPos.hw / 2
				+ " " + Math.abs(touchPoint1.y - puckPos.y) + " " + puckPos.hw
				/ 2);

		if (Math.abs(touchPoint1.x - puckPos.x) < puckPos.hw
				&& Math.abs(touchPoint1.y - puckPos.y) < puckPos.hw / 2 * 3
				|| Math.abs(touchPoint2.x - puckPos.x) < puckPos.hw
				&& Math.abs(touchPoint2.y - puckPos.y) < puckPos.hw / 2 * 3) {
			puckXVelocity = (float) (velocityX / 30.0);
			puckYVelocity = (float) (velocityY / 30.0);

		}

	}

	public void movePuck(float accel) {

		// Log.v("ui",Math.abs(touchPoint1.x-puckPos.x)+" "+puckPos.hw/2+" "+Math.abs(touchPoint1.y-puckPos.y)+" "+puckPos.hw/2);
		// if(Math.abs(puckXVelocity)>10)
		// puckXVelocity*=(float) (accel*1.05);
		// else
		puckXVelocity = (float) (accel * 25.0 * 0.2);
		// puckYVelocity=(float) (velocityY/30.0);

	}

	// Draw Game elements on canvas
	Bitmap thebmp = BitmapFactory.decodeResource(getResources(),
			R.drawable.grass);
	int inc = 0;
	Bitmap tempPlaneBmp;
	Bitmap planeBmp;

	public void drawGameElements(Canvas canvas) {
		// clear the background
		// canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		// canvas.drawColor(0, Mode.CLEAR);
		// canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
		// backgroundPaint);
		// Drawable d = getResources().getDrawable(R.drawable.hockey_arena);
		// d.setBounds(0, 0, screenWidth, screenHeight);
		// d.draw(canvas);

		canvas.drawBitmap(backg, 0, (float) inc, null);
		canvas.drawBitmap(backg, 0, (float) -backg.getHeight() + inc, null);
		inc += backVelocity;
		actualDistance += backVelocity;
		if (inc > backg.getHeight())
			inc = 0;
		// mybmp = BitmapFactory.decodeResource(getResources(),
		// R.drawable.hockey_arena);
		//
		// mybmp=Bitmap.createScaledBitmap(mybmp, screenWidth, screenHeight,
		// true);
		// canvas.drawBitmap(mybmp,0,0, null);

		// display time remaining
		// canvas.drawText(
		// getResources().getString(R.string.time_remaining_format,
		// timeLeft), 30, 50, textPaint);
		// canvas.drawText(""+timeLeft, 30, 50, textPaint);
		// if (cannonballOnScreen)
		// canvas.drawCircle(cannonball.x, cannonball.y, cannonballRadius,
		// cannonballPaint);

		Matrix matrix = new Matrix();

		matrix.setRotate(puckPos.r);// , (float)(puckPos.hw/2.0),
									// (float)(puckPos.hw/2.0));

		tempPlaneBmp = Bitmap.createBitmap(planeBmp, 0, 0, puckPos.hw,
				puckPos.hw, matrix, false);

		// //////////////
		// Canvas canv= new Canvas(mybmp);
		// canv.rotate(puckPos.r, (float)(puckPos.hw/2.0),
		// (float)(puckPos.hw/2.0));
		//

		// mybmp=Bitmap.createBitmap(mybmp,
		// (int)((Math.sqrt(2*puckPos.hw*puckPos.hw/4)-puckPos.hw/2)*(puckPos.r%45/45.0)),
		// (int)((Math.sqrt(2*puckPos.hw*puckPos.hw/4)-puckPos.hw/2)*(puckPos.r%45/45.0)),
		// puckPos.hw, puckPos.hw);
		// //////////////

		// matrix.setScale(puckPos.hw/222f, puckPos.hw/170f,0,0);

		// matrix.setScale(puckPos.hw, puckPos.hw);

		// matrix.postRotate(puckPos.r);
		//
		// matrix.postTranslate((float) (puckPos.x - mybmp.getWidth() / 2),
		// (float) (puckPos.y - mybmp.getHeight() / 2));

		// float[] values = new float[9];
		// matrix.getValues(values);
		// globalX = values[Matrix.MTRANS_X];
		// globalY = values[Matrix.MTRANS_Y];
		// width = values[Matrix.MSCALE_X]*imageWidth;
		// height = values[Matrix.MSCALE_Y]*imageHeight;

		// mybmp = Bitmap.createBitmap(mybmp, 0,
		// 0,(int)(values[Matrix.MSCALE_X]*222) ,(int)
		// (values[Matrix.MSCALE_Y]*170),
		// matrix, false);
		//
		// canvas.drawBitmap(mybmp,0,0,null);

		canvas.drawBitmap(tempPlaneBmp,
				(float) (puckPos.x - tempPlaneBmp.getWidth() / 2),
				(float) (puckPos.y - tempPlaneBmp.getHeight() / 2), null);

		canvas.drawText(
				getResources().getString(R.string.distance, actualDistance,
						totalDistance), 30, contentViewTop, textPaint);

		// draw the cannon barrel
		// canvas.drawLine(0, screenHeight / 2, barrelEnd.x, barrelEnd.y,
		// cannonPaint);
		// // draw the cannon base
		// canvas.drawCircle(0, (int) screenHeight / 2, (int) cannonBaseRadius,
		// cannonPaint);
		// draw the blocker
		// canvas.drawLine(blocker.start.x, blocker.start.y, blocker.end.x,
		// blocker.end.y, blockerPaint);
		// canvas.drawLine(blocker.start.x, blocker.start.y, blocker.end.x,
		// blocker.end.y, blockerPaint);

		// Point currentPoint = new Point(); // start of current target section
		// // initialize curPoint to the starting point of the target
		// currentPoint.x = target.start.x;
		// currentPoint.y = target.start.y;
		// // draw the target

		// if(i>3)
		// currentPoint.x -= lineWidth*4; //RAFA
		// end for
	} // end method drawGameElements

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

		Log.v("aa", "stopGame");
		if (cannonThread != null)
			cannonThread.setRunning(false);
		soundPool.release();
	} // end method stopGame
		// resumes the game

	public void resumeGame() {

		Log.v("aa", "resumeGame");
		if (cannonThread != null) {
			start();
			cannonThread.setRunning(true);

		}

	} // end method stopGame

	// releases resources; called by CannonGame's onDestroy method

	public void releaseResources() {
		soundPool.release(); // release all resources used by the SoundPool
		soundPool = null;
	} // end method releaseResources

	// called when surface changes size
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	} // end method surfaceChanged
		// called when surface is first created

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v("aa", "surfaceCreated");
		if (!dialogIsDisplayed) {
			Log.v("aa", "surfaceCreated2");
			cannonThread = new CannonThread(holder);
			cannonThread.setRunning(true);
			cannonThread.start(); // start the game loop thread
		} // end if
	} // end method surfaceCreated
		// called when the surface is destroyed

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// ensure that thread terminates properly
		boolean retry = true;
		Log.v("aa", "surfaceDestroyed");
		cannonThread.setRunning(false);
		while (retry) {
			try {
				cannonThread.join();
				retry = false;
			} // end try
			catch (InterruptedException e) {
				e.printStackTrace();
			} // end catch
		} // end while
	} // end method surfaceDestroyed

	// The nested CannonThread class
	public class CannonThread extends Thread {
		private SurfaceHolder surfaceHolder; // for manipulating canvas
		private boolean threadIsRunning = true; // running by default

		// initializes the surface holder

		public CannonThread(SurfaceHolder holder) {
			surfaceHolder = holder;
			setName("CannonThread");
		} // end constructor
			// changes running state

		public void setRunning(boolean running) {
			if (!threadIsRunning && running) {

				threadIsRunning = running;
				function();
			}

			threadIsRunning = running;

		} // end method setRunning
			// controls the game loop

		public boolean getRunning() {
			return threadIsRunning;
		} // end method getRunning
			// controls the game loop

		private void function() {

			Canvas canvas = null; // used for drawing
			long previousFrameTime = System.currentTimeMillis();
			while (threadIsRunning) {
				try {
					canvas = surfaceHolder.lockCanvas(null);
					// lock the surfaceHolder for drawing
					synchronized (surfaceHolder) {
						long currentTime = System.currentTimeMillis();
						double elapsedTimeMS = currentTime - previousFrameTime;
						// totalElapsedTime += elapsedTimeMS / 1000.00;
						updatePositions(elapsedTimeMS); // update game state
						drawGameElements(canvas); // draw
						previousFrameTime = currentTime; // update previous time
					} // end synchronized block
				} // end try
				catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (canvas != null)
						surfaceHolder.unlockCanvasAndPost(canvas);
				} // end finally
			} // end while
		}

		@Override
		public void run() {
			function();
		} // end method run
	} // end nested class CannonThread

	class Position {
		public int fx, fy, fr, x, y, hw, r; // r = rotation

		public Position() {

		}

	}

}
