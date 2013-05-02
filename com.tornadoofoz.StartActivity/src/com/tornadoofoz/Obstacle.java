package com.tornadoofoz;


/**
 * Class to hold the various obstacles.  Obstacles in order of "power:" 
 * Flower, Mailbox, Tree, Truck, Small House, Medium House, Water Tower, Big House, Barn
 */
public class Obstacle {
	float x;
	float y;

	float xSize;
	float ySize;
	
	int whichObstacle;
	
	int obstaclePower;
	
	/**
	 * For obstacles like the big house, we can add a "yOffset" value
	 * that will make part of the image non-collidable.
	 */
	float tallness;
	
	boolean destroyed;

	public Obstacle(int x,int y,int whichObstacle) {
		this.x=x;
		this.y=y;
		destroyed = false;
		tallness=0;
		this.whichObstacle=whichObstacle;
		
		switch(whichObstacle) {
		case MainActivity.OBS_FLOWER:
			xSize=1;
			ySize=1;
			obstaclePower=0;
			break;
		case MainActivity.OBS_MAILBOX:
			xSize=1;
			ySize=1;
			obstaclePower=1;
			break;
		case MainActivity.OBS_TREE:
			xSize=1;
			ySize=2;
			obstaclePower=2;
			break;
		case MainActivity.OBS_TRUCK:
			xSize=2;
			ySize=2;
			obstaclePower=3;
			break;
		case MainActivity.OBS_SMALL_HOUSE:
			xSize=2;
			ySize=2;
			obstaclePower=3;
			break;
		case MainActivity.OBS_MEDIUM_HOUSE:
			xSize=3;
			ySize=3;
			obstaclePower=4;
			break;
		case MainActivity.OBS_WATERTOWER:
			xSize=3;
			ySize=4;
			tallness=96;
			obstaclePower=5;
			break;
		case MainActivity.OBS_BIG_HOUSE:
			xSize=6;
			ySize=3;
			obstaclePower=6;
			break;
		case MainActivity.OBS_BARN:
			xSize = 6;
			ySize = 4;
			obstaclePower=7;
			break;
		case MainActivity.OBS_OZ:
			xSize = 30;
			ySize = 30;
			obstaclePower = 8;
			break;
		}
	}
}
