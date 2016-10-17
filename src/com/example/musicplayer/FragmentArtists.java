package com.example.musicplayer;

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
import model_song.ArtistAdapter;
import model_song.Song;

public class FragmentArtists extends Fragment {

	private ArrayList<Song> songList = new ArrayList<Song>();
	private ListView lvwArtists;
	boolean ext = false;
	public void getSongList(){
		//query external audio
		ContentResolver musicResolver = this.getActivity().getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
		//iterate over results if valid
		if(musicCursor!=null && musicCursor.moveToFirst()){
			//get columns
			int nameColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.DISPLAY_NAME);
			int titleColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.ARTIST);
			//add songs to list
			do {
				long thisId = musicCursor.getLong(idColumn);
				String thisName = musicCursor.getString(nameColumn);
				String thisTitle = musicCursor.getString(titleColumn);
				String thisArtist = musicCursor.getString(artistColumn);
				boolean existed = false;
				for(int i = 0; i < songList.size(); i++)
				{
					if(songList.get(i).getArtist().equals(thisArtist))
					{
						existed = true;
						break;
					}
				}
				if(!existed)
					songList.add(new Song(thisId, thisName, thisTitle, thisArtist, ""));
				
			} 
			while (musicCursor.moveToNext());
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_artists, container, false);
		lvwArtists = (ListView) view.findViewById(R.id.lvwArtists);

		getSongList();
		//sort alphabetically by title
		Collections.sort(songList, new Comparator<Song>(){
			public int compare(Song a, Song b){
				return a.getTitle().compareTo(b.getTitle());
			}
		});
		
		//create and set adapter
		ArtistAdapter artistAdt = new ArtistAdapter(this.getActivity(), songList);
		lvwArtists.setAdapter(artistAdt);
		lvwArtists.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getActivity(), SongsActivity.class);
				intent.putExtra("artist", songList.get(arg2).getArtist());
				startActivity(intent);
			}
			
		});
		//setup controller
		//setController();
		return view;
	}
	
	
}
