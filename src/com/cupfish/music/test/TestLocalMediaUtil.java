package com.cupfish.music.test;

import java.util.List;

import android.test.AndroidTestCase;

import com.cupfish.music.bean.Song;
import com.cupfish.music.utils.LocalMediaUtils;

public class TestLocalMediaUtil extends AndroidTestCase {

	public void testGetLocalMedia(){
		List<Song> list = LocalMediaUtils.getLocalSongs(getContext());
		System.out.println(list.size());
		for(Song song : list){
			System.out.println(song.getTitle());
		}
	}
	
}
