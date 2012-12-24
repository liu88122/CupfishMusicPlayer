package com.cupfish.musicplayer.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;

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
		List<MusicFolder> folders = new ArrayList<MusicFolder>();
		File dir = Environment.getExternalStorageDirectory();
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
	
	public static void searchMusicFolder(File dir, List<MusicFolder> folders){
		if (dir != null) {
			if(dir.isDirectory()){
				File noMedia = new File(dir, ".nomedia");
				if(noMedia.exists()){
					return ;
				}
				String[] files = dir.list();
				int count = 0;
				if (files != null) {
					for (String fileName : files) {
						File file = new File(dir, fileName);
						if (file.isFile() && isMusicFile(file)) {
							count++;
						} else if(file.isDirectory()){
							searchMusicFolder(file, folders);
						}
					}
					if (count > 0) {
						MusicFolder folder = new MusicFolder(dir.getName(), dir.getPath(), count);
						folders.add(folder);
					}
				}
			}
		}
	}
	
	public static boolean isMusicFile(File file){
		boolean result = false;
		String name = file.getName().toLowerCase();
		if(name.endsWith("mp3") || name.endsWith("aac") || name.endsWith("wav")){
			result = true;
		}
		return result;
	}
	
}
