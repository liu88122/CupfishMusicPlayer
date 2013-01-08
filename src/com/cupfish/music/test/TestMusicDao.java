package com.cupfish.music.test;

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
		MusicDao mDao = new MusicDao();
		mDao.insertLocalSong(db, song);
		
	}
	
	public void testQuery(){
		MusicDbHelper helper = new MusicDbHelper(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		MusicDao mDao = new MusicDao();
		Song song = mDao.querySongBySongId(getContext(), MusicDao.TABLE_LOCAL_MUSIC, "13932461");
		System.out.println(song);
	}
	
}
