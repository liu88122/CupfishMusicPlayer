package com.cupfish.music.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.cupfish.music.bean.Album;
import com.cupfish.music.bean.Artist;
import com.cupfish.music.bean.MusicFolder;
import com.cupfish.music.bean.Song;
import com.cupfish.music.utils.MusicDbHelper;

public class MusicDao {
	
	public static final String TABLE_LOCAL_MUSIC = "local_music";
	
	private MusicDbHelper mDbHelper;
	
	public MusicDao(Context context){
		mDbHelper = new MusicDbHelper(context);
	}
	
	/**
	 * 创建相应的表
	 * @param db
	 * void
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-8 下午4:58:04
	 */
	public static void createTables(SQLiteDatabase db){
		if(db != null && db.isOpen()){
			createTable(db, TABLE_LOCAL_MUSIC);
		}
	}
	
	private static void createTable(SQLiteDatabase db, String tableName){
		
		String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "("
						+ Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
						+ Columns.SONG_ID + " VARCHAR, "
						+ Columns.TITLE + " VARCHAR, "
						+ Columns.TITLE_PINYIN + " VARCHAR, "
						+ Columns.ALBUM_ID + " INTEGER, "
						+ Columns.ALBUM_TITLE + " VARCHAR, "
						+ Columns.ARTIST + " VARCHAR, "
						+ Columns.COVER_PATH + " VARCHAR, "
						+ Columns.COVER_URL + " VARCHAR, "
						+ Columns.COVER_HD_URL + " VARCHAR, "
						+ Columns.ALBUM_DESC + " VARCHAR, "
						+ Columns.SONG_PATH + " VARCHAR, "
						+ Columns.SONG_URL + " VARCHAR, "
						+ Columns.LRC_PATH + " VARCHAR, "
						+ Columns.LRC_URL + " VARCHAR, "
						+ Columns.AUDIO_TYPE + " VARCHAR, "
						+ Columns.DURATION + " INTEGER, "
						+ Columns.SOURCE + " VARCHAR, "
						+ Columns.CATEGORY + " VARCHAR "
						+ ")";
		db.execSQL(sql);
	}
	
	/**
	 * 插入本地歌曲集合到数据库
	 * @param db
	 * @param songs
	 * void
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-8 下午4:58:28
	 */
	public void insertSongs(SQLiteDatabase db, List<Song> songs) {
		if (db != null && db.isOpen()) {
			if (songs != null && songs.size() > 0) {
				Iterator<Song> iterator = songs.iterator();
				while (iterator.hasNext()) {
					insertSong(db, iterator.next());
				}
			}
		}
	}
	
	/**
	 * 插入歌曲到数据库
	 * @param db
	 * @param song
	 * void
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-8 下午4:59:09
	 */
	public void insertSong(SQLiteDatabase db, Song song){
		insert(db, song, TABLE_LOCAL_MUSIC);
	}
	
	
	private void insert(SQLiteDatabase db, Song song, String table){
		
		if(song == null){
			return;
		}
		
		if(db!= null && db.isOpen()){
			String sql = "INSERT INTO " + table + "(" 
							+ Columns.SONG_ID + ","
							+ Columns.TITLE + ", "
							+ Columns.TITLE_PINYIN + ", "
							+ Columns.ALBUM_ID + ", "
							+ Columns.ALBUM_TITLE + ", "
							+ Columns.ARTIST + ", "
							+ Columns.COVER_PATH + ", "
							+ Columns.COVER_URL + ", "
							+ Columns.COVER_HD_URL + ", "
							+ Columns.ALBUM_DESC + ", "
							+ Columns.SONG_PATH + ", "
							+ Columns.SONG_URL + ", "
							+ Columns.LRC_PATH + ", "
							+ Columns.LRC_URL + ", "
							+ Columns.AUDIO_TYPE + ", "
							+ Columns.DURATION + ", "
							+ Columns.SOURCE + ", "
							+ Columns.CATEGORY
							+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			Album album = song.getAlbum();
			String albumId = null;
			String albumTitle = null;
			String coverPath = null;
			String coverUrl = null;
			String coverHdUrl = null;
			String albumDesc = null;
			if(album != null){
				albumId = album.getId();
				albumTitle = album.getTitle();
				coverPath = album.getCoverPath();
				coverUrl = album.getCoverUrl();
				coverHdUrl = album.getCoverHdUrl();
				albumDesc = album.getDesc();
			}
			
			List<Artist> artists = song.getArtists();
			String artist = null;
			if(artists != null && artists.size() > 0){
				Iterator<Artist> iterator = artists.iterator();
				StringBuilder sb = new StringBuilder();
				while(iterator.hasNext()){
					sb.append(iterator.next() + ",");
				}
				sb.deleteCharAt(sb.length() -1);
				artist = sb.toString();
			}
			
			
			Object[] params = {	song.getSongId(),
								song.getTitle(),
								song.getTitlePinyin(),
								albumId,
								albumTitle,
								artist,
								coverPath,
								coverUrl,
								coverHdUrl,
								albumDesc,
								song.getSongPath(),
								song.getSongUrl(),
								song.getLrcPath(),
								song.getLrcUrl(),
								song.getAudioType(),
								song.getDuration(),
								song.getSource(),
								song.getCategory()
								};
			db.execSQL(sql, params);
		}
	}
	
