package com.example.musicplayer;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FragmentSongs extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String songs[] = new String[]{"Gửi anh xa nhớ", "Hãy ra khỏi người đó đi", "Trách ai bây giờ",
				"Anh cứ đi đi", "Đếm ngày xa em"}; 
		View view = inflater.inflate(R.layout.fragment_songs, container, false);
		ListView lvwSongs = (ListView) view.findViewById(R.id.lvwSongs);
		ArrayList<String> arrList = new ArrayList<String>(Arrays.asList(songs));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, arrList);
		lvwSongs.setAdapter(adapter);
		return view;
	}
}
