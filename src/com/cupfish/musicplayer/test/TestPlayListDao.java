package com.cupfish.musicplayer.test;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.cupfish.musicplayer.dao.PlayerListDao;
import com.cupfish.musicplayer.domain.Song;

public class TestPlayListDao extends AndroidTestCase {

	
	
	public void testDeleteAll(){
		PlayerListDao dao = new PlayerListDao(getContext());
		dao.deleteAll();
	}
	
	public void testGetPlaylist(){
		PlayerListDao dao = new PlayerListDao(getContext());
		List<Song> list = dao.getPlaylist();
		System.out.println(list.size());
	}
}
