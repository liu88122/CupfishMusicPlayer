package com.cupfish.musicplayer.download;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cupfish.musicplayer.dao.DownloadDao;

public class DownloadTask extends Thread {
	
	
	private Context context;
	private String downloadUrl;
	private String dir;
	private RandomAccessFile randomAccessFile;
	private String filePath;
	private boolean exited;
	private int downloaded = 0;
	private int contentLength = 0;
	private DownloadDao mDownloadDao;
	private int threadNum = DownloadEngine.DEFAULT_THREAD_NUM_PER_TASK;
	
	private SQLiteDatabase db;
	private DownloadDbHelper mDownloadDbHelper;
	private List<DownloadListener> mDownloadListeners;
	
	public DownloadTask(Context context, String downloadUrl, String dir, int threadNum){
		this.context =context;
		this.downloadUrl = downloadUrl;
		this.dir = dir;
		mDownloadDao = new DownloadDao(context);
		mDownloadDbHelper = new DownloadDbHelper(context);
		this.threadNum = threadNum;
		mDownloadListeners = new ArrayList<DownloadTask.DownloadListener>();
	}
	
	public void addDownloadListener(DownloadListener listener){
		mDownloadListeners.add(listener);
	}
	
	public void exit(){
		exited = true;
	}
	
	@Override
	public void run() {
		try{
			String fileName = getFileName(downloadUrl);
			File directory = new File(dir);
			if(!directory.exists()){
				directory.mkdirs();
			}
			File file = new File(directory, fileName);
			file.createNewFile();
			filePath = file.getPath();
			randomAccessFile = new RandomAccessFile(file, "rw");
			
			URL url = new URL(downloadUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			int length = conn.getContentLength();
			contentLength = length;
			conn.disconnect();
			if(length < 0){
				return;
			}
			if(mDownloadDao.getThreadNum(downloadUrl) < 1 && file.exists() && file.length() == length){
				for(DownloadListener listener : mDownloadListeners){
					listener.onDownloading(length);
					listener.onDownloadFinish();
				}
				return;
			}
			
			randomAccessFile.setLength(length);
			if(threadNum != mDownloadDao.getThreadNum(downloadUrl)){
				mDownloadDao.delete(downloadUrl);
			}
			db = mDownloadDbHelper.getWritableDatabase();
			int block = length % threadNum==0?length / threadNum : length/threadNum + 1;
			for(int i=0; i<threadNum; i++){
				int currentThreadDownloadLength = mDownloadDao.getDownloadLength(downloadUrl, i+1);
				if(currentThreadDownloadLength < 0){
					currentThreadDownloadLength = 0;
				}
				int start = i*block + currentThreadDownloadLength;
				int end = i*block + block - 1;
				if(end > length){
					end  = length - 1;
				}
				DownloadThread thread = new DownloadThread(i+1, url, start, end);
				thread.start();
			}
			
		} catch (Exception e) {
			if(db != null){
				db.close();
			}
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 从url中获取文件名，如果获取失败，直接用url的MD5值用作文件名返回
	 * @param downloadUrl 下载Url
	 * @return
	 * String 文件名
	 */
	public static String getFileName(String downloadUrl) {
		int index = downloadUrl.lastIndexOf("/");
		if(index > 0){
			return downloadUrl.substring(index + 1);
		}else{
			try {
				MessageDigest md = MessageDigest.getInstance("md5");
				byte[] result = md.digest(downloadUrl.getBytes());
				return new String(result, 0, result.length);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private void addTotalDownloaded(int size){
		synchronized (this) {
			downloaded += size;
			for(DownloadListener listener : mDownloadListeners){
				listener.onDownloading(downloaded);
			}
		}
	}

	private class DownloadThread extends Thread{
		
		private int threadId;
		private int start;
		private int end;
		private URL url;
		private int downloadLength;
		
		public DownloadThread(int threadId, URL url, int start, int end){
			this.threadId = threadId;
			this.start = start;
			this.end = end;
			this.url = url;
		}
		
		@Override
		public void run() {
			try{
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
				conn.connect();
				InputStream is = conn.getInputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while(!exited && (len = is.read(buffer)) != -1){
					synchronized (DownloadTask.this) {
						if(downloadLength < (end - start)){
							randomAccessFile.seek(start + downloadLength);
//						exited = true;
						}
						randomAccessFile.write(buffer, 0, len);
						downloadLength += len;
						mDownloadDao.update(downloadUrl, threadId, downloadLength, db);
						addTotalDownloaded(len);
//						System.out.println("线程" + threadId + " 下载" + (start + downloadLength));
					}
					
				}
//				System.out.println("线程" + threadId + " 下载完成");
				is.close();
				
				if(downloaded ==contentLength){
					randomAccessFile.close();
					for(DownloadListener listener : mDownloadListeners){
						listener.onDownloadFinish();
					}
					db.close();
					mDownloadDao.delete(downloadUrl);
				}
			}catch(Exception e){
				exited = true;
				e.printStackTrace();
			}
			
		}
	}
	
	public static interface DownloadListener {
		void onDownloading(int size);
		void onDownloadFinish();
	}

	public String getDownloadFilePath() {
		if(filePath == null){
			try {
				sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return filePath;
	}
}
