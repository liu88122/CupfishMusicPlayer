package com.cupfish.music.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cupfish.music.R;
import com.cupfish.music.bean.Song;
import com.cupfish.music.exception.NetTimeoutException;
import com.cupfish.music.global.BaseApp;
import com.cupfish.music.global.Constants;
import com.cupfish.music.ui.adapter.OnlineListAdapter;
import com.cupfish.music.utils.BaiduTingHelper;

public class TopListActivity extends Activity implements OnClickListener {

	private final static int INIT_FINISH = 200;
	private final static int REFRESH_FINISH = 300;
	private final static int ERROR = 404;

	protected static final String TAG = "OnlineListActivity";

	private String topType;
	private int flag;

	private ListView mOnlineList;
	private Button mBack;
	private List<Song> mSongs = new ArrayList<Song>();

	private TextView mLoadingTv;
	private ProgressBar mLoadingBar;
	private AlertDialog mDialog;
	private OnlineListAdapter mAdapter;

	// 当点击在线条目后弹出缓冲的Dialog
	private ProgressDialog mLoadingDialog;
	// 当切换歌曲时程序会发出广播，这里接收该广播用来关闭缓冲Dialog
	private CurrentPlayingReceiver mReceiver;
	private Handler dataHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case INIT_FINISH:
				if (mSongs != null) {
					mAdapter = new OnlineListAdapter(TopListActivity.this, mSongs, flag);
					mOnlineList.setAdapter(mAdapter);
					mDialog.dismiss();
				}

				break;
			case REFRESH_FINISH:
				mAdapter.setData(mSongs);
				mAdapter.notifyDataSetChanged();
				if (mLoadingBar != null) {
					mLoadingBar.setVisibility(View.GONE);
				}
				if (mLoadingTv != null) {
					mLoadingTv.setText("点击加载");
				}
				break;
			}
			super.handleMessage(msg);
		}

	};

	private Handler errorHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case ERROR:
				showNetErrorDialog();
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.top_list);

		mBack = (Button) findViewById(R.id.bt_back);

		Intent intent = getIntent();
		flag = intent.getIntExtra("flag", 0);
		topType = intent.getStringExtra("topType");
		mBack.setText(topType);
		if (flag == Constants.FLAG_BAIDU_MUSIC) {
			topType = BaiduTingHelper.getTopListTypeByName(topType);
		}
		mLoadingDialog = new ProgressDialog(this);
		mLoadingDialog.setMessage("正在加载歌曲哦亲");
		
		// 注册广播接收者，用于接受从服务中发来的广播，包含当前播放的歌曲信息
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_CURRENT_SONG_INDEX);
		mReceiver = new CurrentPlayingReceiver();
		registerReceiver(mReceiver, filter);

		mOnlineList = (ListView) findViewById(R.id.lv_music);

		mOnlineList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("song", mSongs.get(position));
				intent.putExtras(bundle);
				intent.putExtra("flag", flag);
				intent.setAction(Constants.ACTION_PLAY);
				sendBroadcast(intent);

				mLoadingDialog.show();
			}
		});

		View footer = View.inflate(this, R.layout.list_footer_refresh, null);
		mLoadingTv = (TextView) footer.findViewById(R.id.tv_loading);
		mLoadingBar = (ProgressBar) footer.findViewById(R.id.pb_loading);
		footer.setOnClickListener(this);
		mOnlineList.addFooterView(footer);

		mBack.setOnClickListener(this);
		mBack.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				((BaseApp) getApplication()).showDialog(TopListActivity.this, "提示", "确定将本榜单加入播放列表吗？", true, new BaseApp.OnClickListener() {
					@Override
					public void onConfirmClick() {
						((BaseApp) getApplication()).playlist.addAll(mSongs);
					}

					@Override
					public void onCancelClick() {

					}
				});
				return false;
			}
		});

		// 显示加载对话框
		mDialog = ((BaseApp) getApplication()).showDialog(this, "提示", "正在努力加载哦...", false, null);
		// 初始化新歌榜数据
		getListData(0, INIT_FINISH, errorHandler);

	}

	/**
	 * 开启新的线程获取新歌榜数据 void
	 */
	private void getListData(final int start, final int msgWhat, final Handler mhandler) {
		new Thread() {
			@Override
			public void run() {
				if (flag != 0 && !TextUtils.isEmpty(topType)) {
					switch (flag) {
					case Constants.FLAG_BAIDU_MUSIC:
						Log.i(TAG, "BaiduTingHelper, HELPING");
						List<Song> tempBaiduSongs = null;
						try {
							tempBaiduSongs = BaiduTingHelper.getSongsFromBaidu(topType);
						} catch (NetTimeoutException e) {
							errorHandler.sendEmptyMessage(ERROR);
						}
						if (tempBaiduSongs != null) {
							mSongs.addAll(tempBaiduSongs);
						}
						dataHandler.sendEmptyMessage(msgWhat);
						break;
					case Constants.FLAG_GOOGLE_MUSIC:
						
						break;
					}
				}
			}
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_back:
			finish();
			break;
		case R.id.refresh:
			mLoadingTv.setText("正在加载...");
			getListData(mSongs.size(), REFRESH_FINISH, errorHandler);
			mLoadingBar.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void showNetErrorDialog() {
		((BaseApp) getApplication()).showDialog(this, "网络异常", "网络好像暂时不太给力哦，请稍候再试", true, null);
	}

	private class CurrentPlayingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.ACTION_CURRENT_SONG_INDEX.equals(action)) {
				if (mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

}
