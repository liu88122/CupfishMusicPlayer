package com.cupfish.musicplayer.test;

import android.os.Environment;
import android.test.AndroidTestCase;

import com.cupfish.musicplayer.download.DownloadTask;
import com.cupfish.musicplayer.download.DownloadTask.DownloadListener;

public class TestDownloadEngine extends AndroidTestCase {

	public void testDownload(){
		String url = "http://www.cupfish.com/apk/CupfishMusicPlayer2.0.apk";
		String dir = Environment.getExternalStorageDirectory() + "/cupfish";
		DownloadTask task = new DownloadTask(getContext(), url, dir, 2);
		task.addDownloadListener(new DownloadListener() {
			
			@Override
			public void onDownloading(int size, int length) {
				// TODO Auto-generated method stub
				System.out.println(size);
				
			}
			
			@Override
			public void onDownloadFinish() {
				// TODO Auto-generated method stub
				System.out.println("Download finished");
				
			}
		});
		task.start();
	}
	
}
