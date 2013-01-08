package com.cupfish.music.test;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.cupfish.music.bean.Song;
import com.cupfish.music.dao.PlayerlistDao;

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
