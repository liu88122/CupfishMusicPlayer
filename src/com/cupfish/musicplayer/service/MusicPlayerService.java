package com.cupfish.musicplayer.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.cupfish.musicplayer.bean.Song;
import com.cupfish.musicplayer.dao.PlayerListDao;
import com.cupfish.musicplayer.download.DownloadEngine;
import com.cupfish.musicplayer.exception.NetTimeoutException;
import com.cupfish.musicplayer.global.BaseApp;
import com.cupfish.musicplayer.global.Constants;
import com.cupfish.musicplayer.lrc.LrcController;
import com.cupfish.musicplayer.lrc.LrcController.OnLrcUpdateListener;
import com.cupfish.musicplayer.utils.BaiduTingHelper;

public class MusicPlayerService extends Service implements OnCompletionListener, OnPreparedListener, OnBufferingUpdateListener {

	private static final String TAG = "MusicPlayerService";
	private static final int INIT_SONG_INFO_FINISH = 200;
	public static MediaPlayer mMediaPlayer;
	private List<Song> mPlayList;
//	private PlayerListDao mDao;
	private int mCurrentSongIndex = 0;
	private Song mCurrentSong;
	private int flag;
	private boolean isFirstTime = true;
	private PlayerControllerReceiver mControllerReceiver;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case INIT_SONG_INFO_FINISH:
				if (msg.obj != null) {
					Song song = (Song) msg.obj;
					Log.i(TAG, song.toString());
					play(song);
				}
				break;
			}
			super.handleMessage(msg);
		}

	};
	//歌词控制类
	private LrcController lrcController = LrcController.getInstance();

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind");
		return null;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		lrcController.stopLrc();
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		
		lrcController.addOnLrcUpdateListener(new OnLrcUpdateListener() {
			
			@Override
			public void onUpdate(long time, String statement) {
				Log.i(TAG, statement);
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}
		});

		//TODO 第一次启动程序，应该加载上次的播放列表，并将播放界面恢复为上次退出的状态
		mPlayList = new ArrayList<Song>();
		((BaseApp) getApplication()).playlist = mPlayList;
