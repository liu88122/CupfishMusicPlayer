package com.cupfish.musicplayer.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.text.TextUtils;
import android.util.Log;

import com.cupfish.musicplayer.domain.Song;
import com.cupfish.musicplayer.exception.NetTimeoutException;
import com.cupfish.musicplayer.global.Constants;

public class GoogleMusicHelper {

	private static final String BASE_URL = "http://www.google.cn/music/chartlisting";
	// http://www.google.cn/music/top100/musicdownload?id=Sb016686efbe36864
	private static final String DOWNLOAD_BASE_URL = "http://www.google.cn/music/top100/musicdownload";

	// q=chinese_new_songs_cn, ea_new_songs_cn, chinese_songs_cn, jk_songs_cn,
	// pop_songs_cn, rock_songs_cn

	private static final String CAT_TYPE = "song";

	private static final String PARAM_SONG_TYPE = "q";
	private static final String PARAM_CAT = "cat";
	private static final String PARAM_START = "start";

	private static final String TAG = "GoogleMusicHelper";

	public static ArrayList<Song> getSongsFromGoogle(String topType, int startIndex) throws NetTimeoutException {
		
		ArrayList<Song> mSongs = new ArrayList<Song>();
		if (TextUtils.isEmpty(topType)) {
			return null;
		}
		String url = BASE_URL + "?q=" + topType + "&cat=song";
		if (startIndex > 0) {
			url = url + "&start=" + startIndex;
		}
		Log.i(TAG, url);
		try {

			HttpGet get = new HttpGet(url);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream is = response.getEntity().getContent();
				File file = new File("/mnt/sdcard/temp.html");

				FileOutputStream fos = new FileOutputStream(file);
				int len = -1;
				byte[] b = new byte[1024 * 4];
				while ((len = is.read(b)) != -1) {
					fos.write(b, 0, len);
				}
				fos.close();
				Document document = Jsoup.parse(file, null);
				Elements songItemEles = document.select("tbody");

				// Element songItemEle = songItemEles.get(10);
				Song song = null;
				for (Element songItemEle : songItemEles) {
					String id = null;
					String title = null;
					String artist = null;
					Elements tdEles = songItemEle.select("td");
					if (tdEles == null) {
						continue;
					}
					for (Element tdEle : tdEles) {
						String className = tdEle.attr("class");
						if (className != null && className.contains("Title")) {
							//获取title信息
							Element aEle = tdEle.select("a").first();
							if (aEle != null) {
								title = aEle.text();
							} else {
								continue;
							}
						} else if (className != null && className.contains("Checkbox")) {
							//获取id信息
							Element inputEle = tdEle.select("input").first();
							if (inputEle != null) {
								id = inputEle.attr("value");
							} else {
								continue;
							}
						} else if (className != null && className.contains("Artist")) {
							//获取Artist信息
							Element spanEle = tdEle.select("span").first();
							if (spanEle != null) {
								artist = spanEle.text();
							} else if (tdEle.select("a").first() != null) {
								artist =  tdEle.select("a").first().text();
							} else {
								continue;
							}
						}  else {
							continue;
						}
					}
					if(!TextUtils.isEmpty(id) && !TextUtils.isEmpty(title) && !TextUtils.isEmpty(artist)){
						song = new Song();
						song.setId(id);
						song.setTitle(title);
//						song.setArtist(artist);
						mSongs.add(song);
					}
				}
				return mSongs;
			} else {
				System.out.println("oh, No");
			}

		} catch (Exception e) {
			throw new NetTimeoutException(e);
		}
		return null;
	}

	// http://g.top100.cn/16667639/html/download.html?id=Sa7451c7414df873c
	public static String getDownloadUrlById(String id) throws NetTimeoutException {
		try {
			String url = DOWNLOAD_BASE_URL + "?id=" + id;
			HttpGet get = new HttpGet(url);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream is = response.getEntity().getContent();
				File file = new File("/mnt/sdcard/temp3.html");

				FileOutputStream fos = new FileOutputStream(file);
				int len = -1;
				byte[] b = new byte[1024 * 4];
				while ((len = is.read(b)) != -1) {
					fos.write(b, 0, len);
					// System.out.println(new String(b, 0 , len));
				}
				fos.close();
				Document document = Jsoup.parse(file, "utf-8");
				Element divEle = document.select("div.download").first();
				Elements aEles = divEle.select("a");
				for (Element aEle : aEles) {
					String hrefStr = aEle.attr("href");
					if (hrefStr != null && hrefStr.contains("mp3")) {
						String download = hrefStr.substring(hrefStr.indexOf("http"), hrefStr.indexOf("mp3") + 3);
						download = download.replace("%25", "%");
						System.out.println(download);
						return download;
					}
				}
			}
		} catch (Exception e) {
			throw new NetTimeoutException(e);
		}
		return null;
	}

	public static void play(String url) {
		try {
			// 用于播放mp3
			MediaPlayer mp = new MediaPlayer();
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.setDataSource(url);
			mp.prepareAsync();
			mp.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					if (percent > 10) {
						mp.start();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getTopListTypeByName(String name) {
		if (!TextUtils.isEmpty(name)) {
			if(Constants.GOOGLE_MUSIC_LIST[0].equals(name)){
				return TopListType.CHINESE_NEW_SONGS;
			}else if(Constants.GOOGLE_MUSIC_LIST[1].equals(name)){
				return TopListType.EA_NEW_SONGS;
			}else if(Constants.GOOGLE_MUSIC_LIST[2].equals(name)){
				return TopListType.CHINESE_HOT_SONGS;
			}else if(Constants.GOOGLE_MUSIC_LIST[3].equals(name)){
				return TopListType.JK_HOT_SONGS;
			}else if(Constants.GOOGLE_MUSIC_LIST[4].equals(name)){
				return TopListType.POP_SONGS;
			}else if(Constants.GOOGLE_MUSIC_LIST[5].equals(name)){
				return TopListType.ROCK_SONGS;
			}
		}
		return null;
	}

	public static class TopListType {
		public static final String CHINESE_NEW_SONGS = "chinese_new_songs_cn";
		public static final String EA_NEW_SONGS = "ea_new_songs_cn";
		public static final String CHINESE_HOT_SONGS = "chinese_songs_cn";
		public static final String JK_HOT_SONGS = "jk_songs_cn";
		public static final String POP_SONGS = "pop_songs_cn";
		public static final String ROCK_SONGS = "rock_songs_cn";
	}
}
