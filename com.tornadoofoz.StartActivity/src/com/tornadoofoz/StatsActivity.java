package com.tornadoofoz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class StatsActivity extends Activity {

	ImageButton playAgainButton, homeButton;

	TextView trees, flowers, blueHouses, mailBoxes,
	stoneHouses, redTrucks, bigTealHouses, waterTowers, barns,
	attempt, health, power, agility, header;

	String title;
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	public static final String PERM_PREFS_NAME = "MyPrefs";


	public static final int CODE_OK = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_stats);

		settings = getSharedPreferences(PERM_PREFS_NAME,0);
		editor = settings.edit();

		initObjects();
		loadPrefs();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			title = extras.getString("title");
			header.setText(title);
			if(extras.getBoolean("Just started")) {
				Intent i = new Intent(getApplicationContext(),MainActivity.class);
				startActivityForResult(i, CODE_OK);
			}
		}

		playAgainButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent i = new Intent(getApplicationContext(),MainActivity.class);
				startActivityForResult(i, CODE_OK);
			}
		});
		homeButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent i = new Intent(getApplicationContext(), StartActivity.class);
				startActivity(i);
				///finish();
			}
		});
	}	

	public void initObjects(){
//		trees = (TextView) findViewById(R.id.treesStat);
//		flowers = (TextView) findViewById(R.id.flowersStat);
//		blueHouses = (TextView) findViewById(R.id.blueHousesStat);
//		mailBoxes = (TextView) findViewById(R.id.mailBoxesStat);
//		stoneHouses = (TextView) findViewById(R.id.stoneHousesStat);
//		redTrucks = (TextView) findViewById(R.id.redTrucksStat);
//		bigTealHouses = (TextView) findViewById(R.id.bigTealHousesStat);
//		waterTowers = (TextView) findViewById(R.id.waterTowersStat);
//		barns = (TextView) findViewById(R.id.barnsStat);
//		attempt = (TextView) findViewById(R.id.attemptStat);
//		health = (TextView) findViewById(R.id.healthStat);
//		power = (TextView) findViewById(R.id.powerStat);
//		agility = (TextView) findViewById(R.id.agilityStat);
//		header = (TextView) findViewById(R.id.statHeader);

		playAgainButton = (ImageButton) findViewById(R.id.playButton);
		homeButton = (ImageButton) findViewById(R.id.homeButton);
	}

	public void loadPrefs(){
		trees.setText("Trees - " + settings.getInt("trees", 0));
		flowers.setText("Flowers - " + settings.getInt("flowers", 0));
		blueHouses.setText("Blue Houses - " + settings.getInt("blueHouses", 0));
		mailBoxes.setText("Mail Boxes - " + settings.getInt("mailBoxes", 0));
		stoneHouses.setText("Stone Houses - " + settings.getInt("stoneHouses", 0));
		redTrucks.setText("Red Trucks - " + settings.getInt("redTrucks", 0));
		bigTealHouses.setText("Big Teal Houses - " + settings.getInt("bigTealHouses", 0));
		waterTowers.setText("Water Towers - " + settings.getInt("waterTowers", 0));
		barns.setText("Barns - " + settings.getInt("barns", 0));
		attempt.setText("Attempt #" + settings.getInt("attempt", 0));
		health.setText("Health: " + settings.getInt("health", 100));
		power.setText("Power: " + settings.getInt("power", 2));
		agility.setText("Agility: " + settings.getInt("agility", 6));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		title = data.getStringExtra("title");
		header.setText(title);
		int[] obsDestroyed = data.getIntArrayExtra("obsDestroyed");
		
		if(obsDestroyed != null) {
			loadCurrPrefs(obsDestroyed);
			saveGlobalStats(obsDestroyed);
		}
	}

	private void saveGlobalStats(int[] obsDestroyed) {

		editor.putInt("trees", settings.getInt("trees",0) + obsDestroyed[MainActivity.OBS_TREE]);
		editor.putInt("flowers", settings.getInt("flowers",0) + obsDestroyed[MainActivity.OBS_FLOWER]);
		editor.putInt("blueHouses", settings.getInt("blueHouses",0) + obsDestroyed[MainActivity.OBS_MEDIUM_HOUSE]);
		editor.putInt("mailboxes", settings.getInt("mailboxes",0) + obsDestroyed[MainActivity.OBS_MAILBOX]);
		editor.putInt("stoneHouses", settings.getInt("stoneHouses",0) + obsDestroyed[MainActivity.OBS_SMALL_HOUSE]);
		editor.putInt("redTrucks", settings.getInt("redTrucks",0) + obsDestroyed[MainActivity.OBS_TRUCK]);
		editor.putInt("bigTealHouses", settings.getInt("bigTealHouses",0) + obsDestroyed[MainActivity.OBS_BIG_HOUSE]);
		editor.putInt("waterTowers", settings.getInt("waterTowers",0) + obsDestroyed[MainActivity.OBS_WATERTOWER]);
		editor.putInt("barns", settings.getInt("barns",0) + obsDestroyed[MainActivity.OBS_BARN]);
		editor.commit();
	}

	private void loadCurrPrefs(int[] obsDestroyed) {

		flowers.setText("Flowers - " + obsDestroyed[MainActivity.OBS_FLOWER]);
		mailBoxes.setText("Mail Boxes - " + obsDestroyed[MainActivity.OBS_MAILBOX]);
		trees.setText("Trees - " + obsDestroyed[MainActivity.OBS_TREE]);
		redTrucks.setText("Red Trucks - " + obsDestroyed[MainActivity.OBS_TRUCK]);
		stoneHouses.setText("Stone Houses - " + obsDestroyed[MainActivity.OBS_SMALL_HOUSE]);
		blueHouses.setText("Blue Houses - " + obsDestroyed[MainActivity.OBS_MEDIUM_HOUSE]);
		bigTealHouses.setText("Big Teal Houses - " + obsDestroyed[MainActivity.OBS_BIG_HOUSE]);
		waterTowers.setText("Water Towers - " + obsDestroyed[MainActivity.OBS_WATERTOWER]);
		barns.setText("Barns - " + obsDestroyed[MainActivity.OBS_BARN]);
		attempt.setText("Attempt #" + settings.getInt("attempt", 0));
		health.setText("Health: " + settings.getInt("health", 100));
		power.setText("Power: " + settings.getInt("power", 2));
		agility.setText("Agility: " + settings.getInt("agility", 6));
		
		
	}


}
