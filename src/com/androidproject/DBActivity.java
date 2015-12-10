package com.androidproject;

import java.sql.SQLException;
import java.util.List;

import com.androidproject.bd.CustomAdapter;
import com.androidproject.bd.GameRoundData;
import com.androidproject.bd.GameRoundsDataSource;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DBActivity extends Activity {

	private GameRoundsDataSource gameRoundsDataSource;
	private ListView listView;
	private ArrayAdapter<GameRoundData> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_db);
		
		getWindow().getDecorView().setBackgroundColor(Color.GRAY);

		// get database access point
		gameRoundsDataSource = new GameRoundsDataSource(getApplicationContext());
		try {
			gameRoundsDataSource.open();
		} catch (SQLException e) {
			Log.e("DATABASE ERROR", e.getMessage());
		}

		// get all registered records in example file
		List<GameRoundData> allRecords = gameRoundsDataSource.getAllRecords();
		gameRoundsDataSource.close();
		adapter = new ArrayAdapter<GameRoundData>(this, android.R.layout.simple_list_item_1, allRecords);
		CustomAdapter ca =  new CustomAdapter(this, allRecords, getApplicationContext().getResources());
		listView = (ListView) findViewById(R.id.listView);

		listView.setAdapter(ca);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.db, menu);
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
		super.onBackPressed();
		overridePendingTransition(R.anim.upfront, R.anim.frontdown);
	}
}
