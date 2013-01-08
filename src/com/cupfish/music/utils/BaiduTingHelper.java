package com.cupfish.music.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.cupfish.music.bean.Album;
import com.cupfish.music.bean.Artist;
import com.cupfish.music.bean.Song;
import com.cupfish.music.exception.NetTimeoutException;
import com.cupfish.music.global.Constants;

public class BaiduTingHelper {

	private static final String BASE_URL = "http://music.baidu.com";
	private static final String TOP_BASE_URL = BASE_URL + "/top";
	private static final String TOP_NEW_BASE_URL = BASE_URL + "/top/new";
	private static final String ALBUM_BASE_URL = BASE_URL + "/album";
	private static final String SONG_BASE_URL = BASE_URL + "/song";
	private static final String SEARCH_BASE_URL = BASE_URL + "/search";
	/* 连接超时时间 */
	private static final int TIME_OUT = 10000;

	public static List<Song> getSongsFromBaidu(String topType) throws NetTimeoutException {
		List<Song> songs = new ArrayList<Song>();
		if (TextUtils.isEmpty(topType)) {
			return null;
		}
		// http://music.baidu.com/top/dayhot
		String urlStr = TOP_BASE_URL + "/" + topType;
		// String url = "http://www.baidu.com";
		int code;
		try {
			// Document document = Jsoup.connect(urlStr).userAgent("Mozilla")
			// .timeout(30000)
			// .post();

			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			HttpGet get = new HttpGet(urlStr);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(get);
			code = response.getStatusLine().getStatusCode();
			System.out.println(code);
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.replace("<textarea style=\"display:none\">", "");
				line = line.replace("</textarea>", "");
				sb.append(line.trim()).append("\n");
			}
			in.close();
			Document document = Jsoup.parse(sb.toString());
			Elements songsElements = document.select("div.song-item");
			Song song = null;
			for (Element element : songsElements) {
				song = new Song();

				// 解析专辑名称及专辑ID
				Elements albumEles = element.select("span.songlist-album-cover");
				if (albumEles != null && albumEles.size() > 0) {
					Element albumEle = albumEles.first().select("a").first();
					String albumIdTmp = albumEle.attr("href");
					String albumId = albumIdTmp.substring(albumIdTmp.lastIndexOf("/") + 1);
					String albumName = albumEle.attr("title");

					Album album = new Album();
					album.setTitle(albumName);
					album.setId(albumId);
					song.setAlbum(album);
					
				}

				// 解析歌曲名称及歌曲ID
				Element songEle = element.select("span.song-title").first().select("a").first();
				String songTitle = songEle.text();
				String songIdTemp = songEle.attr("href");
				String songId = songIdTemp.substring(songIdTemp.lastIndexOf("/") + 1);

				song.setSongId(songId);
				song.setTitle(songTitle);

				// 解析歌手
				Elements singersEle = element.select("span.singer").first().select("span.author_list").first().select("a");
				if (singersEle != null) {
					Artist artist;
					for (Element e : singersEle) {
						artist = new Artist();
						String artistIdTemp = e.attr("href");
						artist.setId(artistIdTemp.substring(artistIdTemp.lastIndexOf("/")));
						artist.setName(e.text());
						song.getArtists().add(artist);
					}
				}

				// sb.append(songId).append("|").append(songTitle).append("|").append(singer);
				songs.add(song);
				song = null;
			}
			return songs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new NetTimeoutException(e);
		}
	}

	/**
	 * 通过歌名搜索歌曲
	 * 
	 * @param title
	 *            歌曲名
	 * @return List<Song> 搜索的结果
	 * @throws NetTimeoutException
	 * 
	 */
	public static List<Song> getSongsByTitle(String title) throws NetTimeoutException {
		List<Song> result = new ArrayList<Song>();
		if (TextUtils.isEmpty(title)) {
			return null;
		}
		// http://ting.baidu.com/search?key=%E4%BD%A0%E5%A5%BD
		String url = SEARCH_BASE_URL + "?key=" + URLEncoder.encode(title);
		int count = 0;
		try {
			Document document = Jsoup.connect(url).timeout(20000).get();
			Elements songsElements = document.select("div.song-item");
			Song song = null;
			for (Element element : songsElements) {
				song = new Song();
				Element titleEle = element.select("span.song-title").first().select("a").first();
				Element singerEle = element.select("span.singer").first().select("a").first();
				Element albumEle = element.select("span.album-title").first().select("a").first();
				if (titleEle != null) {
					String artist = singerEle.text();
					String idStr = titleEle.attr("href");
					String songId = idStr.substring(idStr.lastIndexOf("/") + 1);
					song.setSongId(songId);
					// song.setArtist(artist);
					String songTitle = titleEle.attr("title");
					song.setTitle(songTitle);
				}
				if (albumEle != null) {
					String albumStr = albumEle.text();
					Album album = new Album();
					album.setTitle(albumStr);
					song.setAlbum(album);
				}
				// sb.append(songId).append("|").append(songTitle).append("|").append(singer);
				result.add(song);
				song = null;
				count++;
				if (count >= 20) {
					break;
				}
			}
			return result;
		} catch (Exception e) {
			throw new NetTimeoutException(e);
		}
	}

	/**
	 * 通过albumId获取Album专辑对象
	 * 
	 * @param albumId
	 * @param containSongDetail
	 *            是否包括专辑内所有歌曲的详细信息 如果为true, 将加载所有的歌曲信息，可能导致加载时间变长，或流量增多 建议为false
	 * @return 由albumId得到的专辑对象
	 * @throws NetTimeoutException
	 */
	public static Album getAlbumById(String albumId, boolean containSongDetail) throws NetTimeoutException {

		Album album = new Album();
		String url = ALBUM_BASE_URL + "/" + albumId;
		try {
			Document document = Jsoup.connect(url).userAgent("Mozilla").timeout(TIME_OUT).post();
			String title = document.select("h2.album-name").first().text();
			String coverImg = document.select("span.cover").first().select("img").first().attr("src");
			String desc = document.select("span.description").first().text();

			// 解析歌手
			Elements singersEle = document.select("span.author_list").first().select("a");
			if (singersEle != null) {
				Artist artist;
				for (Element e : singersEle) {
					artist = new Artist();
					String artistIdTemp = e.attr("href");
					artist.setId(artistIdTemp.substring(artistIdTemp.lastIndexOf("/")));
					artist.setName(e.text());
					album.getAuthorList().add(artist);
				}
			}
			
			Elements songsElements = document.select("span.song-title");
			List<Song> songs = new ArrayList<Song>();
			if (containSongDetail) {
				for (Element ele : songsElements) {
					String songHrefStr = ele.select("a").first().attr("href");
					String songId = songHrefStr.substring(songHrefStr.lastIndexOf("/") + 1);
					String songTitle = ele.select("a").first().text();
					Song song = new Song();
					song.setSongId(songId);
					song.setTitle(songTitle);
					songs.add(song);
				}
			}
			album.setId(albumId);
			album.setCoverUrl(coverImg);
			album.setTitle(title);
			album.setSongs(songs);
			album.setDesc(desc);
			return album;
		} catch (Exception e) {
			throw new NetTimeoutException(e);
		}
	}

	public static Song getSongById(String songId) throws NetTimeoutException {
		Song song = new Song();
		String url = SONG_BASE_URL + "/" + songId;
		try {

			Document document = Jsoup.connect(url).userAgent("Mozilla").timeout(TIME_OUT).post();
			// 解析歌手
			Elements singersEle = document.select("span.author_list").first().select("a");
			if (singersEle != null) {
				Artist artist;
				for (Element e : singersEle) {
					artist = new Artist();
					String artistIdTemp = e.attr("href");
					artist.setId(artistIdTemp.substring(artistIdTemp.lastIndexOf("/")));
					artist.setName(e.text());
					song.getArtists().add(artist);
				}
			}
			
			Element titleEle = document.select("span.name").first();
			Element albumEle = null;
			Elements albumTemp = document.select("li.clearfix");
			if (albumTemp != null && albumTemp.first() != null) {
				albumEle = albumTemp.first().select("a").first();
			}
			String albumHrefStr = "";
			String albumId = "";
			if (albumEle != null) {
				albumHrefStr = albumEle.attr("href");
				if(!TextUtils.isEmpty(albumHrefStr)){
					albumId = albumHrefStr.substring(albumHrefStr.lastIndexOf("/") + 1);
				}
			}
			
			Element lrcEle = document.select("a.down-lrc-btn").first();
			String lrcUrl = "";
			if (lrcEle != null) {
				String lrcTemp = lrcEle.attr("data-lyricdata");
				lrcUrl = lrcTemp.substring(lrcTemp.indexOf("/"), lrcTemp.lastIndexOf("lrc") + 3);
			}

			String downloadUrl = getDownloadUrlBySongId(songId);

			String albumCover = "";
			String albumStr = "";
			if (!TextUtils.isEmpty(albumId)) {
				Album album2 = getAlbumById(albumId, false);
				albumCover = album2.getCoverUrl();
				albumStr = album2.getTitle();
			}

			if (!TextUtils.isEmpty(albumId) && getAlbumById(albumId, false) != null) {
				albumCover = getAlbumById(albumId, false).getCoverUrl();
			}
			song.setSongId(songId);
			// song.setArtist(singerEle.text());
			song.setTitle(titleEle.text());
			song.setSongUrl(downloadUrl);
			Album album = new Album();
			album.setTitle(albumStr);
			album.setCoverPath(albumCover);
			song.setAlbum(album);
			song.setLrcUrl(BASE_URL + lrcUrl);
			return song;
		} catch (Exception e) {
			throw new NetTimeoutException(e);
		}
	}

	public static String getDownloadUrlBySongId(String songId) throws NetTimeoutException {

		// http://music.baidu.com/song/13932461/download

		String url = SONG_BASE_URL + "/" + songId + "/download";
		String songUrl = "";
		try {
			// Document document = Jsoup.connect(url).timeout(20000).get();
			Document document = Jsoup.connect(url).userAgent("Mozilla").timeout(30000).post();
			Elements lis = document.select("li");
			if (lis != null && lis.size() > 0) {
				String dataStr = "";
				for (Element e : lis) {
					dataStr = e.attr("data-data");
					if (!TextUtils.isEmpty(dataStr) && dataStr.contains("link")) {
						songUrl = dataStr.substring(dataStr.indexOf("http"), dataStr.length() - 2).replaceAll("\\\\", "");
					}
				}
				return songUrl;
			}

		} catch (Exception e) {
			throw new NetTimeoutException(e);
		}
		return null;
	}

	public static String getTopListTypeByName(String name) {
		if (!TextUtils.isEmpty(name)) {
			if (Constants.BAIDU_MUSIC_LIST[0].equals(name)) {
				return TopListType.TOP_500;
			} else if (Constants.BAIDU_MUSIC_LIST[1].equals(name)) {
				return TopListType.NEW_100;
			} else if (Constants.BAIDU_MUSIC_LIST[2].equals(name)) {
				return TopListType.HUAYU;
			} else if (Constants.BAIDU_MUSIC_LIST[3].equals(name)) {
				return TopListType.YINGSHI;
			} else if (Constants.BAIDU_MUSIC_LIST[4].equals(name)) {
				return TopListType.LOVESONG;
			} else if (Constants.BAIDU_MUSIC_LIST[5].equals(name)) {
				return TopListType.NETSONG;
			} else if (Constants.BAIDU_MUSIC_LIST[6].equals(name)) {
				return TopListType.OLDSONG;
			} else if (Constants.BAIDU_MUSIC_LIST[7].equals(name)) {
				return TopListType.ROCK;
			} else if (Constants.BAIDU_MUSIC_LIST[8].equals(name)) {
				return TopListType.JAZZ;
			} else if (Constants.BAIDU_MUSIC_LIST[9].equals(name)) {
				return TopListType.FORK;
			} else if (Constants.BAIDU_MUSIC_LIST[10].equals(name)) {
				return TopListType.KTV;
			} else if (Constants.BAIDU_MUSIC_LIST[11].equals(name)) {
				return TopListType.CHINAVOICE;
			}
		}
		return null;
	}

	public static class TopListType {
		public static final String TOP_500 = "dayhot";
		public static final String NEW_100 = "new";
		public static final String HUAYU = "huayu";
		public static final String YINGSHI = "yingshijinqu";
		public static final String LOVESONG = "lovesong";
		public static final String NETSONG = "netsong";
		public static final String OLDSONG = "oldsong";
		public static final String ROCK = "rock";
		public static final String JAZZ = "jazz";
		public static final String FORK = "folk";
		public static final String KTV = "ktv";
		public static final String CHINAVOICE = "chinavoice";
	}

}
