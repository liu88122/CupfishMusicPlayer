package com.cupfish.music.ui;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.cupfish.music.R;
import com.cupfish.music.ui.fragment.LeftMenuFragment;
import com.cupfish.music.ui.fragment.MusicPlayerFragment;
import com.slidingmenu.lib.SlidingMenu;

public class Main2Activity extends SherlockFragmentActivity {

	private SlidingMenu slidingMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.main_content);
		getSupportFragmentManager().beginTransaction().replace(R.id.content, new MusicPlayerFragment()).commit();
		
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setBehindScrollScale(0.35f);
		slidingMenu.setBehindOffset(240);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setMenu(R.layout.menu_frame);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setShadowWidth(15);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		getSupportFragmentManager().beginTransaction().replace(R.id.menu, new LeftMenuFragment()).commit();
	}
	
}
