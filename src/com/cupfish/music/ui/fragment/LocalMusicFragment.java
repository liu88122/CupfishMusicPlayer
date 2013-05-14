package com.cupfish.music.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.cupfish.music.R;

public class LocalMusicFragment extends Fragment {

	private View mLocalContent;
	private GridView mCategoriesView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mLocalContent = inflater.inflate(R.layout.local5, container, false);
		setupLayout();
		setupListener();
		return mLocalContent;
	}
	private void setupLayout() {
		
	}
	
	private void setupListener(){
		
	}
	
	
	
	
	
}
