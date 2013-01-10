package com.cupfish.music.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cupfish.music.bean.Song;
import com.cupfish.music.dao.MusicDao;

public class LocalMusicScanner {

	private static LocalMusicScanner sScanner = new LocalMusicScanner();
	private static Context sContext;
	private boolean isScanning = false;
	private List<OnScanListener> mScanListeners;
	
	private LocalMusicScanner(){
		mScanListeners = new ArrayList<OnScanListener>();
	}
	
	public static LocalMusicScanner getInstance(Context context){
		sContext = context;
		return sScanner;
	}
	
	public void start(){
		if(!isScanning){
			ScannerThread scannerThread = new ScannerThread();
			scannerThread.start();
		}
	}
	
	private class ScannerThread extends Thread{
		
		MusicDao mMusicDao = new MusicDao(sContext);
		MusicDbHelper mDbHelper = new MusicDbHelper(sContext);
		
		@Override
		public void run() {
			isScanning = true;
			try {
				List<Song> songs = LocalMediaUtil.getLocalSongs(sContext);
				SQLiteDatabase db = mDbHelper.getWritableDatabase();
				mMusicDao.insertSongs(db, songs);

				if (db != null && db.isOpen()) {
					db.close();
				}
			} finally {
				isScanning = false;
			}
		}
	}
	
	public void addOnScanListener(OnScanListener listener){
		mScanListeners.add(listener);
	}
	
	public void removeOnScanListener(OnScanListener listener){
		mScanListeners.remove(listener);
	}
	
	public static interface OnScanListener {
		void onStart();
		void onScanning(String file);
		void onFinish();
	}
}
