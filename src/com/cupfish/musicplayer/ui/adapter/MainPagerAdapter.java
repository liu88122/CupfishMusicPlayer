package com.cupfish.musicplayer.ui.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.cupfish.musicplayer.ui.view.MyViewPager;

public class MainPagerAdapter extends PagerAdapter {

	private List<View> mViews;
	
	public MainPagerAdapter(List<View> mViews){
		this.mViews = mViews;
	}
	
	@Override
	public int getCount() {
		return mViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((MyViewPager)container).removeView(mViews.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((MyViewPager)container).addView(mViews.get(position));
		return mViews.get(position);
	}

	
}
