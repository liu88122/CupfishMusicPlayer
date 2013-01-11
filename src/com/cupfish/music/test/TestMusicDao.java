package com.cupfish.music.test;

import java.util.List;
import java.util.Set;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.cupfish.music.bean.Artist;
import com.cupfish.music.bean.Song;
import com.cupfish.music.dao.MusicDao;
import com.cupfish.music.exception.NetTimeoutException;
import com.cupfish.music.utils.BaiduMusicHelper;
import com.cupfish.music.utils.BaiduMusicHelper.TopListType;
import com.cupfish.music.utils.MusicDbHelper;

public class TestMusicDao extends AndroidTestCase {

	public void testInsert() throws NetTimeoutException{
		MusicDbHelper helper = new MusicDbHelper(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
//		Song song = BaiduMusicHelper.getSongById("13932461");
		MusicDao mDao = new MusicDao(getContext());
//		song.setTitle("huo");
//		song.setSongPath("/sdcard/a/huo.mp3");
//		mDao.insertSong(db, song);
//		song.setTitle("wo");
//		song.setSongPath("/sdcard/a/wo.mp3");
//		mDao.insertSong(db, song);
		List<Song> songs = BaiduMusicHelper.getSongsFromBaidu(TopListType.CHINAVOICE);
		mDao.insertSongs(db, songs);
	}
	
	public void testQuery(){
		MusicDbHelper helper = new MusicDbHelper(getContext());
		SQLiteDatabase db = helper.getWritableDatabase();
		MusicDao mDao = new MusicDao(getContext());
//		List<Song> songs = mDao.queryAllLocalSongs();
//		System.out.println(songs.size());
//		List<MusicFolder> folders = mDao.queryLocalFolders();
//		List<Song> songs3 = mDao.queryLocalSongsByFolder(folders.get(2).getPath());
//		System.out.println("folder::" + songs3.get(0).getTitle());
//		
		List<Artist> artists = mDao.queryLocalArtists();
		System.out.println(artists.size());
		for(Artist artist : artists){
			System.out.println(artist.getName());
		}
//		
//		List<Album> albums = mDao.queryLocalAlbums();
//		List<Song> songs2 = mDao.queryLocalSongsByAlbum(albums.get(3));
//		System.out.println("album::" + songs2.get(0).getAudioType());
		
//		List<Song> songs = mDao.querySongsBySourceAndCategory("baidu", TopListType.CHINAVOICE, 11, 20);
//		for(Song song : songs){
//			System.out.println(song);
//		}
	}
	
}
