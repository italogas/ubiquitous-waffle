package com.androidproject.bd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Interface between bd operations and code
 * 
 * @author Ítalo
 *
 */
public class PreferencesDataSource {

	private PreferencesDBSQLiteHelper preferencesDBHelper;
	private SQLiteDatabase database;
	private String[] allColumns = { PreferencesDBSQLiteHelper.COLUMN_ID, PreferencesDBSQLiteHelper.COLUMN_PLAYER1,
			PreferencesDBSQLiteHelper.COLUMN_SCORE1, PreferencesDBSQLiteHelper.COLUMN_DISTANCE1,
			PreferencesDBSQLiteHelper.COLUMN_SHIP1, PreferencesDBSQLiteHelper.COLUMN_COLOR1 };

	private Context context;

	public PreferencesDataSource(Context context) {
		preferencesDBHelper = new PreferencesDBSQLiteHelper(context);
		this.context = context;
	}

	public void open() throws SQLException {
		database = preferencesDBHelper.getWritableDatabase();
	}

	public void close() {
		preferencesDBHelper.close();
	}

	public boolean addRecords() {
		return addPrefs(readInputFile());
	}

	/**
	 * @param value
	 *            The string with the values of Player, Score, Distance, Ship and Color, separated by ':'
	 * @return true if updated correctly.
	 */
	public boolean updatePrefs(String value) {
		ContentValues values = new ContentValues();
		String[] empData = value.split(":");
		for (int j = 0; j < empData.length; j++) {
			Log.v("PFILE CONTENTS: " + j, empData[j].toString());
		}

		values.put(PreferencesDBSQLiteHelper.COLUMN_PLAYER1, empData[0]);
		values.put(PreferencesDBSQLiteHelper.COLUMN_SCORE1, empData[1]);
		values.put(PreferencesDBSQLiteHelper.COLUMN_DISTANCE1, empData[2]);
		values.put(PreferencesDBSQLiteHelper.COLUMN_SHIP1, empData[3]);
		values.put(PreferencesDBSQLiteHelper.COLUMN_COLOR1, empData[4]);

		long insertId = database.update(PreferencesDBSQLiteHelper.TABLE_PREFERENCES, values,
				PreferencesDBSQLiteHelper.COLUMN_ID + " = ?", new String[] { "1" });

		if (insertId == -1) {
			Log.v("DATABASE", "DATABASE INSERTION ERROR");
			return false;
		} else {
			Cursor query = database.query(PreferencesDBSQLiteHelper.TABLE_PREFERENCES, allColumns,
					PreferencesDBSQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
			query.moveToFirst();
			PreferencesData roundData = cursorToPreferencesData(query);
			Log.v("DBInsertion result", roundData.toString());
			return true;
		}

	}

	private boolean addPrefs(String value) {
		ContentValues values = new ContentValues();
		String emps = value.split("/n")[0];
		String[] empData = emps.split(":");
		for (int j = 0; j < empData.length; j++) {
			Log.v("PFILE CONTENTS: " + j, empData[j].toString());
		}

		values.put(PreferencesDBSQLiteHelper.COLUMN_PLAYER1, empData[0]);
		values.put(PreferencesDBSQLiteHelper.COLUMN_SCORE1, empData[1]);
		values.put(PreferencesDBSQLiteHelper.COLUMN_DISTANCE1, empData[2]);
		values.put(PreferencesDBSQLiteHelper.COLUMN_SHIP1, empData[3]);
		values.put(PreferencesDBSQLiteHelper.COLUMN_COLOR1, empData[4]);

		long insertId = database.insert(PreferencesDBSQLiteHelper.TABLE_PREFERENCES, null, values);

		if (insertId == -1) {
			Log.v("DATABASE", "DATABASE INSERTION ERROR");
			return false;
		} else {
			Cursor query = database.query(PreferencesDBSQLiteHelper.TABLE_PREFERENCES, allColumns,
					PreferencesDBSQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
			boolean b = query.moveToFirst();
			Log.v("DBInsertion result0", b + "" + insertId + "");
			PreferencesData roundData = cursorToPreferencesData(query);
			Log.v("DBInsertion result", roundData.toString());
			return true;
		}

	}

	private String readInputFile() {
		String filename = "player_pref.txt";
		InputStream fileInputStream = null;
		try {
			fileInputStream = context.getAssets().open(filename);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		StringBuffer buffer = new StringBuffer();

		if (fileInputStream != null) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
				String string = "";
				while ((string = bufferedReader.readLine()) != null) {
					buffer.append(string + "/n");
				}
				if (buffer.length() == 0) {
					Log.w("FILE ERROR", "file: " + filename + "exists but is empty");
				} else {
					Log.w("FILE OK", "file: " + filename + " is not empty");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException i) {
				i.printStackTrace();
			}
		} else {
			Log.w("FILE ERROR", "file: " + filename + "does not exist");
		}

		return buffer.toString();
	}

	public void deleteRecord(PreferencesData prefs) {
		long id = prefs.getId();
		Log.v("DATABASE", "Game data record: " + id + " deleted. ");
		database.delete(PreferencesDBSQLiteHelper.TABLE_PREFERENCES, PreferencesDBSQLiteHelper.COLUMN_ID + " = " + id,
				null);
	}

	public PreferencesData getAllRecords() {
		PreferencesData prefs = new PreferencesData();
		Cursor cursor = database.query(PreferencesDBSQLiteHelper.TABLE_PREFERENCES, allColumns,
				PreferencesDBSQLiteHelper.COLUMN_ID + " = " + 1, null, null, null, null);

		boolean notempty = cursor.moveToFirst();
		if (!notempty) {
			addRecords();
			return getAllRecords();
		}
		while (!cursor.isAfterLast()) {
			prefs = cursorToPreferencesData(cursor);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return prefs;

	}

	private PreferencesData cursorToPreferencesData(Cursor cursor) {
		PreferencesData preferencesData = new PreferencesData();

		preferencesData.setId(cursor.getLong(0));
		// Player 1 data
		preferencesData.setPlayer1(cursor.getString(1));
		preferencesData.setPlayer1_score(cursor.getInt(2));
		preferencesData.setPlayer1_distance(cursor.getInt(3));
		preferencesData.setPlayer1_ship(cursor.getString(4));
		preferencesData.setPlayer1_color(cursor.getString(5));

		return preferencesData;
	}

}
