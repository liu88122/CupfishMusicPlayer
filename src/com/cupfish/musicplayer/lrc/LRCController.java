package com.cupfish.musicplayer.lrc;

import android.content.Context;
import android.media.MediaPlayer;

import com.cupfish.musicplayer.bean.LRC2;
import com.cupfish.musicplayer.bean.Song;
import com.cupfish.musicplayer.utils.LRCReader;

/**
 * 歌词控制类<br/>
 * 这里用到单例模式使得该控制类只有一个实例，使用时只要调用loadLrc即可，
 * 控制类会自动加载或下载对应的歌词文件并解析，并调用OnLrcUpdateListener接口中的
 * onUpdate回调方法更新歌词
 * 
 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
 * @2012-11-19下午4:56:28
 */
public class LRCController {

	private static LRCController sLrcController;
	private static Object sLock = new Object();
	
	private MediaPlayer mMediaPlayer;
	private LrcUpdateThread mLrcUpdateThread;
	private OnLrcUpdateListener mLrcUpdateListener;
	
	private LRCController(){ }
	
	public static LRCController getInstance(){
		synchronized (sLock) {
			if(sLrcController == null){
				sLrcController = new LRCController();
			}
			return sLrcController;
		}
	}
	
	/**
	 * 加载歌词
	 * @param context 上下文
	 * @param song 对应歌曲
	 * @param player 播放器
	 * void
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-19 下午5:00:43
	 */
	public void loadLRC(Context context, Song song, MediaPlayer player){
		stopLrc(); //如果还有歌词在显示，直接停止
		mMediaPlayer = player;
		LRC2 lrc = LRCReader.getLRC(song, context);
		mLrcUpdateThread = new LrcUpdateThread(lrc);
		mLrcUpdateThread.start();
	}
	
	/**
	 * 直接跳到time显示歌词，对应于player.seekTo
	 * @param time
	 * void
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-19 下午5:01:22
	 */
	public void seekTo(long time){
		if(mLrcUpdateThread != null){
			mLrcUpdateThread.seekTo(time);
		}
	}
	
	/**
	 * 暂停显示歌词
	 * 
	 * void
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-19 下午5:02:13
	 */
	public void pauseLrc(){
		if (mLrcUpdateThread != null) {
			mLrcUpdateThread.pauseLrc();
		}
	}
	
	/**
	 * 恢复歌词显示(恢复解析回调)
	 * void
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-19 下午5:02:32
	 */
	public void resumeLrc(){
		if (mLrcUpdateThread != null) {
			mLrcUpdateThread.resumeLrc();
		}
	}
	
	/**
	 * 退出歌词解析线程
	 * void
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-19 下午5:03:25
	 */
	public void stopLrc(){
		if(mLrcUpdateThread != null){
			mLrcUpdateThread.exit();
		}
	}
	
	/**
	 * 设置歌词更新监听类
	 * @param listener
	 * void
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-19 下午5:04:11
	 */
	public void setOnLrcUpdateListener(OnLrcUpdateListener listener){
		mLrcUpdateListener = listener;
	}
	
	/**
	 * 歌词解析，回调工作线程
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-19下午5:04:49
	 */
	private class LrcUpdateThread extends Thread{
		
		public final Object sLock = new Object();
		
		private LRC2 lrc;
		private boolean exit;
		private int currentPosition;
		private int duration = 3600000;
		private long nextTimepoint;
		private boolean pause;
		
		public LrcUpdateThread(LRC2 lrc){
			this.lrc = lrc;
		}
		
		public void resumeLrc() {
			pause = false;
		}

		public void pauseLrc() {
			pause = true;
		}

		public void seekTo(long time) {
			if(lrc!=null){
				nextTimepoint = lrc.getNextTimeline(time);
			}
			
		}

		@Override
		public void run() {
			nextTimepoint = lrc.getNextTimeline(0);
			while (!exit) {
				if (!pause) {
					currentPosition = mMediaPlayer.getCurrentPosition();
					if (currentPosition > nextTimepoint) {
						String statement = lrc.getStatement(currentPosition);
						if (mLrcUpdateListener != null) {
							mLrcUpdateListener.onUpdate(statement);
						}
						nextTimepoint = lrc.getNextTimeline(currentPosition);
					}
					if (nextTimepoint == 3600000) {
						currentPosition += 100;
						if (currentPosition > duration) {
							exit = true;
						}
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void exit(){
			exit = true;
		}
	}
	
	/**
	 * 歌词更新监听类
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-19下午5:05:50
	 */
	public static interface OnLrcUpdateListener{
		void onUpdate(String statement);
	}
}
