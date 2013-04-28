package com.cupfish.music.ui.fragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.cupfish.music.R;
import com.cupfish.music.bean.Album;
import com.cupfish.music.bean.Song;
import com.cupfish.music.common.BaseApp;
import com.cupfish.music.common.Constants;
import com.cupfish.music.download.DownloadEngine;
import com.cupfish.music.download.DownloadTask.DownloadListener;
import com.cupfish.music.lrc.LrcController;
import com.cupfish.music.lrc.LrcController.OnLrcUpdateListener;
import com.cupfish.music.service.MusicPlayerService;
import com.cupfish.music.ui.PlaylistActivity;
import com.cupfish.music.ui.view.LrcView2;
import com.cupfish.music.ui.view.VisualizerView;
import com.cupfish.music.utils.LocalMediaUtil;
import com.cupfish.music.utils.MyImageUtils;
import com.cupfish.music.utils.MyImageUtils.ImageCallback;
import com.cupfish.music.utils.TextFormatUtils;
import com.cupfish.music.utils.VisualizerUtils;

public class MusicPlayerFragment extends Fragment implements ViewFactory, OnClickListener {

	public static final String TAG = MusicPlayerFragment.class.getSimpleName();

	protected static final int CACHING = 1;
	protected static final int CACHED = 2;
	protected static final int TIME_LINE_UPDATE = 3;

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
	private ImageView mVolumeBtn;
	
	private FrameLayout lrcViewContainer;
	// 歌词显示使用TextSwitcher
	private TextSwitcher mLrcSwitcher;
	// TextSwitcher使用到的TextView
	private TextView mTextSwitcherTv;
	// 歌词字体
	private Typeface mTypeface;
	
	// 更新歌词的handler
	private Handler mLrcHandler;

	private SharedPreferences mSp;
	
	// 是否更新当前播放歌曲的时间进度
	private boolean mWillUpdate = true;
	// 更新当前播放时间显示的handler
	private Handler mDurationUpdateHandler;
	
	// 当前播放的歌曲
	private Song mCurrentSong;
	private CurrentPlayingReceiver mCurrentPlayingReceiver;

	// 手势View
	private GestureOverlayView mGestureView;
	private GestureLibrary mGestureLibrary;
	// 手势库加载状态
	private boolean loadState;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		File file = new File("/mnt/sdcard/xujinglei_font.ttf");
		if (file.exists()) {
			mTypeface = Typeface.createFromFile(file);
		}
		mDurationUpdateHandler = new DurationUpdateHandler();
		mLrcHandler = new LrcHandler();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View mPlayingContent = inflater.inflate(R.layout.playing, container, false);
		//设置初始界面
		setupLayout(mPlayingContent);
		//设置监听事件
		setupListener();