	/**
	 * 根据本地Id查找歌曲
	 * @param context
	 * @param table
	 * @param id
	 * @return
	 * Song
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-9 上午11:05:06
	 */
	public Song querySongById(Context context,String table, String id){
		SQLiteDatabase db = new MusicDbHelper(context).getReadableDatabase();
		Song song = null;
		if(db.isOpen()){
			String sql = "SELECT * FROM " + table 
							+ " WHERE " + Columns._ID + " = ? " ;
			String[] params = { id };
			Cursor cursor = db.rawQuery(sql, params);
			if(cursor.moveToFirst()){
				song = extractSongFromCursor(cursor);
				cursor.close();
			}
		}
		return song;
	}
	
	/**
	 * 根据歌曲在线id查找歌曲，可能不完善，有待更新
	 * @param context
	 * @param table
	 * @param songId
	 * @return
	 * Song
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-9 上午11:05:24
	 */
	public Song querySongBySongId(Context context,String table, String songId){
		SQLiteDatabase db = new MusicDbHelper(context).getReadableDatabase();
		Song song = null;
		if(db.isOpen()){
			String sql = "SELECT * FROM " + table 
							+ " WHERE " + Columns.SONG_ID + " = ? " ;
			String[] params = { songId };
			Cursor cursor = db.rawQuery(sql, params);
			if(cursor.moveToFirst()){
				song = extractSongFromCursor(cursor);
				cursor.close();
			}
		}
		return song;
	}
	
	/**
	 * 根据歌曲名和歌手获取歌曲,这里还没有处理类型不同但歌曲名和歌手一致的情况
	 * @return
	 * Song
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-9 下午5:15:19
	 */
	public Song querySongByTitleAndArtist(String songTitle, String artistName){
		if(songTitle == null){
			return null;
		}
		Song song = null;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String sql = null;
		String[] params = null;
		if(TextUtils.isEmpty(artistName)){
			sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC
					+ " WHERE " + Columns.TITLE + " = ?";
			params = new String[]{ songTitle };
		}else{
			sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC
					+ " WHERE " + Columns.TITLE 
					+ " = ? AND " + Columns.ARTIST + " = ?";
			params = new String[]{ songTitle, artistName };
		}
		Cursor cursor = db.rawQuery(sql, params);
		if(cursor.moveToFirst()){
			song = extractSongFromCursor(cursor);
			cursor.close();
		}
		return song;
	}
	
	/**
	 * 查找所有本地歌曲
	 * @return
	 * List<Song>
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-9 上午11:12:51
	 */
	public List<Song> queryAllLocalSongs(){
		List<Song> songs = new ArrayList<Song>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC 
						+ " WHERE " + Columns.SONG_PATH + " IS NOT NULL";
		Cursor cursor = db.rawQuery(sql, new String[]{});
		while(cursor.moveToNext()){
			Song song = extractSongFromCursor(cursor);
			songs.add(song);
		}
		cursor.close();
		return songs;
	}
	
	/**
	 * 根据歌手查找歌曲
	 * @param artist
	 * @return
	 * List<Song>
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-9 下午3:04:12
	 */
	public List<Song> queryLocalSongsByArtist(Artist artist){
		if(artist == null || TextUtils.isEmpty(artist.getName())){
			return null;
		}
		List<Song> songs = new ArrayList<Song>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if(db.isOpen()){
			String sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC 
							+ " WHERE " + Columns.ARTIST + " = ?";
			String[] params = {artist.getName()};
			Cursor cursor = db.rawQuery(sql, params);
			while(cursor.moveToNext()){
				Song song = extractSongFromCursor(cursor);
				songs.add(song);
			}
			cursor.close();
		}
		return songs;
	}
	