//		mDao = new PlayerListDao(this);
//		new Thread() {
//
//			@Override
//			public void run() {
//				mPlayList = mDao.getPlaylist();
//				((BaseApp) getApplication()).playlist = mPlayList;
//
//				// 刷新Playlist完成后发送广播
//				Intent intent = new Intent();
//				intent.setAction(Constants.ACTION_PLAYLIST_REFRESH_FINISH);
//				sendBroadcast(intent);
//			}
//
//		}.start();

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnPreparedListener(this);

		mControllerReceiver = new PlayerControllerReceiver();
		// 注册BroadcastReceiver，处理所有播放器控制命令
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_PLAY);
		filter.addAction(Constants.ACTION_NEXT);
		filter.addAction(Constants.ACTION_PAUSE);
		filter.addAction(Constants.ACTION_PREVIOUS);
		filter.addAction(Constants.ACTION_SEEK_TO);
		filter.addAction(Constants.ACTION_STOP);
		filter.addAction(Constants.ACTION_START);
		filter.addAction(Constants.ACTION_PLAYLIST_REFRESH);
		filter.addAction(Constants.ACTION_ADD_TO_PLAYLIST);
		registerReceiver(mControllerReceiver, filter);

		// 监听电话状态
		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		manager.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);

	}

	/**
	 * 对电话状态进行监听，当有来电时暂停播放器，挂断电话后恢复播放
	 * 
	 * @author Liu88122
	 * 
	 */
	private class MyPhoneListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					mMediaPlayer.pause();
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
					mMediaPlayer.pause();
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
					mMediaPlayer.start();
				}
				break;
			}
		}
	}

	/**
	 * 用于接受播放器控制命令的BroadcastReceiver，处理所有播放器控制命令
	 * 
	 * @author Liu88122
	 * 
	 */
	public class PlayerControllerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			flag = intent.getIntExtra("flag", 0);
			Log.i(TAG, action);

			if (Constants.ACTION_PLAY.equals(action)) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					final Song song = (Song) bundle.getSerializable("song");
					if (song != null) {
						if (!TextUtils.isEmpty(song.getUrl())) {
							// 如果是本地歌曲，则song的url属性不为空，可以直接播放
							// 如果是在线歌曲，url属性为空，必须在线获取
							int index = addIntoPlaylist(song);
							mCurrentSongIndex = index;
							play(mCurrentSongIndex);
							Log.i(TAG, "-------------------------------------" + song.getUrl());
						} else {
							if (!TextUtils.isEmpty(song.getId())) {
								final String songId = song.getId();
								new Thread() {
									@Override
									public void run() {
										Log.i(TAG, "getSongById");
										switch (flag) {
										case 0:
										case Constants.FLAG_BAIDU_MUSIC:
											Song baiduSong = song;
											try {
												baiduSong = BaiduTingHelper.getSongById(songId);
											} catch (NetTimeoutException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
											Message msg = Message.obtain();
											msg.what = INIT_SONG_INFO_FINISH;
											msg.obj = baiduSong;
											handler.sendMessage(msg);
											break;
										}

									}

								}.start();
							}
						}
					}
				} else {
					play();
				}
			}

			if (Constants.ACTION_PAUSE.equals(action)) {
				pause();
			}

			if (Constants.ACTION_NEXT.equals(action)) {
				next();
			}

			if (Constants.ACTION_PREVIOUS.equals(action)) {
				previous();
			}

			if (Constants.ACTION_SEEK_TO.equals(action)) {
				int msec = intent.getIntExtra("msec", 0);
				seekTo(msec);
			}

			if (Constants.ACTION_PLAYLIST_REFRESH.equals(action)) {
				Log.i(TAG, "PlaylistSize:" + mPlayList.size());
				
			}
		}
	}

	private void play() {
		if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
			mMediaPlayer.start();
			lrcController.resumeLrc();
		}
	}

	private void play(int index) {

		Song song = mPlayList.get(index);
		if(song == null){
			return;
		}
		String url = song.getUrl();
		Log.i(TAG, "ID:" + mPlayList.get(index).getId());
		
		if (mMediaPlayer != null && url != null) {
			try {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
				/*
				 * mMediaPlayer.release(); mMediaPlayer = new MediaPlayer();
				 * mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				 */
				
				if(!new File(url).exists()){
					String dir = Environment.getExternalStorageDirectory() + "/cupfish";
					url = DownloadEngine.getInstance().download(getApplicationContext(), url, dir, song.getTitle()+".mp3", 1);
				}
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(url);
				mMediaPlayer.setOnPreparedListener(this);
				mMediaPlayer.setOnCompletionListener(this);
				mMediaPlayer.setOnBufferingUpdateListener(this);
				Log.i(TAG, "Download url::" + url);
				mMediaPlayer.prepareAsync();
				lrcController.loadLRC(this, mPlayList.get(index), mMediaPlayer);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void play(Song song) {

		int index = addIntoPlaylist(song);
		mCurrentSongIndex = index;
		play(mCurrentSongIndex);

	}

	private void pause() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			lrcController.pauseLrc();
		}
	}

	private void next() {
		if (mPlayList.size() == 0) {
			return;
		}
		Log.i(TAG, "" + mPlayList.size());
		if (mCurrentSongIndex == mPlayList.size() - 1) {
			mCurrentSongIndex = 0;
			play(mCurrentSongIndex);
		} else if (mCurrentSongIndex < mPlayList.size() - 1) {
			mCurrentSongIndex++;
			play(mCurrentSongIndex);
		}
		Log.i(TAG, "index::" + mCurrentSongIndex);

	}

	private void previous() {
		if (mPlayList.size() == 0) {
			return;
		}
		if (mCurrentSongIndex == 0) {
			mCurrentSongIndex = mPlayList.size() - 1;
		} else if (mCurrentSongIndex < mPlayList.size()) {
			mCurrentSongIndex--;
		}
		play(mCurrentSongIndex);
	}

	private void seekTo(int msec) {
		mMediaPlayer.seekTo(msec);
		lrcController.seekTo(msec);
	}

	private int addIntoPlaylist(Song song) {
		
		if(mPlayList.contains(song)){
			return mPlayList.indexOf(song);
		} else {
			mPlayList.add(song);
			return mPlayList.size() - 1;
		}
	}

	@Override
	public void onDestroy() {
//		mDao.deleteAll();
//		mDao.insertPlayList(mPlayList);
		unregisterReceiver(mControllerReceiver);
		super.onDestroy();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		next();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
//		if (!isFirstTime) {
//			mp.start();
//		}
		mp.start();
		isFirstTime = false;
		// 发送当前播放信息
		Intent intent = new Intent();
		intent.putExtra("currentSongIndex", mCurrentSongIndex);
		intent.setAction(Constants.ACTION_CURRENT_SONG_INDEX);
		sendBroadcast(intent);
		Log.i(TAG, "ACTION_CURRENT_SONG_INDEX");
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		
	}

}
