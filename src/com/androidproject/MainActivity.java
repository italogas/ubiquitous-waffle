package com.androidproject;


import android.R.anim;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	Animation upfront, downfront, frontup, frontdown;
	private Animation shakeit;
	private Animation fadeInfadeOut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		setContentView(R.layout.activity_main);
		
		getWindow().getDecorView().setBackgroundColor(Color.GRAY);
		
		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		Button btnDB = (Button) findViewById(R.id.btnDBQuery);
		Button btnPrefs = (Button) findViewById(R.id.btnPrefs);
		TextView txtGameTitle = (TextView) findViewById(R.id.txtGameTitle);

		frontdown = AnimationUtils.loadAnimation(this, R.anim.frontdown);
		downfront = AnimationUtils.loadAnimation(this, R.anim.downfront);
		frontup = AnimationUtils.loadAnimation(this, R.anim.frontup);
		upfront = AnimationUtils.loadAnimation(this, R.anim.upfront);
		fadeInfadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_in_fade_out);
		
		frontdown.setInterpolator(interp);
		downfront.setInterpolator(interp);
		frontup.setInterpolator(interp);
		upfront.setInterpolator(interp);
		
		frontdown.setFillAfter(true);
		downfront.setFillAfter(true);
		frontup.setFillAfter(true);
		upfront.setFillAfter(true);
		
		// Hiding other screens

		// setContentView(R.layout.activity_main);
		// get the CannonView
		// gameView = (GameView) findViewById(R.id.gameView);
		
		txtGameTitle.setAnimation(fadeInfadeOut);
		
		Log.v("aa", "oncreate");
		
		btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, GameActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.downfront, R.anim.frontup);
			}
		});
		btnDB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, DBActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.downfront, R.anim.frontup);
			}
		});
		btnPrefs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ChooseActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.downfront, R.anim.frontup);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.downfront, R.anim.frontup);
	}

	Interpolator interp = new Interpolator() {

		@Override
		public float getInterpolation(float arg0) {
			return arg0;
		}
	};

}
