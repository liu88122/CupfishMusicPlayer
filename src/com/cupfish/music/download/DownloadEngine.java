package com.cupfish.music.download;

import java.io.File;
import java.io.FileDescriptor;
import java.util.HashMap;

import android.content.Context;

import com.cupfish.music.download.DownloadTask.DownloadListener;

/**
 * 下载引擎
 * 
 * @author <a href="mailto:liu88122@gmail.com">Liu88122</a>
 * @Time 2012-11-18 下午7:30:10
 */
public class DownloadEngine {
	
	private static DownloadEngine sInstance;
	private static Object sLock = new Object();
	public static final int DEFAULT_THREAD_NUM_PER_TASK = 3;
	private HashMap<String, DownloadTask> tasks;

	private DownloadEngine() {
		tasks = new HashMap<String, DownloadTask>();
	}

	public static DownloadEngine getInstance() {
		synchronized (sLock) {
			if (sInstance == null) {
				sInstance = new DownloadEngine();
			}
			return sInstance;
		}
	}

	public String download(Context context, final String downloadUrl, String dir, String fileName, int threadNum) {
		final DownloadTask task = new DownloadTask(context, downloadUrl, dir, fileName, threadNum);
		task.addDownloadListener(new DownloadListener() {
			@Override
			public void onDownloading(int size, int length) {
				
			}
			@Override
			public void onDownloadFinish() {
				task.exit();
				tasks.remove(downloadUrl);
			}
		});
		tasks.put(downloadUrl, task);
		task.start();
		return task.getDownloadFilePath();
	}

	public void cancel(Context context, String downloadUrl) {
		
	}
	
	public void addDownloadListener(String downloadUrl, DownloadListener listener){
		if(tasks.containsKey(downloadUrl)){
			tasks.get(downloadUrl).addDownloadListener(listener);
		}
	}
	
}