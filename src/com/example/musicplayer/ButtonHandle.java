package com.example.musicplayer;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.widget.Button;
import model_song.Song;

public class ButtonHandle {
	MediaPlayer player;
	SQLiteDatabase database;
	ArrayList<Song> songList;
	String PATH;
	
	Random rd = new Random();
	public ButtonHandle(){
		player = com.example.musicplayer.MainActivity.player;
		database = com.example.musicplayer.MainActivity.database;
		songList = com.example.musicplayer.MainActivity.songList;
		PATH = com.example.musicplayer.MainActivity.PATH;
	}
	
	public int getType() {
		Cursor c = database.query("playingType", null, null, null, null, null, null);
		int type = 0;
		c.moveToFirst();
		if (c.isAfterLast() == false) {
			type = Integer.parseInt(c.getString(0));
			c.moveToNext();
		}
		return type;
	}
	
	public void addRecently(String name, String title, int songPlaying) {
		database.delete("recently", "songName=?", new String[] { name });
		ContentValues values = new ContentValues();
		values.put("songName", name);
		values.put("songTitle", title);
		values.put("songPlaying", songPlaying);
		database.insert("recently", null, values);
	}
	
	public void btnPlayPause(){
		if (player.isPlaying()) {
			player.pause();
			com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.play);
		} else {
			player.start();
			com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
		}
	}
	
	public int btnNext(int songPlaying){
		int type = getType();
		boolean stop = false;
		if (type == 0) {
			songPlaying++;
			if (songPlaying >= songList.size()) {
				songPlaying = songList.size() - 1;
				stop = true;
			}
		} else if (type == 1) {
			songPlaying++;
			if (songPlaying >= songList.size())
				songPlaying = 0;
		} else {
			songPlaying = rd.nextInt(songList.size() - 1);
		}
		
		try {
			player.reset();
			player.setDataSource(PATH + songList.get(songPlaying).getName());
			player.prepare();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!stop){
			player.start();
			com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
			addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle(), songPlaying);
			com.example.musicplayer.MainActivity.btnPre.setBackgroundResource(R.drawable.previous);
			com.example.musicplayer.MainActivity.btnNext.setBackgroundResource(R.drawable.next);
		}
		else{
			com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.play);
			com.example.musicplayer.MainActivity.btnNext.setBackgroundResource(R.drawable.next_un);
		}
		
		return songPlaying;
	}
	
	public int btnPrev(int songPlaying){
		int type = getType();
		boolean stop = false;
		if (type == 0) {
			songPlaying--;
			if (songPlaying < 0) {
				songPlaying = 0;
				stop = true;
			}
		} else if (type == 1) {
			songPlaying--;
			if (songPlaying < 0)
				songPlaying = songList.size() - 1;
		} else {
			songPlaying = rd.nextInt(songList.size() - 1);
		}
		player.reset();
		try {
			player.reset();
			player.setDataSource(PATH + songList.get(songPlaying).getName());
			player.prepare();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!stop){
			player.start();
			com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
			addRecently(songList.get(songPlaying).getName(), songList.get(songPlaying).getTitle(), songPlaying);
			com.example.musicplayer.MainActivity.btnPre.setBackgroundResource(R.drawable.previous);
			com.example.musicplayer.MainActivity.btnNext.setBackgroundResource(R.drawable.next);
		}
		else{
			com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.play);
			com.example.musicplayer.MainActivity.btnPre.setBackgroundResource(R.drawable.previous_un);
		}
		return songPlaying;
	}
}
