package com.androidproject.bd;


/**
 * Java object representation of GameRound data extracted from database		
 * @author Ítalo
 *
 */
public class GameRoundData {

	private long id;
	private String time_stamp;
	
	private String player1;
	private int player1_score;
	private int player1_distance;
	private String player1_ship;
	private String player1_color;
	
	private String player2;
	private int player2_score;
	private int player2_distance;
	private String player2_ship;
	private String player2_color;
	
	private String player3;
	private int player3_score;
	private int player3_distance;
	private String player3_ship;
	private String player3_color;
	
	private String player4;
	private int player4_score;
	private int player4_distance;
	private String player4_ship;
	private String player4_color;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPlayer1() {
		return player1;
	}
	public void setPlayer1(String player1) {
		this.player1 = player1;
	}
	public int getPlayer1_score() {
		return player1_score;
	}
	public void setPlayer1_score(int player1_score) {
		this.player1_score = player1_score;
	}
	public int getPlayer1_distance() {
		return player1_distance;
	}
	public void setPlayer1_distance(int player1_distance) {
		this.player1_distance = player1_distance;
	}
	public String getPlayer1_ship() {
		return player1_ship;
	}
	public void setPlayer1_ship(String player1_ship) {
		this.player1_ship = player1_ship;
	}
	public String getPlayer1_color() {
		return player1_color;
	}
	public void setPlayer1_color(String player1_color) {
		this.player1_color = player1_color;
	}
	public String getPlayer2() {
		return player2;
	}
	public void setPlayer2(String player2) {
		this.player2 = player2;
	}
	public int getPlayer2_score() {
		return player2_score;
	}
	public void setPlayer2_score(int player2_score) {
		this.player2_score = player2_score;
	}
	public int getPlayer2_distance() {
		return player2_distance;
	}
	public void setPlayer2_distance(int player2_distance) {
		this.player2_distance = player2_distance;
	}
	public String getPlayer2_ship() {
		return player2_ship;
	}
	public void setPlayer2_ship(String player2_ship) {
		this.player2_ship = player2_ship;
	}
	public String getPlayer2_color() {
		return player2_color;
	}
	public void setPlayer2_color(String player2_color) {
		this.player2_color = player2_color;
	}
	public String getPlayer3() {
		return player3;
	}
	public void setPlayer3(String player3) {
		this.player3 = player3;
	}
	public int getPlayer3_score() {
		return player3_score;
	}
	public void setPlayer3_score(int player3_score) {
		this.player3_score = player3_score;
	}
	public int getPlayer3_distance() {
		return player3_distance;
	}
	public void setPlayer3_distance(int player3_distance) {
		this.player3_distance = player3_distance;
	}
	public String getPlayer3_ship() {
		return player3_ship;
	}
	public void setPlayer3_ship(String player3_ship) {
		this.player3_ship = player3_ship;
	}
	public String getPlayer3_color() {
		return player3_color;
	}
	public void setPlayer3_color(String player3_color) {
		this.player3_color = player3_color;
	}
	public String getPlayer4() {
		return player4;
	}
	public void setPlayer4(String player4) {
		this.player4 = player4;
	}
	public int getPlayer4_score() {
		return player4_score;
	}
	public void setPlayer4_score(int player4_score) {
		this.player4_score = player4_score;
	}
	public int getPlayer4_distance() {
		return player4_distance;
	}
	public void setPlayer4_distance(int player4_distance) {
		this.player4_distance = player4_distance;
	}
	public String getPlayer4_ship() {
		return player4_ship;
	}
	public void setPlayer4_ship(String player4_ship) {
		this.player4_ship = player4_ship;
	}
	public String getPlayer4_color() {
		return player4_color;
	}
	public void setPlayer4_color(String player4_color) {
		this.player4_color = player4_color;
	}
	public String getTime_stamp() {
		return time_stamp;
	}
	public void setTime_stamp(String time_stamp) {
		this.time_stamp = time_stamp;
	}
	@Override
	public String toString() {
		return time_stamp + ", " + player1 + ", " + player1_score +
				", " + player1_distance + ", " + player1_ship +
				", " + player1_color + ", " + player2 +
				", " + player2_score + ", " + player2_distance +
				", " + player2_ship + ", " + player2_color + ", " + player3 +
				", " + player3_score + ", " + player3_distance +
				", " + player3_ship + ", " + player3_color + ", " + player4 +
				", " + player4_score + ", " + player4_distance +
				", " + player4_ship + ", " + player4_color;
	}
}
