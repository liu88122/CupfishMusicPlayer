package com.cupfish.music.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cupfish.music.global.Constants;

public class DownloadUtil {

	protected static final String TAG = "DownloadUtil";

	public static File load(final String fileName, final String fileUrl, final Handler handler, int type) {

		String dirPath = Constants.SDCARD_MUSIC_SAVE_PATH;
		String fileExtenstion = ".temp";

		switch (type) {
		case Constants.DOWNLOAD_FILE_MP3:
			dirPath = Constants.SDCARD_MUSIC_SAVE_PATH;
			fileExtenstion = ".mp3";
			break;
		case Constants.DOWNLOAD_FILE_LRC:
			dirPath = Constants.SDCARD_LRC_SAVE_PATH;
			fileExtenstion = ".lrc";
			break;
		}

		try {

			HttpGet get = new HttpGet(fileUrl);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream is = response.getEntity().getContent();

				File dir = new File(dirPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(dir, "/" + fileName + fileExtenstion);
				FileOutputStream fos = new FileOutputStream(file);
				int len = -1;
				byte[] b = new byte[1024 * 8];

				if (handler != null) {
					Message startMsg = Message.obtain();
					startMsg.what = Constants.DOWNLOAD_START;
					startMsg.arg1 = Integer.parseInt(response.getHeaders("Content-Length")[0].getValue());
					startMsg.obj = fileName;
					handler.sendMessage(startMsg);
				}

				int currentProgress = 0;
				while ((len = is.read(b)) != -1) {
					fos.write(b, 0, len);
					currentProgress += len;
					if (handler != null) {
						Message downloadingMsg = Message.obtain();
						downloadingMsg.what = Constants.DOWNLOAD_DOWNLOADING;
						downloadingMsg.arg2 = currentProgress;
						downloadingMsg.obj = fileName;
						handler.sendMessage(downloadingMsg);
					}
				}
				fos.flush();
				fos.close();
				if (handler != null) {
					Message finishMsg = Message.obtain();
					finishMsg.what = Constants.DOWNLOAD_FINISH;
					handler.sendMessage(finishMsg);
				}
				return file;
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (handler != null) {
				Message errorMsg = Message.obtain();
				errorMsg.what = Constants.DOWNLOAD_CANCEL;
				handler.sendMessage(errorMsg);
			}
			return null;
		}
		return null;

	}

	/*
	 * public static File load(final String fileName, final String fileUrl,
	 * final Handler handler) {
	 * 
	 * Runnable runnable = new Runnable() {
	 * 
	 * @Override public void run() { try { URL url = new URL(fileUrl);
	 * URLConnection conn = url.openConnection(); BufferedInputStream bis = new
	 * BufferedInputStream(conn.getInputStream()); File dir = new
	 * File(Constants.SDCARD_MUSIC_SAVE_PATH); if (!dir.exists()) {
	 * dir.mkdirs(); } File file = new File(dir, "/" + fileName + ".mp3");
	 * FileOutputStream fos = new FileOutputStream(file); int len = -1; byte[] b
	 * = new byte[1024 * 8];
	 * 
	 * if (handler != null) { Message startMsg = Message.obtain(); startMsg.what
	 * = Constants.DOWNLOAD_START; startMsg.arg1 = conn.getContentLength();
	 * handler.sendMessage(startMsg); }
	 * 
	 * int currentProgress = 0; while ((len = bis.read(b)) != -1) { fos.write(b,
	 * 0, len); currentProgress += len; Log.i(TAG, "progress:" +
	 * currentProgress); if (handler != null) { Message downloadingMsg =
	 * Message.obtain(); downloadingMsg.what = Constants.DOWNLOAD_DOWNLOADING;
	 * downloadingMsg.arg1 = currentProgress;
	 * handler.sendMessage(downloadingMsg); } } fos.flush(); fos.close(); if
	 * (handler != null) { Message finishMsg = Message.obtain(); finishMsg.what
	 * = Constants.DOWNLOAD_FINISH; handler.sendMessage(finishMsg); }
	 * 
	 * } catch (IOException e) { Log.i(MyImageUtils.class.getSimpleName(),
	 * e.getLocalizedMessage(), e); if (handler != null) { Message errorMsg =
	 * Message.obtain(); errorMsg.what = Constants.DOWNLOAD_CANCEL;
	 * handler.sendMessage(errorMsg); } } } };
	 * 
	 * ThreadPoolManager.getInstance().addTask(runnable);
	 * 
	 * return null; }
	 */
}
