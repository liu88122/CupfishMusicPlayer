package com.cupfish.music.ui;

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

import com.cupfish.music.R;
import com.cupfish.music.bean.Song;
import com.cupfish.music.common.BaseApp;
import com.cupfish.music.common.Constants;
import com.cupfish.music.exception.NetTimeoutException;
import com.cupfish.music.ui.adapter.SearchResultAdapter;
import com.cupfish.music.utils.BaiduMusicHelper;

public class SearchResultActivity extends Activity implements OnClickListener {

	private final static int INIT_FINISH = 200;

	private ListView mSearchResultLv;
	private View mLoading;
	private Button mBack;
	private List<Song> mSongs;
	private SearchResultAdapter mAdapter;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case INIT_FINISH:
				if (mSongs != null && mSongs.size() > 0) {
					mAdapter = new SearchResultAdapter(SearchResultActivity.this, mSongs);
					mSearchResultLv.setAdapter(mAdapter);
					mLoading.setVisibility(View.GONE);
					mSearchResultLv.setVisibility(View.VISIBLE);
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
		setContentView(R.layout.search_result);

		mBack = (Button) findViewById(R.id.bt_back);
		mLoading = findViewById(R.id.rl_loading);
		mSearchResultLv = (ListView) findViewById(R.id.lv_search_result);

		mSearchResultLv.setOnItemClickListener(new OnItemClickListener() {
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

		Intent intent = getIntent();
		String title = intent.getStringExtra("title");

		if (title != null || !"".equals(title)) {
			// 初始化新歌榜数据
			initSearchData(title);
		}

	}

	/**
	 * 开启新的线程获取搜索数据 void
	 */
	private void initSearchData(final String title) {
		new Thread() {
			@Override
			public void run() {
				try {
					mSongs = BaiduMusicHelper.getSongsByTitle(title);
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

	private void showNetErrorDialog() {
		((BaseApp) getApplication()).showDialog(this, "网络异常", "网络好像暂时不太给力哦，请稍候再试", true, null);
	}
}
