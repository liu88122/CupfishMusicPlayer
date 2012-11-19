package com.cupfish.musicplayer.bean;

import java.util.Iterator;
import java.util.TreeMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cupfish.musicplayer.global.Constants;
import com.cupfish.musicplayer.utils.LRCManager;
import com.cupfish.musicplayer.utils.LRCReader;

@Deprecated
public class LRC extends BroadcastReceiver implements Runnable {

	private static final String TAG = "LRC";
	private Context mContext;
	private Song mCurrentSong;
	private Handler mLrcHandler;
	private MediaPlayer mMediaPlayer;
	private long startTime = 0;
	private long pauseTime = 0;
	private long pausedTime = 0;
	private boolean isPaused = false;
	private long showTime = 0;
	private String lrcContent = "";
	private boolean isEnd = false;
	private TreeMap<Long, String> lrc = null;

	public LRC(Context context, Song song, Handler handler, MediaPlayer mediaPlayer) {
		mContext = context;
		mCurrentSong = song;
		mLrcHandler = handler;
		mMediaPlayer = mediaPlayer;
		registerReceiver();
	}

	@Override
	public void run() {

		startTime = System.currentTimeMillis();
		pauseTime = System.currentTimeMillis();

		try {
			lrc = LRCReader.getLRCTreeMap(mCurrentSong, mContext);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (lrc != null && lrc.size() > 0) {
			Iterator<Long> timeIterator = lrc.keySet().iterator();
			if (timeIterator.hasNext() && (timeIterator.next() != 3600000)) {
				showTime = timeIterator.next();
				lrcContent = lrc.get(showTime);
			} else {
				return;
			}

			while (!isEnd) {
				if (!isPaused) {
					/*
					 * long currentTime = System.currentTimeMillis() -
					 * pausedTime; long deltaTime = (currentTime - startTime) /
					 * 10 * 10;
					 */
					long deltaTime = 0;
					if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
						deltaTime = mMediaPlayer.getCurrentPosition();
					}
					if (deltaTime > showTime) {
						Message msg = Message.obtain();
						msg.obj = lrcContent;
						mLrcHandler.sendMessage(msg);
						if (timeIterator.hasNext()) {
							showTime = timeIterator.next();
							if (showTime == 3600000) {
								isEnd = true;
								break;
							}
							lrcContent = lrc.get(showTime);
						}
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();

		if (Constants.ACTION_PLAY.equals(action)) {
			isPaused = false;
			pausedTime = pausedTime + System.currentTimeMillis() - pauseTime - 10;
		}

		if (Constants.ACTION_PAUSE.equals(action)) {
			isPaused = true;
			pauseTime = System.currentTimeMillis();
		}

		if (Constants.ACTION_NEXT.equals(action)) {
			LRCManager.getInstance().removeLRC();
		}

		if (Constants.ACTION_PREVIOUS.equals(action)) {
			LRCManager.getInstance().removeLRC();
		}

		if (Constants.ACTION_SEEK_TO.equals(action)) {
			int msec = intent.getIntExtra("msec", 0);
			if (lrc != null) {
				Iterator<Long> it = lrc.keySet().iterator();
				while (it.hasNext()) {
					long temp = it.next();
					if (msec < temp) {
						showTime = temp;
						lrcContent = lrc.get(showTime);
						Log.i(TAG, "MSEC:" + msec);
						Log.i(TAG, "showTime:" + showTime);
						break;
					}
				}
			}
		}
	}

	public void registerReceiver() {
		// 注册BroadcastReceiver，处理所有播放器控制命令
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_PLAY);
		filter.addAction(Constants.ACTION_NEXT);
		filter.addAction(Constants.ACTION_PAUSE);
		filter.addAction(Constants.ACTION_PREVIOUS);
		filter.addAction(Constants.ACTION_SEEK_TO);
		filter.addAction(Constants.ACTION_STOP);
		filter.addAction(Constants.ACTION_START);
		mContext.registerReceiver(this, filter);
	}

	public void unregisterReceiver() {
		mContext.unregisterReceiver(this);
	}

	public void setPause(boolean pause) {
		isPaused = true;
	}
}
