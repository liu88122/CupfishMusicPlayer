package com.cupfish.music.test;

import java.util.List;

import android.test.AndroidTestCase;

import com.cupfish.music.bean.Album;
import com.cupfish.music.bean.Song;
import com.cupfish.music.exception.NetTimeoutException;
import com.cupfish.music.utils.BaiduMusicHelper;

public class TestBaiduMusicHelper extends AndroidTestCase {

	public void testGetSongById() throws NetTimeoutException{
		Song song = BaiduMusicHelper.getSongById("13932461");
		System.out.println(song);
	}
	
	public void testGetUrl() throws NetTimeoutException{
		String url = BaiduMusicHelper.getDownloadUrlBySongId("31149822");
		System.out.println(url);
	}
	
	public void testGetSongsByTitle() throws NetTimeoutException{
		List<Song> songs = BaiduMusicHelper.getSongsByTitle("断点");
		System.out.println(songs.size());
		for(Song s : songs){
			System.out.println(s);
		}
	}
	
	public void testGetSongsFromBaidu() throws NetTimeoutException{
		List<Song> songs = BaiduMusicHelper.getSongsFromBaidu(BaiduMusicHelper.TopListType.NEW_100);
		System.out.println("SIZE:" + songs.size());
		for(Song song : songs){
			System.out.println("TITLE:" + song.getTitle() + " ID:" + song.getSongId());
		}
	}
	
	public void testGetAlbumById() throws NetTimeoutException{
		Album a = BaiduMusicHelper.getAlbumById("31149823", true);
		System.out.println(a);
	}
	
}