	/**
	 * 根据专辑获取歌曲
	 * @param album
	 * @return
	 * List<Song>
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-9 下午4:13:00
	 */
	public List<Song> queryLocalSongsByAlbum(Album album){
		if(album == null || TextUtils.isEmpty(album.getTitle())){
			return null;
		}
		StringBuilder artistsBuilder = new StringBuilder();
		List<Artist> artists = album.getArtists();
		if(artists != null && artists.size() > 0){
			Iterator<Artist> iterator = artists.iterator();
			while(iterator.hasNext()){
				artistsBuilder.append(iterator.next().getName() + ",");
			}
			artistsBuilder.deleteCharAt(artistsBuilder.length() - 1);
		}
		List<Song> songs = new ArrayList<Song>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if(db.isOpen()){
			String sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC
							+ " WHERE " + Columns.ALBUM_TITLE
							+ " = ? AND " + Columns.ARTIST + " LINK %?%";
			String[] params = {album.getTitle(), artistsBuilder.toString()};
			Cursor cursor = db.rawQuery(sql, params);
			while(cursor.moveToNext()){
				Song song = extractSongFromCursor(cursor);
				songs.add(song);
			}
			cursor.close();
		}
		return songs;
	}
	
	/**
	 * 根据歌曲文件夹名查找歌曲
	 * @param folderPath
	 * @return
	 * List<Song>
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-9 下午4:53:52
	 */
	public List<Song> queryLocalSongsByFolder(String folderPath){
		if(TextUtils.isEmpty(folderPath)){
			return null;
		}
		List<Song> songs = new ArrayList<Song>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if(db.isOpen()){
			String sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC
							+ " WHERE " + Columns.SONG_PATH + " IS NOT NULL";
			Cursor cursor = db.rawQuery(sql, new String[]{});
			while(cursor.moveToNext()){
				Song song  = extractSongFromCursor(cursor);
				if(song != null){
					String songPath = song.getSongPath();
					if(songPath != null){
						int index = songPath.lastIndexOf("/");
						if(index > 0){
							String subPath = songPath.substring(0, index);
							if(folderPath.equalsIgnoreCase(subPath)){
								songs.add(song);
							}
						}
					}
				}
			}
		}
		return songs;
	}
	
	/**
	 * 获取所有的歌手
	 * @return
	 */
	public List<Artist> queryLocalArtists(){
		List<Artist> artists = new ArrayList<Artist>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if(db.isOpen()){
			String sql = "SELECT DISTINCT " + Columns.ARTIST  + " FROM " + TABLE_LOCAL_MUSIC;
			Cursor cursor = db.rawQuery(sql, new String[]{});
			while(cursor.moveToNext()){
				Artist artist = new Artist();
				String name = cursor.getString(cursor.getColumnIndex(Columns.ARTIST));
				artist.setName(name);
				artists.add(artist);
			}
			cursor.close();
		}
		return artists;
	}
	
	public List<MusicFolder> queryLocalFolders(){
		List<MusicFolder> folders = new ArrayList<MusicFolder>();
		Map<String, Integer> tempFolders = new HashMap<String, Integer>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if(db.isOpen()){
			String sql = "SELECT " + Columns.SONG_PATH + " FROM " + TABLE_LOCAL_MUSIC;
			Cursor cursor = db.rawQuery(sql, new String[]{});
			while(cursor.moveToNext()){
				String songPath = cursor.getString(cursor.getColumnIndex(Columns.SONG_PATH));
				if(!TextUtils.isEmpty(songPath)){
					int index = songPath.lastIndexOf(File.separator);
					if(index > 0){
						String subPath = songPath.substring(0, index + 1);
						int count = tempFolders.get(subPath) + 1;
						tempFolders.put(subPath, count);
					}
				}
			}
			if(tempFolders.size() > 0){
				for(Map.Entry<String, Integer> entry : tempFolders.entrySet()){
					String folderPath = entry.getKey();
					String folderTitle = null;
					int index = folderPath.lastIndexOf(File.separator);
					if(index > 0){
						folderTitle = folderPath.substring(index);
					}
					int count = entry.getValue();
					MusicFolder folder = new MusicFolder(folderTitle, folderPath, count);
					folders.add(folder);
				}
			}
			
		}
		return folders;
	}
	
