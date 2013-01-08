package com.cupfish.music.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.cupfish.music.bean.Album;
import com.cupfish.music.bean.Artist;
import com.cupfish.music.bean.Song;
import com.cupfish.music.utils.MusicDbHelper;

public class MusicDao {
	
	public static final String TABLE_LOCAL_MUSIC = "local_music";
	public static final String TABLE_ONLINE_MUSIC = "online_music";
	
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
			createTable(db, TABLE_ONLINE_MUSIC);
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
	public void insertLocalSongs(SQLiteDatabase db, List<Song> songs) {
		if (db != null && db.isOpen()) {
			if (songs != null && songs.size() > 0) {
				Iterator<Song> iterator = songs.iterator();
				while (iterator.hasNext()) {
					insertLocalSong(db, iterator.next());
				}
			}
		}
	}
	
	/**
	 * 插入在线歌曲集合到数据库
	 * @param db
	 * @param songs
	 * void
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-8 下午4:58:53
	 */
	public void insertOnlineSongs(SQLiteDatabase db, List<Song> songs) {
		if (db != null && db.isOpen()) {
			if (songs != null && songs.size() > 0) {
				Iterator<Song> iterator = songs.iterator();
				while (iterator.hasNext()) {
					insertOnlineSong(db, iterator.next());
				}
			}
		}
	}
	
	/**
	 * 插入本地歌曲到数据库
	 * @param db
	 * @param song
	 * void
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-8 下午4:59:09
	 */
	public void insertLocalSong(SQLiteDatabase db, Song song){
		insert(db, song, TABLE_LOCAL_MUSIC);
	}
	
	/**
	 * 插入在线歌曲到数据库
	 * @param db
	 * @param song
	 * void
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-1-8 下午4:59:21
	 */
	public void insertOnlineSong(SQLiteDatabase db, Song song){
		insert(db, song, TABLE_ONLINE_MUSIC);
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
	
	private Song extractSongFromCursor(Cursor cursor) {
		String _id = cursor.getString(cursor.getColumnIndex(Columns._ID));
		String songId = cursor.getString(cursor.getColumnIndex(Columns.SONG_ID));
		String title = cursor.getString(cursor.getColumnIndex(Columns.TITLE));
		String titlePinyin = cursor.getString(cursor.getColumnIndex(Columns.TITLE_PINYIN));
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
		
		Song song = new Song(_id, songId, albumTitle, artist, album, songPath, songUrl, lrcPath, lrcUrl, audioType, duration, artists, source, category);
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
