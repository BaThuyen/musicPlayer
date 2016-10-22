package com.example.musicplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.MediaController.MediaPlayerControl;
import model_song.Song;

public class MainActivity extends Activity implements OnClickListener {

	public static ArrayList<Song> songList;

	int lp;
	Random rd = new Random();
	private static String playingSongName;
	private static String playingSongTitle;
	public static SQLiteDatabase database;
	// binding
	public static MediaPlayer player;
	public static String PATH;
	public static String myPlaylist = "";
	public static ButtonHandle handle;

	// controller

	// activity and playback pause flags
	private boolean paused = false, playbackPaused = false;
	public static int songPlaying;

	EditText txtKeyword;
	static TextView txtSongPlaying;
	static Button btnPlay, btnNext, btnPre;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		player = new MediaPlayer();
		setContentView(R.layout.activity_main);
		myPlaylist = "";
		playingSongName = "";
		playingSongTitle = "";
		openDatabase();
		getData();
		getPATH();
		getType();
		songList = new ArrayList<Song>();
		updatePlaylist();
		Fragment myfragment;
		myfragment = new Fragment_main();

		player = new MediaPlayer();

		btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(this);

		btnNext = (Button) findViewById(R.id.btnNext_M);
		btnNext.setOnClickListener(this);

		btnPre = (Button) findViewById(R.id.btnPre_M);
		btnPre.setOnClickListener(this);
		handle = new ButtonHandle();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.frgSwitch, myfragment);
		fragmentTransaction.commit();
		txtKeyword = (EditText) findViewById(R.id.txtKeyword);
		txtSongPlaying = (TextView) findViewById(R.id.txtSongPlaying);

		if (playingSongName.length() > 0) {
			if (playingSongTitle.length() > 20)
				playingSongTitle = playingSongTitle.substring(0, 20) + "..";
			txtSongPlaying.setText(playingSongTitle);
			try {
				player.reset();
				player.setDataSource(PATH + playingSongName);
				player.prepare();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				songPlaying = handle.btnNext(songPlaying);
				try {
					playingSongName = songList.get(songPlaying).getTitle();
					if (playingSongName.length() > 15)
						playingSongName = playingSongName.substring(0, 15).concat("..");
					txtSongPlaying.setText(playingSongName);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}

	public void openDatabase() {
		database = openOrCreateDatabase("musicPlayer.db", MODE_PRIVATE, null);
		String query = "CREATE TABLE IF NOT EXISTS recently (songName TEXT, songTitle TEXT, songPlaying INTEGER);";
		database.execSQL(query);

		query = "CREATE TABLE IF NOT EXISTS path(directory TEXT);";
		database.execSQL(query);

		query = "CREATE TABLE IF NOT EXISTS favorite (songTitle TEXT, path TEXT);";
		database.execSQL(query);

		query = "CREATE TABLE IF NOT EXISTS playingType(type INTERGER)";
		database.execSQL(query);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		player.stop();
		System.exit(1);
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
					songList.add(new Song(thisId, thisName, thisTitle, thisArtist, thisAlbum));
				}
			} while (musicCursor.moveToNext());
		}
	}

	public static void getData() {
		Cursor c = database.query("recently", null, null, null, null, null, null);
		c.moveToLast();
		if (c.isAfterLast() == false) {
			playingSongName = c.getString(0);
			playingSongTitle = c.getString(1);
			songPlaying = c.getInt(2);
		}
		c.close();
	}

	public void FragmentMain(View view) {
		Fragment myfragment;
		myfragment = new Fragment_main();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.frgSwitch, myfragment);
		fragmentTransaction.commit();
	}

	public void FragmentSongs(View view) {
		myPlaylist = "";
		Intent intent = new Intent(this, SongsActivity.class);
		startActivity(intent);
	}

	public void FragmentAlbums(View view) {
		Fragment myfragment;
		myfragment = new FragmentAlbums();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.frgSwitch, myfragment);
		fragmentTransaction.commit();
	}

	public void FragmentArtists(View view) {
		Fragment myfragment;
		myfragment = new FragmentArtists();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.frgSwitch, myfragment);
		fragmentTransaction.commit();
	}

	public void FragmentFolders(View view) {
		Intent intent = new Intent(this, FileExplorerActivity.class);

		startActivity(intent);
	}

	public void FragmentRecently(View view) {
		Fragment myfragment;
		myfragment = new FragmentRecently();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.frgSwitch, myfragment);
		fragmentTransaction.commit();
	}

	public void FragmentFavorite(View view) {
		Fragment myfragment;
		myfragment = new FragmentMyfavorite();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.frgSwitch, myfragment);
		fragmentTransaction.commit();
	}

	public void ActivityPlaying(View view) {
		
		Intent intent = new Intent(this, NowPlaying.class);
		startActivity(intent);
	}

	public void MyPlaylist(View view) {
		myPlaylist = System.getenv("EXTERNAL_STORAGE") + "/myPlaylist/";
		File folder = new File(myPlaylist);
		if(folder.exists()){
			
		}
		else{
			folder.mkdir();
		}
		Intent intent = new Intent(this, SongsActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btnPlay:
			handle.btnPlayPause();
			break;
		case R.id.btnNext_M:
			songPlaying = handle.btnNext(songPlaying);
			try {
				playingSongName = songList.get(songPlaying).getTitle();
				if (playingSongName.length() > 15)
					playingSongName = playingSongName.substring(0, 15).concat("..");
				txtSongPlaying.setText(playingSongName);
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case R.id.btnPre_M:
			songPlaying = handle.btnPrev(songPlaying);
			try {
				playingSongName = songList.get(songPlaying).getTitle();
				if (playingSongName.length() > 15)
					playingSongName = playingSongName.substring(0, 15).concat("..");
				txtSongPlaying.setText(playingSongName);
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		default:
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

	public void getType() {
		Cursor c = database.query("playingType", null, null, null, null, null, null);
		c.moveToFirst();
		if (c.isAfterLast() == false) {
			lp = Integer.parseInt(c.getString(0));
			c.moveToNext();
		}
	}
}
