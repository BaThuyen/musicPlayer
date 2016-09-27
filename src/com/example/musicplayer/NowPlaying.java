package com.example.musicplayer;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NowPlaying extends Activity {
	
	String lp = "fw";
	Boolean fvt = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.now_playing);
		
		Button play, next, pre;
		Button loop, share, favorite;
	}
	
	public void back(View view)
	{
		finish();
	}
	
	public void loop(View view)
	{
		Button btnLoop = (Button) findViewById(R.id.btnLoop);
		switch (lp) {
		case "lp":
			btnLoop.setBackgroundResource(R.drawable.shuffle);
			lp = "sf";
			break;
		case "fw":
			btnLoop.setBackgroundResource(R.drawable.shuffle);
			lp = "lp";
			break;
		case "sf":
			btnLoop.setBackgroundResource(R.drawable.forward);
			lp = "fw";
			break;
		}
	}
	
	public void favorite(View view)
	{
		Button btnFavorite = (Button) findViewById(R.id.btnFavorite);
		if(fvt)
		{
			btnFavorite.setBackgroundResource(R.drawable.heart1);
			fvt = false;
		}
		else
		{
			btnFavorite.setBackgroundResource(R.drawable.heart2);
			fvt = true;
		}
	}
}
