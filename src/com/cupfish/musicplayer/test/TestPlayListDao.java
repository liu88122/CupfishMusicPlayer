package com.cupfish.musicplayer.test;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.cupfish.musicplayer.bean.Song;
import com.cupfish.musicplayer.dao.PlayerlistDao;

public class TestPlayListDao extends AndroidTestCase {

	
	
	public void testDeleteAll(){
		PlayerlistDao dao = new PlayerlistDao(getContext());
		dao.deleteAll();
	}
	
	public void testGetPlaylist(){
		PlayerlistDao dao = new PlayerlistDao(getContext());
		List<Song> list = dao.getPlaylist();
		System.out.println(list.size());
	}
}
