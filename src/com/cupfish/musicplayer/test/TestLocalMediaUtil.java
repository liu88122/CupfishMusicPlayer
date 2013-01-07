package com.cupfish.musicplayer.test;

import java.util.List;

import android.test.AndroidTestCase;

import com.cupfish.musicplayer.bean.Song;
import com.cupfish.musicplayer.utils.LocalMediaUtil;

public class TestLocalMediaUtil extends AndroidTestCase {

	public void testGetLocalMedia(){
		List<Song> list = LocalMediaUtil.getLocalSongs(getContext());
		System.out.println(list.size());
		for(Song song : list){
			System.out.println(song.getTitle());
		}
	}
	
}
