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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;
import android.widget.Toast;
import model_song.*;

public class SongsActivity extends Activity {

	public static Button btnPlayPause, btnNext, btnPre;
	public ArrayList<Song> songList;
	private ListView lvwSongs;
	private MediaPlayer player;
	private SQLiteDatabase database;
	private String playingSongName = "";
	private String playingSongTitle = "";

	private static String PATH;
	private static String myPlaylist;
	public static TextView txtSongName;
	private int songPlaying;

	private String album;
	private String artist;
	int lp;
	Random rd = new Random();
	ButtonHandle handle = com.example.musicplayer.MainActivity.handle;

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
		myPlaylist = com.example.musicplayer.MainActivity.myPlaylist;
		Toast.makeText(this, myPlaylist, Toast.LENGTH_LONG).show();
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			album = null;
			artist = null;
		} else {
			artist = extras.getString("artist");
			album = extras.getString("album");
		}
		lvwSongs = (ListView) findViewById(R.id.lvwSongs);
		txtSongName = (TextView) findViewById(R.id.txtSongName);

		songList = new ArrayList<Song>();
		player = com.example.musicplayer.MainActivity.player;
		btnPlayPause = (Button) findViewById(R.id.btnPlayPause);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnPre = (Button) findViewById(R.id.btnPre);

		updatePlaylist();
		getData();
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
		lvwSongs.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Long click", Toast.LENGTH_LONG).show();
				return true;
			}
		});

		btnPlayPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (player.isPlaying()) {
					player.pause();
					btnPlayPause.setBackgroundResource(R.drawable.play);
					com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.play);
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
				songPlaying = handle.btnNext(songPlaying);
				try {
					String songName = songList.get(songPlaying).getTitle();
					if (songName.length() > 15)
						songName = songName.substring(0, 15).concat("..");
					txtSongName.setText(songName);

					addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle());
					com.example.musicplayer.MainActivity.txtSongPlaying.setText(songName);
					if (player.isPlaying()) {
						btnPlayPause.setBackgroundResource(R.drawable.pause);
						com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
					}
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
				songPlaying = handle.btnPrev(songPlaying);
				try {
					String songName = songList.get(songPlaying).getTitle();
					if (songName.length() > 15)
						songName = songName.substring(0, 15).concat("..");
					txtSongName.setText(songName);
					addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle());
					com.example.musicplayer.MainActivity.txtSongPlaying.setText(songName);
					if (player.isPlaying()) {
						btnPlayPause.setBackgroundResource(R.drawable.pause);
						com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
					}
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

		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				songPlaying = handle.btnNext(songPlaying);
				try {
					String songName = songList.get(songPlaying).getTitle();
					if (songName.length() > 15)
						songName = songName.substring(0, 15).concat("..");
					txtSongName.setText(songName);

					addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle());
					com.example.musicplayer.MainActivity.txtSongPlaying.setText(songName);
					if (player.isPlaying()) {
						btnPlayPause.setBackgroundResource(R.drawable.pause);
						com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}// end of onCreate

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
			if (playingSongTitle.length() > 20) {
				playingSongTitle = playingSongTitle.substring(0, 18) + "..";
			}
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
		} else {
			if (songList.size() > 0) {
				try {
					player.setDataSource(PATH + songList.get(0).getName());
					player.prepare();
				} catch (Exception ex) {

				}

				String songName = songList.get(0).getTitle();
				if (songName.length() > 15)
					songName = songName.substring(0, 15).concat("..");
				txtSongName.setText(songName);
			} else {
				if (!player.isPlaying())
					txtSongName.setText("Thư mục trống!");
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
				if (myPlaylist.length() > 0) {
					if (new File(myPlaylist + thisName).exists()) {
						if (album == null && artist == null)
							songList.add(new Song(thisId, thisName, thisTitle, thisArtist, thisAlbum));
						else if (thisAlbum.equals(album))
							songList.add(new Song(thisId, thisName, thisTitle, thisArtist, thisAlbum));
						else if (thisArtist.equals(artist))
							songList.add(new Song(thisId, thisName, thisTitle, thisArtist, thisAlbum));
					}
				} else {
					if (new File(PATH + thisName).exists()) {
						if (album == null && artist == null)
							songList.add(new Song(thisId, thisName, thisTitle, thisArtist, thisAlbum));
						else if (thisAlbum.equals(album))
							songList.add(new Song(thisId, thisName, thisTitle, thisArtist, thisAlbum));
						else if (thisArtist.equals(artist))
							songList.add(new Song(thisId, thisName, thisTitle, thisArtist, thisAlbum));
					}
				}
			} while (musicCursor.moveToNext());
		}
	}// end of updatePlaylist

	public void ActivityPlaying(View view) {
		Intent intent = new Intent(this, NowPlaying.class);
		startActivity(intent);

	}

	public void getType() {
		Cursor c = database.query("playingType", null, null, null, null, null, null);
		c.moveToFirst();
		if (c.isAfterLast() == false) {
			lp = Integer.parseInt(c.getString(0));
			c.moveToNext();
		}
	}
}
