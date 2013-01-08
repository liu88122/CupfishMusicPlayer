package com.cupfish.music.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PinnedHeaderListView extends ListView {

	private PinnedHeaderAdapter mPinnedAdapter;
	private View mHeaderView;
	private boolean mHeaderViewVisible;
	
	private int mHeaderViewWidth;
	private int mHeaderViewHeight;
	
	public PinnedHeaderListView(Context context) {
		super(context);
	}
	public PinnedHeaderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setHeaderView(View view){
		mHeaderView = view;
		
		//Disable vertical fading when the pinned header is present
		if(mHeaderView != null){
			setFadingEdgeLength(0);
		}
		requestLayout();
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		mPinnedAdapter = (PinnedHeaderAdapter) adapter;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(mHeaderView != null){
			measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
			mHeaderViewWidth = mHeaderView.getMeasuredWidth();
			mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(mHeaderView != null){
			mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
			adjustHeaderView(getFirstVisiblePosition());
		}
	}
	
	public void adjustHeaderView(int position){
		if(mHeaderView  == null){
			return;
		}
		int state = mPinnedAdapter.getPinnedHeaderState(position);
		switch(state){
		case PinnedHeaderAdapter.PINNED_HEADER_GONE:
			mHeaderViewVisible = false;
			break;
		case PinnedHeaderAdapter.PINNED_HEADER_VISIBLE:
			mPinnedAdapter.configurePinnedHeader(mHeaderView, position);
			if(mHeaderView.getTop() != 0){
				mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
			}
			mHeaderViewVisible = true;
			break;
		case PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP:
			View firstView = getChildAt(0);
			int bottom = firstView.getBottom();
			int headerHeight = mHeaderView.getHeight();
			int deltaY = 0;
			System.out.println("bottom:" + bottom + " height:" + headerHeight + "deltaY:" + deltaY);
			if(bottom < headerHeight){
				deltaY = bottom - headerHeight;
			} else {
				deltaY = 0;
			}
			mPinnedAdapter.configurePinnedHeader(mHeaderView, position);
			if(mHeaderView != null){
				mHeaderView.layout(0, deltaY, mHeaderViewWidth, mHeaderViewHeight + deltaY);
			}
			mHeaderViewVisible = true;
			break;
		}
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if(mHeaderViewVisible){
			drawChild(canvas, mHeaderView, getDrawingTime());
		}
	}
	
	/**
	 * list adapter must implement this interface.
	 * @author liu88122
	 *
	 */
	public static interface PinnedHeaderAdapter{
		//STATE: don't show the header
		public static final int PINNED_HEADER_GONE = 0;
		
		//STATE: show the header at the top of the list
		public static final int PINNED_HEADER_VISIBLE = 1;
		
		//STATE: show the header, if the header extends beyond the bottom
		//      of the first shown element, push it up and clip
		public static final int PINNED_HEADER_PUSHED_UP = 2;
		
		//Get current pinned state
		int getPinnedHeaderState(int position);
		
		//Configure header view, such as set the content of the header view
		void configurePinnedHeader(View view, int position);
	}
	

}
