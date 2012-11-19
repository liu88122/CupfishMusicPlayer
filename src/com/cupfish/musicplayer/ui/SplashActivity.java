package com.cupfish.musicplayer.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.bean.AppUpdateInfo;
import com.cupfish.musicplayer.global.Constants;
import com.cupfish.musicplayer.utils.UpdateManager;

public class SplashActivity extends Activity {

	protected static final int DOWNLOAD = 0;
	protected static final int SHOW_UPDATE_DIALOG = 1;
	protected static final int CANCEL_DOWNLOAD = 3;
	protected static final int NO_UPDATE = 4;
	private boolean isPlaylistOk;
	private InitReceiver initReceiver;
	// private Typeface mTypeface =
	// Typeface.createFromFile("/mnt/sdcard/xujinglei_font.ttf");
	// private TextView mLogoText;
	
	private UpdateManager updateManager;
	private boolean hasUpdate;  /*是否有更新*/
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SHOW_UPDATE_DIALOG:
				showUpdateDialog((AppUpdateInfo)msg.obj);
				break;
			case NO_UPDATE:
			case CANCEL_DOWNLOAD:
				isPlaylistOk = true;
				Intent intent = new Intent(SplashActivity.this, MusicPlayerActivity.class);
				startActivity(intent);
				finish();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_PLAYLIST_REFRESH_FINISH);
		initReceiver = new InitReceiver();
		registerReceiver(initReceiver, filter);

		 updateManager = UpdateManager.getInstance();

		new Thread() {
			@Override
			public void run() {
				AppUpdateInfo info = updateManager.checkUpdate(SplashActivity.this);
				if(info == null){
					mHandler.sendEmptyMessage(NO_UPDATE);
				}else{
					hasUpdate = true;
					Message msg = Message.obtain();
					msg.obj = info;
					msg.what = SHOW_UPDATE_DIALOG;
					mHandler.sendMessage(msg);
				}
				// ----------------------------------------------测试用：将isPlaylistOk设置为true------------------------------------------------------------------------------
				
			}

		}.start();
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				if (!hasUpdate) {
					mHandler.sendEmptyMessage(NO_UPDATE);
				}
			}
		}, 3000);
	}

	private class InitReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.ACTION_PLAYLIST_REFRESH_FINISH.equals(action)) {
				isPlaylistOk = true;
			}
		}

	}
	
	private void showUpdateDialog(final AppUpdateInfo info){
		AlertDialog.Builder builder =new AlertDialog.Builder(this);
		builder.setTitle("有新的程序包哦").setMessage("");
		if(!TextUtils.isEmpty(info.updateLog)){
			String[] updateLogs = info.updateLog.split("#");
			if(updateLogs!= null){
				StringBuilder sUpdateLog = new StringBuilder();
				for(int i=0; i<updateLogs.length; i++){
					sUpdateLog.append(i+1).append(".").append(updateLogs[i]).append("\n");
				}
				builder.setMessage(sUpdateLog.toString());
			}else{
				builder.setMessage(info.updateLog);
			}
		}
		builder.setPositiveButton("下载", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateManager.downloadApk(SplashActivity.this, info.downloadUrl);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(CANCEL_DOWNLOAD);
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	protected void onDestroy() {
		if (initReceiver != null) {
			unregisterReceiver(initReceiver);
		}
		super.onDestroy();
	}

}
