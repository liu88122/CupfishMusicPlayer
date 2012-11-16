package com.cupfish.musicplayer.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore.Audio.Playlists;

import com.cupfish.musicplayer.domain.Song;
import com.cupfish.musicplayer.utils.PlayerListDbHelper;

public class PlayerListDao {

	private static PlayerListDbHelper helper;
	private SQLiteDatabase db;

	public PlayerListDao(Context context) {
		helper = new PlayerListDbHelper(context);
	}

	public void insertPlayList(List<Song> playlist) {
		try {

			db = helper.getWritableDatabase();
			if (db.isOpen()) {
				String sql = "insert into playlist (id, title, album, albumid, artist, url, duration, islocal) values(?,?,?,?,?,?,?,?)";
				for (Song song : playlist) {
					Object[] params = { song.getId(), song.getTitle(), song.getAlbum(), song.getAlbumId(), song.getAuthorList(), song.getUrl(), song.getDuration(),
							song.isLocal()?1:0 };
					db.execSQL(sql, params);
				}
				db.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteAll() {
		try {
			db = helper.getWritableDatabase();
			if (db.isOpen()) {
				String sql = "delete from playlist";
				db.execSQL(sql);
				db.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Song> getPlaylist(){
		List<Song> result = new ArrayList<Song>();
		db = helper.getWritableDatabase();
		if(db.isOpen()){
			try{
				String sql = "SELECT * FROM playlist";
				String[] params = { };
				Cursor cursor = db.rawQuery(sql, params);
				Song song = null;
				//(id integer, title varchar(20), album varchar(20), albumid varchar(20), 
				//artist varchar(20), url varchar(255), duration integer, islocal integer)
				while(cursor.moveToNext()){
					System.out.println(cursor.getColumnCount());
					String id = cursor.getString(cursor.getColumnIndex("id"));
					String title = cursor.getString(cursor.getColumnIndex("title"));
					String album = cursor.getString(cursor.getColumnIndex("album"));
					String albumId = cursor.getString(cursor.getColumnIndex("albumid"));
					String artist = cursor.getString(cursor.getColumnIndex("artist"));
					String url = cursor.getString(cursor.getColumnIndex("url"));
					long duration = cursor.getLong(cursor.getColumnIndex("duration"));
					int isLocal = cursor.getInt(cursor.getColumnIndex("islocal"));
					
					//TODO artistList已经发生变化(原来是String，现在改为List)，相应的方法都需要修改
					song = new Song(id, title, album, albumId, null, url, "", "", duration, isLocal==1?true:false);
					result.add(song);
				}
				cursor.close();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				db.close();
			}
		}
		return result;
	}
}
