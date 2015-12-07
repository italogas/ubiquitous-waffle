package com.androidproject;

import java.util.ArrayList;

import com.androidproject.bluetooth.Connection;
import com.androidproject.bluetooth.Connection.OnConnectionLostListener;
import com.androidproject.bluetooth.Connection.OnConnectionServiceReadyListener;
import com.androidproject.bluetooth.Connection.OnIncomingConnectionListener;
import com.androidproject.bluetooth.Connection.OnMaxConnectionsReachedListener;
import com.androidproject.bluetooth.Connection.OnMessageReceivedListener;
import com.androidproject.bluetooth.ServerListActivity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.Toast;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		Button btserver =  (Button)findViewById(R.id.button1);
		Button btclient =  (Button)findViewById(R.id.button2);
		Button btsend =  (Button)findViewById(R.id.button3);
		

        self = this;
       
        mPaddlePoints = new ArrayList<Point>();
        mPaddleTimes = new ArrayList<Long>();

        //Intent startingIntent = getIntent();
       // mType = startingIntent.getIntExtra("TYPE", 0);
btserver.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View v) {
		 mType =0;
		  mConnection = new Connection(self, serviceReadyListener);
	}
});
btclient.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View v) {
		 mType =1;
		  mConnection = new Connection(self, serviceReadyListener);
	}
});
       // setContentView(R.layout.main);
       // mSurface = (SurfaceView) findViewById(R.id.surface);
     //   mHolder = mSurface.getHolder();

    

       // mPlayer = MediaPlayer.create(this, R.raw.collision);

       
		
		

      
      //  mHolder.addCallback(self);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static final String TAG = "AirHockey";

    private static final int SERVER_LIST_RESULT_CODE = 42;

    public static final int UP = 3;

    public static final int DOWN = 4;

    public static final int FLIPTOP = 5;

    private TestActivity self;

    private int mType; // 0 = server, 1 = client

    private SurfaceView mSurface;

    private SurfaceHolder mHolder;

    private Paint bgPaint;

    private Paint goalPaint;

    private Paint ballPaint;

    private Paint paddlePaint;

    //private PhysicsLoop pLoop;

    private ArrayList<Point> mPaddlePoints;

    private ArrayList<Long> mPaddleTimes;

    private int mPaddlePointWindowSize = 5;

    private int mPaddleRadius = 55;

    private Bitmap mPaddleBmp;

    //private Demo_Ball mBall;

    private int mBallRadius = 40;

    private Connection mConnection;

    private String rivalDevice = "";

    private SoundPool mSoundPool;

    private int tockSound = 0;

    private MediaPlayer mPlayer;

    private int hostScore = 0;

    private int clientScore = 0;

    private OnMessageReceivedListener dataReceivedListener = new OnMessageReceivedListener() {
        @Override
		public void OnMessageReceived(String device, String message) {
        	
            if (message.indexOf("SCORE") == 0) {
                String[] scoreMessageSplit = message.split(":");
                hostScore = Integer.parseInt(scoreMessageSplit[1]);
                clientScore = Integer.parseInt(scoreMessageSplit[2]);
                showScore();
            } else {
              //  mBall.restoreState(message);
            }
        }
    };

    private OnMaxConnectionsReachedListener maxConnectionsListener = new OnMaxConnectionsReachedListener() {
        @Override
		public void OnMaxConnectionsReached() {

        }
    };

    private OnIncomingConnectionListener connectedListener = new OnIncomingConnectionListener() {
        @Override
		public void OnIncomingConnection(String device) {
            rivalDevice = device;
            WindowManager w = getWindowManager();
            Display d = w.getDefaultDisplay();
            int width = d.getWidth();
            int height = d.getHeight();
//            mBall = new Demo_Ball(true, width, height - 60);
//            mBall.putOnScreen(width / 2, (height / 2 + (int) (height * .05)), 0, 0, 0, 0, 0);
        }
    };

    private OnConnectionLostListener disconnectedListener = new OnConnectionLostListener() {
        @Override
		public void OnConnectionLost(String device) {
            class displayConnectionLostAlert implements Runnable {
                @Override
				public void run() {
                    Builder connectionLostAlert = new Builder(self);

                    connectionLostAlert.setTitle("Connection lost");
                    connectionLostAlert
                            .setMessage("Your connection with the other player has been lost.");

                    connectionLostAlert.setPositiveButton("Ok", new OnClickListener() {
                        @Override
						public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    connectionLostAlert.setCancelable(false);
                    try {
                    connectionLostAlert.show();
                    } catch (BadTokenException e){
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

    private OnConnectionServiceReadyListener serviceReadyListener = new OnConnectionServiceReadyListener() {
        @Override
		public void OnConnectionServiceReady() {
            if (mType == 0) {
                mConnection.startServer(1, connectedListener, maxConnectionsListener,
                        dataReceivedListener, disconnectedListener);
                self.setTitle("Game: " + mConnection.getName() + "-" + mConnection.getAddress());
            } else {
                WindowManager w = getWindowManager();
                Display d = w.getDefaultDisplay();
                int width = d.getWidth();
                int height = d.getHeight();
                //mBall = new Demo_Ball(false, width, height - 60);
                Intent serverListIntent = new Intent(self, ServerListActivity.class);
                startActivityForResult(serverListIntent, SERVER_LIST_RESULT_CODE);
            }
        }
    };

    
    
    
    
    
    
    

    @Override
    protected void onDestroy() {
        if (mConnection != null) {
            mConnection.shutdown();
        }
        if (mPlayer != null) {
            mPlayer.release();
        }
        super.onDestroy();
    }

//    @Override
//	public void surfaceCreated(SurfaceHolder holder) {
//        pLoop = new PhysicsLoop();
//        pLoop.start();
//    }
//
//    private void draw() {
//        Canvas canvas = null;
//        try {
//            canvas = mHolder.lockCanvas();
//            if (canvas != null) {
//                doDraw(canvas);
//            }
//        } finally {
//            if (canvas != null) {
//                mHolder.unlockCanvasAndPost(canvas);
//            }
//        }
//    }

//    private void doDraw(Canvas c) {
//        c.drawRect(0, 0, c.getWidth(), c.getHeight(), bgPaint);
//        c.drawRect(0, c.getHeight() - (int) (c.getHeight() * 0.02), c.getWidth(), c.getHeight(),
//                goalPaint);
//
//        if (mPaddleTimes.size() > 0) {
//            Point p = mPaddlePoints.get(mPaddlePoints.size() - 1);
//
//            // Debug circle
//            // Point debugPaddleCircle = getPaddleCenter();
//            // c.drawCircle(debugPaddleCircle.x, debugPaddleCircle.y,
//            // mPaddleRadius, ballPaint);
//            if (p != null) {
//                c.drawBitmap(mPaddleBmp, p.x - 60, p.y - 200, new Paint());
//            }
//        }
//        if ((mBall == null) || !mBall.isOnScreen()) {
//            return;
//        }
//        float x = mBall.getX();
//        float y = mBall.getY();
//        if ((x != -1) && (y != -1)) {
//            float xv = mBall.getXVelocity();
//            Bitmap bmp = BitmapFactory
//                    .decodeResource(this.getResources(), R.drawable.android_right);
//            if (xv < 0) {
//                bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.android_left);
//            }
//
//            // Debug circle
//            Point debugBallCircle = getBallCenter();
//            // c.drawCircle(debugBallCircle.x, debugBallCircle.y, mBallRadius,
//            // ballPaint);
//
//            c.drawBitmap(bmp, x - 17, y - 23, new Paint());
//        }
//    }

//    @Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//        try {
//            pLoop.safeStop();
//        } finally {
//            pLoop = null;
//        }
//    }
//
//    private class PhysicsLoop extends Thread {
//        private volatile boolean running = true;
//
//        @Override
//        public void run() {
//            while (running) {
//                try {
//                    Thread.sleep(5);
//                    draw();
//                    if (mBall != null) {
//                        handleCollision();
//                        int position = mBall.update();
//                        mBall.setAcceleration(0, 0);
//                        if (position != 0) {
//                            if ((position == UP) && (rivalDevice.length() > 1)) {
//                                mConnection.sendMessage(rivalDevice, mBall.getState() + "|"
//                                        + FLIPTOP);
//                            } else if (position == DOWN) {
//                                if (mType == 0) {
//                                    clientScore = clientScore + 1;
//                                } else {
//                                    hostScore = hostScore + 1;
//                                }
//                                mConnection.sendMessage(rivalDevice, "SCORE:" + hostScore + ":"
//                                        + clientScore);
//                                showScore();
//                                WindowManager w = getWindowManager();
//                                Display d = w.getDefaultDisplay();
//                                int width = d.getWidth();
//                                int height = d.getHeight();
//                                mBall.putOnScreen(width / 2, (height / 2 + (int) (height * .05)),
//                                        0, 0, 0, 0, 0);
//                            } else {
//                                mBall.doRebound();
//                            }
//                        }
//
//                    }
//                } catch (InterruptedException ie) {
//                    running = false;
//                }
//            }
//        }
//
//        public void safeStop() {
//            running = false;
//            interrupt();
//        }
//    }
//
//    @Override
//	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((resultCode == Activity.RESULT_OK) && (requestCode == SERVER_LIST_RESULT_CODE)) {
            String device = data.getStringExtra(ServerListActivity.EXTRA_SELECTED_ADDRESS);
            int connectionStatus = mConnection.connect(device, dataReceivedListener,
                    disconnectedListener);
            if (connectionStatus != Connection.SUCCESS) {
                Toast.makeText(self, "Unable to connect; please try again.", 1).show();
            } else {
                rivalDevice = device;
            }
            return;
        }
    }


    private void showScore() {
        class showScoreRunnable implements Runnable {
            @Override
			public void run() {
                String scoreString = "";
                if (mType == 0) {
                    scoreString = hostScore + " - " + clientScore;
                } else {
                    scoreString = clientScore + " - " + hostScore;
                }
                Toast.makeText(self, scoreString, 0).show();
            }
        }
        self.runOnUiThread(new showScoreRunnable());
    }

    
    
    
    
    
    
    
    
}
