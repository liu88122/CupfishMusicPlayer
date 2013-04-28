package com.cupfish.music.test;

import android.test.AndroidTestCase;

import com.cupfish.music.bean.Song;
import com.cupfish.music.common.Constants;
import com.cupfish.music.exception.NetTimeoutException;
import com.cupfish.music.utils.BaiduMusicHelper;
import com.cupfish.music.utils.DownloadUtil;

public class TestDownloadUtil  extends AndroidTestCase {

	//http://ting.baidu.com/data/music/file?link=http%3A%2F%2Fzhangmenshiting2.baidu.com%2Fdata2%2Fmusic%2F13931938%2F%E7%9A%87%E4%B8%8A%E5%90%89%E7%A5%A5.mp3

	public void testDownload() throws NetTimeoutException{
		Song song = BaiduMusicHelper.getSongById("256839");
		System.out.println(song.getSongId());
		String fileUrl = BaiduMusicHelper.getDownloadUrlBySongId(song.getSongId());
		System.out.println(fileUrl);
		DownloadUtil.load(song.getTitle(), fileUrl, null, Constants.DOWNLOAD_FILE_MP3);
	}
}
