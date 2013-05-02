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

public class StartActivity extends Activity {
	ImageButton playButton, storyButton, tutorialButton, scoresButton, statsButton;
	
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	public static final String PREFS_NAME = "MyPrefs";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		//these 3 lines make it fullscreen.  Magically.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_start);
		
		
		settings = getSharedPreferences(PREFS_NAME,0);
		editor = settings.edit();
		
		checkSavedState();

		playButton = (ImageButton) findViewById(R.id.playButton);		
		playButton.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {				
				Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(nextScreen);				
			}
		});
		storyButton = (ImageButton) findViewById(R.id.storyButton);
		storyButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent storyScreen = new Intent(getApplicationContext(), StoryActivity.class);
				startActivity(storyScreen);
			}
		});
		tutorialButton = (ImageButton) findViewById(R.id.tutorialButton);
		tutorialButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent storyScreen = new Intent(getApplicationContext(), TutorialActivity.class);
				startActivity(storyScreen);
			}
		});
		scoresButton = (ImageButton) findViewById(R.id.scoresButton);
		scoresButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent storyScreen = new Intent(getApplicationContext(), ScoresActivity.class);
				startActivity(storyScreen);
			}
		});
		statsButton = (ImageButton) findViewById(R.id.statsButton);
		statsButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent storyScreen = new Intent(getApplicationContext(), StatsActivity.class);
				startActivity(storyScreen);
			}
		});
	}
	
	public void checkSavedState(){
		if(settings.getAll().equals(null)){
			editor.putInt("trees", 0);
			editor.putInt("houses", 0);
			editor.putInt("flowers", 0);
			editor.putInt("attempts", 0);
			editor.putInt("power", 1);
			editor.putInt("controllability", 10);
			editor.putInt("health", 100);
			editor.commit();
		}

	}	
}
