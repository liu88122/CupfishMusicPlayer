package com.cupfish.musicplayer.utils;

import java.io.File;

import android.text.TextUtils;
import android.util.Log;

import com.cupfish.musicplayer.bean.Song;

public class FileManageAssistant {

	private static final String TAG = "FileManageAssistant";

	public static void delete(Song song){
		String songUrl = song.getUrl();
		if(TextUtils.isEmpty(songUrl)){
			return;
		}
		File file = new File(songUrl);
		if(file.exists()){
			file.delete();
		}
	}
	
}
