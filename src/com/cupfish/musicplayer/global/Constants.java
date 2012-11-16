package com.cupfish.musicplayer.global;

import android.os.Environment;

public interface Constants {

	// 音乐、图片、歌词保存路径
	String SDCARD_CACHE_IMG_PATH = Environment.getExternalStorageDirectory().getPath() + "/cupfish/images";
	String SDCARD_MUSIC_SAVE_PATH = Environment.getExternalStorageDirectory().getPath() + "/cupfish/music";
	String SDCARD_LRC_SAVE_PATH = Environment.getExternalStorageDirectory().getPath() + "/cupfish/lrc";

	// 播放器操作命令
	String ACTION_PLAY = "com.cupfish.action.ACTION_PLAY";
	String ACTION_PAUSE = "com.cupfish.action.ACTION_PAUSE";
	String ACTION_STOP = "com.cupfish.action.ACTION_STOP";
	String ACTION_START = "com.cupfish.action.ACTION_START";
	String ACTION_SEEK_TO = "com.cupfish.action.ACTION_SEEK_TO";
	String ACTION_NEXT = "com.cupfish.action.ACTION_NEXT";
	String ACTION_PREVIOUS = "com.cupfish.action.ACTION_PREVIOUS";
	String ACTION_CURRENT_SONG_INDEX = "com.cupfish.action.ACTION_SONG_INDEX";
	String ACTION_ADD_TO_PLAYLIST = "com.cupfish.action.ACTION_ADD_TO_PLAYLIST";
	String ACTION_PLAYLIST_REFRESH = "com.cupfish.action.ACTION_PLAYLIST_REFRESH";
	String ACTION_PLAYLIST_REFRESH_FINISH = "com.cupfish.action.ACTION_PLAYLIST_REFRESH_FINISH";

	// 播放器播放模式
	int PLAYMODE_NORMAL = 1001;
	int PLAYMODE_SHUFFLE = 1002;
	int PLAYMODE_REPEAT_ALL = 1003;
	int PLAYMODE_REPEAT_ONE = 1004;

	// 下载相关
	int DOWNLOAD_START = 2001;
	int DOWNLOAD_DOWNLOADING = 2002;
	int DOWNLOAD_FINISH = 2003;
	int DOWNLOAD_CANCEL = 2004;
	int DOWNLOAD_FILE_MP3 = 2005;
	int DOWNLOAD_FILE_LRC = 2006;
	String ACTION_ADD_DOWNLOAD_TASK = "com.cupfish.action.ACTION_ADD_DOWNLOAD_TASK";

	// 谷歌音乐榜单
	String[] GOOGLE_MUSIC_LIST = { "华语新歌", "欧美新歌", "华语热歌", "日韩热歌", "流行100", "摇滚100" };
	// 百度音乐榜单
	String[] BAIDU_MUSIC_LIST = { "歌曲TOP500",
									"新歌TOP100", 
									"华语新歌榜", 
									"影视金曲榜", 
									"情歌对唱榜", 
									"网络歌曲榜", 
									"经典老歌榜", 
									"摇滚榜", 
									"爵士榜", 
									"民谣榜",
									"KTV热歌榜",
									"好声音金曲榜"};

	int FLAG_LOCAL_MUSIC = 2010;
	int FLAG_GOOGLE_MUSIC = 2011;
	int FLAG_BAIDU_MUSIC = 2012;
	
	String LRC_AD = "杯里鱼音乐 QQ:262720041";

}
