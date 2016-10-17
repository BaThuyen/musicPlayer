package model_song;

import java.util.ArrayList;

import com.example.musicplayer.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * This is demo code to accompany the Mobiletuts+ series:
 * Android SDK: Creating a Music Player
 * 
 * Sue Smith - February 2014
 */

public class AlbumAdapter extends BaseAdapter {
	
	//song list and layout
	private ArrayList<Song> songs;
	private LayoutInflater songInf;
	
	//constructor
	public AlbumAdapter(Context c, ArrayList<Song> theSongs){
		songs=theSongs;
		songInf=LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return songs.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//map to song layout
		LinearLayout item = (LinearLayout)songInf.inflate
				(R.layout.list_item, parent, false);
		//get title and artist views
		ImageView img = (ImageView) item.findViewById(R.id.imvIcon);
		img.setImageResource(R.drawable.album);
		TextView albumView = (TextView)item.findViewById(R.id.txtName);
		//get song using position
		Song currSong = songs.get(position);
		//get title and artist strings
		albumView.setText(currSong.getAlbum());
		//set position as tag
		item.setTag(position);
		return item;
	}

}
