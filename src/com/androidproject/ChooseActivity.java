package com.androidproject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.androidproject.bd.GameRoundData;
import com.androidproject.bd.GameRoundsDataSource;
import com.androidproject.bd.PreferencesData;
import com.androidproject.bd.PreferencesDataSource;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ChooseActivity extends Activity {
	PreferencesDataSource preferencesDataSource;
	int chooseship = 1;
	int choosecolor = 0;
	ImageView imgv;
	EditText editName;
	EditText editDistance;
	ArrayList<Integer> shipsandcolors = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose);
		Button ship = (Button) findViewById(R.id.button1);
		Button color = (Button) findViewById(R.id.button2);
		imgv = (ImageView) findViewById(R.id.imageView1);
		editName = (EditText) findViewById(R.id.editText1);
		editDistance = (EditText) findViewById(R.id.editText2);
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

		ship.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				chooseship += 1;
				updateImg();
			}
		});
		color.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				choosecolor += 1;
				updateImg();
			}
		});

		// get database access point
		preferencesDataSource = new PreferencesDataSource(getApplicationContext());
		try {
			preferencesDataSource.open();
		} catch (SQLException e) {
			Log.e("DATABASE ERROR", e.getMessage());
		}

		// get all registered records in example file
		PreferencesData allRecords = preferencesDataSource.getAllRecords();
		editName.setText(allRecords.getPlayer1());
		editDistance.setText(allRecords.getPlayer1_distance() + "");
		choosecolor = Integer.parseInt(allRecords.getPlayer1_color());
		chooseship = Integer.parseInt(allRecords.getPlayer1_ship());
		updateImg();
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void updateImg() {
		choosecolor = choosecolor > 3 ? 0 : choosecolor;
		chooseship = chooseship > 3 ? 1 : chooseship;
		if (Build.VERSION.SDK_INT >= 21) {
			imgv.setImageDrawable(this.getDrawable(shipsandcolors.get((chooseship - 1) * 4 + choosecolor)));
		} else {
			imgv.setImageDrawable(getResources().getDrawable(shipsandcolors.get((chooseship - 1) * 4 + choosecolor)));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose, menu);
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
	public void onBackPressed() {
		preferencesDataSource.updatePrefs(
				editName.getText() + ":0:" + editDistance.getText() + ":" + chooseship + ":" + choosecolor);

		super.onBackPressed();
		overridePendingTransition(R.anim.upfront, R.anim.frontdown);
	}
}
