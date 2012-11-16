package com.cupfish.musicplayer.ui;

import java.util.List;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.domain.Song;
import com.cupfish.musicplayer.global.Constants;
import com.cupfish.musicplayer.ui.adapter.MusicListNameAdapter;
import com.cupfish.musicplayer.ui.adapter.SearchResultAdapter;
import com.cupfish.musicplayer.utils.BaiduTingHelper;
import com.cupfish.musicplayer.utils.GoogleMusicHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TopListNameActivity extends Activity implements OnClickListener {


	private ListView mMusicLv;
	private Button mBack;
	private List<Song> mSongs;
	private MusicListNameAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.top_list);

		mBack = (Button) findViewById(R.id.bt_back);
		mMusicLv = (ListView) findViewById(R.id.lv_music);
		
		String title  = getIntent().getStringExtra("title");
		mBack.setText(title);
		
		//根据flag判断是百度榜单还是谷歌榜单
		final int flag = getIntent().getIntExtra("flag", 0);
		System.out.println(flag);
		switch(flag){
		case Constants.FLAG_GOOGLE_MUSIC:
			mAdapter = new MusicListNameAdapter(this, Constants.GOOGLE_MUSIC_LIST);
			break;
		case Constants.FLAG_BAIDU_MUSIC:
			mAdapter= new MusicListNameAdapter(this, Constants.BAIDU_MUSIC_LIST);
			break;
		}
		
		mMusicLv.setAdapter(mAdapter);
		
		mMusicLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent(TopListNameActivity.this, TopListActivity.class);
				if(flag == Constants.FLAG_BAIDU_MUSIC){
					intent.putExtra("topType",Constants.BAIDU_MUSIC_LIST[position]);
				}else if(flag == Constants.FLAG_GOOGLE_MUSIC){
					intent.putExtra("topType",Constants.GOOGLE_MUSIC_LIST[position]);
				}
				intent.putExtra("flag", flag);
				startActivity(intent);
			}
		});
		
		
		mBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_back:
			finish();
			break;
		}
	}


}
