package com.cupfish.musicplayer.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.domain.LRC;
import com.cupfish.musicplayer.domain.Song;
import com.cupfish.musicplayer.global.BaseApp;
import com.cupfish.musicplayer.global.Constants;
import com.cupfish.musicplayer.service.DownloadService;
import com.cupfish.musicplayer.service.MusicPlayerService;
import com.cupfish.musicplayer.ui.adapter.LocalMusicAdapter;
import com.cupfish.musicplayer.ui.adapter.MainPagerAdapter;
import com.cupfish.musicplayer.ui.view.MyViewPager;
import com.cupfish.musicplayer.utils.ConnectivityHelper;
import com.cupfish.musicplayer.utils.FileManageAssistant;
import com.cupfish.musicplayer.utils.LRCManager;
import com.cupfish.musicplayer.utils.LocalMediaUtil;
import com.cupfish.musicplayer.utils.MyImageUtils;
import com.cupfish.musicplayer.utils.MyImageUtils.ImageCallback;
import com.cupfish.musicplayer.utils.ShakeDetector;
import com.cupfish.musicplayer.utils.ShakeDetector.OnShakeListener;
import com.cupfish.musicplayer.utils.TextFormatUtils;

public class MusicPlayerActivity extends Activity implements OnClickListener, ViewFactory {

	// 头部标签索引
	private static final int TAB_PLAYING_INDEX = 1;
	private static final int TAB_LOCAL_INDEX = 2;
	private static final int TAB_ONLINE_INDEX = 3;
	private static final int INIT_SUCCESS = 200;

	// 语音识别请求码
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	protected static final String TAG = "MusicPlayerActivity";

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

	// LayoutInflater,ViewPager,及里要展示的3个View
	private LayoutInflater mInflater;
	private MyViewPager mViewPager;
	private View mPlayingContent;
	private View mLocalContent;
	private View mOnlineContent;

	// 本地音乐相关的ListView，Adapter以及数据
	private ListView mLocalMusicList;
	private LocalMusicAdapter mLocalMusicAdapter;
	private List<Song> mLocalSongs;
	// 本地音乐歌曲数
	private TextView mSongsCount;
	// 代表长按的某首歌
	private String[] mLocalOperation = { "加入播放列表", "从本地删除" };

	// ViewPager需要显示的View集合以及相应的Adapter
	private List<View> mViews;
	private MainPagerAdapter mPageAdapter;

	// 播放器界面相关
	private ImageView mPlayBtn;
	private ImageView mNextBtn;
	private ImageView mPreviousBtn;
	private TextView mArtist;
	private TextView mTitle;
	private TextView mAlbum;
	private TextView mCurrentDuration;
	private TextView mTotalDuration;
	private SeekBar mMainPlayerProgress;
	// private ProgressBar mMiniPlayerProgress;
	private ImageView mPlaylistBtn;
	private ImageView mAlbumCover;
	private ImageView mGuestureBtn;

