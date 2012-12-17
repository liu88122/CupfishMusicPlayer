package com.cupfish.musicplayer.ui.view;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import com.cupfish.musicplayer.lrc.LrcController;

public class LrcView2 extends View {

	private Context context;
	
	private int mPaddingTop;
	private int mPaddingBottom;
	private int mMeasuredWidth;
	private int mMeasuredHeight;
	private int mLrcTextNormalSize;
	private int mLrcTextFocusSize;
	private int mLrcLineHeight;
	
	private Paint mLrcNormalPaint;
	private Paint mLrcHighlightPaint;
	
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	
	private LrcController mLrcController;
	private TreeMap<Long, String> statements;
	
	private int mLastMotionY;
	
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

	private void init(){
		mLrcTextNormalSize = 17*2;
		mLrcTextFocusSize = 20*2;
		mLrcLineHeight =  mLrcTextNormalSize + 10;
		
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
		loadLrc();
	}
	
	private void loadLrc(){
		statements = mLrcController.getCurrentLrcTreeMap();
//		addLrcStatementToView();
		requestLayout();
		invalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mMeasuredHeight = MeasureSpec.getSize(heightMeasureSpec);
		mPaddingBottom = mPaddingTop = mMeasuredHeight / 2;
		if(statements != null && statements.size() > 0){
			mMeasuredHeight = (statements.size() - 1) * mLrcLineHeight 
								+ mLrcLineHeight 	
								+ mPaddingBottom
								+ mPaddingTop;
		}
		mMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(mMeasuredWidth, mMeasuredHeight);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(statements != null){
			String statement;
			int count = 1;
			for(Map.Entry<Long, String> entry: statements.entrySet()){
				statement = entry.getValue();
				int textWidth = (int) mLrcNormalPaint.measureText(statement);
				canvas.drawText(statement, (mMeasuredWidth - textWidth) / 2, count * mLrcLineHeight + mPaddingTop, mLrcNormalPaint);
				count++;
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		System.out.println("scrollY" + getScrollY());
		
		initVelocityTracker();
		mVelocityTracker.addMovement(event);
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = (int) event.getY();
			if(!mScroller.isFinished()){
				mScroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int currentY = (int) event.getY();
			int deltaY = currentY - mLastMotionY;
			mLastMotionY = currentY;
			if(getScrollY() - deltaY < 0 || getScrollY() - deltaY > getMeasuredHeight() -(2 * mPaddingTop)){
				break;
			}
			scrollBy(0, -deltaY);
			break;
		case MotionEvent.ACTION_UP:
			if(!mScroller.isFinished()){
				mScroller.abortAnimation();
				mScroller.forceFinished(true);
			}
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityY = (int) velocityTracker.getYVelocity();
			System.out.println("velocity:" + velocityY);
			mScroller.fling(0, getScrollY(), 0, -velocityY, 0, 0, 0, getMeasuredHeight() - (2 * mPaddingBottom));
			// if(velocityY > 0){
			// mScroller.startScroll(0, getScrollY(), 0, 0);
			// }else{
			// mScroller.startScroll(0, getScrollY(), 0, getMeasuredHeight() -
			// (2 * mPaddingBottom));
			// }
			invalidate();
			recycleVelocityTracker();
			break;
		}
		return true;
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
	
	private void initVelocityTracker(){
		synchronized (this) {
			if(mVelocityTracker == null){
				mVelocityTracker = VelocityTracker.obtain();
			}
		}
	}
	
	private void recycleVelocityTracker(){
		if(mVelocityTracker != null){
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

}
