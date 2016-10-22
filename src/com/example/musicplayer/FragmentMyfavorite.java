package com.example.musicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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

public class FragmentMyfavorite extends Fragment {

	String favorite[];
	ArrayList<Model> arrList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_myfavorite, container, false);
		ListView lvwMyfavorite = (ListView) view.findViewById(R.id.lvwMyfavorite);
		arrList = new ArrayList<Model>();
		getFavorite();

		MyAdapter adapter = new MyAdapter(this.getActivity(), arrList);
		lvwMyfavorite.setAdapter(adapter);
		lvwMyfavorite.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				try {
					com.example.musicplayer.MainActivity.player.reset();
					com.example.musicplayer.MainActivity.player.setDataSource(arrList.get(arg2).getPath());
					com.example.musicplayer.MainActivity.player.prepare();
					com.example.musicplayer.MainActivity.player.start();
					com.example.musicplayer.MainActivity.btnPlay.setBackgroundResource(R.drawable.pause);
					com.example.musicplayer.MainActivity.txtSongPlaying.setText(arrList.get(arg2).getSongName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
			}
		});
		return view;
	}
	private void getFavorite(){
		Cursor c = com.example.musicplayer.MainActivity.database.query("favorite", null, null, null, null, null, null);
		c.moveToFirst();
		while(c.isAfterLast() == false){
			arrList.add(new Model(c.getString(0), c.getString(1)));
			c.moveToNext();
		}
	}
	public class Model{
	    private String songName;
	    private String path;
	 
	    public Model(String songName, String path) {
	        super();
	        this.songName = songName;
	        this.path = path;
	    }

		public String getSongName() {
			return songName;
		}
		public String getPath() {
			return path;
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
            imgView.setImageResource(R.drawable.heart2);
            titleView.setText(modelsArrayList.get(position).getSongName());

            // 5. retrn rowView
            return rowView;
        }
	}
}
