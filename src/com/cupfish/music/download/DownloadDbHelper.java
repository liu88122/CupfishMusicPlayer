package com.cupfish.music.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 下载引擎数据库帮助类,<br/>
 * 该数据库保存任务下载时多个线程分别下载的进度
 * @author <a href="mailto:liu88122@gmail.com">Liu88122</a>
 * @edited @ 2012-11-18 下午7:50:52
 */
public class DownloadDbHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "download.db";
	public static final int VERSION = 1;
	public static final String TABLE_NAME="download";
	
	/* 数据库字段 */
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_DOWNLOAD_URL = "downloadUrl";
	public static final String COLUMN_THREAD_ID="threadId";
	public static final String COLUMN_DOWNLOAD_LENGTH="downloadLength";
	
	private static final String SQL_CREATE="CREATE TABLE IF NOT EXISTS " 
												+ TABLE_NAME + "("
												+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
												+ COLUMN_DOWNLOAD_URL + " VARCHAR(255),"
												+ COLUMN_THREAD_ID + " INTEGER,"
												+ COLUMN_DOWNLOAD_LENGTH + " INTEGER )";
	private static final String SQL_DELETE="DROP TABLE IF EXIST " + TABLE_NAME;
	
	public DownloadDbHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE);
		db.execSQL(SQL_CREATE);
	}

}
