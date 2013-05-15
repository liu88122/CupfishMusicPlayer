package com.cupfish.music.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.cupfish.music.R;
import com.cupfish.music.ui.LocalAllActivity;

public class LocalMusicFragment extends Fragment implements OnClickListener {

	private View mLocalContent;
	private Button mAllMusicBtn;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mLocalContent = inflater.inflate(R.layout.local5, container, false);
		setupLayout();
		setupListener();
		return mLocalContent;
	}
	private void setupLayout() {
		mAllMusicBtn = (Button) mLocalContent.findViewById(R.id.btn_all_songs);
	}
	
	private void setupListener(){
		mAllMusicBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_all_songs:
			Intent intent = new Intent(getActivity(), LocalAllActivity.class);
			startActivity(intent);
			break;
		}
	}
	
	
	
	
}
