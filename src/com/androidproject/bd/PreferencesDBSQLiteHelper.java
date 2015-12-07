package com.androidproject.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLiteHelper extension used for GameRound tables
 * @author Ítalo
 *
 */
public class PreferencesDBSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_PREFERENCES = "preferences";
	public static final String COLUMN_ID = "_id";
	
	public static final String COLUMN_PLAYER1 = "player1";
	public static final String COLUMN_SCORE1 = "player1_score";
	public static final String COLUMN_DISTANCE1 = "player1_distance";
	public static final String COLUMN_SHIP1 = "player1_ship";
	public static final String COLUMN_COLOR1 = "player1_color";
	
	private static final String DATABASE_NAME = "playerpref.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create table "
		      + TABLE_PREFERENCES + " (" 
			  + COLUMN_ID + " integer primary key autoincrement, "
		      + COLUMN_PLAYER1 + " text not null,"
		      + COLUMN_SCORE1 + " integer not null,"
		      + COLUMN_DISTANCE1 + " integer not null,"
		      + COLUMN_SHIP1 + " text,"
		      + COLUMN_COLOR1 + " text" + ");";
		      
		      
	public PreferencesDBSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(PreferencesDBSQLiteHelper.class.getName(),
	            "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES);
	        onCreate(db);
	}

}
