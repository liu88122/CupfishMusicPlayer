package com.cupfish.music.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cupfish.music.R;
import com.cupfish.music.bean.Album;
import com.cupfish.music.bean.Artist;
import com.cupfish.music.bean.LocalItem;
import com.cupfish.music.bean.MusicFolder;
import com.cupfish.music.bean.Song;
import com.cupfish.music.dao.MusicDao;
import com.cupfish.music.ui.LocalAllActivity;
import com.cupfish.music.ui.LocalFolderActivity;

public class LocalCategoryFactory {

	public static List<LocalItem> getAllCategoris(Context context) {
		List<LocalItem> categories = new ArrayList<LocalItem>();
		MusicDao mMusicDao = new MusicDao(context);
		for(int i=0; i<CATEGORY_NUM; i++){
			String name = context.getString(defaultNames[i]);
			LocalItem item = new LocalItem(name, foregroundCovers[i], backgroundCovers[i]);
			switch(i){
			case 0:
				ArrayList<Song> songs = mMusicDao.queryAllLocalSongs();
				if (songs != null) {
					item.setData(songs);
					item.setItemNum(songs.size());
					item.setTarget(LocalAllActivity.class);
				}
				break;
			case 1:
				ArrayList<Artist> artists = mMusicDao.queryLocalArtists();
				if(artists != null){
					item.setData(artists);
					item.setItemNum(artists.size());
				}
				break;
			case 2:
				ArrayList<Album> albums = mMusicDao.queryLocalAlbums();
				if (albums != null) {
					item.setItemNum(albums.size());
					item.setData(albums);
				}
				break;
			case 3:
				ArrayList<MusicFolder> folders = mMusicDao.queryLocalFolders();
				if (folders != null) {
					item.setItemNum(folders.size());
					item.setData(folders);
					item.setTarget(LocalFolderActivity.class);
				}
				break;
			case 4:
				item.setItemNum(LocalManager.getAllFavorites(context).size());
				break;
			case 5:
				item.setItemNum(LocalManager.getAllPlaylists(context).size());
				break;
			}
			categories.add(item);
		}
		return categories;
	}
	
	

	private static final int CATEGORY_NUM = 6;

	private static int[] foregroundCovers = { R.drawable.nav_item_decco_all, 
									R.drawable.nav_item_decco_artist,
									R.drawable.nav_item_decco_album, 
									R.drawable.nav_item_decco_folder, 
									R.drawable.nav_item_decco_favorate,
									R.drawable.nav_item_decco_playlist };
	
	private static int[] backgroundCovers = {R.drawable.music_local_all,
									R.drawable.music_local_artist,
									R.drawable.music_local_album,
									R.drawable.music_local_folder,
									R.drawable.music_local_favorates,
									R.drawable.music_local_playlist};
	private static int[] defaultNames = {R.string.local_all_songs,
								R.string.local_artist,
								R.string.local_album,
								R.string.local_folder,
								R.string.local_favorate,
								R.string.local_playlist };
}
