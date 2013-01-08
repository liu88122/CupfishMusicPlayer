package com.cupfish.music.ui;

import android.app.Activity;
import android.os.Bundle;

import com.cupfish.music.R;
import com.cupfish.music.bean.Song;
import com.cupfish.music.global.Constants;
import com.cupfish.music.utils.BaiduTingHelper;
import com.cupfish.music.utils.DownloadUtil;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.volume_bar);

		try{
		Song song = BaiduTingHelper.getSongById("13931930");
		System.out.println(song.getSongId());
		String fileUrl = BaiduTingHelper.getDownloadUrlBySongId(song.getSongId());
		System.out.println(fileUrl);
		DownloadUtil.load(song.getTitle(), fileUrl, null, Constants.DOWNLOAD_FILE_MP3);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
