package com.tornadoofoz;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class TornadoDrawView extends View implements OnTouchListener {

	private static final int ARRAY_SIZE = MainActivity.ARRAY_SIZE;
	public int TILE_SIZE = MainActivity.TILE_SIZE;

	private int mHeight;
	private int mWidth;
	Paint paint = new Paint();
	Paint badPaint = new Paint();
	Paint healthPaint = new Paint();
	Paint missingHealthPaint = new Paint();
	Bitmap grass;
	Bitmap mountain;
	Bitmap tornado;
	Bitmap tornado1, tornado2, tornado3, tornado4;
	Bitmap tornado1b, tornado2b, tornado3b, tornado4b;
	Bitmap whichAnimation, mailbox, mailboxBroken, houseSmall, houseSmallBroken, houseBig, houseBigBroken;
	Bitmap houseMedium, houseMediumBroken;
	Bitmap grass1, grass2, grass3;
	Bitmap flower, flowerBroken;
	Bitmap waterTower,waterTowerBroken, truck,truckBroken,treeBroken,tree,barn,barnBroken;
	Bitmap oz;
	Bitmap blue, red, green;
	Bitmap lion,scarecrow,tinman;

	int currentAnimation = 1;

	int[][] map;

	int[] obsDestroyed = new int[10];

	float dXOffset = 0;
	float dYOffset = 0;

	float accXOffset = 0;
	float accYOffset = 0;

	private long nextFrameTime = System.currentTimeMillis();
	private long nextHealthTick = System.currentTimeMillis();


	RectF tornadoDrawRect;
	RectF tornadoDrawRectB;
	RectF tornadoHitRect;
	RectF screenRect;
	RectF fullHealthRect;
	RectF tornadoDrawRectBB;
	RectF tornadoDrawRectBBB;

	int tornadoPower;
	int maxHealth;
	private int tornadoHealth;

	boolean areWeOnAMountain = false;

	ArrayList<Rect> mountainRects = new ArrayList<Rect>();
	ArrayList<Obstacle> obstacles;
	ArrayList<Powerup> powerups;

	float yOffset = 0;
	float xOffset = 0;

	MainActivity ma;

	public TornadoDrawView(Context context) {
		super(context);
	}

	public TornadoDrawView(Context context, int mWidth, int mHeight, int[][] map, ArrayList<Obstacle> obstacles, ArrayList<Powerup> powerups, MainActivity ma) {
		super(context);

		//make sure we can receive touch events
		setFocusable(true);
		setFocusableInTouchMode(true);

		this.mHeight = mHeight;
		this.mWidth = mWidth;
		this.map = map;
		this.obstacles = obstacles;
		this.powerups = powerups;
		this.ma = ma;
		maxHealth = ma.settings.getInt("health", 100);
		tornadoHealth = maxHealth;
		tornadoPower = ma.settings.getInt("power", 2);
		

		screenRect = new RectF(0,0,mWidth,mHeight);

		xOffset = -ARRAY_SIZE*TILE_SIZE/2;
		yOffset = -ARRAY_SIZE*TILE_SIZE/2;

		tornadoDrawRect = new RectF(mWidth/2-60,mHeight/2-60,mWidth/2+60,mHeight/2+60);
		tornadoDrawRectB = new RectF(mWidth/2-50,mHeight/2-50,mWidth/2+50,mHeight/2+50);
		tornadoDrawRectBB = new RectF(mWidth/2-40,mHeight/2-40,mWidth/2+40,mHeight/2+40);
		tornadoDrawRectBBB = new RectF(mWidth/2-30,mHeight/2-30,mWidth/2+30,mHeight/2+30);
		tornadoHitRect = new RectF(mWidth/2-10,mHeight/2-10,mWidth/2+10,mHeight/2+10);
		fullHealthRect = new RectF(mWidth/2-maxHealth/2,40,mWidth/2+maxHealth/2,60);

		grass = BitmapFactory.decodeResource(getResources(), R.drawable.grasstile);
		mountain = BitmapFactory.decodeResource(getResources(),R.drawable.mountains);
		//tornado = BitmapFactory.decodeResource(getResources(), R.drawable.tornado);

		tornado1 = BitmapFactory.decodeResource(getResources(), R.drawable.torn1);
		tornado2 = BitmapFactory.decodeResource(getResources(), R.drawable.torn2);
		tornado3 = BitmapFactory.decodeResource(getResources(), R.drawable.torn3);
		tornado4 = BitmapFactory.decodeResource(getResources(), R.drawable.torn4);
		tornado1b = BitmapFactory.decodeResource(getResources(), R.drawable.torn1np);
		tornado2b = BitmapFactory.decodeResource(getResources(), R.drawable.torn2np);
		tornado3b = BitmapFactory.decodeResource(getResources(), R.drawable.torn3np);
		tornado4b = BitmapFactory.decodeResource(getResources(), R.drawable.torn4np);

		grass1 = BitmapFactory.decodeResource(getResources(), R.drawable.grass1);
		grass2 = BitmapFactory.decodeResource(getResources(), R.drawable.grass2);
		grass3 = BitmapFactory.decodeResource(getResources(), R.drawable.grass3);

		blue = BitmapFactory.decodeResource(getResources(), R.drawable.blue_gem);
		red = BitmapFactory.decodeResource(getResources(), R.drawable.red_gem);
		green = BitmapFactory.decodeResource(getResources(), R.drawable.green_gem);

		flower = BitmapFactory.decodeResource(getResources(), R.drawable.flower);
		flowerBroken = BitmapFactory.decodeResource(getResources(), R.drawable.flower_broken);

		waterTower = BitmapFactory.decodeResource(getResources(), R.drawable.water_tower);
		waterTowerBroken = BitmapFactory.decodeResource(getResources(), R.drawable.water_tower_broken);

		truck = BitmapFactory.decodeResource(getResources(), R.drawable.truck);
		truckBroken = BitmapFactory.decodeResource(getResources(), R.drawable.truck_broken);

		tree = BitmapFactory.decodeResource(getResources(), R.drawable.tree);
		treeBroken = BitmapFactory.decodeResource(getResources(), R.drawable.tree_broken);

		mailbox = BitmapFactory.decodeResource(getResources(), R.drawable.mailbox);
		mailboxBroken = BitmapFactory.decodeResource(getResources(), R.drawable.mailbox_broken);
		houseSmall = BitmapFactory.decodeResource(getResources(), R.drawable.house_small);
		houseSmallBroken = BitmapFactory.decodeResource(getResources(), R.drawable.house_small_broken);

		houseMedium = BitmapFactory.decodeResource(getResources(), R.drawable.house_medium);
		houseMediumBroken = BitmapFactory.decodeResource(getResources(), R.drawable.house_medium_broken);

		houseBig = BitmapFactory.decodeResource(getResources(), R.drawable.house_big);
		houseBigBroken = BitmapFactory.decodeResource(getResources(), R.drawable.house_big_broken);

		barn = BitmapFactory.decodeResource(getResources(), R.drawable.barn);
		barnBroken = BitmapFactory.decodeResource(getResources(), R.drawable.barn_broken);
		
		lion = BitmapFactory.decodeResource(getResources(), R.drawable.lion);
		scarecrow = BitmapFactory.decodeResource(getResources(), R.drawable.scarecrow);
		tinman = BitmapFactory.decodeResource(getResources(), R.drawable.tinman);

		oz = BitmapFactory.decodeResource(getResources(), R.drawable.oz);

		paint.setColor(Color.RED);
		badPaint.setColor(Color.RED);
		badPaint.setAlpha(200);
		healthPaint.setColor(Color.GREEN);
		missingHealthPaint.setColor(Color.RED);

		Log.d("Something","xOffset: " + xOffset + " yOffset: " + yOffset);


	}

	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}


	Rect mCollidedMountain = new Rect(0,0,0,0);
	Rect mMountainRect = new Rect(0,0,0,0);

	RectF topBoundary = new RectF();
	RectF leftBoundary = new RectF();
	RectF bottomBoundary = new RectF();
	RectF rightBoundary = new RectF();

	RectF drawRect = new RectF();
	RectF obsHitRect = new RectF();
	RectF missingHealthRect = new RectF();

	/**
	 * When we hit too big an obstacle,
	 * we temporarily disable the accelerometer
	 * so the user doesn't run into the same obstacle again.
	 * 
	 * We do this by saying
	 */
	private long disableAccUntil;
	private boolean done = false;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		long now = System.currentTimeMillis();

		areWeOnAMountain = false;

		topBoundary.set(xOffset,yOffset,(xOffset + ARRAY_SIZE*TILE_SIZE),
				(yOffset+TILE_SIZE));

		leftBoundary.set(0,TILE_SIZE,TILE_SIZE,ARRAY_SIZE*TILE_SIZE);
		leftBoundary.offset((int)xOffset, (int)yOffset);

		bottomBoundary.set(0,ARRAY_SIZE*TILE_SIZE-TILE_SIZE,
				ARRAY_SIZE*TILE_SIZE,ARRAY_SIZE*TILE_SIZE);
		bottomBoundary.offset((int)xOffset, (int)yOffset);

		rightBoundary.set(ARRAY_SIZE*TILE_SIZE-TILE_SIZE,0,ARRAY_SIZE*TILE_SIZE,
				ARRAY_SIZE*TILE_SIZE);
		rightBoundary.offset((int)xOffset, (int)yOffset);


		int approxX = (int) -FloatMath.floor(xOffset/TILE_SIZE);
		int approxY = (int) -FloatMath.floor(yOffset/TILE_SIZE);

		//draw the grass and the mountains
		//the number of tiles across the screen is given by:
		//mWidth/TILE_SIZE
		for(int i = approxX-2; i < approxX+mWidth/TILE_SIZE+1 && i < ARRAY_SIZE; i++) {
			for(int j = approxY-2; j < approxY+mHeight/TILE_SIZE+1 && j < ARRAY_SIZE; j++) {


				float drawX = i*TILE_SIZE + xOffset;
				float drawY = j*TILE_SIZE + yOffset;

				if(i > -1 && j > -1) {
					drawRect.set(drawX,drawY,drawX+TILE_SIZE,drawY+TILE_SIZE);
					if(RectF.intersects(drawRect,screenRect)) {

						switch(map[i][j]) {
						case MainActivity.GRASS_ID_1:
							canvas.drawBitmap(grass1,null,drawRect,paint);
							break;
						case MainActivity.GRASS_ID_2:
							canvas.drawBitmap(grass2,null,drawRect,paint);
							break;
						case MainActivity.GRASS_ID_3:
							canvas.drawBitmap(grass3,null,drawRect,paint);
							break;
						}

						if(map[i][j] == MainActivity.MOUNTAIN_ID) {
							canvas.drawBitmap(mountain,null,drawRect,paint);
						}
					}
				}
			}
		}

		if(obstacles != null) {
			for(Obstacle o : obstacles) {
				float drawX = o.x*TILE_SIZE + xOffset;
				float drawY = o.y*TILE_SIZE + yOffset;

				drawRect.set(drawX, drawY - o.tallness, drawX + TILE_SIZE*o.xSize, drawY + TILE_SIZE*o.ySize);
				obsHitRect.set(drawX, drawY, drawX + TILE_SIZE*o.xSize, drawY + TILE_SIZE*o.ySize);

				//check if the obstacle on the screen, e.g. if we should draw it.
				if( !RectF.intersects(drawRect,screenRect)) {
					continue;
				}

				if(o.destroyed == false && RectF.intersects(tornadoHitRect, obsHitRect)) {

					if(o.obstaclePower > tornadoPower) {
						//TODO: take away tornado health here
						tornadoHealth -= (o.obstaclePower - tornadoPower)*5;

						//if the tornado's RIGHT side is touching the obstacles LEFT side,
						//or vice versa, we must be hitting HORIZONTALLY.
						if(Math.abs(obsHitRect.left - tornadoHitRect.right) < 5) {
							dXOffset = 5;
							disableAccUntil = now + 500;
						} else if(Math.abs(obsHitRect.right - tornadoHitRect.left) < 5){
							dXOffset = -5;
							disableAccUntil = now + 500;
							//same deal with vertical collision.
						} else if(Math.abs(obsHitRect.top - tornadoHitRect.bottom) < 5) {
							dYOffset = 5;
							disableAccUntil = now + 500;
						} else if(Math.abs(obsHitRect.bottom - tornadoHitRect.top) < 5) {
							dYOffset = -5;
							disableAccUntil = now + 500;
						} else {
							dXOffset = -dXOffset;
							dYOffset = -dYOffset;
							disableAccUntil = now + 500;
						}
					} else {
						//we ran into obstacle o
						obsDestroyed[o.whichObstacle]++;

						//debug:
						o.destroyed = true;
						//						dXOffset *= 1;
						//						dYOffset *= 1;
						//TODO:add back health or something. Also add back in the lil boost.  
						tornadoHealth += o.obstaclePower*5;
						if(tornadoHealth > maxHealth)
							tornadoHealth = maxHealth;
					}
				}

				switch(o.whichObstacle) {
				case MainActivity.OBS_FLOWER:
					if(o.destroyed) {

						canvas.drawBitmap(flowerBroken,null,drawRect,paint);
					} else {
						canvas.drawBitmap(flower,null,drawRect,paint);
					}
					break;
				case MainActivity.OBS_MAILBOX:
					if(o.destroyed) {
						canvas.drawBitmap(mailboxBroken,null,drawRect,paint);
					} else {
						canvas.drawBitmap(mailbox,null,drawRect,paint);
					}
					break;
				case MainActivity.OBS_TREE:
					if(o.destroyed) {
						canvas.drawBitmap(treeBroken,null,drawRect,paint);
					} else {
						canvas.drawBitmap(tree,null,drawRect,paint);
					}
					break;
				case MainActivity.OBS_TRUCK:
					if(o.destroyed) {
						canvas.drawBitmap(truckBroken,null,drawRect,paint);
					} else {
						canvas.drawBitmap(truck,null,drawRect,paint);
					}
					break;
				case MainActivity.OBS_SMALL_HOUSE:
					if(o.destroyed) {
						canvas.drawBitmap(houseSmallBroken,null,drawRect,paint);
					} else {
						canvas.drawBitmap(houseSmall,null,drawRect,paint);
					}
					break;
				case MainActivity.OBS_MEDIUM_HOUSE:
					if(o.destroyed) {
						canvas.drawBitmap(houseMediumBroken,null,drawRect,paint);
					} else {
						canvas.drawBitmap(houseMedium,null,drawRect,paint);
					}
					break;
				case MainActivity.OBS_WATERTOWER:
					if(o.destroyed) {
						canvas.drawBitmap(waterTowerBroken,null,drawRect,paint);
					} else {
						canvas.drawBitmap(waterTower,null,drawRect,paint);
					}
					break;
				case MainActivity.OBS_BIG_HOUSE:
					if(o.destroyed) {
						canvas.drawBitmap(houseBigBroken,null,drawRect,paint);
					} else {
						canvas.drawBitmap(houseBig,null,drawRect,paint);
					}
					break;
				case MainActivity.OBS_BARN:
					if(o.destroyed) {
						canvas.drawBitmap(barnBroken,null,drawRect,paint);
					} else {
						canvas.drawBitmap(barn,null,drawRect,paint);
					}
					break;
				case MainActivity.OBS_OZ:
					canvas.drawBitmap(oz, null, drawRect, paint);
					break;
				}

			}
		
			if(powerups != null) {
				for(Powerup p : powerups) {
					float drawX = p.x*TILE_SIZE + xOffset;
					float drawY = p.y*TILE_SIZE + yOffset;
		
					drawRect.set(drawX, drawY, drawX + TILE_SIZE*p.xSize, drawY + TILE_SIZE*p.ySize);
					obsHitRect.set(drawX, drawY, drawX + TILE_SIZE*p.xSize, drawY + TILE_SIZE*p.ySize);
		
					//check if the obstacle on the screen, e.g. if we should draw it.
					if( !RectF.intersects(drawRect,screenRect)) {
						continue;
					}
					if(p.destroyed == false && RectF.intersects(tornadoHitRect, obsHitRect)) {
						if(p.whichPowerup == MainActivity.POW_BLUE_GEM) {
							if(ma.settings.getInt("agility", 6)<20){
								ma.editor.putInt("agility", ma.settings.getInt("agility", 8)+1);
								ma.editor.commit();
								ma.agility = ma.settings.getInt("agility", 8);
		
							}
						}else if(p.whichPowerup == MainActivity.POW_GREEN_GEM){
							if(ma.settings.getInt("health", 100)<300){
								ma.editor.putInt("health", ma.settings.getInt("health", 100)+15);
								ma.editor.commit();
								maxHealth = (ma.settings.getInt("health", 100));
		
							}
						}else if(p.whichPowerup == MainActivity.POW_RED_GEM){
							if(ma.settings.getInt("power", 2)<20){
								ma.editor.putInt("power", ma.settings.getInt("power", 2)+1);
								ma.editor.commit();
								tornadoPower = ma.settings.getInt("power", 2);
							}
						} else if(p.whichPowerup == MainActivity.LION) {
							ma.editor.putBoolean("lion", true);
						} else if(p.whichPowerup == MainActivity.SCARECROW) {
							ma.editor.putBoolean("scarecrow", true);
						} else if(p.whichPowerup == MainActivity.TIN_MAN) {
							ma.editor.putBoolean("tinman", true);
						}
						p.destroyed = true;
					}
		
					switch(p.whichPowerup) {
					case MainActivity.POW_BLUE_GEM:
						if(!p.destroyed) 
		
							canvas.drawBitmap(blue,null,drawRect,paint);
		
						break;
					case MainActivity.POW_RED_GEM:
						if(!p.destroyed) 
							canvas.drawBitmap(red,null,drawRect,paint);
		
						break;
					case MainActivity.POW_GREEN_GEM:
						if(!p.destroyed) 
							canvas.drawBitmap(green,null,drawRect,paint);
		
						break;
					case MainActivity.LION:
						if(!p.destroyed)
							canvas.drawBitmap(lion, null, drawRect, paint);
						break;
						
					case MainActivity.SCARECROW:
						if(!p.destroyed)
							canvas.drawBitmap(scarecrow, null, drawRect, paint);
						break;
						
					case MainActivity.TIN_MAN:
						if(!p.destroyed)
							canvas.drawBitmap(tinman, null, drawRect, paint);
						break;
					}
				}
			}
			//...unless the accelerometer is disabled
			if(disableAccUntil > now) {
				//draw a nice red rectangle over the whole thing
				//to show how hurt we are :(
				badPaint.setAlpha((int) (255*(disableAccUntil - now)/500));
				canvas.drawRect(screenRect,badPaint);
			} else {
				dXOffset += accXOffset;
				dYOffset += accYOffset;
			}
					
			//apply friction
			dXOffset *= .95;
			dYOffset *= .95;
	
	
			if(now > nextFrameTime) {
				//it has been 100ms since we last changed the tornado's rotation...
				//change it!
				boolean changed = false;
				if(currentAnimation == 1 && !changed) {
					currentAnimation++;
					changed = true;
				}
				if(currentAnimation == 2 && !changed) {
					currentAnimation++;
					changed = true;
				}
				if(currentAnimation == 3 && !changed) {
					currentAnimation++;
					changed = true;
				}
				if(currentAnimation == 4 && !changed) {
					currentAnimation = 1;
					changed = true;
				}
	
				nextFrameTime = now+100;
			}
	
			drawTornado(canvas);
	
			//draw the health bar
	
	
			if(areWeOnAMountain) {
				paint.setColor(Color.RED);
			}
	
			if(RectF.intersects(topBoundary,tornadoHitRect)) {
	
				if(dYOffset > 8)
					dYOffset = -7;
				if(dYOffset > 4)
					dYOffset = -3;
				if(dYOffset > 0)
					dYOffset = 0;
			}
	
	
			if(RectF.intersects(leftBoundary,tornadoHitRect)) {
	
				if(dXOffset > 8)
					dXOffset = -7;
				if(dXOffset > 4)
					dXOffset = -3;
				if(dXOffset > 0)
					dXOffset = 0;
			}
	
			if(RectF.intersects(bottomBoundary,tornadoHitRect)) {
				if(dYOffset < -8)
					dYOffset = 7;
				if(dYOffset < -4)
					dYOffset = 3;
				if(dYOffset < 0)
					dYOffset = 0;
			}
	
			if(RectF.intersects(rightBoundary,tornadoHitRect)) {
	
				if(dXOffset < -8)
					dXOffset = 7;
				if(dXOffset < -4)
					dXOffset = 3;
				if(dXOffset < 0)
					dXOffset = 0;
			}
	
	
			//draw tornado's health bar
			canvas.drawRect(fullHealthRect,healthPaint);
			missingHealthRect.set(fullHealthRect);
	
			missingHealthRect.left += tornadoHealth;
			canvas.drawRect(missingHealthRect,missingHealthPaint);
	
			xOffset += dXOffset;
			yOffset += dYOffset;
	
			if(now > nextHealthTick) {
				tornadoHealth--;
				nextHealthTick = now+100;
	
				if(tornadoHealth <= 0) {
					if(!done )
						ma.die();
					done = true;
				}
			}
	
			invalidate();
		}

	}



	/**
	 * @param canvas
	 */
	private void drawTornado(Canvas canvas) {
		//draw the current frame of the tornado
		tornadoDrawRect.offset(dXOffset*5, dYOffset*5);
		tornadoDrawRectB.offset(dXOffset*3, dYOffset*3);
		tornadoDrawRectBB.offset(dXOffset, dYOffset);


		if(currentAnimation == 4) {
			canvas.drawBitmap(tornado2b, null, tornadoDrawRectBBB, paint);
			canvas.drawBitmap(tornado3b, null, tornadoDrawRectBB, paint);
			canvas.drawBitmap(tornado4, null, tornadoDrawRectB, paint);
			canvas.drawBitmap(tornado1, null, tornadoDrawRect, paint);
		}
		if(currentAnimation == 3) {
			canvas.drawBitmap(tornado3b, null, tornadoDrawRectBBB, paint);
			canvas.drawBitmap(tornado4b, null, tornadoDrawRectBB, paint);
			canvas.drawBitmap(tornado1, null, tornadoDrawRectB, paint);
			canvas.drawBitmap(tornado2, null, tornadoDrawRect, paint);
		}
		if(currentAnimation == 2) {
			canvas.drawBitmap(tornado4b, null, tornadoDrawRectBBB, paint);
			canvas.drawBitmap(tornado1b, null, tornadoDrawRectBB, paint);
			canvas.drawBitmap(tornado2, null, tornadoDrawRectB, paint);
			canvas.drawBitmap(tornado3, null, tornadoDrawRect, paint);
		}
		if(currentAnimation == 1) {
			canvas.drawBitmap(tornado1b, null, tornadoDrawRectBBB, paint);
			canvas.drawBitmap(tornado2b, null, tornadoDrawRectBB, paint);
			canvas.drawBitmap(tornado3, null, tornadoDrawRectB, paint);
			canvas.drawBitmap(tornado4, null, tornadoDrawRect, paint);
		}

		tornadoDrawRect.offset(-dXOffset*5, -dYOffset*5);
		tornadoDrawRectB.offset(-dXOffset*3, -dYOffset*3);
		tornadoDrawRectBB.offset(-dXOffset, -dYOffset);
	}





}
