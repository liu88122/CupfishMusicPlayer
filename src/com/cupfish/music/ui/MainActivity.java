package com.cupfish.music.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.cupfish.music.R;
import com.cupfish.music.global.BaseApp;
import com.cupfish.music.service.DownloadService;
import com.cupfish.music.service.MusicPlayerService;
import com.cupfish.music.ui.adapter.MainPagerAdapter;
import com.cupfish.music.ui.fragment.LocalMusicFragment;
import com.cupfish.music.ui.fragment.MusicPlayerFragment;
import com.cupfish.music.ui.fragment.OnlineMusicFragment;
import com.cupfish.music.ui.view.MyViewPager;
import com.cupfish.music.utils.ConnectivityHelper;
import com.cupfish.music.utils.ShakeDetector;
import com.cupfish.music.utils.ShakeDetector.OnShakeListener;

public class MainActivity extends FragmentActivity implements OnClickListener {

	// 头部标签索引
	private static final int TAB_PLAYING_INDEX = 1;
	private static final int TAB_LOCAL_INDEX = 2;
	private static final int TAB_ONLINE_INDEX = 3;

	protected static final String TAG = "MusicPlayerActivity";

	private View mMain;
	// 当前Tab的index,默认为第一个，即TAB_PLAYING_INDEX
	private int currentIndicate = TAB_PLAYING_INDEX;

	// tab项
	private RelativeLayout mTabPlaying;
	private RelativeLayout mTabLocal;
	private RelativeLayout mTabOnline;

	// Tab的指示器
	private RelativeLayout mTabIndicator;
	// 当点击Tab时 指示器滑动的动画
	private TranslateAnimation mIndicatorAnimation;


	// ViewPager需要显示的View集合以及相应的Adapter
	private MyViewPager mViewPager;
	private List<Fragment> mFragments;
	private MainPagerAdapter mPageAdapter;

