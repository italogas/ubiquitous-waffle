package com.androidproject.bd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Interface between bd operations and code
 * @author Ítalo
 *
 */
public class GameRoundsDataSource {

	private GameRoundDBSQLiteHelper gameRoundDBHelper;
	private SQLiteDatabase database;
	private String[] allColumns = { GameRoundDBSQLiteHelper.COLUMN_ID, GameRoundDBSQLiteHelper.COLUMN_TIMESTAMP,
			GameRoundDBSQLiteHelper.COLUMN_PLAYER1, GameRoundDBSQLiteHelper.COLUMN_SCORE1,
			GameRoundDBSQLiteHelper.COLUMN_DISTANCE1, GameRoundDBSQLiteHelper.COLUMN_SHIP1,
			GameRoundDBSQLiteHelper.COLUMN_COLOR1, GameRoundDBSQLiteHelper.COLUMN_PLAYER2, 
			GameRoundDBSQLiteHelper.COLUMN_SCORE2, GameRoundDBSQLiteHelper.COLUMN_DISTANCE2, 
			GameRoundDBSQLiteHelper.COLUMN_SHIP2, GameRoundDBSQLiteHelper.COLUMN_COLOR2,
			GameRoundDBSQLiteHelper.COLUMN_PLAYER3, GameRoundDBSQLiteHelper.COLUMN_SCORE3,
			GameRoundDBSQLiteHelper.COLUMN_DISTANCE3, GameRoundDBSQLiteHelper.COLUMN_SHIP3,
			GameRoundDBSQLiteHelper.COLUMN_COLOR3,
			GameRoundDBSQLiteHelper.COLUMN_PLAYER4, GameRoundDBSQLiteHelper.COLUMN_SCORE4,
			GameRoundDBSQLiteHelper.COLUMN_DISTANCE4, GameRoundDBSQLiteHelper.COLUMN_SHIP4,
			GameRoundDBSQLiteHelper.COLUMN_COLOR4};
	
	
	private Context context;

	public GameRoundsDataSource(Context context) {
		gameRoundDBHelper = new GameRoundDBSQLiteHelper(context);
		this.context = context;
	}
	
	public void open() throws SQLException {
		database = gameRoundDBHelper.getWritableDatabase();
		//addRecords();
	}
	
	public void close() {
		gameRoundDBHelper.close();
	}
    public boolean addRecords() {
    	ContentValues values = new ContentValues();
    	String inputFile = readInputFile();
    	//System.out.println(inputFile);
    	String[] emps = inputFile.split("/n");
    	//System.out.println(emps[0]);
    	int i=0;
    	while( i < emps.length) {
    		String[] empData = emps[i].split(":");
    		for (int j = 0; j<empData.length; j++) {
    			Log.v("FILE CONTENTS: " + j, empData[j].toString());
    		}
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_TIMESTAMP, empData[0]);
	    	
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_PLAYER1, empData[1]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_SCORE1, empData[2]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_DISTANCE1, empData[3]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_SHIP1, empData[4]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_COLOR1, empData[5]);
	    	
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_PLAYER2, empData[6]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_SCORE2, empData[7]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_DISTANCE2, empData[8]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_SHIP2, empData[9]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_COLOR2, empData[10]);
	    	
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_PLAYER3, empData[11]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_SCORE3, empData[12]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_DISTANCE3, empData[13]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_SHIP3, empData[14]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_COLOR3, empData[15]);
	    	
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_PLAYER4, empData[16]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_SCORE4, empData[17]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_DISTANCE4, empData[18]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_SHIP4, empData[19]);
	    	values.put(GameRoundDBSQLiteHelper.COLUMN_COLOR4, empData[20]);
	    	
    		long insertId = database.insert(GameRoundDBSQLiteHelper.TABLE_GAME_ROUND, null, values);
    		
    		if ( insertId == -1) { 
    			Log.v("DATABASE", "DATABASE INSERTION ERROR");
    		} else {
				Cursor query = database.query(GameRoundDBSQLiteHelper.TABLE_GAME_ROUND, allColumns, 
    					GameRoundDBSQLiteHelper.COLUMN_ID + " = " +insertId, null, null, null, null);
				query.moveToFirst();
				GameRoundData roundData = cursorToGameRoundData(query);
				Log.v("DBInsertion result", roundData.toString());
			}
    		
    		i++;
		}
       
        return true;
     }
    
    public String readInputFile() {
        String filename = "player_data.txt";
        InputStream fileInputStream = null;
		try {
			fileInputStream = context.getAssets().open(filename);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        StringBuffer buffer = new StringBuffer();
        
        if ( fileInputStream != null ) {
        	try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
				String string = "";
				while( (string = bufferedReader.readLine() ) != null) {
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
    
	public void insertRecord(GameRoundData round) {
		//TODO
	}
	
	public void deleteRecord(GameRoundData round) {
	    long id = round.getId();
	    Log.v("DATABASE", "Game data record: " + id + " deleted. ");
	    database.delete(GameRoundDBSQLiteHelper.TABLE_GAME_ROUND, 
	    		GameRoundDBSQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public List<GameRoundData> getAllRecords() {
	    List<GameRoundData> rounds = new ArrayList<GameRoundData>();

	    Cursor cursor = database.query(GameRoundDBSQLiteHelper.TABLE_GAME_ROUND,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      GameRoundData round = cursorToGameRoundData(cursor);
	      rounds.add(round);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return rounds;
	  }

	private GameRoundData cursorToGameRoundData(Cursor cursor) {
		 GameRoundData gameRoundData = new GameRoundData();
		 
		 gameRoundData.setId(cursor.getLong(0));
		 gameRoundData.setTime_stamp(cursor.getString(1));
		 //Player 1 data
		 gameRoundData.setPlayer1(cursor.getString(2));
		 gameRoundData.setPlayer1_score(cursor.getInt(3));
		 gameRoundData.setPlayer1_distance(cursor.getInt(4));
		 gameRoundData.setPlayer1_ship(cursor.getString(5));
		 gameRoundData.setPlayer1_color(cursor.getString(6));
		//Player 2 data
		 gameRoundData.setPlayer2(cursor.getString(7));
		 gameRoundData.setPlayer2_score(cursor.getInt(8));
		 gameRoundData.setPlayer2_distance(cursor.getInt(9));
		 gameRoundData.setPlayer2_ship(cursor.getString(10));
		 gameRoundData.setPlayer2_color(cursor.getString(11));
		//Player 3 data
		 gameRoundData.setPlayer3(cursor.getString(12));
		 gameRoundData.setPlayer3_score(cursor.getInt(13));
		 gameRoundData.setPlayer3_distance(cursor.getInt(14));
		 gameRoundData.setPlayer3_ship(cursor.getString(15));
		 gameRoundData.setPlayer3_color(cursor.getString(16));
		//Player 4 data
		 gameRoundData.setPlayer4(cursor.getString(17));
		 gameRoundData.setPlayer4_score(cursor.getInt(18));
		 gameRoundData.setPlayer4_distance(cursor.getInt(19));
		 gameRoundData.setPlayer4_ship(cursor.getString(20));
		 gameRoundData.setPlayer4_color(cursor.getString(21));
		 
		 return gameRoundData;
	}

}
