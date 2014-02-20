package com.cupfish.music.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cupfish.music.R;
import com.cupfish.music.common.Constants;
import com.cupfish.music.ui.BaiduMusicActivity;

public class OnlineMusicFragment extends Fragment implements OnClickListener {
	
	private View mOnlineContent;
	
	// 在线听内的2个按钮
	private ImageView mBaiduMusic;
	private ImageView mSongtaste;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mOnlineContent = inflater.inflate(R.layout.online, container, false);

		// 获得在线音乐条目按钮
		mBaiduMusic = (ImageView) mOnlineContent.findViewById(R.id.iv_baidu_music);

		// 语音识别按钮
		mSongtaste =  (ImageView) mOnlineContent.findViewById(R.id.iv_songtaste);

		// 在线音乐条目按钮的点击事件
		mBaiduMusic.setOnClickListener(this);
		mSongtaste.setOnClickListener(this);
		return mOnlineContent;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.iv_baidu_music:
			Intent baiduIntent = new Intent(getActivity(), BaiduMusicActivity.class);
			baiduIntent.putExtra("flag", Constants.FLAG_BAIDU_MUSIC);
			baiduIntent.putExtra("title", "百度音乐");
			startActivity(baiduIntent);
			break;
		case R.id.iv_songtaste:
			Toast.makeText(getActivity(), "敬请期待", 0).show();
			break;
		}
	}
	
}
