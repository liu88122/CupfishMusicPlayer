package com.cupfish.music.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.cupfish.music.R;
import com.cupfish.music.service.DownloadService;
import com.cupfish.music.service.MusicPlayerService;
import com.cupfish.music.ui.fragment.LeftMenuFragment;
import com.cupfish.music.ui.fragment.MusicPlayerFragment;
import com.cupfish.music.utils.ConnectivityHelper;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class Main2Activity extends SherlockFragmentActivity {

	private SlidingMenu slidingMenu;
	private ServiceConnection musicServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}
	};

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

		LeftMenuFragment leftMenuFragment = new LeftMenuFragment();
		leftMenuFragment.setSlidingMenu(slidingMenu);

		getSupportFragmentManager().beginTransaction().replace(R.id.menu, leftMenuFragment).commit();

		// 绑定音乐服务
		Intent musicServiceIntent = new Intent(this, MusicPlayerService.class);
		bindService(musicServiceIntent, musicServiceConn, Context.BIND_AUTO_CREATE);

		// 开启下载服务
		Intent downloadServiceIntent = new Intent(this, DownloadService.class);
		startService(downloadServiceIntent);

		// 如果当前网络连接是否可用及是否为wifi连接，提示用户
		ConnectivityHelper.showNetworkType(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(musicServiceConn);

		if (MusicPlayerService.mMediaPlayer != null) {
			if (MusicPlayerService.mMediaPlayer.isPlaying()) {
				MusicPlayerService.mMediaPlayer.stop();
			}
			MusicPlayerService.mMediaPlayer.release();
		}
		// 停止摇晃检测
		// if (mShakeDetector != null) {
		// mShakeDetector.stop();
		// }
	}

}
