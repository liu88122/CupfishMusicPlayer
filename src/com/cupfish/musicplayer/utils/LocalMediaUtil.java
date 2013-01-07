package com.cupfish.musicplayer.utils;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.bean.Album;
import com.cupfish.musicplayer.bean.Artist;
import com.cupfish.musicplayer.bean.Song;

public class LocalMediaUtil {

	private static final String TAG = "LocalMediaUtil";
	private static final Uri mArtworkUri = Uri.parse("content://media/external/audio/albumart");
	private static final BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();

	public static List<Song> getLocalSongs(Context context) {

		List<Song> localSongs = new ArrayList<Song>();

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return localSongs;
		}

		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
		Song song;
		while (cursor.moveToNext()) {
			String songId = "" + cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
			String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
			String albumTitle = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
			String albumId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
			String artistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
			String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
			
			//for special songs (duo mi)
			if(!TextUtils.isEmpty(title)){
				String[] temp = title.split("_");
				if(temp!= null && temp.length == 2){
					title = temp[0];
					artistName = temp[1];
				}
			}
			
			Album album = new Album();
			album.setId(albumId);
			album.setTitle(albumTitle);
			
			Artist artist = new Artist();
			artist.setName(artistName);
			ArrayList<Artist> artists = new ArrayList<Artist>();
			artists.add(artist);
			song = new Song();
			song.setsId(songId);
			song.setTitle(title);
			song.setsPath(url);
			song.setAlbum(album);
			song.setArtists(artists);
			localSongs.add(song);
			song = null;
		}
		cursor.close();
		//对结果进行排序
		Collections.sort(localSongs);
		return localSongs;
	}

	/**
	 * Get album art for specified album. You should not pass in the album id
	 * for the "unknown" album here (use -1 instead)
	 */
	public static Bitmap getArtwork(Context context, long song_id, long album_id) {

		if (album_id < 0) {
			// This is something that is not in the database, so get the album
			// art directly
			// from the file.
			if (song_id >= 0) {
				Bitmap bm = getArtworkFromFile(context, song_id, -1);
				if (bm != null) {
					return bm;
				}
			}
			
			return null;
		}

		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(mArtworkUri, album_id);
		if (uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, mBitmapOptions);
			} catch (FileNotFoundException ex) {
				// The album art thumbnail does not actually exist. Maybe the
				// user deleted it, or
				// maybe it never existed to begin with.
				Bitmap bm = getArtworkFromFile(context, song_id, album_id);
				if (bm != null) {
					if (bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
					}
				}
				return bm;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
				}
			}
		}

		return null;
	}


	private static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
		Bitmap bm = null;

		if (albumid < 0 && songid < 0) {
			throw new IllegalArgumentException("Must specify an album or a song id");
		}

		try {
			if (albumid < 0) {
				Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}
			} else {
				Uri uri = ContentUris.withAppendedId(mArtworkUri, albumid);
				ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}
			}
		} catch (FileNotFoundException ex) {
			//
		}
		return bm;
	}

	private static Bitmap getDefaultArtwork(Context context) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.music_album_default), null, opts);
	}
}
