package com.cupfish.musicplayer.ui.view;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.cupfish.musicplayer.lrc.LrcController;
import com.cupfish.musicplayer.service.MusicPlayerService;
import com.cupfish.musicplayer.utils.TextFormatUtils;

public class LrcView2 extends ViewGroup {

	private Context context;

	private static final int LRC_START = 0;
	private static final int LRC_UPDATE = 1;

	private int mMeasuredWidth;
	private int mMeasuredHeight;
	private int mLrcTextNormalSize;
	private int mLrcTextFocusSize;
	private int mTopBottomPadding;
	private int mCurrentScrollY;
	private int mLrcTextHeight;

	private Paint mLrcNormalPaint;
	private Paint mLrcHighlightPaint;

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private LrcController mLrcController;
	private LrcUpdateHandler mLrcHandler;
	private LrcUpdateListener mLrcUpdateListener;
	private TreeMap<Long, String> statements;

	private int mLastMotionY;
	private boolean mDrawTimeline;
	private long mCurrentTime;

	public LrcView2(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public LrcView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public LrcView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	private void init() {
		mLrcTextNormalSize = 18;
		mLrcTextFocusSize = 22;

		mLrcNormalPaint = new Paint();
		mLrcNormalPaint.setAntiAlias(true);
		mLrcNormalPaint.setColor(Color.WHITE);
		mLrcNormalPaint.setTextSize(mLrcTextNormalSize);

		mLrcHighlightPaint = new Paint();
		mLrcHighlightPaint.setAntiAlias(true);
		mLrcHighlightPaint.setColor(Color.WHITE);
		mLrcHighlightPaint.setTextSize(mLrcTextFocusSize);

		mScroller = new Scroller(context);
		mLrcController = LrcController.getInstance();
		mLrcHandler = new LrcUpdateHandler();
		mLrcUpdateListener = new LrcUpdateListener(mLrcHandler);
		mLrcController.addOnLrcUpdateListener(mLrcUpdateListener);
		loadLrc();
	}

	private void loadLrc() {
		statements = mLrcController.getCurrentLrcTreeMap();
		addLrcStatementToView();
		requestLayout();
		invalidate();
	}

	private void addLrcStatementToView() {
		if (statements != null) {
			String statement;
			for (Map.Entry<Long, String> entry : statements.entrySet()) {
				statement = entry.getValue();
				TextView tv = new TextView(context);
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				tv.setLayoutParams(params);
				tv.setText(statement);
				tv.setTextSize(mLrcTextNormalSize);
				tv.setTextColor(Color.WHITE);
				tv.setGravity(Gravity.CENTER_HORIZONTAL);
				tv.setTag(entry.getKey());
				addView(tv);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mMeasuredHeight = MeasureSpec.getSize(heightMeasureSpec);
		mTopBottomPadding = mMeasuredHeight / 2 - mLrcTextNormalSize / 2;
		int count = getChildCount();
		if (count > 0) {
			mMeasuredHeight = 0;
			for (int i = 0; i < count; i++) {
				View child = getChildAt(i);
				measureChild(child, widthMeasureSpec, heightMeasureSpec);
				mLrcTextHeight = child.getMeasuredHeight();
				mMeasuredHeight = mMeasuredHeight + mLrcTextHeight;
			}
			mMeasuredHeight += mTopBottomPadding * 2;
		}
		mMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(mMeasuredWidth, mMeasuredHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mDrawTimeline && getChildCount() > 0) {
			canvas.drawLine(0, mTopBottomPadding + mCurrentScrollY, mMeasuredWidth, mTopBottomPadding + mCurrentScrollY, mLrcHighlightPaint);
			View view = getChildAt(0);
			if (view.getMeasuredHeight() > 0) {
				int currentChild = mCurrentScrollY / getChildAt(0).getMeasuredHeight();
				if (currentChild > 0 && currentChild < getChildCount()) {
					View child = getChildAt(currentChild);
					mCurrentTime = (Long) child.getTag();
					String text = TextFormatUtils.getPrettyFormatDuration(mCurrentTime);
					if (mCurrentTime == 3600000) {
						text = "OVER";
					}
					canvas.drawText(text, 0, mTopBottomPadding + mCurrentScrollY, mLrcHighlightPaint);
				}
			}
		}
		System.out.println("=========Height:" + getMeasuredHeight());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		mCurrentScrollY = getScrollY();
		System.out.println("scrollY:" + mCurrentScrollY);

		initVelocityTracker();
		mVelocityTracker.addMovement(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			mDrawTimeline = true;

			mLastMotionY = (int) event.getY();
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int currentY = (int) event.getY();
			int deltaY = currentY - mLastMotionY;
			mLastMotionY = currentY;
			if (getScrollY() - deltaY < 0 || getScrollY() - deltaY > getMeasuredHeight() - (2 * mTopBottomPadding)) {
				break;
			}
			scrollBy(0, -deltaY);
			break;
		case MotionEvent.ACTION_UP:

			mDrawTimeline = false;
			seekToCurrentTimeline();
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
				mScroller.forceFinished(true);
			}
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityY = (int) velocityTracker.getYVelocity();
			System.out.println("velocity:" + velocityY);
			mScroller.fling(0, getScrollY(), 0, -velocityY, 0, 0, 0, getMeasuredHeight() - (2 * mTopBottomPadding));

			invalidate();
			recycleVelocityTracker();
			break;
		}
		return true;
	}

	private void seekToCurrentTimeline() {
		// TODO Auto-generated method stub
		if(MusicPlayerService.mMediaPlayer != null){
			if(mCurrentTime > 0 && mCurrentTime < 3600000){
				MusicPlayerService.mMediaPlayer.seekTo((int)mCurrentTime);
			}
		}
		
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	private void initVelocityTracker() {
		synchronized (this) {
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
		}
	}

	private void recycleVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int count = getChildCount();
		if (count > 0) {
			int curTop = mTopBottomPadding;
			for (int i = 0; i < count; i++) {
				View v = getChildAt(i);
				v.layout(l, curTop, r, b);
				curTop += v.getMeasuredHeight();
			}
		}
	}

	private class LrcUpdateListener implements LrcController.OnLrcUpdateListener {

		Handler handler;

		public LrcUpdateListener(LrcUpdateHandler handler) {
			this.handler = handler;
		}

		@Override
		public void onStart() {
			Message msg = handler.obtainMessage();
			msg.what = LRC_START;
			handler.sendMessage(msg);
		}

		@Override
		public void onUpdate(long time, String statement) {
			Message msg = handler.obtainMessage();
			msg.what = LRC_UPDATE;
			Bundle data = new Bundle();
			data.putLong("time", time);
			data.putString("statement", statement);
			msg.setData(data);
			handler.sendMessage(msg);
		}

	}

	private class LrcUpdateHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case LRC_START:
				scrollTo(0, 0);
				break;
			case LRC_UPDATE:
				Bundle data = msg.getData();
				if (data != null) {
					String statement = data.getString("statement");
					long time = data.getLong("time");
					int count = getChildCount();
					if (count > 0) {
						for (int i = 0; i < count; i++) {
							TextView tv = (TextView) getChildAt(i);
							if (statement.equals(tv.getText().toString()) && time == (Long) tv.getTag()) {
								tv.setTextColor(Color.YELLOW);
								tv.setShadowLayer(3, 2, 2, Color.BLACK);
								if (!mDrawTimeline) {
									mScroller.startScroll(0, getScrollY(), 0, tv.getTop() - getScrollY() - mTopBottomPadding, 1000);
									System.out.println("top::::" + tv.getTop());
									;
								}
							} else {
								tv.setTextColor(Color.WHITE);
								tv.setShadowLayer(0, 0, 0, Color.WHITE);
							}
						}
					}
				}
				
				break;
			}

		}
	}

}
