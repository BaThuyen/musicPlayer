package com.example.musicplayer;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.R.xml;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.ClipData.Item;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileExplorerActivity extends Activity {

	private File dir;
	ListView lvwFolders;
	SQLiteDatabase database;
	ArrayList<Model> arrList;
	TextView curFolder;
	public static String PATH = com.example.musicplayer.MainActivity.PATH;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.file_explorer);
		dir = new File(PATH);
		curFolder = (TextView) findViewById(R.id.txtCurFolder);
		curFolder.setText("Current Folder: " + PATH);
		lvwFolders = (ListView) findViewById(R.id.lvwFolders);
		fill(dir);
		lvwFolders.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				Model folder = arrList.get(position);
				if (!folder.getfolderName().endsWith(".mp3")) {
					dir = new File(folder.getDirectory());
					fill(dir);
				}
			}
		});
		Button btnSelectFolder = (Button) findViewById(R.id.btnSelectFolder);
		btnSelectFolder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setPATH(dir.getPath());
				PATH = dir.getPath();
				Intent intent = new Intent(getApplicationContext(), SongsActivity.class);
				startActivity(intent);
			}
		});
	}

	private void fill(File f) {
		File[] dirs = f.listFiles();
		arrList = new ArrayList<Model>();
		if (!f.getParent().equals("/"))
			arrList.add(new Model("../", f.getParent(), false));
		try {
			for (File ff : dirs) {

				File[] fbuf = ff.listFiles();
				int buf = 0;
				if (fbuf != null) {
					buf = fbuf.length;
				} else
					buf = 0;
				String num_item = String.valueOf(buf);
				if (buf == 0)
					num_item = num_item + " item";
				else
					num_item = num_item + " items";
				if (!ff.isFile())
					arrList.add(new Model(ff.getName(), ff.getPath(), false));
				else if (ff.getName().endsWith(".mp3"))
					arrList.add(new Model(ff.getName(), ff.getPath(), true));

			}
		} catch (Exception e) {

		}

		MyAdapter adapter = new MyAdapter(this, arrList);
		lvwFolders.setAdapter(adapter);
	}

	public void setPATH(String directory) {
		database = openOrCreateDatabase("musicPlayer.db", MODE_PRIVATE, null);
		database.delete("path", null, null);
		ContentValues values = new ContentValues();
		values.put("directory", directory);
		database.insert("path", null, values);
	}

	public class Model {
		private String folderName;
		private String directory;
		private boolean isSong = false;

		public Model(String folderName, String directory, boolean isSong) {
			super();
			this.folderName = folderName;
			this.directory = directory;
			this.isSong = isSong;
		}

		public String getfolderName() {
			return folderName;
		}

		public String getDirectory() {
			return directory;
		}

		public boolean isSong() {
			return isSong;
		}

		public void setSong(boolean isSong) {
			this.isSong = isSong;
		}
	}

	public class MyAdapter extends ArrayAdapter<Model> {
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
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// 2. Get rowView from inflater

			View rowView = inflater.inflate(R.layout.list_item, parent, false);

			ImageView imgView = (ImageView) rowView.findViewById(R.id.imvIcon);
			TextView titleView = (TextView) rowView.findViewById(R.id.txtName);

			// 4. Set the text for textView
			if (modelsArrayList.get(position).isSong)
				imgView.setImageResource(R.drawable.songs);
			else
				imgView.setImageResource(R.drawable.folder);
			titleView.setText(modelsArrayList.get(position).getfolderName());

			// 5. retrn rowView
			return rowView;
		}
	}
}
