package com.cupfish.music.utils;

import com.cupfish.music.dao.LocalMusicFolderDao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicDbHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "music.db";
	private static final int DB_VERSION = 1;

	public MusicDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(LocalMusicFolderDao.SQL_TABLE_LOCAL_FOLDER_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
