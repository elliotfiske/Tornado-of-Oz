package com.tornadoofoz;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity implements SensorEventListener{

	/**
	 * How big is each tile in pixels?
	 */
	public static int TILE_SIZE = 20;
	public static final int GRASS_ID_1 = 1;
	public static final int GRASS_ID_2 = 2;
	public static final int GRASS_ID_3 = 3;
	public static final int MOUNTAIN_ID = 69;

	/**
	 * How many tiles wide is the map? (includes mountains)
	 */
	public static final int ARRAY_SIZE = 402;
	private static final int NUM_SECTIONS = (ARRAY_SIZE-2)/30; //take off the 2! mountains aren't counted here.
	private static final int SECTION_SIZE = (ARRAY_SIZE-2)/NUM_SECTIONS;

	public static final int OBS_FLOWER = 0;
	public static final int OBS_MAILBOX = 1;
	public static final int OBS_TREE = 2;
	public static final int OBS_TRUCK = 3;
	public static final int OBS_SMALL_HOUSE = 4;
	public static final int OBS_MEDIUM_HOUSE = 5;
	public static final int OBS_BIG_HOUSE = 6;
	public static final int OBS_WATERTOWER = 7;
	public static final int OBS_BARN = 8;
	public static final int OBS_OZ = 9;
	public static final int POW_BLUE_GEM = 10;
	public static final int POW_RED_GEM = 11;
	public static final int POW_GREEN_GEM = 12;
	public static final int TIN_MAN = 13;
	public static final int LION = 14;
	public static final int SCARECROW = 15;

	SharedPreferences settings;
	SharedPreferences.Editor editor;
	public static final String PREFS_NAME = "MyPrefs";

	private static final int OBSTACLES_PER_SECTION = 20;
	private static final int POWERUPS_PER_SECTION = 4; 


	TornadoDrawView mDrawView;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	int mWidth;
	int mHeight;
	static Vibrator v;
	int[][] map = new int[ARRAY_SIZE][ARRAY_SIZE];
	int[][] obsMap = new int[ARRAY_SIZE-2][ARRAY_SIZE-2];
	int[][] difficultyMap = new int[ARRAY_SIZE/20][ARRAY_SIZE/20];
	public float agility;; //TODO: reset this
	ArrayList<Obstacle> mObstacles = new ArrayList<Obstacle>();
	ArrayList<Powerup> mPowerups = new ArrayList<Powerup>();

	boolean lionCollected;
	boolean scarecrowCollected;
	boolean tinmanCollected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		agility = settings.getInt("agility", 8);
		lionCollected = settings.getBoolean("lion", false);
		scarecrowCollected = settings.getBoolean("scarecrow", false);
		tinmanCollected = settings.getBoolean("tinman", false);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		mHeight = metrics.heightPixels;
		mWidth = metrics.widthPixels;

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		createMap(ARRAY_SIZE,ARRAY_SIZE);


		generateDifficulty();
		Log.d("DEBUG", "Generated Difficulty!");

		for(int i = 0; i <= NUM_SECTIONS; i++) {
			for(int j = 0; j <= NUM_SECTIONS; j++) {
				generateObstacles(1 + i*SECTION_SIZE, 1 + j*SECTION_SIZE);
				generatePowerups(1 + i*SECTION_SIZE, 1 + j*SECTION_SIZE);

			}
		}

		generateCharacters();

		if(lionCollected && scarecrowCollected && tinmanCollected) {
			generateOz();
		}





		Log.d("DEBUG", "STARTIN UP");
		mDrawView = new TornadoDrawView(this, mWidth, mHeight, map, mObstacles, mPowerups, this);

		setContentView(mDrawView);
		mDrawView.requestFocus();

		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void generateOz() {
		// TODO Auto-generated method stub

	}

	private void generateCharacters() {
		if(!lionCollected) {
			while(true) {
				double randX = (ARRAY_SIZE * 1/4) * Math.random() + ARRAY_SIZE * 1/2;
				double randY = (ARRAY_SIZE * 1/4) * Math.random() + ARRAY_SIZE * 1/2;		

				if(obsMap[(int)randX][(int)randY] != 0){
					//there was a conflict.  Try again PAL.
					Log.d("DEBUG","Lion conflict");
					continue;
				}
				Log.d("DEBUG","Lion at " + randX + ", " + randY);

				mPowerups.add(new Powerup((int)randX,(int)randY,LION));
				break;
			}
		}

		if(!scarecrowCollected) {
			while(true){
				double randX = (ARRAY_SIZE * 2/3) * Math.random() + ARRAY_SIZE * 1/6;
				double randY = (ARRAY_SIZE * 2/3) * Math.random() + ARRAY_SIZE * 1/6;			

				if(obsMap[(int)randX][(int)randY] != 0){
					//there was a conflict. Try again buddy.
					Log.d("DEBUG","Scarecrow conflict");
					continue;
				}
				Log.d("DEBUG","Scarecrow at " + randX + ", " + randY);
				mPowerups.add(new Powerup((int)randX,(int)randY,SCARECROW));
				break;
			}
		}

		if(!tinmanCollected) {

			while(true) {
				double randX = (ARRAY_SIZE - 2) * Math.random()	 + 1; 
				double randY = (ARRAY_SIZE - 2) * Math.random()	 + 1;
				if(obsMap[(int)randX][(int)randY] != 0){
					//there was a conflict.  Try again mate.
					Log.d("DEBUG","Tin man conflict");
					continue;
				}
				Log.d("DEBUG","Tin man at " + randX + ", " + randY);
				mPowerups.add(new Powerup((int)randX,(int)randY,TIN_MAN));
				break;
			}
		}

	}

	/**
	 * Make the DIFFICULTY of each section of the map
	 */
	private void generateDifficulty() {


		for(int i = 0; i < NUM_SECTIONS; i++) {
			for(int j = 0; j < NUM_SECTIONS; j++) {
				difficultyMap[i][j] = 5;
			}
		}


		for(int i = NUM_SECTIONS * 1/10; i < NUM_SECTIONS * 9/10; i++) {
			for(int j = NUM_SECTIONS * 1/10; j < NUM_SECTIONS * 9/10; j++)  {
				difficultyMap[i][j] = 4;
			}
		}

		for(int i = NUM_SECTIONS * 2/10; i < NUM_SECTIONS * 8/10; i++) {
			for(int j = NUM_SECTIONS * 2/10; j < NUM_SECTIONS * 8/10; j++)  {
				difficultyMap[i][j] = 3;
			}
		}

		for(int i = NUM_SECTIONS * 3/10; i < NUM_SECTIONS * 7/10; i++) {
			for(int j = NUM_SECTIONS * 3/10; j < NUM_SECTIONS * 7/10; j++)  {
				difficultyMap[i][j] = 2;
			}
		}

		for(int i = NUM_SECTIONS * 4/10; i < NUM_SECTIONS * 6/10; i++) {
			for(int j = NUM_SECTIONS * 4/10; j < NUM_SECTIONS * 6/10; j++)  {
				difficultyMap[i][j] = 1;
			}
		}


		//CHOOSE ONE TILE ON THE EDGE FOR OZ!!!!
		double randomBugger = Math.random();

		if(randomBugger > 0.5) { //it's gonna be on the right/left side
			double randomY = Math.random() * NUM_SECTIONS;
			if(randomBugger > .75) { //by that I mean the RIGHT side

				difficultyMap[NUM_SECTIONS][(int) randomY] = 0;

				mObstacles.add(new Obstacle(NUM_SECTIONS*SECTION_SIZE,(int) randomY*SECTION_SIZE, OBS_OZ));

				Log.d("DEBUG", "Oz iz at: " + NUM_SECTIONS + ", " + randomY);
			} else { // by that I mean the LEFT side

				difficultyMap[0][(int) randomY] = 0;

				mObstacles.add(new Obstacle(0,(int) randomY*SECTION_SIZE, OBS_OZ));

				Log.d("DEBUG", "Oz iz at: " + 0 + ", " + randomY);
			}

		} else { //it's gonna be on the top/bottom side
			double randomX = Math.random() * NUM_SECTIONS;
			if(randomBugger < 0.25) { //by that I mean the BOTTOM side
				difficultyMap[NUM_SECTIONS][(int) randomX] = 0;

				mObstacles.add(new Obstacle(NUM_SECTIONS*SECTION_SIZE,(int) randomX*SECTION_SIZE, OBS_OZ));

				Log.d("DEBUG", "Oz iz at: " + 0 + ", " + randomX);
			} else { //by that I mean the TOP side
				difficultyMap[0][(int) randomX] = 0;

				mObstacles.add(new Obstacle(0,(int) randomX*SECTION_SIZE, OBS_OZ));

				Log.d("DEBUG", "Oz iz at: " + 0 + ", " + randomX);
			}
		}
	}

	/**
	 * Add obstacles eveeerywhere!
	 * @param sectionX The x position, on the entire array, of the current section.
	 * @param sectionY The y position, on the entire array, of the current section.
	 */
	private void generateObstacles(int sectionX, int sectionY) {
		//split up the map into 20x20 "sections"
		//generate obstacles in each section based on how far it is from the
		//center.  The sections[][] array should have the difficulty stored as an int.

		//grab the difficulty for each section
		int difficulty = difficultyMap[(sectionX-1)/SECTION_SIZE][(sectionY-1)/SECTION_SIZE];

		//generate the obstacles in a 20x20 section
		//		int[][]	section = new int[20][20];

		int numNewObstacles = 0;

		while(numNewObstacles < OBSTACLES_PER_SECTION) {
			//randomly choose an X and Y for the obstacle
			int obX = (int) (Math.random() * SECTION_SIZE + sectionX);
			int obY = (int) (Math.random() * SECTION_SIZE + sectionY);

			//Stuff to make sure the obstacles don't overlap
			boolean isConflict = false;
			int conflicts = 0;

			//choose which obstacle it should be.  
			int newObsId = OBS_FLOWER;

			//random number we'll use for obstacle generation down there
			double randomGuy = Math.random();

			//If difficulty = 1, flowers, mailboxes, and the occasional house.


			if(difficulty == 1) {
				if(randomGuy < 0.5) {
					newObsId = OBS_FLOWER;
				} else if(randomGuy<0.8) {
					newObsId = OBS_MAILBOX;
				} else {
					newObsId = OBS_SMALL_HOUSE;
				}

			}

			//diff 2: one or two flowers, mailboxes, trees, and more houses.
			if(difficulty == 2) {
				if(randomGuy < 0.1) {
					newObsId = OBS_FLOWER;
				} else if(randomGuy<0.3) {
					newObsId = OBS_MAILBOX;
				} else if(randomGuy<.6) {
					newObsId = OBS_TREE;
				} else if(randomGuy<.9){
					newObsId = OBS_SMALL_HOUSE;
				} else {
					newObsId = OBS_MEDIUM_HOUSE;
				}
			}

			//diff 3: Some trees, some trucks, many small/med houses, and a watertower sometimes.
			if(difficulty == 3) {
				if(randomGuy < .1) {
					newObsId = OBS_TREE;
				} else if(randomGuy < .3) {
					newObsId = OBS_TRUCK;
				} else if(randomGuy < .5) {
					newObsId = OBS_SMALL_HOUSE;
				} else if(randomGuy < .9) {
					newObsId = OBS_MEDIUM_HOUSE;
				} else {
					newObsId = OBS_WATERTOWER;
				}
			}

			//diff 4: Less trucks/medium houses, more watertowers and big houses.  Coupla barns.
			if(difficulty == 4) {
				if(randomGuy < .2) {
					newObsId = OBS_TRUCK;
				} else if(randomGuy < .5) {
					newObsId = OBS_MEDIUM_HOUSE;
				} else if(randomGuy < .7) {
					newObsId = OBS_WATERTOWER;
				} else if(randomGuy < .9) {
					newObsId = OBS_BIG_HOUSE;
				} else {
					newObsId = OBS_BARN;
				}
			}
			//diff 5: Medium hauses, big hauses, barns!  Also oz.  Somewhere.
			if(difficulty == 5) {
				if(randomGuy < .3) {
					newObsId = OBS_MEDIUM_HOUSE;
				} else if(randomGuy < .6) {
					newObsId = OBS_BIG_HOUSE;
				} else {
					newObsId = OBS_BARN;
				}
			}

			if(difficulty == 0) {  //OZ ALERT BWEEOOOWEEEEOOOOWEEEOOO
				newObsId = OBS_OZ;
				obX = sectionX;
				obY = sectionY;
				numNewObstacles = OBSTACLES_PER_SECTION;
			}

			Obstacle newObs = new Obstacle(obX,obY,newObsId);

			//check if we actually CAN put the obstacle here.
			//If not, no harm done, just try again matey.

			//First, check if we're outta bounds
			if(newObs.x + newObs.xSize > ARRAY_SIZE-2 || newObs.y + newObs.ySize > ARRAY_SIZE-2) {
				continue;
			}

			//Then see if we're touching another obstacle
			for(int i = 0; i < newObs.xSize; i++) {
				for(int j = 0; j < newObs.ySize; j++) {
					if(obsMap[(int) (i + newObs.x)][(int) (j + newObs.y)] != 0) {
						isConflict = true;

						//if there's too many conflicts something went wrong.  Abort + try again.
						conflicts++;
						if(conflicts > 100) {
							for(i=numNewObstacles; i>0; i--) {
								mObstacles.remove(mObstacles.size());
								numNewObstacles=0;
							}
						}
					}
				}
			}

			if(!isConflict) {
				//no conflicts! add obstacle to list of obstacles
				mObstacles.add(newObs);


				//add the obstacle to the bigass array, so we don't accidentally 
				//overlap obstacles
				for(int i = 0; i < newObs.xSize; i++) {
					for(int j = 0; j < newObs.ySize; j++) {
						//what we're doing here is making the obstacle take up more room in the array
						//if it's a big-ass obstacle.
						obsMap[(int) newObs.x + i][(int) newObs.y + j] = newObs.whichObstacle;
					}
				}
				numNewObstacles++;
			}

		}
	}


	private void generatePowerups(int sectionX, int sectionY) {
		//split up the map into 20x20 "sections"
		//generate obstacles in each section based on how far it is from the
		//center.  The sections[][] array should have the difficulty stored as an int.

		//generate the obstacles in a 20x20 section
		//		int[][]	section = new int[20][20];

		int numNewObstacles = 0;

		while(numNewObstacles < POWERUPS_PER_SECTION) {
			//randomly choose an X and Y for the obstacle
			int obX = (int) (Math.random() * SECTION_SIZE + sectionX);
			int obY = (int) (Math.random() * SECTION_SIZE + sectionY);

			//Stuff to make sure the obstacles don't overlap
			boolean isConflict = false;
			int conflicts = 0;

			//choose which obstacle it should be.  
			int newObsId;

			//random number we'll use for obstacle generation down there
			double randomGuy = Math.random();

			//If difficulty = 1, flowers, mailboxes, and the occasional house.



			if(randomGuy < 0.2) {
				newObsId = POW_RED_GEM;
			} else if(randomGuy<0.5) {
				newObsId = POW_BLUE_GEM;
			} else {
				newObsId = POW_GREEN_GEM;
			}

			Powerup newObs = new Powerup(obX,obY,newObsId);

			//check if we actually CAN put the obstacle here.
			//If not, no harm done, just try again matey.

			//First, check if we're outta bounds
			if(newObs.x + newObs.xSize > ARRAY_SIZE-2 || newObs.y + newObs.ySize > ARRAY_SIZE-2) {
				continue;
			}

			//Then see if we're touching another obstacle
			for(int i = 0; i < newObs.xSize; i++) {
				for(int j = 0; j < newObs.ySize; j++) {
					if(obsMap[(int) (i + newObs.x)][(int) (j + newObs.y)] != 0) {
						isConflict = true;
						numNewObstacles++;
					}
				}
			}

			if(!isConflict) {
				//no conflicts! add obstacle to list of obstacles
				mPowerups.add(newObs);


				//add the obstacle to the bigass array, so we don't accidentally 
				//overlap obstacles
				for(int i = 0; i < newObs.xSize; i++) {
					for(int j = 0; j < newObs.ySize; j++) {
						//what we're doing here is making the obstacle take up more room in the array
						//if it's a big-ass obstacle.
						obsMap[(int) newObs.x + i][(int) newObs.y + j] = newObs.whichPowerup;
					}
				}
				numNewObstacles++;
			}

		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * @param x the width of the map
	 * @param y the height of the map
	 * 
	 * @Description Creates a randomly generated map w/ grass in the middle and mountains on the border.
	 */
	public void createMap(int x, int y){
		for(int i = 0;i<y;i++){
			for(int j = 0;j<x;j++){

				//randomize the grass tiles
				double rando  = Math.random();
				if(rando < 0.33) {
					map[i][j] = GRASS_ID_1;
				} else if (rando < .66) {
					map[i][j] = GRASS_ID_2;
				} else {
					map[i][j] = GRASS_ID_3;
				}

				if(i == 0 || i == ARRAY_SIZE-1)
					map[i][j] = MOUNTAIN_ID;

				if(j==0 || j == ARRAY_SIZE-1)
					map[i][j] = MOUNTAIN_ID;
			}
		}
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		//nothing here
	}

	public void onSensorChanged(SensorEvent event) {

		//grab the x and y acceleration from the accelerometer
		float yAcc = event.values[0];
		float xAcc = event.values[1];

		//tell the drawview what position the phone is in.
		mDrawView.accXOffset = -xAcc*agility/100;
		mDrawView.accYOffset = -yAcc*agility/100;
	}

	public void die() {
		Intent statsIntent = new Intent(getApplicationContext(),StatsActivity.class);
		statsIntent.putExtra("obsDestroyed", mDrawView.obsDestroyed);
		statsIntent.putExtra("title", "End of Round! Try Again!");
		setResult(StatsActivity.CODE_OK,statsIntent);
		editor.putInt("attempt" ,settings.getInt("attempt",0)+1);
		editor.commit();
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}


}
