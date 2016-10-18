package com.example.musicplayer;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import model_song.Song;
import android.widget.Toast;

public class NowPlaying extends Activity implements OnClickListener{

	String lp = "fw";
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

	private double startTime = 0;
	private double finalTime = 0;
	private Handler myHandler = new Handler();;

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
		Button loop, share, favorite;

		txtSongName = (TextView) findViewById(R.id.txtSongName_NP);
		txtStart = (TextView) findViewById(R.id.txtStart);
		txtFinal = (TextView) findViewById(R.id.txtFinal);

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
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				songPlaying++;
				if (songPlaying >= songList.size())
					songPlaying = 0;
				try {
					Toast.makeText(getApplicationContext(), songList.get(songPlaying).getName(), Toast.LENGTH_LONG)
							.show();
					player.reset();
					player.setDataSource(PATH + songList.get(songPlaying).getName());
					player.prepare();
					player.start();
					String songName = songList.get(songPlaying).getTitle();
					if (songName.length() > 15)
						songName = songName.substring(0, 15).concat("..");
					txtSongName.setText(songName);
					play.setBackgroundResource(R.drawable.pause);
					addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle());
					play.setBackgroundResource(R.drawable.pause);
					finalTime = player.getDuration();
					txtFinal.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
							TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
									- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
					sb = (SeekBar) findViewById(R.id.seekBar);
					sb.setMax((int) finalTime);
					com.example.musicplayer.MainActivity.txtSongPlaying.setText(songName);
					com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
					com.example.musicplayer.SongsActivity.txtSongName.setText(songName);
					com.example.musicplayer.SongsActivity.btnPlayPause.setBackgroundResource(R.drawable.pause);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}

	private Runnable UpdateSongTime = new Runnable() {
		public void run() {
			startTime = player.getCurrentPosition();
			txtStart.setText(String.format("%02d:%02d",

					TimeUnit.MILLISECONDS.toMinutes((long) startTime), TimeUnit.MILLISECONDS.toSeconds((long) startTime)
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
		switch (lp) {
		case "lp":
			btnLoop.setBackgroundResource(R.drawable.shuffle);
			lp = "sf";
			break;
		case "fw":
			btnLoop.setBackgroundResource(R.drawable.loop);
			lp = "lp";
			break;
		case "sf":
			btnLoop.setBackgroundResource(R.drawable.forward);
			lp = "fw";
			break;
		}
	}

	public void favorite(View view) {
		Button btnFavorite = (Button) findViewById(R.id.btnFavorite);
		if (fvt) {
			btnFavorite.setBackgroundResource(R.drawable.heart1);
			fvt = false;
		} else {
			btnFavorite.setBackgroundResource(R.drawable.heart2);
			fvt = true;
		}
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
			if (songPlaying > 0) {
				songPlaying--;
			} else {
				songPlaying = songList.size() - 1;
			}
			player.reset();
			try {
				player.setDataSource(PATH + songList.get(songPlaying).getName());
				player.prepare();
				player.start();

				String songName = songList.get(songPlaying).getTitle();
				if (songName.length() > 15)
					songName = songName.substring(0, 15).concat("..");
				txtSongName.setText(songName);
				play.setBackgroundResource(R.drawable.pause);
				addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.btnPre_NP:
			if (songPlaying < songList.size() - 1) {
				songPlaying++;
			} else {
				songPlaying = 0;
			}
			player.reset();
			try {
				player.setDataSource(PATH + songList.get(songPlaying).getName());
				player.prepare();
				player.start();

				String songName = songList.get(songPlaying).getTitle();
				if (songName.length() > 15)
					songName = songName.substring(0, 15).concat("..");
				txtSongName.setText(songName);
				play.setBackgroundResource(R.drawable.pause);
				addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
}