	// 歌词显示使用TextSwitcher
	private TextSwitcher mLrcSwitcher;
	// TextSwitcher使用到的TextView
	private TextView mTextSwitcherTv;
	private LRC mLrc;
	private SharedPreferences mSp;
	// 歌词字体
	//private Typeface mTypeface = Typeface.createFromFile("/mnt/sdcard/xujinglei_font.ttf");
	// 是否更新当前播放歌曲的时间进度
	private boolean mWillUpdate = true;
	// 是否为第一次初始化播放界面，若为第一次打开，歌曲处于停止状态，不需要更新歌词
	private boolean isFirstTime = true;
	// 当前播放的歌曲
	private Song mCurrentSong;
	private CurrentPlayingReceiver mCurrentPlayingReceiver;
	private ServiceConnection musicServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}
	};

	// 打碟用
	// private float mPositionX;
	// private float mPositionY;

	// 判断专辑封面是否为默认，true:默认时封面有rotateAnimation, 点击播放按钮时动画有响应
	// false: 点击播放按钮时动画无响应
	private boolean isDefaultAlbum = true;

	// 在线听内的1个按钮
	private ImageView mBaiduMusic;

	// 在线语音识别搜索按钮
	private Button mSearch;

	// 用于更新本地歌曲LocalSongs的Handler
	private Handler mLocalHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case INIT_SUCCESS:
				Log.i(TAG, "INIT_SUCCESS");
				if (mLocalMusicList != null) {
					// 为本地音乐ListView设置数据---Adapter，同时为歌曲总数赋值
					mLocalMusicAdapter = new LocalMusicAdapter(MusicPlayerActivity.this, mLocalSongs);
					Log.i(TAG, "size:" + mLocalSongs.size());
					mLocalMusicList.setAdapter(mLocalMusicAdapter);
					// mLocalMusicAdapter.notifyDataSetChanged();
					mSongsCount.setText("共有" + mLocalSongs.size() + "首歌曲");
					mViewPager.setAdapter(mPageAdapter);
				}
				break;
			}

			super.handleMessage(msg);
		}

	};

	// 摇晃检测
	private ShakeDetector mShakeDetector;

	// 手势View
	private GestureOverlayView mGestureView;
	private GestureLibrary mGestureLibrary;
	// 手势库加载状态
	private boolean loadState;

	// 音量控制相关
	private static final int CLOSE_WINDOW = 0;
	private PopupWindow mVolumeWindow;
	private ImageView mVolumeBtn;
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
		setContentView(R.layout.main);

		mSp = getSharedPreferences("config", Context.MODE_PRIVATE);

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

		// 注册广播接收者，用于接受从服务中发来的广播，包含当前播放的歌曲信息
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_CURRENT_SONG_INDEX);

		// 注册用于监听当前播放歌曲信息的BroadcastReceiver
		mCurrentPlayingReceiver = new CurrentPlayingReceiver();
		registerReceiver(mCurrentPlayingReceiver, filter);

		mInflater = LayoutInflater.from(this);
		init();
		initGueture();

		// 如果当前网络连接是否可用及是否为wifi连接，提示用户
		ConnectivityHelper.showNetworkType(this);

		// 检测语音识别对应的Activity是否可用
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) {
			mSearch.setOnClickListener(this);
		} else {
			mSearch.setEnabled(false);
			mSearch.setText("语音识别当前不可用");
		}
	}

	/**
	 * 初始化相关界面
	 */
	private void init() {

		// Tab初始化
		mTabPlaying = (RelativeLayout) findViewById(R.id.rl_tab_playing);
		mTabLocal = (RelativeLayout) findViewById(R.id.rl_tab_local);
		mTabOnline = (RelativeLayout) findViewById(R.id.rl_tab_online);
		mTabIndicator = (RelativeLayout) findViewById(R.id.rl_tab_indicator1);

		// 获得需要展示的View并加入到List中
		mPlayingContent = mInflater.inflate(R.layout.playing, null);
		mLocalContent = mInflater.inflate(R.layout.local, null);
		mOnlineContent = mInflater.inflate(R.layout.online, null);

		// 播放器界面初始化
		mPlayBtn = (ImageView) mPlayingContent.findViewById(R.id.iv_player_play_pause);
		mNextBtn = (ImageView) mPlayingContent.findViewById(R.id.iv_player_next);
		mPreviousBtn = (ImageView) mPlayingContent.findViewById(R.id.iv_player_previous);
		mArtist = (TextView) mPlayingContent.findViewById(R.id.tv_song_singer);
		mAlbum = (TextView) mPlayingContent.findViewById(R.id.tv_song_album);
		mTitle = (TextView) mPlayingContent.findViewById(R.id.tv_song_title);
		mCurrentDuration = (TextView) mPlayingContent.findViewById(R.id.tv_current_duration);
		mTotalDuration = (TextView) mPlayingContent.findViewById(R.id.tv_total_duration);
		mMainPlayerProgress = (SeekBar) mPlayingContent.findViewById(R.id.sb_player_progress);
		// mMiniPlayerProgress = (ProgressBar)
		// mOnlineContent.findViewById(R.id.pb_miniplayer_progress);
		mPlaylistBtn = (ImageView) mPlayingContent.findViewById(R.id.iv_playlist_btn);
		mGuestureBtn = (ImageView) mPlayingContent.findViewById(R.id.iv_guesture);
		mVolumeBtn = (ImageView) mPlayingContent.findViewById(R.id.iv_volume);

		// 歌词相关初始化
		mLrcSwitcher = (TextSwitcher) mPlayingContent.findViewById(R.id.ts_lrc);
		mLrcSwitcher.setFactory(this);
		Animation inAnimation = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
		Animation outAnimation = AnimationUtils.loadAnimation(this, R.anim.push_up_out);
		mLrcSwitcher.setInAnimation(inAnimation);
		mLrcSwitcher.setOutAnimation(outAnimation);
		mLrcSwitcher.setOnLongClickListener(new CustomizeLRCSentenceListener());

		// 初始化Play按钮
		mPlayBtn.setImageResource(R.drawable.player_play_btn_selector);
		/*
		 * if (MusicPlayerService.mMediaPlayer != null) { if
		 * (MusicPlayerService.mMediaPlayer.isPlaying()) {
		 * mPlayBtn.setImageResource(R.drawable.player_pause_btn_selector); }
		 * else {
		 * mPlayBtn.setImageResource(R.drawable.player_play_btn_selector); } }
		 */
		// 用于显示本地歌曲数目
		mSongsCount = (TextView) mLocalContent.findViewById(R.id.tv_songs_count);

		mAlbumCover = (ImageView) mPlayingContent.findViewById(R.id.iv_album_cover);

		/**
		 * 以下只是为了模拟搓碟效果，哈哈 java.lang.IllegalStateException at
		 * android.media.MediaPlayer.isPlaying(Native Method)
		 */
		/*
		 * int tempIndex = 0; final SoundPool pool = new SoundPool(2,
		 * AudioManager.STREAM_MUSIC, 0); try { tempIndex =
		 * pool.load(getAssets().openFd("disk.mp3"), 1); } catch (IOException e)
		 * { e.printStackTrace(); } final int id = tempIndex;
		 * 
		 * mAlbumCover.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) {
		 * 
		 * switch (event.getAction()) { case MotionEvent.ACTION_DOWN: if
		 * (MusicPlayerService.mMediaPlayer != null &&
		 * MusicPlayerService.mMediaPlayer.isPlaying()) { pool.play(id, 1.0f,
		 * 1.0f, 0, 0, 1); } mPositionX = event.getX(); mPositionY =
		 * event.getY(); break; case MotionEvent.ACTION_MOVE: float x =
		 * event.getX(); float y = event.getY(); if (Math.abs(x - mPositionX) >
		 * 100 || Math.abs(y - mPositionY) > 100) { pool.play(id, 1.0f, 1.0f, 0,
		 * 0, 1); } break; case MotionEvent.ACTION_UP: break; } return false; }
		 * });
		 */

		Log.i(TAG, "--383--daodao");
		Bitmap temp = MyImageUtils.createReflectionImageWithOrigin(BitmapFactory.decodeResource(getResources(), R.drawable.daodao));
		mAlbumCover.setImageBitmap(MyImageUtils.zoomBitmap(temp, 0.6f));

		// 为ViewPager添加View
		mViews = new ArrayList<View>();
		mViews.add(mPlayingContent);
		mViews.add(mLocalContent);
		mViews.add(mOnlineContent);
		mViewPager = (MyViewPager) findViewById(R.id.vp_main);
		mPageAdapter = new MainPagerAdapter(mViews);
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

		// 本地音乐ListView以及其点击事件
		mLocalMusicList = (ListView) mLocalContent.findViewById(R.id.lv_local);

		mLocalMusicList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Animation animation = AnimationUtils.loadAnimation(MusicPlayerActivity.this, R.anim.fade_out);
				animation.setDuration(300);
				view.startAnimation(animation);

				Song song = (Song) mLocalMusicList.getItemAtPosition(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable("song", song);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				intent.putExtra("flag", Constants.FLAG_LOCAL_MUSIC);
				intent.setAction(Constants.ACTION_PLAY);
				sendBroadcast(intent);
			}
		});
		mLocalMusicList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

				AlertDialog.Builder builder = new AlertDialog.Builder(MusicPlayerActivity.this);
				builder.setTitle("请选择");
				builder.setItems(mLocalOperation, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						int position = mLocalMusicList.getPositionForView(view);
						Song mCurSong = mLocalSongs.get(position);

						switch (which) {
						case 0:
							if (!((BaseApp) getApplication()).playlist.contains(mCurSong)) {
								((BaseApp) getApplication()).playlist.add(mCurSong);
							}
							break;
						case 1:
							FileManageAssistant.delete(mCurSong);
							mLocalSongs.remove(mCurSong);
							mLocalMusicAdapter.notifyDataSetChanged();
							Toast.makeText(MusicPlayerActivity.this, "删除成功", 0).show();
							sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));

							break;
						}
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				return false;
			}
		});

		// 获得在线音乐条目按钮
		mBaiduMusic = (ImageView) mOnlineContent.findViewById(R.id.iv_baidu_music);

		// 语音识别按钮
		mSearch = (Button) mOnlineContent.findViewById(R.id.bt_search_online);

		// 在线音乐条目按钮的点击事件
		mBaiduMusic.setOnClickListener(this);
		mSearch.setOnClickListener(this);

		// Tab的点击事件
		mTabPlaying.setOnClickListener(this);
		mTabLocal.setOnClickListener(this);
		mTabOnline.setOnClickListener(this);

		// 播放器界面按钮事件
		mPlayBtn.setOnClickListener(this);
		mNextBtn.setOnClickListener(this);
		mPreviousBtn.setOnClickListener(this);
		mPlaylistBtn.setOnClickListener(this);
		mGuestureBtn.setOnClickListener(this);
		mVolumeBtn.setOnClickListener(this);
		// 播放进度拖放响应事件
		mMainPlayerProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();
				Intent intent = new Intent();
				intent.putExtra("msec", progress);
				sendPlayerCommand(Constants.ACTION_SEEK_TO, intent);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			}
		});

		// 注册摇晃检测监听
		mShakeDetector.registerOnShakeListener(new OnShakeListener() {
			@Override
			public void onShake() {
				// 发送播放下一曲命令
				sendPlayerCommand(Constants.ACTION_NEXT, null);
			}
		});
		// 开启监听
		try {
			mShakeDetector.start();
		} catch (Exception e) {

		}

	}

	private void initGueture() {
		mGestureView = (GestureOverlayView) mPlayingContent.findViewById(R.id.myGestureView);
		mGestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		mGestureView.addOnGesturePerformedListener(new MusicGuestureListener());
		loadState = mGestureLibrary.load();

	}

	private void initLocalMusic() {
		Log.i(TAG, "initLocalMusic");
		mLocalSongs = new ArrayList<Song>();
		new Thread() {
			@Override
			public void run() {
				mLocalSongs = LocalMediaUtil.getLocalSongs(MusicPlayerActivity.this);
				Message msg = Message.obtain();
				msg.what = INIT_SUCCESS;
				mLocalHandler.sendMessage(msg);
			}
		}.start();
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

		case R.id.iv_player_play_pause:
			if (MusicPlayerService.mMediaPlayer != null) {
				if (MusicPlayerService.mMediaPlayer.isPlaying()) {
					sendPlayerCommand(Constants.ACTION_PAUSE, null);
					mPlayBtn.setImageResource(R.drawable.player_play_btn_selector);
					mAlbumCover.clearAnimation();
				} else {
					sendPlayerCommand(Constants.ACTION_PLAY, null);
					mPlayBtn.setImageResource(R.drawable.player_pause_btn_selector);
					Animation animation = AnimationUtils.loadAnimation(MusicPlayerActivity.this, R.anim.roate_circle);
					if (isDefaultAlbum) {
						mAlbumCover.startAnimation(animation);
					}
				}
			}
			break;
		case R.id.iv_player_next:
			sendPlayerCommand(Constants.ACTION_NEXT, null);
			break;
		case R.id.iv_player_previous:
			sendPlayerCommand(Constants.ACTION_PREVIOUS, null);
			break;

		case R.id.iv_playlist_btn:
			Intent intent = new Intent(MusicPlayerActivity.this, PlaylistActivity.class);
			startActivity(intent);
			break;

		case R.id.iv_baidu_music:
			Intent baiduIntent = new Intent(this, TopListNameActivity.class);
			baiduIntent.putExtra("flag", Constants.FLAG_BAIDU_MUSIC);
			baiduIntent.putExtra("title", "百度音乐");
			startActivity(baiduIntent);
			break;

		case R.id.bt_search_online:
			startVoiceRecognitionActivity();
			break;
		case R.id.iv_guesture:
			if (mGestureView.isShown()) {
				mGestureView.setVisibility(View.GONE);
				mViewPager.setTouchIntercept(true);
				Toast.makeText(this, "手势控制已关闭", 0).show();
			} else {
				mGestureView.setVisibility(View.VISIBLE);
				mViewPager.setTouchIntercept(false);
				Toast.makeText(this, "手势控制已开启", 0).show();
			}
			break;
		case R.id.iv_volume:
			showVolumeWindow(mPlayingContent);
			break;
		}
	}

	/**
	 * 发送音乐播放器控制命令
	 * 
	 * @param action
	 *            命令, 如Constants.ACTION_PLAY
	 * @param intent
	 *            如果需要传递数据，则将数据放入intent并传入, 否则，传入null即可
	 */
	private void sendPlayerCommand(String action, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setAction(action);
		sendBroadcast(intent);
	}

	// 更新当前播放时间显示的handler
	private Handler mDurationUpdateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				long currentPosition = MusicPlayerService.mMediaPlayer.getCurrentPosition();
				mCurrentDuration.setText(TextFormatUtils.getPrettyFormatDuration(currentPosition));
				mMainPlayerProgress.setProgress((int) currentPosition);
				if ("00:00".equals(mTotalDuration.getText().toString())) {
					long duration = MusicPlayerService.mMediaPlayer.getDuration();
					mTotalDuration.setText(TextFormatUtils.getPrettyFormatDuration(duration));
				}
				// mMiniPlayerProgress.setProgress((int) currentPosition);
				break;
			}
		}

	};

	// 更新歌词的handler
	private Handler mLrcHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.obj != null) {
				String content = (String) msg.obj;
				mLrcSwitcher.setText(content);
			}
		}

	};

	/**
	 * 当播放歌曲时会收到CurrentPlayingReceiver广播，此时调用此方法刷新播放器界面
	 */
	private void buildCurrentPlayingView() {
		Log.i(TAG, "buildCurrentPlayingView");

		mViewPager.setCurrentItem(0);
		doTabIndicatorAnim(TAB_PLAYING_INDEX);

		if (mPlayBtn != null) {
			mPlayBtn.setImageResource(R.drawable.player_pause_btn_selector);
		}

		if (mLrcSwitcher != null) {
			mLrcSwitcher.setText("杯里鱼 爱音乐");
		}

		if (mCurrentSong != null) {
			if(mCurrentSong.getAuthorList() != null && mCurrentSong.getAuthorList().size() > 0){
				mArtist.setText(mCurrentSong.getAuthorList().get(0).name);
			}else{
				mArtist.setText(R.string.love_life);
			}
			mAlbum.setText(mCurrentSong.getAlbum());
			mTitle.setText(mCurrentSong.getTitle());
			String imageUrl = mCurrentSong.getAlbumCover();
			Bitmap bitmap = null;

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			long albumId = 0;
			long songId = 0;
			try {
				albumId = Long.parseLong(mCurrentSong.getAlbumId());
				songId = Long.parseLong(mCurrentSong.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// bitmap =
			// MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(),
			// origId, Thumbnails.MICRO_KIND, options);
			// bitmap =
			// MyImageUtils.zoomBitmap(LocalMediaUtil.getArtwork(MusicPlayerActivity.this,
			// songId, albumId), 0.8f);
			bitmap = MyImageUtils.createReflectionImageWithOrigin(LocalMediaUtil.getArtwork(MusicPlayerActivity.this, songId, albumId));
			
			//TODO 专辑封面下载有问题，待修复
			if (bitmap == null) {
				String imageName = MyImageUtils.md5(mCurrentSong.getTitle());
				Log.i(TAG, "Loading ImageCover");
				bitmap = MyImageUtils.loadImage(imageName, imageUrl, new ImageCallback() {
					@Override
					public void loadImage(Bitmap bitmap, String imagePath) {
						isDefaultAlbum = false;
						mAlbumCover.clearAnimation();
						mAlbumCover.setImageBitmap(MyImageUtils.getFitableBitmapWithReflection(MusicPlayerActivity.this, bitmap));
					}
				});
			}

			if (bitmap == null) {
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.music_album_default);
				mAlbumCover.setImageBitmap(MyImageUtils.zoomBitmap(bitmap, 0.8f));
				Animation animation = AnimationUtils.loadAnimation(MusicPlayerActivity.this, R.anim.roate_circle);
				mAlbumCover.startAnimation(animation);
			} else {
				isDefaultAlbum = false;
				mAlbumCover.setImageBitmap(MyImageUtils.getFitableBitmapWithReflection(MusicPlayerActivity.this, bitmap));
				mAlbumCover.clearAnimation();
				bitmap.recycle();
			}

			// 更新歌词ver1.0
			/*
			 * new Thread() {
			 * 
			 * @Override public void run() { long startTime =
			 * System.currentTimeMillis(); // int duration = //
			 * MusicPlayerService.mMediaPlayer.getDuration(); TreeMap<Long,
			 * String> lrc = null; try { lrc = LRCReader.getLRC(mCurrentSong); }
			 * catch (Exception e1) { e1.printStackTrace(); } if (lrc != null &&
			 * lrc.size() > 0) { long showTime = 0; String lrcContent = "";
			 * boolean isEnd = false; Iterator<Long> timeIterator =
			 * lrc.keySet().iterator(); if (timeIterator.hasNext() &&
			 * (timeIterator.next() != 3600000)) { showTime =
			 * timeIterator.next(); lrcContent = lrc.get(showTime); } else {
			 * Log.i(TAG, "LRC ERROR"); return; }
			 * 
			 * while (!isEnd) { long currentTime = System.currentTimeMillis();
			 * long deltaTime = (currentTime - startTime) / 10 * 10; if
			 * (deltaTime > showTime) { Message msg = Message.obtain(); msg.obj
			 * = lrcContent; mLrcHandler.sendMessage(msg); if
			 * (timeIterator.hasNext()) { showTime = timeIterator.next(); if
			 * (showTime == 3600000) { isEnd = true; break; } lrcContent =
			 * lrc.get(showTime); } } try { Thread.sleep(10); } catch
			 * (InterruptedException e) { e.printStackTrace(); } } Log.i(TAG,
			 * "LRC FINISH"); } }
			 * 
			 * }.start();
			 */

			// 更新歌词ver2.0

			mLrc = new LRC(MusicPlayerActivity.this, mCurrentSong, mLrcHandler, MusicPlayerService.mMediaPlayer);
			LRCManager.getInstance().removeLRC();
			LRCManager.getInstance().addLRC(mLrc);
			if (isFirstTime) {
				mLrc.setPause(true);
				if (mPlayBtn != null) {
					mPlayBtn.setImageResource(R.drawable.player_play_btn_selector);
				}
				mAlbumCover.clearAnimation();
				isFirstTime = false;
			}

			new Thread() {
				@Override
				public void run() {
					while (mWillUpdate) {
						Message msg = Message.obtain();
						msg.what = 2;
						mDurationUpdateHandler.sendMessage(msg);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}
		if (MusicPlayerService.mMediaPlayer != null) {
			long duration = MusicPlayerService.mMediaPlayer.getDuration();
			mTotalDuration.setText(TextFormatUtils.getPrettyFormatDuration(duration));
			mMainPlayerProgress.setMax((int) duration);
			// mMiniPlayerProgress.setMax((int) duration);
		}
	}

	/**
	 * 当MusicPlayerService开始播放每一首歌曲时，将发送一个广播，包含当前播放歌曲的index信息 从而使播放器界面及时更新
	 * 
	 * @author Liu88122
	 */
	private class CurrentPlayingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constants.ACTION_CURRENT_SONG_INDEX.equals(action)) {
				int currentSongIndex = intent.getIntExtra("currentSongIndex", -1);
				if (currentSongIndex != -1) {
					mCurrentSong = ((BaseApp) getApplication()).playlist.get(currentSongIndex);
					buildCurrentPlayingView();
					Log.i(TAG, "onReceive:buildCurrentPlayingView");
				}
			}
		}
	}

	@Override
	protected void onResume() {
		// 初始化Play按钮
		/*
		 * if (MusicPlayerService.mMediaPlayer != null) { if
		 * (MusicPlayerService.mMediaPlayer.isPlaying()) {
		 * mPlayBtn.setImageResource(R.drawable.player_pause_btn_selector); }
		 * else {
		 * mPlayBtn.setImageResource(R.drawable.player_play_btn_selector); } }
		 */
		// 更新List，主要用初始化list的数据
		super.onResume();
		initLocalMusic();
		mWillUpdate = true;
		
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
		mWillUpdate = false;
		unbindService(musicServiceConn);
		if (mCurrentPlayingReceiver != null) {
			unregisterReceiver(mCurrentPlayingReceiver);
		}
		LRCManager.getInstance().removeLRC();
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

	private void showExitDialog() {
		((BaseApp) getApplication()).showDialog(this, "", "确定要退出杯里鱼音乐吗?", true, new BaseApp.OnClickListener() {
			@Override
			public void onConfirmClick() {
				/*
				 * if (mCurrentPlayingReceiver != null) {
				 * unregisterReceiver(mCurrentPlayingReceiver); }
				 */
				MusicPlayerActivity.this.finish();
			}

			@Override
			public void onCancelClick() {

			}
		});
	}

	/**
	 * 这是歌词显示的TextSwitcher的ViewFactory需要实现的方法
	 */
	@Override
	public View makeView() {
		mTextSwitcherTv = new TextView(this);
		mTextSwitcherTv.setGravity(Gravity.CENTER);
		mTextSwitcherTv.setTextSize(20);
		mTextSwitcherTv.setTextColor(getResources().getColor(R.color.white));

		/*if (mTypeface != null) {
			mTextSwitcherTv.setTypeface(mTypeface);
		}*/

		return mTextSwitcherTv;
	}

	// 以下为语音搜索相关
	private void startVoiceRecognitionActivity() {

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "比如，相信自己, 可惜不是你");
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	// 语音识别返回的结果，并需要在此处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (matches != null && matches.size() > 0) {
				Log.i(TAG, matches.get(0));
				Intent intent = new Intent(this, SearchResultActivity.class);
				intent.putExtra("title", matches.get(0));
				startActivity(intent);
			}
		}
		mViewPager.setCurrentItem(2);
		// super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 手势识别监听
	 * 
	 * @author Liu88122
	 */
	private class MusicGuestureListener implements OnGesturePerformedListener {
		@Override
		public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
			if (loadState) {
				ArrayList<Prediction> predictions = mGestureLibrary.recognize(gesture);
				if (!predictions.isEmpty()) {
					Prediction prediction = predictions.get(0);
					if (prediction.score > 2) {
						String cmdName = prediction.name;
						String action = null;
						if ("pause".equals(cmdName)) {
							action = Constants.ACTION_PAUSE;

							// 控制默认封面是否加载Circle动画
							mPlayBtn.setImageResource(R.drawable.player_play_btn_selector);
							mAlbumCover.clearAnimation();
						} else if ("play".equals(cmdName)) {
							action = Constants.ACTION_PLAY;

							// 控制默认封面是否加载Circle动画
							mPlayBtn.setImageResource(R.drawable.player_pause_btn_selector);
							Animation animation = AnimationUtils.loadAnimation(MusicPlayerActivity.this, R.anim.roate_circle);
							if (isDefaultAlbum) {
								mAlbumCover.startAnimation(animation);
							}
						} else if ("next".equals(cmdName)) {
							action = Constants.ACTION_NEXT;
						} else if ("previous".equals(cmdName)) {
							action = Constants.ACTION_PREVIOUS;
						} else if ("music".equals(cmdName)) {
							Toast.makeText(MusicPlayerActivity.this, "爱音乐，爱生活，更爱杯里鱼", 0).show();
						} else if ("exit".equals(cmdName)) {
							Toast.makeText(MusicPlayerActivity.this, "exit", 0).show();
							showExitDialog();
						}
						if (!TextUtils.isEmpty(action)) {
							// 发送命令
							sendPlayerCommand(action, null);

						}
					}
				}
			}
		}

	}

	// 音量控制
	private void showVolumeWindow(View parent) {
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
		mVolumeWindow.setHeight(150);
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
		mVolumeWindow.showAtLocation(parent, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 50);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (mPlayingContent != null && isFirstTimeShow) {
				showVolumeWindow(mPlayingContent);
				isFirstTimeShow = false;
			}
			if (mVolumeControlBar != null) {
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
				mVolumeControlBar.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (mPlayingContent != null && isFirstTimeShow) {
				showVolumeWindow(mPlayingContent);
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

	/**
	 * 当显示歌词的控件被长按时，弹出对话框，自定义个性歌词
	 * 
	 * @author Liu88122
	 */
	private class CustomizeLRCSentenceListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			// Toast.makeText(MusicPlayerActivity.this, "自定义歌词", 0).show();
			showCustomizeLRCDialog();
			return true;
		}

	}

	private void showCustomizeLRCDialog() {

		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.customize_lrc_dialog);
		// 设置背景模糊
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		// 设置对话框背景为透明
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 设置对话框的宽度
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = getWindowManager().getDefaultDisplay().getWidth() * 5 / 6;
		dialog.getWindow().setAttributes(params);
		// 获得两个按钮，并绑定监听
		Button mConfirmBtn = (Button) dialog.findViewById(R.id.bt_confirm);
		Button mCancelBtn = (Button) dialog.findViewById(R.id.bt_cancel);
		final EditText mCustomizedLrc = (EditText) dialog.findViewById(R.id.et_customize_lrc);
		mCustomizedLrc.setText(mSp.getString("myLrc", ""));
		mConfirmBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String mLrcStr = mCustomizedLrc.getText().toString().trim();
				if (TextUtils.isEmpty(mLrcStr)) {
					mLrcStr = Constants.LRC_AD;
				}
				// 将输入的值保存到SharedPreference中
				mSp.edit().putString("myLrc", mLrcStr).commit();
				dialog.dismiss();
			}
		});
		mCancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

}