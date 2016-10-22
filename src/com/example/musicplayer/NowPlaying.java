package com.example.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import model_song.Song;
import android.widget.Toast;

public class NowPlaying extends Activity implements OnClickListener {

	int lp = 2;
	Boolean fvt = false;
	SeekBar sb;
	MediaPlayer player;
	public ArrayList<Song> songList = com.example.musicplayer.MainActivity.songList;
	private SQLiteDatabase database;
	private String playingSongName = "";
	private String playingSongTitle = "";

	private static String PATH;
	private TextView txtSongName;
	private int songPlaying;
	Button play, next, pre;
	TextView txtStart, txtFinal;
	private ImageView imgDisk;

	private double startTime = 0;
	private double finalTime = 0;
	private Handler myHandler = new Handler();
	float angle = 0;
	Random rd = new Random();
	ButtonHandle handle = com.example.musicplayer.MainActivity.handle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.now_playing);
		player = com.example.musicplayer.MainActivity.player;
		openDatabase();
		getPATH();
		play = (Button) findViewById(R.id.btnPlay_NP);
		play.setOnClickListener(this);
		next = (Button) findViewById(R.id.btnNext_NP);
		next.setOnClickListener(this);
		pre = (Button) findViewById(R.id.btnPre_NP);
		pre.setOnClickListener(this);
		Button share, favorite;
		share = (Button) findViewById(R.id.btnShare);
		share.setOnClickListener(this);

		txtSongName = (TextView) findViewById(R.id.txtSongName_NP);
		txtStart = (TextView) findViewById(R.id.txtStart);
		txtFinal = (TextView) findViewById(R.id.txtFinal);
		imgDisk = (ImageView) findViewById(R.id.imgDisk);

		finalTime = player.getDuration();
		txtFinal.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
				TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
		sb = (SeekBar) findViewById(R.id.seekBar);
		sb.setMax((int) finalTime);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				if (fromUser) {
					player.seekTo(progress);
					seekBar.setProgress(progress);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}
		});

		myHandler.postDelayed(UpdateSongTime, 100);

		getData();
		getFavoriteStatus();
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				angle = 0;
				songPlaying = handle.btnNext(songPlaying);
				try {
					getFavoriteStatus();
					playingSongTitle = songList.get(songPlaying).getTitle();
					txtSongName.setText(playingSongTitle);
					finalTime = player.getDuration();
					txtFinal.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
							TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
									- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
					sb.setMax((int) finalTime);
					setBackground();
					
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});
	}

	private Runnable UpdateSongTime = new Runnable() {
		public void run() {
			if (player.isPlaying()) {
				imgDisk.animate().rotation(angle).start();
				angle += 5;
			}
			startTime = player.getCurrentPosition();
			txtStart.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) startTime),
					TimeUnit.MILLISECONDS.toSeconds((long) startTime)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
			sb.setProgress((int) startTime);
			myHandler.postDelayed(this, 100);
		}
	};

	public void back(View view) {
		finish();
	}

	public void loop(View view) {
		Button btnLoop = (Button) findViewById(R.id.btnLoop);
		Cursor c = database.query("playingType", null, null, null, null, null, null);
		c.moveToFirst();
		if (c.isAfterLast() == false) {
			lp = Integer.parseInt(c.getString(0));
			c.moveToNext();
		}
		switch (lp) {
		case 0:
			btnLoop.setBackgroundResource(R.drawable.loop);
			lp = 1;
			break;
		case 1:
			btnLoop.setBackgroundResource(R.drawable.shuffle);
			lp = 2;
			break;
		case 2:
			btnLoop.setBackgroundResource(R.drawable.forward);
			lp = 0;
			break;
		}
		database.delete("playingType", null, null);
		ContentValues values = new ContentValues();
		values.put("type", lp);
		database.insert("playingType", null, values);
	}

	public void favorite(View view) {
		Button btnFavorite = (Button) findViewById(R.id.btnFavorite);
		Cursor c = database.query("favorite", null, null, null, null, null, null);
		c.moveToFirst();
		String p = PATH + songList.get(songPlaying).getName();
		while (c.isAfterLast() == false) {
			if(p.equals(c.getString(1))){
				btnFavorite.setBackgroundResource(R.drawable.heart1);
				database.delete("favorite", "songTitle=?", new String[]{c.getString(0)});
				break;
			}
			c.moveToNext();
		}
		if (c.isAfterLast()) {
			btnFavorite.setBackgroundResource(R.drawable.heart2);
			ContentValues values = new ContentValues();
			values.put("songTitle", songList.get(songPlaying).getTitle());
			values.put("path", p);
			database.insert("favorite", null, values);
		}
	}
	public void getFavoriteStatus()
	{
		Button btnFavorite = (Button) findViewById(R.id.btnFavorite);
		Cursor c = database.query("favorite", null, null, null, null, null, null);
		c.moveToFirst();
		String p = PATH + songList.get(songPlaying).getName();
		while (c.isAfterLast() == false) {
			if(p.equals(c.getString(1))){
				btnFavorite.setBackgroundResource(R.drawable.heart2);
				break;
			}
			c.moveToNext();
		}
		if(c.isAfterLast())
			btnFavorite.setBackgroundResource(R.drawable.heart1);
	}

	public void openDatabase() {
		database = openOrCreateDatabase("musicPlayer.db", MODE_PRIVATE, null);
	}

	public void getData() {
		Cursor c = database.query("recently", null, null, null, null, null, null);
		c.moveToLast();
		if (c.isAfterLast() == false) {
			playingSongName = c.getString(0);
			playingSongTitle = c.getString(1);
			songPlaying = Integer.parseInt(c.getString(2));
			c.moveToNext();
		}
		c.close();
		if (playingSongName.length() > 0) {
			txtSongName.setText(playingSongTitle);
			if (player.isPlaying()) {
				play.setBackgroundResource(R.drawable.pause);
			} else {
				try {
					player.reset();
					player.setDataSource(PATH + playingSongName);
					player.prepare();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		getType();
	}

	public void getType() {
		Button btnLoop = (Button) findViewById(R.id.btnLoop);
		Cursor c = database.query("playingType", null, null, null, null, null, null);
		c.moveToFirst();
		if (c.isAfterLast() == false) {
			lp = Integer.parseInt(c.getString(0));
			c.moveToNext();
		}
		switch (lp) {
		case 0:
			btnLoop.setBackgroundResource(R.drawable.forward);
			break;
		case 1:
			btnLoop.setBackgroundResource(R.drawable.loop);
			break;
		case 2:
			btnLoop.setBackgroundResource(R.drawable.shuffle);
			break;
		}
	}

	public void addRecently(String name, String title) {
		database.delete("recently", "songName=?", new String[] { name });
		ContentValues values = new ContentValues();
		values.put("songName", name);
		values.put("songTitle", title);
		values.put("songPlaying", songPlaying);
		database.insert("recently", null, values);
	}

	public void getPATH() {

		Cursor c = database.query("path", null, null, null, null, null, null);
		c.moveToFirst();
		if (c.isAfterLast() == false) {
			PATH = c.getString(0) + "/";
			c.moveToNext();
		} else {
			PATH = System.getenv("EXTERNAL_STORAGE");
		}
		c.close();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnPlay_NP:
			if (player.isPlaying()) {
				player.pause();
				play.setBackgroundResource(R.drawable.play);
				com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.play);
			} else {
				player.start();
				play.setBackgroundResource(R.drawable.pause);
				com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
			}
			break;
		case R.id.btnNext_NP:
			angle = 0;
			songPlaying = handle.btnNext(songPlaying);
			try {
				getFavoriteStatus();
				playingSongTitle = songList.get(songPlaying).getTitle();
				txtSongName.setText(playingSongTitle);
				finalTime = player.getDuration();
				txtFinal.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
						TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
								- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
				sb.setMax((int) finalTime);
				setBackground();
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case R.id.btnPre_NP:
			angle = 0;
			songPlaying = handle.btnPrev(songPlaying);
			try {
				getFavoriteStatus();
				playingSongTitle = songList.get(songPlaying).getTitle();
				txtSongName.setText(playingSongTitle);
				finalTime = player.getDuration();
				txtFinal.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
						TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
								- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
				sb.setMax((int) finalTime);
				setBackground();
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case R.id.btnShare:
			File audio = new File(PATH + playingSongName);
			Intent intent = new Intent(Intent.ACTION_SEND).setType("audio/*");
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(audio));
			startActivity(Intent.createChooser(intent, "Share to"));
			break;
		default:
			break;
		}
	}
	
	private void setBackground(){
		if (player.isPlaying()) {
			play.setBackgroundResource(R.drawable.pause);
			com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
		}
		else{
			play.setBackgroundResource(R.drawable.play);
			com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.play);
		}
		try{
			if(player.isPlaying())
				com.example.musicplayer.SongsActivity.btnPlayPause.setBackgroundResource(R.drawable.pause);	
			else
				com.example.musicplayer.SongsActivity.btnPlayPause.setBackgroundResource(R.drawable.play);	
		}
		catch(Exception ex)
		{
			
		}
		com.example.musicplayer.MainActivity.txtSongPlaying.setText(playingSongTitle);
		com.example.musicplayer.SongsActivity.txtSongName.setText(playingSongTitle);
	}
}
