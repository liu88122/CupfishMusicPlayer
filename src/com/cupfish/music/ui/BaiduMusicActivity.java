package com.cupfish.music.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.cupfish.music.R;
import com.cupfish.music.bean.Song;
import com.cupfish.music.common.Constants;
import com.cupfish.music.ui.adapter.MusicListNameAdapter;

public class BaiduMusicActivity extends Activity implements OnClickListener {


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
				Intent intent = new Intent(BaiduMusicActivity.this, TopListActivity.class);
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
