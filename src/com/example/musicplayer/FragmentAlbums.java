package com.example.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import android.R.xml;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import model_song.AlbumAdapter;
import model_song.ArtistAdapter;
import model_song.Song;

public class FragmentAlbums extends Fragment {

	private ArrayList<Song> songList = new ArrayList<Song>();
	private ListView lvwAlbums;
	boolean ext = false;
	public void getSongList(){
		//query external audio
		ContentResolver musicResolver = this.getActivity().getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
		//iterate over results if valid
		if(musicCursor != null && musicCursor.moveToFirst()){
			//get column
			int idColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media._ID);
			int albumColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.ALBUM);
			//add songs to list
			do {
				long thisId = musicCursor.getLong(idColumn);
				String thisAlbum = musicCursor.getString(albumColumn);
				boolean existed = false;
				for(int i = 0; i < songList.size(); i++)
				{
					if(songList.get(i).getAlbum().equals(thisAlbum))
					{
						existed = true;
						break;
					}
				}
				if(!existed)
					songList.add(new Song(thisId, "", "", "", thisAlbum));
			} 
			while (musicCursor.moveToNext());
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_albums, container, false);
		lvwAlbums = (ListView) view.findViewById(R.id.lvwAlbums);

		getSongList();
		//sort alphabetically by title
		Collections.sort(songList, new Comparator<Song>(){
			public int compare(Song a, Song b){
				return a.getTitle().compareTo(b.getTitle());
			}
		});
		
		//create and set adapter
		AlbumAdapter albumAdt = new AlbumAdapter(this.getActivity(), songList);
		lvwAlbums.setAdapter(albumAdt);
		lvwAlbums.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getActivity(), SongsActivity.class);
				intent.putExtra("album", songList.get(arg2).getAlbum());
				startActivity(intent);
			}
			
		});
		return view;
	}
}
