package com.cupfish.musicplayer.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.widget.ScrollView;

import com.cupfish.musicplayer.bean.AppUpdateInfo;
import com.cupfish.musicplayer.parser.JsonParser;

/**
 * APP升级管理类
 * 
 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
 * @2012-11-16下午1:51:46
 */
public class UpdateManager {

	private static UpdateManager sInstance;
	private static Object sLock = new Object();
	private static final String UPDATE_URL = "http://www.cupfish.com/VERSION";

	private UpdateManager() {

	}

	public static UpdateManager getInstance() {
		synchronized (sLock) {
			if (sInstance == null) {
				sInstance = new UpdateManager();
			}
			return sInstance;
		}
	}

	/**
	 * 检查更新，如果有更新，返回AppUpdateInfo，否则返回空
	 * 
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-16 下午2:51:16
	 */
	public AppUpdateInfo checkUpdate(Context context) {
		// 网络不可用时不用更新
		if (!checkNetwork(context)) {
			return null;
		}

		int localVersionCode = getCurrentVersion(context);
		AppUpdateInfo appUpdateInfo = getAppUpdateInfo();
		if (appUpdateInfo != null) {
			if (appUpdateInfo.versionCode > localVersionCode) {
				return appUpdateInfo;
			}
		}
		return null;
	}

	private AppUpdateInfo getAppUpdateInfo() {
		try {
			URL url = new URL(UPDATE_URL);
			HttpURLConnection conncetion = (HttpURLConnection) url.openConnection();
			InputStream is = conncetion.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			byte[] bytes = baos.toByteArray();
			String json = new String(bytes, 0, bytes.length);
			return JsonParser.parseAppUpdateInfo(json);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取当前App版本号
	 * 
	 * @param context
	 * @return int 当前App版本号
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-16 下午3:08:03
	 */
	public static int getCurrentVersion(Context context) {
		PackageManager packageManager = context.getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 下载APK
	 * 
	 * @param url
	 *            APK下载地址
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-16 下午2:49:02
	 */
	public void downloadApk(final Context context, final String downloadUrl) {

		final ProgressDialog dialog = new ProgressDialog(context);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setTitle("Downloading...");
		
		Thread downloadThread = new Thread(new Runnable() {
			@Override
			public void run() {
				final String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
				InputStream is = null;
				FileOutputStream fos = null;
				try {
					URL url = new URL(downloadUrl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					is = conn.getInputStream();
					dialog.setMax(conn.getContentLength());
					
					File file = new File(context.getExternalCacheDir(), fileName);
					fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024 * 8];
					int len = 0;
					int currentLoaded = 0;
					while((len = is.read(buffer)) != -1){
						fos.write(buffer, 0, len);
						currentLoaded += len;
						dialog.setProgress(currentLoaded);
					}
					
					if(file.exists()){
						dialog.dismiss();
						installApk(context, file.getPath());
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (fos != null) {
							fos.close();
						}
					} catch (Exception e) {
					}
					try {
						if (is != null) {
							is.close();
						}
					} catch (Exception e) {
					}
				}
//				final String dir = Environment.getExternalStorageDirectory() + "/cupfish";
//				DownloadTask task = new DownloadTask(context, downloadUrl, dir, 2);
//				task.setDownloadListener(new DownloadListener() {
//					
//					@Override
//					public void onDownloading(int size) {
//						dialog.setProgress(size);
//						
//					}
//					
//					@Override
//					public void onDownloadFinish() {
//						// TODO Auto-generated method stub
//						installApk(context, dir + "/" + fileName);
//					}
//				});
//				task.start();
			}
		});
		downloadThread.start();
		dialog.show();
	}

	/**
	 * 安装APK文件
	 * 
	 * @param path
	 *            APK文件路径
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-16 下午2:50:15
	 */
	public void installApk(Context context, String path) {
		System.out.println(path);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 检查网络是否可用
	 * 
	 * @param context
	 * @return if true 当前网络可用，if false 当前网络不可用
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-16 下午2:47:05
	 */
	public boolean checkNetwork(Context context) {
		return ConnectivityHelper.isNetworkActivie(context);
	}

}
