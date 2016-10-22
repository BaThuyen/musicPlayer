package com.example.musicplayer;

import java.util.ArrayList;
import java.util.Arrays;

import com.example.musicplayer.FragmentMyfavorite.Model;

import android.R.xml;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentRecently extends Fragment {

	ArrayList<Model> arrList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recently, container, false);
		ListView lvwSongRecently = (ListView) view.findViewById(R.id.lvwSongRecently);
		arrList = new ArrayList<Model>();
		getRecently();
		MyAdapter adapter = new MyAdapter(this.getActivity(), arrList);
		lvwSongRecently.setAdapter(adapter);
		lvwSongRecently.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				try {
					com.example.musicplayer.MainActivity.player.reset();
					com.example.musicplayer.MainActivity.player.setDataSource(com.example.musicplayer.MainActivity.PATH + arrList.get(arg2).getSongName());
					com.example.musicplayer.MainActivity.player.prepare();
					com.example.musicplayer.MainActivity.player.start();
					com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
					com.example.musicplayer.MainActivity.txtSongPlaying.setText(arrList.get(arg2).getSongTitle());
					com.example.musicplayer.MainActivity.songPlaying = arrList.get(arg2).getSongPlaying();
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
			}
		});
		return view;
	}
	private void getRecently(){
		Cursor c = com.example.musicplayer.MainActivity.database.query("recently", null, null, null, null, null, null);
		c.moveToFirst();
		while(c.isAfterLast() == false){
			arrList.add(new Model(c.getString(0), c.getString(1), Integer.parseInt(c.getString(2))));
			c.moveToNext();
		}
	}
	public class Model{
	    private String songName;
	    private String songTitle;
	    int songPlaying;
		public Model(String songName, String songTitle, int songPlaying) {
			super();
			this.songName = songName;
			this.songTitle = songTitle;
			this.songPlaying = songPlaying;
		}

		public String getSongName() {
			return songName;
		}
		public String getSongTitle() {
			return songTitle;
		}
		public int getSongPlaying() {
			return songPlaying;
		}
		
	}
	public class MyAdapter extends ArrayAdapter<Model>{
		private final Context context;
        private final ArrayList<Model> modelsArrayList;
 
        public MyAdapter(Context context, ArrayList<Model> modelsArrayList) {
 
            super(context, R.layout.list_item, modelsArrayList);
 
            this.context = context;
            this.modelsArrayList = modelsArrayList;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
 
            // 1. Create inflater 
            LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
            // 2. Get rowView from inflater
 
            View rowView = inflater.inflate(R.layout.list_item, parent, false);

            ImageView imgView = (ImageView) rowView.findViewById(R.id.imvIcon); 
            TextView titleView = (TextView) rowView.findViewById(R.id.txtName);
 
                // 4. Set the text for textView 
            imgView.setImageResource(R.drawable.recent);
            titleView.setText(modelsArrayList.get(position).getSongName());

            // 5. retrn rowView
            return rowView;
        }
	}
}
