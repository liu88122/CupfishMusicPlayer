package com.cupfish.music.utils;

import java.io.File;

import android.text.TextUtils;
import android.util.Log;

import com.cupfish.music.bean.Song;

public class FileManageAssistant {

	private static final String TAG = "FileManageAssistant";

	public static void delete(Song song){
		String songUrl = song.getSongPath();
		if(TextUtils.isEmpty(songUrl)){
			return;
		}
		File file = new File(songUrl);
		if(file.exists()){
			file.delete();
		}
	}
	
}
