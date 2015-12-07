package com.androidproject.bd;


/**
 * Java object representation of GameRound data extracted from database		
 * @author Ítalo
 *
 */
public class PreferencesData {

	private long id;
	
	private String player1;
	private int player1_score;
	private int player1_distance;
	private String player1_ship;
	private String player1_color;
	
	
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
	@Override
	public String toString() {
		return player1 + ", " + player1_score +
				", " + player1_distance + ", " + player1_ship +
				", " + player1_color;
	}
}
