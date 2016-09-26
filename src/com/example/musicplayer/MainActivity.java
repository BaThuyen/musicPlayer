package com.example.musicplayer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	EditText txtKeyword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Fragment myfragment;
		myfragment = new Fragment_main();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.frgSwitch, myfragment);
		fragmentTransaction.commit();
		txtKeyword = (EditText) findViewById(R.id.txtKeyword);
		txtKeyword.setFocusable(false);	
	}
	
	class NavItem {
	    String mTitle;
	    String mSubtitle;
	    int mIcon;
	 
	    public NavItem(String title, String subtitle, int icon) {
	        mTitle = title;
	        mSubtitle = subtitle;
	        mIcon = icon;
	    }
	}

	public void FragmentSongs(View view)
	{
		Fragment myfragment;
		myfragment = new FragmentSongs();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.frgSwitch, myfragment);
		fragmentTransaction.commit();
	}
}
