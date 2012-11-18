package com.cupfish.musicplayer.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringBufferInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.bean.AppUpdateInfo;
import com.cupfish.musicplayer.utils.UpdateManager;

public class JsonParser {

	private static final String JSON_KEY_VERSION_CODE = "versionCode";
	private static final String JSON_KEY_APP_NAME = "appName";
	private static final String JSON_KEY_DOWNLOAD_URL = "downloadUrl";
	private static final String JSON_KEY_UPDATE_LOG = "updateLog";
	
	public static AppUpdateInfo parseAppUpdateInfo(String json){
		try {
			JSONObject jObject = new JSONObject(json);
			AppUpdateInfo appUpdateInfo = new AppUpdateInfo();
			appUpdateInfo.versionCode = jObject.getInt(JSON_KEY_VERSION_CODE);
			appUpdateInfo.appName = jObject.getString(JSON_KEY_APP_NAME);
			appUpdateInfo.downloadUrl = jObject.getString(JSON_KEY_DOWNLOAD_URL);
			appUpdateInfo.updateLog = jObject.getString(JSON_KEY_UPDATE_LOG);
			return appUpdateInfo;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 生成更新文件，这里生成的文件放在服务器，其他APP访问此文件检查是否更新
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-16 下午4:27:08
	 */
	public static void generateCurrentAppUpdateInfoJsonFile(Context context, String downloadUrl, String updateLog){
		
		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			return;
		}
		File file = new File(Environment.getExternalStorageDirectory() + File.separator + "VERSION");
		JSONObject obj = new JSONObject();
		try {
			obj.put(JSON_KEY_VERSION_CODE, UpdateManager.getCurrentVersion(context));
			obj.put(JSON_KEY_APP_NAME, context.getString(R.string.app_name));
			obj.put(JSON_KEY_DOWNLOAD_URL, downloadUrl);
			obj.put(JSON_KEY_UPDATE_LOG, updateLog);
			FileOutputStream fos = new FileOutputStream(file);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "utf8"));
			writer.write(obj.toString());
			writer.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
