package com.cupfish.musicplayer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.cupfish.musicplayer.bean.LRC2;
import com.cupfish.musicplayer.bean.Song;
import com.cupfish.musicplayer.global.Constants;

public class LRCReader {

	private static String LRC_AD = null;
	
	/**
	 * 从本地读取lrc文件
	 * 
	 * @param songName
	 * @return HashMap<Long,String>
	 * 
	 */
	public static LRC2 getLRC(Song song, Context context) {

		TreeMap<Long, String> result = null;
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		LRC_AD = sp.getString("myLrc", Constants.LRC_AD);

		String title = song.getTitle();
		String lrcUrl = song.getLrcUrl();
		if (TextUtils.isEmpty(title)) {
			return null;
		}
		File file = new File(Constants.SDCARD_LRC_SAVE_PATH + "/" + title + ".lrc");

		if (!file.exists()  && !TextUtils.isEmpty(lrcUrl)) {
			file = DownloadUtil.load(title, lrcUrl, null, Constants.DOWNLOAD_FILE_LRC);
		}else{
			//TODO 如果是没有歌词文件的歌曲，需要直接下载
//			song = BaiduTingHelper.inflateSong(song);
//			if(!TextUtils.isEmpty(song.getLrcUrl())){
//				file = DownloadUtil.load(title, song.getLrcUrl(), null, Constants.DOWNLOAD_FILE_LRC);
//			}
		}
		result = parseLRCFromFile(file);
		return new LRC2(result);
	}
	
	
	public static TreeMap<Long, String> getLRCTreeMap(Song song, Context context) {

		TreeMap<Long, String> result = null;
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		LRC_AD = sp.getString("myLrc", Constants.LRC_AD);

		String title = song.getTitle();
		String lrcUrl = song.getLrcUrl();
		if (TextUtils.isEmpty(title)) {
			return null;
		}
		File file = new File(Constants.SDCARD_LRC_SAVE_PATH + "/" + title + ".lrc");

		if (!file.exists()  && !TextUtils.isEmpty(lrcUrl)) {
			file = DownloadUtil.load(title, lrcUrl, null, Constants.DOWNLOAD_FILE_LRC);
		}else{
			//TODO 如果是没有歌词文件的歌曲，需要直接下载
//			song = BaiduTingHelper.inflateSong(song);
//			if(!TextUtils.isEmpty(song.getLrcUrl())){
//				file = DownloadUtil.load(title, song.getLrcUrl(), null, Constants.DOWNLOAD_FILE_LRC);
//			}
		}
		result = parseLRCFromFile(file);
		return result;
	}

	public static TreeMap<Long, String> parseLRCFromFile(File file){

		if (file == null || !file.exists()) {
			return null;
		}
		TreeMap<Long, String> result = new TreeMap<Long, String>(new Comparator<Long>() {

			@Override
			public int compare(Long time1, Long time2) {
				return (int) (time1 - time2);
			}

		});

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));

			// \\[(\\d{2}:\\d{2}\\.\\d{2})\\]
			// \\[([^\\]]+)\\]
			// Pattern pattern =
			// Pattern.compile("\\[(\\d{2}:\\d{2}\\.\\d{2})\\]");
			Pattern pattern = Pattern.compile("\\[(\\d{2}:\\d{2}\\.\\d{2})\\]");
			String line = null;
			while ((line = reader.readLine()) != null) {
				// [01:15.59]好儿郎 一生要 志在四方
				matchLrc(result, pattern, line);
			}
			reader.close();
			result.put((long) 3600000, "end");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private static void matchLrc(TreeMap<Long, String> result, Pattern pattern, String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			String content = line.substring(line.lastIndexOf("]") + 1);
			if(TextUtils.isEmpty(content)){
				content = LRC_AD;
			}
			String matchStr = matcher.group();
			String timeStr = matchStr.substring(matchStr.indexOf("[") + 1, matchStr.lastIndexOf("]"));
			long time = getMillis(timeStr);
			result.put(time, content);
			String tempLine = line.replace(matchStr, "");
			matchLrc(result, pattern, tempLine);
		}
	}

	private static long getMillis(String time) {
		// 01:15.59
		String[] timeSlit = time.split(":");
		long minuteMillis = Long.parseLong(timeSlit[0]) * 60 * 1000;
		String[] secondPart = timeSlit[1].split("\\.");
		long secondMillis = Long.parseLong(secondPart[0]) * 1000 + Long.parseLong(secondPart[1]) * 10;
		return minuteMillis + secondMillis;
	}
}
