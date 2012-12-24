package com.cupfish.musicplayer.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cupfish.musicplayer.bean.MusicFolder;

public class LocalMusicFolderDao {
	
	private static final String TABBLE_LOCAL_FOLDER = "local_folder";
	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_TITLE = "title";
	private static final String COLUMN_PATH = "path";
	private static final String COLUMN_COUNT = "count";
	public static final String SQL_TABLE_LOCAL_FOLDER_CREATE = "CREATE TABLE IF NOT EXISTS " 
			+ TABBLE_LOCAL_FOLDER + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ COLUMN_TITLE + " VARCHAR(255),"
			+ COLUMN_PATH + " VARCHAR(255),"
			+ COLUMN_COUNT + " INTEGER)";
	
	public void insert(SQLiteDatabase db, MusicFolder folder){
		String sql = "INSERT INTO " + TABBLE_LOCAL_FOLDER 
				+ " ( " + COLUMN_TITLE + "," 
				+ COLUMN_PATH + "," 
				+ COLUMN_COUNT  
				+ ") VALUES(?,?,?)";
		Object[] params = {folder.getTitle(), folder.getPath(), folder.getCount()};
		if(db.isOpen()){
			db.execSQL(sql, params);
		}
	}
	
	public List<MusicFolder> queryAll(SQLiteDatabase db){
		List<MusicFolder> folders = new ArrayList<MusicFolder>();
		String sql = "SELECT * FROM " + TABBLE_LOCAL_FOLDER;
		if(db.isOpen()){
			Cursor cursor = db.rawQuery(sql, new String[]{});
			while(cursor.moveToNext()){
				String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
				String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
				int count = cursor.getInt(cursor.getColumnIndex(COLUMN_COUNT));
				MusicFolder folder = new MusicFolder(title, path, count);
				folders.add(folder);
			}
			cursor.close();
		}
		return folders;
		
	}
	
	public void deleteAll(SQLiteDatabase db){
		String sql = "DELETE FROM " + TABBLE_LOCAL_FOLDER;
		if(db.isOpen()){
			db.execSQL(sql);
		}
	}
	
}
