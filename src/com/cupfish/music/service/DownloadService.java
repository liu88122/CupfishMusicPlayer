package com.cupfish.music.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import com.cupfish.music.R;
import com.cupfish.music.bean.Song;
import com.cupfish.music.common.Constants;
import com.cupfish.music.exception.NetTimeoutException;
import com.cupfish.music.utils.BaiduMusicHelper;
import com.cupfish.music.utils.DownloadUtil;
import com.cupfish.music.utils.LocalMediaUtil;

public class DownloadService extends Service {

	private static final String TAG = "DownloadService";
	private ArrayList<Song> downloadList;
	private List<Song> localSongs;
	private DownloadTaskReceiver taskReceiver;
	private NotificationManager notificationManager;
	private Notification mNotification;
	private RemoteViews mRemoteViews;
	private int mProgressMax;
	private int flag;
	private Song mCurrentDownloadSong;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.DOWNLOAD_START:

				mProgressMax = msg.arg1;

				notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				int icon = R.drawable.download;
				CharSequence tickerText = "开始下载";
				long when = System.currentTimeMillis();

				mNotification = new Notification(icon, tickerText, when);
				mNotification.flags = Notification.FLAG_ONGOING_EVENT;

				mRemoteViews = new RemoteViews(DownloadService.this.getPackageName(), R.layout.notification);
				mRemoteViews.setTextViewText(R.id.tv_title, "正在下载歌曲:" + msg.obj);
				mRemoteViews.setTextColor(R.id.tv_title, getResources().getColor(R.color.main_blue_light));

				PendingIntent pendingIntent = PendingIntent.getActivity(DownloadService.this, 0, null, 0);
				mNotification.contentIntent = pendingIntent;
				mNotification.contentView = mRemoteViews;
				notificationManager.notify(0, mNotification);
				break;
			case Constants.DOWNLOAD_DOWNLOADING:
				Log.i(TAG, "" + mProgressMax);
				Log.i(TAG, "" + msg.arg2);
				mRemoteViews = new RemoteViews(DownloadService.this.getPackageName(), R.layout.notification);
				// mRemoteViews.setTextViewText(R.id.tv_title, "正在下载歌曲:" +
				// msg.obj);
				mRemoteViews.setTextColor(R.id.tv_title, getResources().getColor(R.color.main_blue_light));
				mRemoteViews.setProgressBar(R.id.pb_download, mProgressMax, msg.arg2, false);
				mNotification.contentView = mRemoteViews;
				notificationManager.notify(0, mNotification);

				break;
			case Constants.DOWNLOAD_FINISH:

				// 刷新媒体库
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));

				mNotification.flags = Notification.FLAG_AUTO_CANCEL;
				mNotification.contentView = null;

				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				if (mCurrentDownloadSong == null) {
					break;
				}
				bundle.putSerializable("song", mCurrentDownloadSong);
				intent.setAction(Constants.ACTION_PLAY);
				intent.putExtras(bundle);
				PendingIntent contentIntent = PendingIntent.getBroadcast(DownloadService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
				mNotification.setLatestEventInfo(DownloadService.this, "下载完成", "文件已下载完毕", contentIntent);

				notificationManager.notify(0, mNotification);

				if (downloadList != null && downloadList.size() > 0) {
					startDownload();
				}
				break;
			case Constants.DOWNLOAD_CANCEL:

				break;
			}
		}

	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();

		downloadList = new ArrayList<Song>();
		localSongs = LocalMediaUtil.getLocalSongs(this);

		taskReceiver = new DownloadTaskReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_ADD_DOWNLOAD_TASK);
		registerReceiver(taskReceiver, filter);

		if (downloadList != null && downloadList.size() > 0) {
			startDownload();
		}
	}

	private void startDownload() {
		mCurrentDownloadSong = downloadList.get(0);
		synchronized (this) {
			downloadList.remove(0);
		}
		boolean isLocal = false;
		if (localSongs != null) {
			for (Song s : localSongs) {
				if (s.getTitle() != null && mCurrentDownloadSong.getTitle() != null && s.getTitle().equals(mCurrentDownloadSong.getTitle())) {
					isLocal = true;
				}
			}
		}
		if (!isLocal) {
			if (TextUtils.isEmpty(mCurrentDownloadSong.getSongId())) {
				return;
			}
			// 根据songId获取歌曲的下载地址
			String tempUrl = null;
			switch (flag) {
			case Constants.FLAG_BAIDU_MUSIC:
				try {
					tempUrl = BaiduMusicHelper.getDownloadUrlBySongId(mCurrentDownloadSong.getSongId());
				} catch (NetTimeoutException e) {
					e.printStackTrace();
				}
				break;
			case Constants.FLAG_GOOGLE_MUSIC:
				
				break;
			}
			final String fileUrl = tempUrl;
			if (TextUtils.isEmpty(fileUrl)) {
				return;
			}

			// 如果文件名为空的话，则设置为当前时间
			final String fileName = TextUtils.isEmpty(mCurrentDownloadSong.getTitle()) ? DateFormat.format("yyyy_MM_dd_hh_MM_ss",
					new Date(System.currentTimeMillis())).toString() : mCurrentDownloadSong.getTitle();
			new Thread() {
				@Override
				public void run() {
					File file = DownloadUtil.load(fileName, fileUrl, handler, Constants.DOWNLOAD_FILE_MP3);
					mCurrentDownloadSong.setSongPath(file.getPath());
				}
			}.start();
		}
	}

	private synchronized void addDownloadTask(Song song) {
		for (Song s : downloadList) {
			if (s.getTitle() != null && song.getTitle() != null && s.getTitle().equals(song.getTitle())) {
				return;
			}
		}
		downloadList.add(song);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private class DownloadTaskReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "DownloadService onReceiver");
			String action = intent.getAction();
			flag = intent.getIntExtra("flag", 0);
			if (Constants.ACTION_ADD_DOWNLOAD_TASK.equals(action)) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					Song song = (Song) bundle.getSerializable("song");
					if (song != null) {
						addDownloadTask(song);
						startDownload();
					}
				}
			}
		}
	}

}
