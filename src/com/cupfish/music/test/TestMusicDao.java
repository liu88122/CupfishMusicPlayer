package com.cupfish.music.test;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.cupfish.music.bean.Song;
import com.cupfish.music.dao.MusicDao;
import com.cupfish.music.exception.NetTimeoutException;
import com.cupfish.music.utils.BaiduTingHelper;
import com.cupfish.music.utils.MusicDbHelper;

public class TestMusicDao extends AndroidTestCase {

	public void testInsert() throws NetTimeoutException{
		MusicDbHelper helper = new MusicDbHelper(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		Song song = BaiduTingHelper.getSongById("13932461");
		MusicDao mDao = new MusicDao(getContext());
		song.setTitle("huo");
		song.setSongPath("/sdcard/a/huo.mp3");
		mDao.insertSong(db, song);
		song.setTitle("wo");
		song.setSongPath("/sdcard/a/wo.mp3");
		mDao.insertSong(db, song);
	}
	
	public void testQuery(){
		MusicDbHelper helper = new MusicDbHelper(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		MusicDao mDao = new MusicDao(getContext());
		List<Song> songs = mDao.queryLocalSongsByFolder("/sdcard/a");
		System.out.println(songs.size());
	}
	
}
