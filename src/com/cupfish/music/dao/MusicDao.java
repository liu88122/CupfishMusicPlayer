package com.cupfish.music.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.cupfish.music.bean.Album;
import com.cupfish.music.bean.Artist;
import com.cupfish.music.bean.MusicFolder;
import com.cupfish.music.bean.Song;
import com.cupfish.music.utils.MusicDbHelper;

public class MusicDao {
	
	public static final String TABLE_LOCAL_MUSIC = "local_music";

	private static final String TAG = MusicDao.class.getSimpleName();
	
	private MusicDbHelper mDbHelper;
	
	public MusicDao(Context context){
		mDbHelper = new MusicDbHelper(context);
	}
	
	/**
	 * 创建相应的表
	 * @param db
	 * void
	 * @author <a href="liu88122@gmail.com">L iuZhongde</a>
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
						+ Columns.CATEGORY + " VARCHAR, "
						+ Columns.RANK + " INTEGER "
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
				db.close();
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
	
	/**
	 * 插入歌曲，
	 * 如果歌曲已经存在，则删除插入最新的
	 * @param db
	 * @param song
	 * @param table
	 */
	private void insert(SQLiteDatabase db, Song song, String table){
		
		if(song == null){
			return;
		}
		
		if(db!= null && db.isOpen()){
			String artist = artist2String(song.getArtists());
			Song oldSong = querySongByTitleArtistAndAudioType(song.getTitle(), artist, song.getAudioType());
			if(oldSong != null){
				deleteSong(db, oldSong);
			}
			String sql = "INSERT INTO " + table + "(" 
						+ Columns.SONG_ID + ", "
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
						+ Columns.CATEGORY + ", "
						+ Columns.RANK
						+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
							song.getCategory(),
							song.getRank()
							};
			db.execSQL(sql, params);
		}
	}
	
	
	public void update(SQLiteDatabase db, String title, String artist, String audioType, ContentValues values){
		if(TextUtils.isEmpty(title)){
			return;
		}
		if (db.isOpen()) {
			String where = null;
			String[] args = null;
			
			if(TextUtils.isEmpty(artist)){
				where = Columns.TITLE + " = ? ";
				args = new String[]{ title };
			} else if(TextUtils.isEmpty(audioType)){
				where = Columns.TITLE + " = ? AND " + Columns.ARTIST + " = ?";
				args = new String[]{ title , artist };
			} else{
				where = Columns.TITLE + " = ? AND " 
						+ Columns.ARTIST + " = ? AND "
						+ Columns.AUDIO_TYPE + " = ? ";
				args = new String[]{ title, artist, audioType };
			}
			
			db.update(TABLE_LOCAL_MUSIC, values, where, args);
		}
	}
	
	public void updateSong(Song oldSong, Song newSong){
		String oldTitle = oldSong.getTitle();
		String newTitle = newSong.getTitle();
		if(TextUtils.isEmpty(oldTitle) || TextUtils.isEmpty(newTitle) || !oldTitle.equalsIgnoreCase(newTitle)){
			return;
		}
		ContentValues values = new ContentValues();
		
		String _id = newSong.get_id();
		if(!TextUtils.isEmpty(_id)){
			values.put(Columns._ID, _id);
		}
		
		Album album = newSong.getAlbum();
		if(album != null){
			String albumId = album.getId();
			String albumTitle = album.getTitle();
			String coverPath = album.getCoverPath();
			String coverUrl = album.getCoverUrl();
			String coverHdUrl = album.getCoverHdUrl();
			String albumDesc = album.getDesc();
			if(!TextUtils.isEmpty(albumId)){
				values.put(Columns.ALBUM_ID, albumId);
			}
			if(!TextUtils.isEmpty(albumTitle)){
				values.put(Columns.ALBUM_TITLE, albumTitle);
			}
			if(!TextUtils.isEmpty(coverPath)){
				values.put(Columns.COVER_PATH, coverPath);
			}
			if(!TextUtils.isEmpty(coverUrl)){
				values.put(Columns.COVER_URL, coverUrl);
			}
			if(!TextUtils.isEmpty(coverHdUrl)){
				values.put(Columns.COVER_HD_URL, coverHdUrl);
			}
			if(!TextUtils.isEmpty(albumDesc)){
				values.put(Columns.ALBUM_DESC, albumDesc);
			}
		}
		
		List<Artist> artists = newSong.getArtists();
		if(artists!= null && artists.size() > 0){
			String artist = artist2String(artists);
			values.put(Columns.ARTIST, artist);
		}
		
		String lrcPath = newSong.getLrcPath();
		if(!TextUtils.isEmpty(lrcPath)){
			values.put(Columns.LRC_PATH, lrcPath);
		}
		
		String lrcUrl = newSong.getLrcPath();
		if(!TextUtils.isEmpty(lrcUrl)){
			values.put(Columns.LRC_URL, lrcUrl);
		}
		
		String songId = newSong.getSongId();
		if (!TextUtils.isEmpty(songId)) {
			values.put(Columns.SONG_ID, songId);
		}
		
		String songPath = newSong.getSongPath();
		if (!TextUtils.isEmpty(songPath)) {
			values.put(Columns.SONG_PATH, songPath);
		}
		
		String songUrl = newSong.getSongUrl();
		if (!TextUtils.isEmpty(songUrl)) {
			values.put(Columns.SONG_URL, songUrl);
		}
		
		String source = newSong.getSource();
		if (!TextUtils.isEmpty(source)) {
			values.put(Columns.SOURCE, source);
		}
		
		String category = newSong.getCategory();
		if (!TextUtils.isEmpty(category)) {
			values.put(Columns.CATEGORY, category);
		}
		
		String audioType = newSong.getAudioType();
		if (!TextUtils.isEmpty(audioType)) {
			values.put(Columns.AUDIO_TYPE, audioType);
		}
		
		int rank = newSong.getRank();
		if (rank > 0) {
			values.put(Columns.RANK, rank);
		}
		
		long duration = newSong.getDuration();
		if (duration > 0) {
			values.put(Columns.DURATION, duration);
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if(db.isOpen()){
			update(db, oldTitle, artist2String(oldSong.getArtists()), oldSong.getAudioType(), values);
			db.close();
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
	public Song querySongById(Context context, String id){
		SQLiteDatabase db = new MusicDbHelper(context).getReadableDatabase();
		Song song = null;
		if(db.isOpen()){
			String sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC 
							+ " WHERE " + Columns._ID + " = ? " ;
			String[] params = { id };
			Cursor cursor = db.rawQuery(sql, params);
			if(cursor.moveToFirst()){
				song = extractSongFromCursor(cursor);
				cursor.close();
			}
		}
		db.close();
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
	public Song querySongBySongId(Context context, String songId){
		SQLiteDatabase db = new MusicDbHelper(context).getReadableDatabase();
		Song song = null;
		if(db.isOpen()){
			String sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC 
							+ " WHERE " + Columns.SONG_ID + " = ? " ;
			String[] params = { songId };
			Cursor cursor = db.rawQuery(sql, params);
			if(cursor.moveToFirst()){
				song = extractSongFromCursor(cursor);
				cursor.close();
			}
			db.close();
		}
		return song;
	}
	
	/**
	 * 根据歌曲名 歌手和歌曲类型获取歌曲
	 * @return
	 * Song
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-9 下午5:15:19
	 */
	public Song querySongByTitleArtistAndAudioType(String songTitle, String artistName, String audioType){
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
		}else if(TextUtils.isEmpty(audioType)){
			sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC
					+ " WHERE " + Columns.TITLE 
					+ " = ? AND " + Columns.ARTIST + " = ?";
			params = new String[]{ songTitle, artistName };
		} else {
			sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC
					+ " WHERE " + Columns.TITLE + " =  ?" 
					+ " AND " + Columns.ARTIST + " = ?" 
					+ " AND " + Columns.AUDIO_TYPE + " = ?";
			params = new String[]{ songTitle, artistName, audioType };
		}
		Cursor cursor = db.rawQuery(sql, params);
		if(cursor.moveToFirst()){
			song = extractSongFromCursor(cursor);
			cursor.close();
		}
		db.close();
		return song;
	}
	
	/**
	 * 查找所有本地歌曲
	 * @return
	 * List<Song>
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-9 上午11:12:51
	 */
	public ArrayList<Song> queryAllLocalSongs(){
		ArrayList<Song> songs = new ArrayList<Song>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC 
						+ " WHERE " + Columns.SONG_PATH + " IS NOT NULL";
		Cursor cursor = db.rawQuery(sql, new String[]{});
		while(cursor.moveToNext()){
			Song song = extractSongFromCursor(cursor);
			songs.add(song);
		}
		cursor.close();
		db.close();
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
	public ArrayList<Song> queryLocalSongsByArtist(Artist artist){
		if(artist == null || TextUtils.isEmpty(artist.getName())){
			return null;
		}
		ArrayList<Song> songs = new ArrayList<Song>();
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
			db.close();
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
	public ArrayList<Song> queryLocalSongsByAlbum(Album album){
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
		ArrayList<Song> songs = new ArrayList<Song>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if(db.isOpen()){
			String sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC + " WHERE " + Columns.ALBUM_TITLE + " = ? AND " + Columns.ARTIST + " = ?";
			String[] params = null;
			if (artistsBuilder.length() == 0) {
				params = new String[] { album.getTitle(), null };
			} else {
				params = new String[] { album.getTitle(), artistsBuilder.toString()};
			}
			Cursor cursor = db.rawQuery(sql, params);
			while(cursor.moveToNext()){
				Song song = extractSongFromCursor(cursor);
				songs.add(song);
			}
			cursor.close();
			db.close();
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
	public ArrayList<Song> queryLocalSongsByFolder(String folderPath){
		if(TextUtils.isEmpty(folderPath)){
			return null;
		}
		ArrayList<Song> songs = new ArrayList<Song>();
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
			cursor.close();
			db.close();
		}
		return songs;
	}
	
	/**
	 * 根据歌曲来源和分类，以及排行位置
	 * @param source
	 * @param category
	 * @param start
	 * @param end
	 * @return
	 * List<Song>
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-11 上午11:01:09
	 */
	public ArrayList<Song> querySongsBySourceAndCategory(String source, String category, int start, int end){
		if(TextUtils.isEmpty(source) || TextUtils.isEmpty(category)){
			return null;
		}
		if(start <= 0 || end <= 0 || start > end){
			Log.i(TAG, "RANK RANGE INVALID");
			return null;
		}
		ArrayList<Song> songs = new ArrayList<Song>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if(db.isOpen()){
			String sql = "SELECT * FROM " + TABLE_LOCAL_MUSIC
							 + " WHERE " + Columns.SOURCE + " = ? AND "
							 + Columns.CATEGORY + " = ? AND "
							 + Columns.RANK + " BETWEEN ? AND ?";
			String[] args = {source, category, String.valueOf(start), String.valueOf(end)};
			Cursor cursor = db.rawQuery(sql, args);
			while(cursor.moveToNext()){
				Song song = extractSongFromCursor(cursor);
				songs.add(song);
			}
			cursor.close();
			db.close();
		}
		return songs;
	}
	
	
	/**
	 * 获取所有的歌手
	 * @return
	 */
	public ArrayList<Artist> queryLocalArtists() {
		Set<Artist> artists = new HashSet<Artist>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if (db.isOpen()) {
			String sql = "SELECT DISTINCT " + Columns.ARTIST + " FROM " + TABLE_LOCAL_MUSIC;
			Cursor cursor = db.rawQuery(sql, new String[] {});
			while (cursor.moveToNext()) {
				String artistStr = cursor.getString(cursor.getColumnIndex(Columns.ARTIST));
				if (TextUtils.isEmpty(artistStr)) {
					continue;
				}
				String[] temp = artistStr.split(",");
				if (temp == null || temp.length == 0) {
					Artist a = new Artist();
					a.setName(artistStr);
					artists.add(a);
				} else {
					for (String str : temp) {
						Artist a = new Artist();
						a.setName(str);
						artists.add(a);
					}
				}
			}
			cursor.close();
			db.close();
		}
		ArrayList<Artist> artistList = new ArrayList<Artist>();
		artistList.addAll(artists);
		
		return artistList;
	}
	
	/**
	 * 获取所有音乐文件夹
	 * @return
	 * List<MusicFolder>
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-10 下午2:07:20
	 */
	public ArrayList<MusicFolder> queryLocalFolders(){
		ArrayList<MusicFolder> folders = new ArrayList<MusicFolder>();
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
						Integer count = tempFolders.get(subPath);
						if(count == null){
							count = 1;
						} else {
							count++;
						}
						tempFolders.put(subPath, count);
					}
				}
			}
			cursor.close();
			db.close();
			if(tempFolders.size() > 0){
				for(Map.Entry<String, Integer> entry : tempFolders.entrySet()){
					String folderPath = entry.getKey();
					String folderTitle = null;
					
					//
					folderPath = folderPath.substring(0, folderPath.length() - 1);
					
					int index = folderPath.lastIndexOf(File.separator);
					if(index > 0 && (index + 1 <folderPath.length())){
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
	
	/**
	 * 获取所有的专辑
	 * @return
	 * List<Album>
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-10 下午2:07:55
	 */
	public ArrayList<Album> queryLocalAlbums(){
		ArrayList<Album> albums = new ArrayList<Album>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		if(db.isOpen()){
			String sql = "SELECT " + Columns.ALBUM_TITLE + " , " 
								+ Columns.ARTIST + ", "
								+ Columns.ALBUM_ID + ", "
								+  Columns.ALBUM_DESC 
								+ " FROM " + TABLE_LOCAL_MUSIC
								+ " GROUP BY " + Columns.ALBUM_TITLE;
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
			db.close();
		}
		
		return albums;
	}
	
	
	public void deleteSong(SQLiteDatabase db, Song song){
		if(song == null){
			return;
		}
		if(db.isOpen()){
			String sql = null;
			Object[] params = null;
			List<Artist> artists = song.getArtists();
			String artist = artist2String(artists);
			if(song.get_id() != null){
				sql = "DELETE FROM " + TABLE_LOCAL_MUSIC
						+ " WHERE " + Columns._ID + " = ?";
				params = new Object[]{ song.get_id() };
			} else if (!TextUtils.isEmpty(song.getTitle()) && TextUtils.isEmpty(artist)) {
				sql = "DELETE FROM " + TABLE_LOCAL_MUSIC
						+ " WHERE " + Columns.TITLE + " = ?";
				params = new Object[]{ song.getTitle()};
			} else {
				sql = "DELETE FROM " + TABLE_LOCAL_MUSIC
						+ " WHERE " + Columns.TITLE + " = ? AND "
						+ Columns.ARTIST + " = ?";
				params = new Object[]{song.getTitle(), artist};
			}
			db.execSQL(sql, params);
		}
	}
	
	private String artist2String(List<Artist> artists){
		String artist = null;
		if(artists != null && artists.size() > 0){
			Iterator<Artist> iterator = artists.iterator();
			StringBuilder sb = new StringBuilder();
			while(iterator.hasNext()){
				sb.append(iterator.next().getName() + ",");
			}
			sb.deleteCharAt(sb.length() -1);
			artist = sb.toString();
		}
		return artist;
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
		int rank = cursor.getInt(cursor.getColumnIndex(Columns.RANK));
		
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
		
		Song song = new Song(_id, songId, title, artist, album, songPath, songUrl, lrcPath, lrcUrl, audioType, duration, artists, source, category, rank);
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
		public static final String RANK = "rank";
	}
	
}