	public List<Album> queryLocalAlbums(){
		List<Album> albums = new ArrayList<Album>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if(db.isOpen()){
			String sql = "SELECT　DISTINCT " + Columns.ALBUM_TITLE 
								+ " , DISTINCT " + Columns.ARTIST + ", "
								 + Columns.ALBUM_ID + ", "
								+  Columns.ALBUM_DESC 
								+ " FROM " + TABLE_LOCAL_MUSIC;
			Cursor cursor = db.rawQuery(sql, new String[]{});
			while(cursor.moveToNext()){
				String albumId = cursor.getString(cursor.getColumnIndex(Columns.ALBUM_ID));
				String albumTitle = cursor.getString(cursor.getColumnIndex(Columns.ALBUM_TITLE));
				String artist = cursor.getString(cursor.getColumnIndex(Columns.ARTIST));
				String desc = cursor.getString(cursor.getColumnIndex(Columns.ALBUM_DESC));
				Album album = new Album();
				album.setId(albumId);
				album.setTitle(albumTitle);
				album.setDesc(desc);
				List<Artist> artists = new ArrayList<Artist>();
				if (artist != null) {
					String[] arts = artist.split(",");
					if (arts == null) {
						Artist a = new Artist();
						a.setName(artist);
						artists.add(a);
					} else {
						for (String aStr : arts) {
							Artist a = new Artist();
							a.setName(aStr);
							artists.add(a);

						}
					}
				}
				album.setArtists(artists);
				albums.add(album);
			}
			cursor.close();
		}
		
		return albums;
	}
	
	private Song extractSongFromCursor(Cursor cursor) {
		String _id = cursor.getString(cursor.getColumnIndex(Columns._ID));
		String songId = cursor.getString(cursor.getColumnIndex(Columns.SONG_ID));
		String title = cursor.getString(cursor.getColumnIndex(Columns.TITLE));
		String albumId = cursor.getString(cursor.getColumnIndex(Columns.ALBUM_ID));
		String albumTitle = cursor.getString(cursor.getColumnIndex(Columns.ALBUM_TITLE));
		String artist = cursor.getString(cursor.getColumnIndex(Columns.ARTIST));
		String coverPath = cursor.getString(cursor.getColumnIndex(Columns.COVER_PATH));
		String coverUrl = cursor.getString(cursor.getColumnIndex(Columns.COVER_URL));
		String coverHdUrl = cursor.getString(cursor.getColumnIndex(Columns.COVER_HD_URL));
		String albumDesc = cursor.getString(cursor.getColumnIndex(Columns.ALBUM_DESC));
		String songPath = cursor.getString(cursor.getColumnIndex(Columns.SONG_PATH));
		String songUrl = cursor.getString(cursor.getColumnIndex(Columns.SONG_URL));
		String lrcPath = cursor.getString(cursor.getColumnIndex(Columns.LRC_PATH));
		String lrcUrl = cursor.getString(cursor.getColumnIndex(Columns.LRC_URL));
		String audioType = cursor.getString(cursor.getColumnIndex(Columns.AUDIO_TYPE));
		int duration = cursor.getInt(cursor.getColumnIndex(Columns.DURATION));
		String source = cursor.getString(cursor.getColumnIndex(Columns.SOURCE));
		String category = cursor.getString(cursor.getColumnIndex(Columns.CATEGORY));
		
		List<Artist> artists = new ArrayList<Artist>();
		if(!TextUtils.isEmpty(artist)){
			String[] arts = artist.split(",");
			for(String name : arts){
				Artist a = new Artist(null, name);
				artists.add(a);
			}
		}
		
		Album album = new Album();
		album.setId(albumId);
		album.setTitle(albumTitle);
		album.setCoverHdUrl(coverHdUrl);
		album.setCoverPath(coverPath);
		album.setCoverUrl(coverUrl);
		album.setDesc(albumDesc);
		
		Song song = new Song(_id, songId, title, artist, album, songPath, songUrl, lrcPath, lrcUrl, audioType, duration, artists, source, category);
		return song;
	}

	public static class Columns{
		public static final String _ID = "_id";
		public static final String SONG_ID = "song_id";
		public static final String TITLE = "title";
		public static final String TITLE_PINYIN = "title_pinyin";
		public static final String ALBUM_ID = "album_id";
		public static final String ALBUM_TITLE = "album_title";
		public static final String ARTIST = "artist";
		public static final String COVER_PATH = "cover_path";
		public static final String COVER_URL = "cover_url";
		public static final String COVER_HD_URL = "cover_hd_url";
		public static final String ALBUM_DESC = "album_desc";
		public static final String SONG_PATH = "song_path";
		public static final String SONG_URL = "song_url";
		public static final String LRC_PATH = "lrc_path";
		public static final String LRC_URL = "lrc_url";
		public static final String AUDIO_TYPE = "audio_type";
		public static final String DURATION = "duration";
		public static final String SOURCE = "source";
		public static final String CATEGORY = "category";
	}
	
}
