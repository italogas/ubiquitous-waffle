package com.androidproject;


public class Player implements Comparable<Player> {
	public String name;
	public String ship;// 1, 2, 3
	public String color;// 0, 1, 2, 3
	public int distance;
	public int score;
	public String device;

	public Player(String device) {
		this.device = device;
	}

	public Player(String name, String ship, String color, int distance, int score) {
		this.name = name;
		this.ship = ship;
		this.color = color;
		this.distance = distance;
		this.score = score;
	}

	public Player(String name, String ship, String color, int distance, int score, String device) {
		this.name = name;
		this.ship = ship;
		this.color = color;
		this.distance = distance;
		this.score = score;
		this.device = device;
	}
	
	
// IN A INVERTED WAY, BECAUSE WHEN WE CALL COLLECTIONS.SORT, IT SORTS IN ASCENDING ORDER, WE WANT DESCENDING, THANKS. ~RAFA
	@Override
	public int compareTo(Player another) {
		return ((Integer) another.score).compareTo((Integer) this.score);
	}

	// SHIPCOLOR =  (SHIP-1)*4+COLOR
	public int getShipColor(){		
		return (Integer.parseInt(this.ship)-1)*4+Integer.parseInt(this.color);
	}
	public void setShipColor(int shipcolor){
		this.ship=((int)(Math.floor(shipcolor/4.0)+1))+"";
		this.color=(shipcolor%4)+"";
		
	}
	public static String convertfromShipColortoShip(int shipcolor){
		return ((int)(Math.floor(shipcolor/4.0))+1)+"";
		
	}
	public static String convertfromShipColortoColor(int shipcolor){
		return (shipcolor%4)+"";
		
	}
	public static String convertfromShipAndColorToShipColor(int ship, int color){
		return (int)((ship-1)*4+(color))+"";
			}
	public static String convertfromShipAndColorToShipColor(String ship, String color){
		return (int)((Integer.parseInt(ship)-1)*4+Integer.parseInt(color))+"";
		}
}