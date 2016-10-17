package com.example.musicplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.zip.Inflater;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;
import android.widget.Toast;
import model_song.*;
import model_song.MusicService.MusicBinder;

public class SongsActivity extends Activity {

	private Button btnPlayPause, btnNext, btnPre;
	public ArrayList<Song> songList;
	private ListView lvwSongs;
	private MediaPlayer player;
	private MusicController controller;
	private SQLiteDatabase database;
	private String playingSongName = "";
	private String playingSongTitle = "";

	private static String PATH;
	private TextView txtSongName;
	private int songPlaying;

	private String album;
	private String artist;

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs);
		openDatabase();
		getPATH();
		Toast.makeText(this, PATH, Toast.LENGTH_LONG).show();
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			album = null;
			artist = null;
		} else {
			artist = extras.getString("artist");
			album = extras.getString("album");
		}
		controller = new MusicController(this);
		lvwSongs = (ListView) findViewById(R.id.lvwSongs);
		txtSongName = (TextView) findViewById(R.id.txtSongName);
		txtSongName.setText("");
		songList = new ArrayList<Song>();
		player = com.example.musicplayer.MainActivity.player;

		updatePlaylist();
		SongAdapter songAdt = new SongAdapter(this, songList);
		lvwSongs.setAdapter(songAdt);
		lvwSongs.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				// TODO Auto-generated method stub
				try {
					player.reset();
					player.setDataSource(PATH + songList.get(position).getName());
					player.prepare();
					player.start();
					// set the data source
					songPlaying = position;
					String songName = songList.get(songPlaying).getTitle();
					if (songName.length() > 15)
						songName = songName.substring(0, 15).concat("..");
					txtSongName.setText(songName);
					btnPlayPause.setBackgroundResource(R.drawable.pause);
					addRecently(songList.get(position).getName(), songList.get(position).getTitle());
					com.example.musicplayer.MainActivity.txtSongPlaying.setText(songName);
					com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_LONG).show();
				}
			}
		});
		btnPlayPause = (Button) findViewById(R.id.btnPlayPause);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnPre = (Button) findViewById(R.id.btnPre);

		btnPlayPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (player.isPlaying()) {
					player.pause();
					btnPlayPause.setBackgroundResource(R.drawable.play);
				} else {
					player.start();
					btnPlayPause.setBackgroundResource(R.drawable.pause);
					com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
				}
			}
		});
		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
					btnPlayPause.setBackgroundResource(R.drawable.pause);
					addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle());
					com.example.musicplayer.MainActivity.txtSongPlaying.setText(songName);
					com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnPre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
					btnPlayPause.setBackgroundResource(R.drawable.pause);
					addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle());
					com.example.musicplayer.MainActivity.txtSongPlaying.setText(songName);
					com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		Button btnRandom = (Button) findViewById(R.id.btnRandom);
		btnRandom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Random rd = new Random();
				songPlaying = rd.nextInt(songList.size() - 1);
				player.reset();
				try {
					player.setDataSource(PATH + songList.get(songPlaying).getName());
					player.prepare();
					player.start();

					String songName = songList.get(songPlaying).getTitle();
					if (songName.length() > 15)
						songName = songName.substring(0, 15).concat("..");
					txtSongName.setText(songName);
					btnPlayPause.setBackgroundResource(R.drawable.pause);
					addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle());
					com.example.musicplayer.MainActivity.txtSongPlaying.setText(songName);
					com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		getData();
		
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				songPlaying++;
				if (songPlaying >= songList.size())
					songPlaying = 0;
				try {
					player.reset();
					player.setDataSource(PATH + songList.get(songPlaying).getName());
					player.prepare();
					player.start();
					String songName = songList.get(songPlaying).getTitle();
					if (songName.length() > 15)
						songName = songName.substring(0, 15).concat("..");
					txtSongName.setText(songName);
					btnPlayPause.setBackgroundResource(R.drawable.pause);
					addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle());

					com.example.musicplayer.MainActivity.txtSongPlaying.setText(songName);
					com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}// end of onCreate

	public void openDatabase() {
		database = openOrCreateDatabase("musicPlayer.db", MODE_PRIVATE, null);
	}

	public void getData() {
		Cursor c = database.query("recently", null, null, null, null, null, null);
		c.moveToFirst();
		if (c.getCount() > 0)
			if (c.isAfterLast()) {
				playingSongName = c.getString(0);
				playingSongTitle = c.getString(1);
				songPlaying = Integer.parseInt(c.getString(2));
				Toast.makeText(this, playingSongTitle, Toast.LENGTH_LONG).show();
				c.moveToNext();
			}
		c.close();
		if (playingSongName.length() > 0) {
			if (playingSongTitle.length() > 20)
				playingSongTitle = playingSongTitle.substring(0, 20) + "..";
			txtSongName.setText(playingSongTitle);
			if (player.isPlaying()) {
				btnPlayPause.setBackgroundResource(R.drawable.pause);
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

	public void updatePlaylist() {
		ContentResolver musicResolver = getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
		// iterate over results if valid
		if (musicCursor != null && musicCursor.moveToFirst()) {
			// get columns
			int nameColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DISPLAY_NAME);
			int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
			int albumColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
			// add songs to list
			do {
				long thisId = musicCursor.getLong(idColumn);
				String thisName = musicCursor.getString(nameColumn);
				String thisTitle = musicCursor.getString(titleColumn);
				String thisArtist = musicCursor.getString(artistColumn);
				String thisAlbum = musicCursor.getString(albumColumn);
				if (new File(PATH + thisName).exists()) {
					if (album == null && artist == null)
						songList.add(new Song(thisId, thisName, thisTitle, thisArtist, thisAlbum));
					else if (thisAlbum.equals(album))
						songList.add(new Song(thisId, thisName, thisTitle, thisArtist, thisAlbum));
					else if (thisArtist.equals(artist))
						songList.add(new Song(thisId, thisName, thisTitle, thisArtist, thisAlbum));
				}
			} while (musicCursor.moveToNext());
		}
	}// end of updatePlaylist
}
