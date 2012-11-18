package com.cupfish.musicplayer.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.bean.Song;
import com.cupfish.musicplayer.exception.NetTimeoutException;
import com.cupfish.musicplayer.global.BaseApp;
import com.cupfish.musicplayer.global.Constants;
import com.cupfish.musicplayer.ui.adapter.OnlineListAdapter;
import com.cupfish.musicplayer.utils.BaiduTingHelper;

public class NewTopActivity extends Activity implements OnClickListener {

	private final static int INIT_FINISH = 200;

	private ListView mNewTopListView;
	private View mLoading;
	private Button mBack;
	private List<Song> mSongs;
	private OnlineListAdapter mAdapter;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case INIT_FINISH:
				if (mSongs != null) {
					mAdapter = new OnlineListAdapter(NewTopActivity.this, mSongs, Constants.FLAG_BAIDU_MUSIC);
					mNewTopListView.setAdapter(mAdapter);
					mLoading.setVisibility(View.GONE);
					mNewTopListView.setVisibility(View.VISIBLE);
				}

				break;
			}

			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_top);

		mBack = (Button) findViewById(R.id.bt_back);
		mLoading = findViewById(R.id.rl_loading);
		mNewTopListView = (ListView) findViewById(R.id.lv_new_top);
		
		mNewTopListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("song", mSongs.get(position));
				intent.putExtras(bundle);
				intent.setAction(Constants.ACTION_PLAY);
				sendBroadcast(intent);
			}
		});
		
		
		mBack.setOnClickListener(this);
		
		//初始化新歌榜数据
		initNewTopData();
		
	}

	/**
	 * 开启新的线程获取新歌榜数据
	 * void
	 */
	private void initNewTopData() {
		new Thread() {
			@Override
			public void run() {
				try {
					mSongs = BaiduTingHelper.getSongsFromBaidu(BaiduTingHelper.TopListType.CHINAVOICE);
				} catch (NetTimeoutException e) {
					showNetErrorDialog();
				}
				handler.sendEmptyMessage(INIT_FINISH);
			}
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_back:
			finish();
			break;
		}
	}

	private void showNetErrorDialog(){
		((BaseApp)getApplication()).showDialog(NewTopActivity.this, "网络异常", "网络好像暂时不太给力哦，请稍候再试", true, null);
	}
	
}
