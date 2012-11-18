package com.cupfish.musicplayer.test;

import android.test.AndroidTestCase;

import com.cupfish.musicplayer.bean.Song;
import com.cupfish.musicplayer.exception.NetTimeoutException;
import com.cupfish.musicplayer.global.Constants;
import com.cupfish.musicplayer.utils.BaiduTingHelper;
import com.cupfish.musicplayer.utils.DownloadUtil;

public class TestDownloadUtil  extends AndroidTestCase {

	//http://ting.baidu.com/data/music/file?link=http%3A%2F%2Fzhangmenshiting2.baidu.com%2Fdata2%2Fmusic%2F13931938%2F%E7%9A%87%E4%B8%8A%E5%90%89%E7%A5%A5.mp3

	public void testDownload() throws NetTimeoutException{
		Song song = BaiduTingHelper.getSongById("256839");
		System.out.println(song.getId());
		String fileUrl = BaiduTingHelper.getDownloadUrlBySongId(song.getId());
		System.out.println(fileUrl);
		DownloadUtil.load(song.getTitle(), fileUrl, null, Constants.DOWNLOAD_FILE_MP3);
	}
}