		return mPlayingContent;
	}

	private void setupListener() {
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
		
		mLrcSwitcher.setOnClickListener(this);
		
		//设置歌词监听
		setupLrcListener();
	}

	
	/**
	 * 设置歌词监听器
	 */
	private void setupLrcListener() {
		// 更新歌词ver3.0
		LrcController.getInstance().addOnLrcUpdateListener(new OnLrcUpdateListener() {
			@Override
			public void onUpdate(long time, String statement) {
				Message msg = Message.obtain();
				msg.obj = statement;
				mLrcHandler.sendMessage(msg);
				System.out.println("switcher:" + statement);
			}

			@Override
			public void onStart() {

			}
		});
	}

	/**
	 * 设置界面
	 * @param mPlayingContent
	 */
	private void setupLayout(View mPlayingContent) {
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
		mPlaylistBtn = (ImageView) mPlayingContent.findViewById(R.id.iv_playlist_btn);
		mGuestureBtn = (ImageView) mPlayingContent.findViewById(R.id.iv_guesture);
		mVolumeBtn = (ImageView) mPlayingContent.findViewById(R.id.iv_volume);
		mAlbumCover = (ImageView) mPlayingContent.findViewById(R.id.iv_album_cover);

		Log.i(TAG, "--383--daodao");
		Bitmap temp = MyImageUtils.createReflectionImageWithOrigin(BitmapFactory.decodeResource(getResources(),
				R.drawable.daodao));
		mAlbumCover.setImageBitmap(MyImageUtils.zoomBitmap(temp, 0.6f));
		
		//手势相关初始化
		initGueture(mPlayingContent);

		// 歌词相关初始化
		lrcViewContainer = (FrameLayout) mPlayingContent.findViewById(R.id.fl_cover_lrc_container);
		mLrcSwitcher = (TextSwitcher) mPlayingContent.findViewById(R.id.ts_lrc);
		mLrcSwitcher.setFactory(this);
		Animation inAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in);
		Animation outAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_out);
		mLrcSwitcher.setInAnimation(inAnimation);
		mLrcSwitcher.setOutAnimation(outAnimation);

		// 初始化Play按钮
		mPlayBtn.setImageResource(R.drawable.player_play_btn_selector);
		
		//初始化VisualizerView
		WeakReference<VisualizerView> visualizerView = new WeakReference<VisualizerView>((VisualizerView) mPlayingContent.findViewById(R.id.vv_visualizer_view));
		VisualizerUtils.updateVisualizerView(visualizerView);
	}
	
	private void initGueture(View mPlayingContent) {
		mGestureView = (GestureOverlayView) mPlayingContent.findViewById(R.id.myGestureView);
		mGestureLibrary = GestureLibraries.fromRawResource(getActivity(), R.raw.gestures);
		mGestureView.addOnGesturePerformedListener(new MusicGuestureListener());
		loadState = mGestureLibrary.load();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	
	@Override
	public void onResume() {
		super.onResume();
		// 注册广播接收者，用于接受从服务中发来的广播，包含当前播放的歌曲信息
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_CURRENT_SONG_INDEX);

		// 注册用于监听当前播放歌曲信息的BroadcastReceiver
		mCurrentPlayingReceiver = new CurrentPlayingReceiver();
		getActivity().registerReceiver(mCurrentPlayingReceiver, filter);
		mWillUpdate = true;
		sendPlayerCommand(Constants.ACTION_GET_CURRENT, null);
		Log.i(TAG, "onResume");
	
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mCurrentPlayingReceiver != null) {
			getActivity().unregisterReceiver(mCurrentPlayingReceiver);
		}
		mWillUpdate = false;
	}
	
	/**
	 * 歌词textSwitch工厂的实现方法
	 */
	@Override
	public View makeView() {
		mTextSwitcherTv = new TextView(getActivity());
		mTextSwitcherTv.setGravity(Gravity.CENTER);
		mTextSwitcherTv.setTextSize(20);
		mTextSwitcherTv.setTextColor(getResources().getColor(R.color.white));
		if (mTypeface != null) {
			mTextSwitcherTv.setTypeface(mTypeface);
		}

		return mTextSwitcherTv;
	}

	/**
	 * 当播放歌曲时会收到CurrentPlayingReceiver广播，此时调用此方法刷新播放器界面
	 */
	private void buildCurrentPlayingView() {
		Log.i(TAG, "buildCurrentPlayingView");
		if (mPlayBtn != null) {
			mPlayBtn.setImageResource(R.drawable.player_pause_btn_selector);
		}

		if (mLrcSwitcher != null) {
			mLrcSwitcher.setText("杯里鱼 爱音乐");
		}

		if (mCurrentSong != null) {
			//artists
			if (mCurrentSong.getArtists() != null && mCurrentSong.getArtists().size() > 0) {
				mArtist.setText(mCurrentSong.getArtists().get(0).getName());
			} else {
				mArtist.setText(R.string.love_life);
			}
			//album
			Album album = mCurrentSong.getAlbum();
			if (album != null) {
				mAlbum.setText(album.getTitle());
				mTitle.setText(mCurrentSong.getTitle());
				String imageUrl = album.getCoverUrl();
				Bitmap bitmap = null;

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inDither = false;
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				long albumId = 0;
				long songId = 0;
				try {
					albumId = Long.parseLong(album.getId());
					songId = Long.parseLong(mCurrentSong.getSongId());
				} catch (Exception e) {
					e.printStackTrace();
				}

				bitmap = MyImageUtils.createReflectionImageWithOrigin(LocalMediaUtil.getArtwork(getActivity(), songId, albumId));

				// TODO 专辑封面下载有问题，待修复
				if (bitmap == null) {
					String imageName = MyImageUtils.md5(mCurrentSong.getTitle());
					Log.i(TAG, "Loading ImageCover");
					bitmap = MyImageUtils.loadImage(imageName, imageUrl, new ImageCallback() {
						@Override
						public void loadImage(Bitmap bitmap, String imagePath) {
							mAlbumCover.setImageBitmap(MyImageUtils.getFitableBitmapWithReflection(getActivity(), bitmap));
						}
					});
				}

				if (bitmap == null) {
					bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.music_album_default);
					mAlbumCover.setImageBitmap(MyImageUtils.zoomBitmap(bitmap, 0.8f));
					mAlbumCover.setScaleType(ScaleType.CENTER);
				} else {
					mAlbumCover.setImageBitmap(MyImageUtils.getFitableBitmapWithReflection(getActivity(), bitmap));
					mAlbumCover.setScaleType(ScaleType.FIT_CENTER);
					bitmap.recycle();
				}
			}
			new Thread() {
				@Override
				public void run() {
					while (mWillUpdate) {
						Message msg = Message.obtain();
						msg.what = TIME_LINE_UPDATE;
						mDurationUpdateHandler.sendMessage(msg);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();

			DownloadEngine.getInstance().addDownloadListener(mCurrentSong.getSongUrl(), new DownloadListener() {

				@Override
				public void onDownloading(int size, int length) {
					Message msg = Message.obtain();
					msg.what = CACHING;
					msg.arg1 = size;
					msg.arg2 = length;
					mDurationUpdateHandler.sendMessage(msg);
				}

				@Override
				public void onDownloadFinish() {
					Message msg = Message.obtain();
					msg.what = CACHED;
					mDurationUpdateHandler.sendMessage(msg);

				}
			});
		}
		if (MusicPlayerService.mMediaPlayer != null) {
			long duration = MusicPlayerService.mMediaPlayer.getDuration();
			mTotalDuration.setText(TextFormatUtils.getPrettyFormatDuration(duration));
			mMainPlayerProgress.setMax((int) duration);
			// mMiniPlayerProgress.setMax((int) duration);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_player_play_pause:
			if (MusicPlayerService.mMediaPlayer != null) {
				if (MusicPlayerService.mMediaPlayer.isPlaying()) {
					sendPlayerCommand(Constants.ACTION_PAUSE, null);
					mPlayBtn.setImageResource(R.drawable.player_play_btn_selector);
				} else {
					sendPlayerCommand(Constants.ACTION_PLAY, null);
					mPlayBtn.setImageResource(R.drawable.player_pause_btn_selector);
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
			Intent intent = new Intent(getActivity(), PlaylistActivity.class);
			startActivity(intent);
			break;
		case R.id.iv_guesture:
			
			//TODO 需要解决手势识别与viewPager冲突
			if (mGestureView.isShown()) {
				mGestureView.setVisibility(View.GONE);
//				mViewPager.setTouchIntercept(true);
				Toast.makeText(getActivity(), "手势控制已关闭", 0).show();
			} else {
				mGestureView.setVisibility(View.VISIBLE);
//				mViewPager.setTouchIntercept(false);
				Toast.makeText(getActivity(), "手势控制已开启", 0).show();
			}
			break;
		case R.id.ts_lrc:
			if (lrcViewContainer.getChildCount() > 1) {
				lrcViewContainer.removeViewAt(1);
				mLrcSwitcher.setTag(true);
			} else {
				LrcView2 lrcView = new LrcView2(getActivity());
				lrcView.setBackgroundColor(Color.parseColor("#66333333"));
				lrcViewContainer.addView(lrcView);
				mLrcSwitcher.setTag(false);
				mLrcSwitcher.setText("杯里鱼音乐");
			}
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
		getActivity().sendBroadcast(intent);
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
							
						} else if ("next".equals(cmdName)) {
							action = Constants.ACTION_NEXT;
						} else if ("previous".equals(cmdName)) {
							action = Constants.ACTION_PREVIOUS;
						} else if ("music".equals(cmdName)) {
							Toast.makeText(getActivity(), "爱音乐，爱生活，更爱杯里鱼", 0).show();
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

	/**
	 * 当MusicPlayerService开始播放每一首歌曲时，将发送一个广播，包含当前播放歌曲的index信息 从而使播放器界面及时更新
	 * 
	 * @author Liu88122
	 */
	private class CurrentPlayingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "onReceive:buildCurrentPlayingView");
			String action = intent.getAction();
			if (Constants.ACTION_CURRENT_SONG_INDEX.equals(action)) {
				int currentSongIndex = intent.getIntExtra("currentSongIndex", -1);
				if (currentSongIndex != -1 && ((BaseApp) getActivity().getApplication()).playlist.size() > 0) {
					mCurrentSong = ((BaseApp) getActivity().getApplication()).playlist.get(currentSongIndex);
					buildCurrentPlayingView();
				}
			}
		}
	}
	
	private class LrcHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.obj != null) {
				String content = (String) msg.obj;
				Boolean show = (Boolean) mLrcSwitcher.getTag();
				if (show != null) {
					if (!show) {
						return;
					}
				}
				mLrcSwitcher.setText(content);
			}
		}
	}
	
	private class DurationUpdateHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case CACHING:
				int size = msg.arg1;
				int length = msg.arg2;
				if (size != 0 && length != 0) {
					int secondProgress = (int) (size * ((double) length / mMainPlayerProgress.getMax()));
					mMainPlayerProgress.setSecondaryProgress(secondProgress);
				}
				break;

			case CACHED:
				mMainPlayerProgress.setSecondaryProgress(mMainPlayerProgress.getMax());
				break;
			case TIME_LINE_UPDATE:
				int currentPosition = MusicPlayerService.mMediaPlayer.getCurrentPosition();
				mCurrentDuration.setText(TextFormatUtils.getPrettyFormatDuration(currentPosition));
				mMainPlayerProgress.setProgress(currentPosition);
				if ("00:00".equals(mTotalDuration.getText().toString())) {
					long duration = MusicPlayerService.mMediaPlayer.getDuration();
					mTotalDuration.setText(TextFormatUtils.getPrettyFormatDuration(duration));
				}
				// mMiniPlayerProgress.setProgress((int) currentPosition);
				break;
			}
		}
	}

	
	
}
