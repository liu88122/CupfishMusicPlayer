package com.cupfish.musicplayer.test;

import com.cupfish.musicplayer.exception.NetTimeoutException;
import com.cupfish.musicplayer.utils.GoogleMusicHelper;
import com.cupfish.musicplayer.utils.GoogleMusicHelper.TopListType;

import android.test.AndroidTestCase;

public class TestGoogleMusicHelper extends AndroidTestCase {

	public void test() throws NetTimeoutException{
		GoogleMusicHelper.getSongsFromGoogle(TopListType.CHINESE_NEW_SONGS, 0);
	}
	
	public void test2() throws NetTimeoutException{
		//// http://g.top100.cn/16667639/html/download.html?id=Sa7451c7414df873c
		String url = GoogleMusicHelper.getDownloadUrlById("Sa7451c7414df873c");
		System.out.println(url);
		//GoogleMusicHelper.play(url);
	}
	
}
