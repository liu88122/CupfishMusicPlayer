package com.cupfish.music.test;

import java.util.List;

import com.cupfish.music.bean.Song;
import com.cupfish.music.dao.MusicDao;
import com.cupfish.music.utils.LocalMusicScanner;
import com.cupfish.music.utils.MusicDbHelper;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class TestMusicScanner extends AndroidTestCase {

	public void test(){
		LocalMusicScanner scanner = LocalMusicScanner.getInstance(getContext());
		scanner.start();
	}
	
	public void testQuery(){
		MusicDbHelper helper = new MusicDbHelper(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		MusicDao mDao = new MusicDao(getContext());
		List<Song> songs = mDao.queryAllLocalSongs();
		System.out.println(songs.size());
	}
	
}
