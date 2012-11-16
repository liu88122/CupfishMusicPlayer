package com.cupfish.musicplayer.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

	private Context context;
	private boolean willIntercept = true;
	
	public MyViewPager(Context context) {
		super(context);
		this.context = context;
	}
	
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(willIntercept){
			return super.onInterceptTouchEvent(arg0);
		}else{
			return false;
		}
		
	}

	/**
	 * 设置ViewPager是否拦截点击事件
	 * @param value if true, ViewPager拦截点击事件
	 * 				if false, ViewPager将不能滑动，ViewPager的子View可以获得点击事件
	 * 				主要受影响的点击事件为横向滑动
	 *
	 */
	public void setTouchIntercept(boolean value){
		willIntercept = value;
	}
	

}
