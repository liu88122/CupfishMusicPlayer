package com.cupfish.musicplayer.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cupfish.musicplayer.bean.Album;
import com.cupfish.musicplayer.bean.Artist;
import com.cupfish.musicplayer.bean.MusicFolder;
import com.cupfish.musicplayer.bean.Playlist;
import com.cupfish.musicplayer.bean.Song;

public class LocalManager {
	
	public static List<Song> getAllSongs(Context context){
		return LocalMediaUtil.getLocalSongs(context);
	}
	
	public static List<Artist> getAllArtists(Context context){
		List<Artist> artists = new ArrayList<Artist>();
		return artists;
	}
	
	public static List<Album> getAllAlbums(Context context){
		List<Album> albums = new ArrayList<Album>();
		return albums;
	}
	
	public static List<MusicFolder> getAllFolders(Context context){
		List<MusicFolder> folders= new ArrayList<MusicFolder>();
		return folders;
	}
	
	public static List<Song> getAllFavorites(Context context){
		List<Song> favorites = new ArrayList<Song>();
		return favorites;
	}
	
	public static List<Playlist> getAllPlaylists(Context context){
		List<Playlist> playlists = new ArrayList<Playlist>();
		return playlists;
	}
	
}
