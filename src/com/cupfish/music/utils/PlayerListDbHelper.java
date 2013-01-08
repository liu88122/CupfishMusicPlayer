package com.cupfish.music.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PlayerListDbHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "playerlist.db";
	private static final int VERSION = 1;
	
	public PlayerListDbHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE playlist(id integer, title varchar(20), album varchar(20), albumid varchar(20),  artist varchar(20), url varchar(255), duration long, islocal integer)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
