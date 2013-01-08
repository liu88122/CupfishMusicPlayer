package com.cupfish.music.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.test.AndroidTestCase;

import com.cupfish.music.bean.MusicFolder;
import com.cupfish.music.utils.LocalManager;

public class TestLocalManager extends AndroidTestCase {

	public void testSearchMusicFolder(){
		List<MusicFolder> folders = new ArrayList<MusicFolder>();
		File dir = Environment.getExternalStorageDirectory();
		long current = System.currentTimeMillis();
		LocalManager.searchMusicFolder(dir, folders);
		System.out.println(System.currentTimeMillis() - current);
//		for(MusicFolder folder : folders){
//			System.out.println(folder.getTitle() + " " + folder.getPath() + " " + folder.getCount());
//		}
	}
	
}
