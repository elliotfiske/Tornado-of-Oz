package com.tornadoofoz;

public class Powerup {
	float x;
	float y;

	float xSize = 1;
	float ySize = 1;
	boolean destroyed;
	int whichPowerup;
	
	public Powerup(int x, int y, int whichPowerup) {
		this.x = x;
		this.y = y;
		this.whichPowerup = whichPowerup;
		destroyed = false;
		
		switch(whichPowerup) {
		case MainActivity.LION:
			xSize=3;
			ySize=3;
			break;
		case MainActivity.SCARECROW:
			xSize=3;
			ySize=3;
			break;
		case MainActivity.TIN_MAN:
			xSize = 3;
			ySize=3;
			break;
		}
		
	}
}
