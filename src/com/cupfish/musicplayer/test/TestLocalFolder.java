package com.cupfish.musicplayer.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.cupfish.musicplayer.bean.MusicFolder;
import com.cupfish.musicplayer.dao.LocalMusicFolderDao;
import com.cupfish.musicplayer.utils.LocalManager;
import com.cupfish.musicplayer.utils.MusicDbHelper;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.test.AndroidTestCase;

public class TestLocalFolder extends AndroidTestCase {

	public void test(){
		MusicDbHelper dbHelper = new MusicDbHelper(getContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		LocalMusicFolderDao mDao = new LocalMusicFolderDao();
		List<MusicFolder> folders = new ArrayList<MusicFolder>();
		File dir = Environment.getExternalStorageDirectory();
		LocalManager.searchMusicFolder(dir, folders);
		mDao.insert(db, folders.get(0));
		MusicFolder folder = mDao.queryAll(db).get(0);
		System.out.println(folder.getPath());
	}
	
}
