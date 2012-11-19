package com.cupfish.musicplayer.test;

import junit.framework.Assert;

import com.cupfish.musicplayer.dao.DownloadDao;
import com.cupfish.musicplayer.download.DownloadDbHelper;

import android.test.AndroidTestCase;

public class TestDownloadDao extends AndroidTestCase {
	
	DownloadDao mDao;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mDao = new DownloadDao(getContext());
	}
	
	public void testInsert(){
		mDao = new DownloadDao(getContext());
		mDao.insert("http://www.baidu.com", 1);
		mDao.insert("http://www.baidu.com", 2);
		mDao.insert("http://www.baidu.com", 3);
		mDao.insert("http://www.cupfish.com", 4);
	}
	
	public void testUpdate(){
		DownloadDbHelper mHelper = new DownloadDbHelper(getContext());
		mDao.update("http://www.baidu.com", 6, 200, mHelper.getWritableDatabase());
	}
	
	public void testGetDownloadLength(){
		int length = mDao.getDownloadLength("http://www.baidu.com", 6);
		Assert.assertEquals(-1, length);
	}
	
	public void testDelete(){
		mDao.delete("http://www.baidu.com");
	}
	
}
