package com.cupfish.musicplayer.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.global.Constants;
import com.cupfish.musicplayer.service.MusicPlayerService;

public class SplashActivity extends Activity {

	private boolean isPlaylistOk;
	private InitReceiver initReceiver;
	//private Typeface mTypeface = Typeface.createFromFile("/mnt/sdcard/xujinglei_font.ttf");
	//private TextView mLogoText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);
		
		//mLogoText = (TextView) findViewById(R.id.tv_logo);
		//mLogoText.setTypeface(mTypeface);
		
		/*Intent intent = new Intent(this, MusicPlayerService.class);
		startService(intent);*/
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_PLAYLIST_REFRESH_FINISH);
		initReceiver = new InitReceiver();
		registerReceiver(initReceiver, filter);
		
		new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					while(!isPlaylistOk){
						Thread.sleep(500);
						
//----------------------------------------------测试用：将isPlaylistOk设置为true------------------------------------------------------------------------------
						isPlaylistOk = true;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(SplashActivity.this, MusicPlayerActivity.class);
				startActivity(intent);
				finish();
			}
			
		}.start();
	}

	private class InitReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(Constants.ACTION_PLAYLIST_REFRESH_FINISH.equals(action)){
				isPlaylistOk = true;
			}
		}
		
	}

	@Override
	protected void onDestroy() {
		if(initReceiver!=null){
			unregisterReceiver(initReceiver);
		}
		super.onDestroy();
	}
	
	
}
