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
public class GameRoundDBSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_GAME_ROUND = "game_round";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TIMESTAMP = "time_stamp";
	
	public static final String COLUMN_PLAYER1 = "player1";
	public static final String COLUMN_SCORE1 = "player1_score";
	public static final String COLUMN_DISTANCE1 = "player1_distance";
	public static final String COLUMN_SHIP1 = "player1_ship";
	public static final String COLUMN_COLOR1 = "player1_color";
	
	public static final String COLUMN_PLAYER2 = "player2";
	public static final String COLUMN_SCORE2 = "player2_score";
	public static final String COLUMN_DISTANCE2 = "player2_distance";
	public static final String COLUMN_SHIP2 = "player2_ship";
	public static final String COLUMN_COLOR2 = "player2_color";
	
	public static final String COLUMN_PLAYER3 = "player3";
	public static final String COLUMN_SCORE3 = "player3_score";
	public static final String COLUMN_DISTANCE3 = "player3_distance";
	public static final String COLUMN_SHIP3 = "player3_ship";
	public static final String COLUMN_COLOR3 = "player3_color";
	
	public static final String COLUMN_PLAYER4 = "player4";
	public static final String COLUMN_SCORE4 = "player4_score";
	public static final String COLUMN_DISTANCE4 = "player4_distance";
	public static final String COLUMN_SHIP4 = "player4_ship";
	public static final String COLUMN_COLOR4 = "player4_color";
	
	private static final String DATABASE_NAME = "player.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create table "
		      + TABLE_GAME_ROUND + " (" 
			  + COLUMN_ID + " integer primary key autoincrement, " 
		      + COLUMN_TIMESTAMP + " text not null,"
		      + COLUMN_PLAYER1 + " text not null,"
		      + COLUMN_SCORE1 + " integer not null,"
		      + COLUMN_DISTANCE1 + " integer not null,"
		      + COLUMN_SHIP1 + " text,"
		      + COLUMN_COLOR1 + " text,"
		      + COLUMN_PLAYER2 + " text,"
		      + COLUMN_SCORE2 + " integer,"
		      + COLUMN_DISTANCE2 + " integer,"
		      + COLUMN_SHIP2 + " text,"
		      + COLUMN_COLOR2 + " text,"
		      + COLUMN_PLAYER3 + " text,"
		      + COLUMN_SCORE3 + " integer,"
		      + COLUMN_DISTANCE3 + " integer,"
		      + COLUMN_SHIP3 + " text,"
		      + COLUMN_COLOR3 + " text,"
		      + COLUMN_PLAYER4 + " text,"
		      + COLUMN_SCORE4 + " integer,"
		      + COLUMN_DISTANCE4 + " integer,"
		      + COLUMN_SHIP4 + " text,"
		      + COLUMN_COLOR4 + " text" + ");";
		      
		      
	public GameRoundDBSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(GameRoundDBSQLiteHelper.class.getName(),
	            "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_ROUND);
	        onCreate(db);
	}

}
