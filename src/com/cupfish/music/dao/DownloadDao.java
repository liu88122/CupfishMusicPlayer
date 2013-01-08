package com.cupfish.music.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cupfish.music.download.DownloadDbHelper;

/**
 * 下载引擎数据库操作类
 * @author <a href="mailto:liu88122@gmail.com">Liu88122</a>
 * @edited @ 2012-11-18 下午9:28:58
 */
public class DownloadDao {

	private DownloadDbHelper mDownloadDbHelper;
	
	public DownloadDao(Context context){
		mDownloadDbHelper = new DownloadDbHelper(context);
	}
	
	/**
	 * 插入操作，用于初始化，此时每个线程的下载长度均为0
	 * @param downloadUrl 下载Url
	 * @param threadId 线程ID
	 * void
	 */
	public void insert(String downloadUrl, int threadId){
		SQLiteDatabase db = mDownloadDbHelper.getWritableDatabase();
		if (db.isOpen()) {
			try {
				ContentValues values = new ContentValues();
				values.put(DownloadDbHelper.COLUMN_DOWNLOAD_URL, downloadUrl);
				values.put(DownloadDbHelper.COLUMN_THREAD_ID, threadId);
				values.put(DownloadDbHelper.COLUMN_DOWNLOAD_LENGTH, 0);
				db.insertWithOnConflict(DownloadDbHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				db.close();
			}
		}
	}
	
	/**
	 * 根据下载Url和线程ID获取该线程已下载的长度(位置)
	 * @param downloadUrl 下载Url
	 * @param threadId 线程ID
	 * @return
	 * int  当前线程下载的长度
	 *
	 */
	public int getDownloadLength(String downloadUrl, int threadId){
		SQLiteDatabase db = mDownloadDbHelper.getReadableDatabase();
		try{
			Cursor cursor = db.query(DownloadDbHelper.TABLE_NAME, 
					new String[]{DownloadDbHelper.COLUMN_DOWNLOAD_LENGTH}, 
					DownloadDbHelper.COLUMN_DOWNLOAD_URL + "=? AND " + DownloadDbHelper.COLUMN_THREAD_ID +"=?",
					new String[]{downloadUrl, String.valueOf(threadId)}, 
					null, null, null);
			if(cursor.moveToFirst()){
				return cursor.getInt(cursor.getColumnIndexOrThrow(DownloadDbHelper.COLUMN_DOWNLOAD_LENGTH));
			}
		}catch(Exception e){
			e.printStackTrace();
		} finally{
			db.close();
		}
		return -1;
	}
	
	/**
	 * 返回下载url对应的线程数
	 * @param downloadUrl 下载Url
	 * @return
	 * int url对应用到的线程数
	 *
	 */
	public int getThreadNum(String downloadUrl){
		SQLiteDatabase db = mDownloadDbHelper.getReadableDatabase();
		try{
			Cursor cursor = db.query(DownloadDbHelper.TABLE_NAME,
					new String[]{DownloadDbHelper.COLUMN_ID},
					DownloadDbHelper.COLUMN_DOWNLOAD_URL + "=?",
					new String[]{downloadUrl}, null, null, null);
			return cursor.getCount();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.close();
		}
		return -1;
	}
	
	/**
	 * 更新downloadUrl 对应threadId下载的最新长度
	 * @param downloadUrl 下载 Url
	 * @param threadId 线程ID
	 * @param downloadLength 最新的下载长度 
	 */
	public void update(String downloadUrl, int threadId, int downloadLength,SQLiteDatabase db){
		if (db != null && db.isOpen()) {
			try {
				ContentValues values = new ContentValues();
				values.put(DownloadDbHelper.COLUMN_DOWNLOAD_LENGTH, downloadLength);
				String where = DownloadDbHelper.COLUMN_DOWNLOAD_URL + "=? AND " + DownloadDbHelper.COLUMN_THREAD_ID + "=? ";
				db.update(DownloadDbHelper.TABLE_NAME, values, where, new String[] { downloadUrl, String.valueOf(threadId) });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 下载完成或其他异常情况删除下载url相关的记录
	 * @param downloadUrl 下载Url
	 */
	public void delete(String downloadUrl){
		SQLiteDatabase db = mDownloadDbHelper.getWritableDatabase();
		try{
			String where = DownloadDbHelper.COLUMN_DOWNLOAD_URL + "=?";
			db.delete(DownloadDbHelper.TABLE_NAME, where, new String[]{downloadUrl});
		}catch(Exception e){
			e.printStackTrace();
		} finally{
			db.close();
		}
	}
	
	
}
