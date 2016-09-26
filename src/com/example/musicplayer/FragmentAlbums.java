package com.example.musicplayer;

import java.util.ArrayList;
import java.util.Arrays;

import android.R.xml;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentAlbums extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String albums[] = new String[]{"Chillax", "#NgungLamBan", "Nobody but me",
				"Ôm giấc mơ em", "Idina"}; 
		View view = inflater.inflate(R.layout.fragment_albums, container, false);
		ListView lvwAlbums = (ListView) view.findViewById(R.id.lvwAlbums);
		ArrayList<Model> arrList = new ArrayList<Model>();
		for(int i = 0; i < albums.length; i++)
		{
			arrList.add(new Model(albums[i]));
		}
		MyAdapter adapter = new MyAdapter(this.getActivity(), arrList);
		lvwAlbums.setAdapter(adapter);
		return view;
	}
	
	public class Model{
	    private String albumName;
	 
	    public Model(String albumName) {
	        super();
	        this.albumName = albumName;
	    }

		public String getAlbumName() {
			return albumName;
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
            imgView.setImageResource(R.drawable.album);
            titleView.setText(modelsArrayList.get(position).getAlbumName());

            // 5. retrn rowView
            return rowView;
        }
	}
}