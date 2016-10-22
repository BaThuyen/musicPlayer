package com.example.musicplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
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
	CountDownTimer count;
	int index =0;
	// service

	private static String playingSongName;
	private static String playingSongTitle;
	private static SQLiteDatabase database;
	// binding
	private boolean musicBound = false;
	public static MediaPlayer player;
	public static String PATH;

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

		playingSongName = "";
		playingSongTitle = "";
		openDatabase();
		getData();
		getPATH();
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
				songPlaying++;
				if (songPlaying >= songList.size())
					songPlaying = 0;
				try {
					player.reset();
					player.setDataSource(PATH + songList.get(songPlaying).getName());
					player.prepare();
					player.start();
					txtSongPlaying.setText(songList.get(songPlaying).getName());
					btnPlay.setBackgroundResource(R.drawable.pause);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event)  {
//	    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
//	            && keyCode == KeyEvent.KEYCODE_BACK
//	            && event.getRepeatCount() == 0) {
//	    	onBackPressed();
//	    }
//
//	    return super.onKeyDown(keyCode, event);
//	}
//	
//	int flag = 0;
//	@Override
//	
//	public void onBackPressed() {
//		//Toast.makeText(getApplicationContext(), index+" "+ flag , Toast.LENGTH_LONG).show();
//		if(flag == 1 && index < 80){
//			finish();
//		}
//		if(flag == 0){
//			Toast.makeText(getApplicationContext(), "press back again to exit", Toast.LENGTH_LONG).show();
//			flag =1;
//			count = new CountDownTimer(5000, 100) {
//				
//				@Override
//				public void onTick(long arg0) {
//					// TODO Auto-generated method stub
//					index ++;
//					
//					Log.d("abc", "a"+index);
//				}
//				
//				@Override
//				public void onFinish() {
//					flag = 0;
//				}
//			};
//		}
//		
//	    count.start();
//	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
 
            showExitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
 
    private void showExitDialog() {
        // TODO Auto-generated method stub
 
        AlertDialog.Builder adb = new Builder(this);
        adb.setTitle("Warning");
        adb.setMessage("Are you sure you want to quit ?");
        adb.setIcon(R.drawable.ic_launcher);
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
 
                if (database.isOpen())
                    database.close();
                finish();
            }
        });
 
        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
 
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
 
                arg0.dismiss();
            }
        });
 
        AlertDialog ad = adb.create();
        ad.show();
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btnPlay:
			if (player.isPlaying()) {
				player.pause();
				btnPlay.setBackgroundResource(R.drawable.play);
			} else {
				player.start();
				btnPlay.setBackgroundResource(R.drawable.pause);
				;
			}
			break;
		case R.id.btnNext_M:
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
				txtSongPlaying.setText(songName);
				btnPlay.setBackgroundResource(R.drawable.pause);
				addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.btnPre_M:
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
				txtSongPlaying.setText(songName);
				btnPlay.setBackgroundResource(R.drawable.pause);
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
	


	public void addRecently(String name, String title) {
		database.delete("recently", "songName=?", new String[] { name });
		ContentValues values = new ContentValues();
		values.put("songName", name);
		values.put("songTitle", title);
		values.put("songPlaying", songPlaying);
		database.insert("recently", null, values);
	}
}
