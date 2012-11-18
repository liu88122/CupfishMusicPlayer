package com.cupfish.musicplayer.ui;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.bean.Song;
import com.cupfish.musicplayer.global.Constants;
import com.cupfish.musicplayer.utils.BaiduTingHelper;
import com.cupfish.musicplayer.utils.DownloadUtil;

import android.app.Activity;
import android.os.Bundle;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.volume_bar);

		try{
		Song song = BaiduTingHelper.getSongById("13931930");
		System.out.println(song.getId());
		String fileUrl = BaiduTingHelper.getDownloadUrlBySongId(song.getId());
		System.out.println(fileUrl);
		DownloadUtil.load(song.getTitle(), fileUrl, null, Constants.DOWNLOAD_FILE_MP3);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