	private ServiceConnection musicServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}
	};

	// 摇晃检测
	private ShakeDetector mShakeDetector;

	// 音量控制相关
	private static final int CLOSE_WINDOW = 0;
	private PopupWindow mVolumeWindow;
	private SeekBar mVolumeControlBar;
	private AudioManager mAudioManager;
	private boolean isVolumeBarClicked = false;
	boolean isFirstTimeShow = true;
	private Handler mVolumeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CLOSE_WINDOW:
				closeVolumeWindow();
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		mMain = View.inflate(this, R.layout.main, null);
		setContentView(mMain);

		// 获取音频管理服务，主要用于调节音量
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		// 创建摇晃检测器
		mShakeDetector = new ShakeDetector(this);

		// 绑定音乐服务
		Intent musicServiceIntent = new Intent(this, MusicPlayerService.class);
		bindService(musicServiceIntent, musicServiceConn, Context.BIND_AUTO_CREATE);

		// 开启下载服务
		Intent downloadServiceIntent = new Intent(this, DownloadService.class);
		startService(downloadServiceIntent);

		setupLayout();

		// 如果当前网络连接是否可用及是否为wifi连接，提示用户
		ConnectivityHelper.showNetworkType(this);

	}

	/**
	 * 初始化相关界面
	 */
	private void setupLayout() {

		// Tab初始化
		mTabPlaying = (RelativeLayout) findViewById(R.id.rl_tab_playing);
		mTabLocal = (RelativeLayout) findViewById(R.id.rl_tab_local);
		mTabOnline = (RelativeLayout) findViewById(R.id.rl_tab_online);
		mTabIndicator = (RelativeLayout) findViewById(R.id.rl_tab_indicator1);

		// 为ViewPager添加Fragment
		mFragments = new ArrayList<Fragment>();
		mFragments.add(new MusicPlayerFragment());
		mFragments.add(new LocalMusicFragment());
		mFragments.add(new OnlineMusicFragment());
		
		mViewPager = (MyViewPager) findViewById(R.id.vp_main);
		mViewPager.setOffscreenPageLimit(3);
		mPageAdapter = new MainPagerAdapter(getSupportFragmentManager(),mFragments);
		mViewPager.setAdapter(mPageAdapter);
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				doTabIndicatorAnim(arg0 + 1);
				currentIndicate = arg0 + 1;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		// Tab的点击事件
		mTabPlaying.setOnClickListener(this);
		mTabLocal.setOnClickListener(this);
		mTabOnline.setOnClickListener(this);

		// 注册摇晃检测监听
		//TODO 先取消摇晃切歌功能 
		mShakeDetector.registerOnShakeListener(new OnShakeListener() {
			@Override
			public void onShake() {
				// 发送播放下一曲命令
//				sendPlayerCommand(Constants.ACTION_NEXT, null);
			}
		});
		// 开启监听
		try {
			mShakeDetector.start();
		} catch (Exception e) {

		}

	}

	/**
	 * 执行Tab指示器的动画
	 * 
	 * @param index
	 *            被点击的Tab的index,即指示器要指向的Tab的index void
	 * 
	 */
	private void doTabIndicatorAnim(int index) {

		// 如果点击当前Tab，指示器不需要动画
		if (index == currentIndicate) {
			return;
		}

		// 判断指向的Tab在当前Tab的左边还是右边
		// 然后创建相应的translate动画
		int flag = index - currentIndicate;
		currentIndicate = index;
		if (flag > 0) {
			int delta = Math.abs(flag);
			mIndicatorAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, delta, Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 0);
		} else {
			int delta = Math.abs(flag);
			mIndicatorAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -delta, Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 0);
		}
		mIndicatorAnimation.setDuration(300);
		mIndicatorAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		mIndicatorAnimation.setFillAfter(false);

		// 当动画完成后，隐藏当前的指示器，并使目标指示器显示出来
		mIndicatorAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				switch (currentIndicate) {
				case TAB_PLAYING_INDEX:
					mTabIndicator.setVisibility(View.INVISIBLE);
					mTabIndicator = (RelativeLayout) findViewById(R.id.rl_tab_indicator1);
					mTabIndicator.setVisibility(View.VISIBLE);
					Log.i(TAG, "TAB_PLAYING_INDEX");
					break;
				case TAB_LOCAL_INDEX:
					mTabIndicator.setVisibility(View.INVISIBLE);
					mTabIndicator = (RelativeLayout) findViewById(R.id.rl_tab_indicator2);
					mTabIndicator.setVisibility(View.VISIBLE);
					Log.i(TAG, "TAB_LOCAL_INDEX");
					break;
				case TAB_ONLINE_INDEX:
					mTabIndicator.setVisibility(View.INVISIBLE);
					mTabIndicator = (RelativeLayout) findViewById(R.id.rl_tab_indicator3);
					mTabIndicator.setVisibility(View.VISIBLE);
					Log.i(TAG, "TAB_ONLINE_INDEX");
					break;
				}
			}
		});
		mTabIndicator.startAnimation(mIndicatorAnimation);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_tab_playing:
			doTabIndicatorAnim(TAB_PLAYING_INDEX);
			mViewPager.setCurrentItem(0);
			break;
		case R.id.rl_tab_local:
			doTabIndicatorAnim(TAB_LOCAL_INDEX);
			mViewPager.setCurrentItem(1);
			break;
		case R.id.rl_tab_online:
			doTabIndicatorAnim(TAB_ONLINE_INDEX);
			mViewPager.setCurrentItem(2);
			break;
		case R.id.iv_volume:
			showVolumeWindow();
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			ExifInterface exifInterface = new ExifInterface("");
			exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "");
			exifInterface.saveAttributes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(musicServiceConn);
		
		if (MusicPlayerService.mMediaPlayer != null) {
			if (MusicPlayerService.mMediaPlayer.isPlaying()) {
				MusicPlayerService.mMediaPlayer.stop();
			}
			MusicPlayerService.mMediaPlayer.release();
		}
		// 停止摇晃检测
		if (mShakeDetector != null) {
			mShakeDetector.stop();
		}
	}

	@Override
	public void onBackPressed() {
		showExitDialog();
	}

	public void showExitDialog() {
		((BaseApp) getApplication()).showDialog(this, "", "确定要退出杯里鱼音乐吗?", true, new BaseApp.OnClickListener() {
			@Override
			public void onConfirmClick() {
				MainActivity.this.finish();
			}

			@Override
			public void onCancelClick() {

			}
		});
	}
	// 音量控制
	private void showVolumeWindow() {
		boolean isBarClicked = false;
		if (mVolumeWindow != null) {
			mVolumeWindow.dismiss();
		}
		Display display = this.getWindowManager().getDefaultDisplay();
		View view = View.inflate(this, R.layout.volume_control, null);
		mVolumeControlBar = (SeekBar) view.findViewById(R.id.sb_volume);
		mVolumeControlBar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		mVolumeControlBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
		mVolumeControlBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				isVolumeBarClicked = true;
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						mVolumeHandler.sendEmptyMessage(CLOSE_WINDOW);
					}
				}, 3000);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			}
		});
		mVolumeWindow = new PopupWindow(view);
		mVolumeWindow.setWidth(display.getWidth() * 2 / 3);
		mVolumeWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		mVolumeWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_OUTSIDE:
					closeVolumeWindow();
					break;
				}
				return false;
			}
		});
		mVolumeWindow.setAnimationStyle(R.anim.fade_in);
		mVolumeWindow.setTouchable(true);
		mVolumeWindow.setOutsideTouchable(true);
		mVolumeWindow.showAtLocation(mMain, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 100);
		if (!isVolumeBarClicked) {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					mVolumeHandler.sendEmptyMessage(CLOSE_WINDOW);
				}
			}, 3000);
		}
	}

	private void closeVolumeWindow() {
		if (mVolumeWindow != null) {
			isFirstTimeShow = true;
			mVolumeWindow.dismiss();
			mVolumeWindow = null;
		}
	}

	
	//TODO 声音面板需要全局显示
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (mMain != null && isFirstTimeShow) {
				showVolumeWindow();
				isFirstTimeShow = false;
			}
			if (mVolumeControlBar != null) {
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
				mVolumeControlBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (mMain != null && isFirstTimeShow) {
				showVolumeWindow();
				isFirstTimeShow = false;
			}
			if (mVolumeControlBar != null) {
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
				mVolumeControlBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}