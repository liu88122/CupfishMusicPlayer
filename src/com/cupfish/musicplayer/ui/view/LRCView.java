package com.cupfish.musicplayer.ui.view;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.bean.LRC2;
import com.cupfish.musicplayer.lrc.LrcController;
import com.cupfish.musicplayer.lrc.LrcController.OnLrcUpdateListener;

public class LRCView extends ScrollView {

	public static final int LRC_UPDATE = 0;
	public static final int LRC_START = 1;
	private Context context;
	private LrcController mController;
	private LrcListener mLRCListener;
	private Handler mLrcHandler;
	private TreeMap<Long, String> statements;
	private LinearLayout statementContainer;
	private Typeface mTypeface;
	private int lastY;
	private int deltaY;
	private int timelineY = Integer.MAX_VALUE;
	private boolean autoScroll = true;
	private Paint paint;
	private MotionEvent lastDownMotionEvent;
	private boolean isFirstDown = true;
	
	public LRCView(Context context) {
		super(context);
		this.context = context;
		mLrcHandler = new LRCHandler();
		mLRCListener = new LrcListener(mLrcHandler);
		mController = LrcController.getInstance();
		mController.addOnLrcUpdateListener(mLRCListener);
		
		statementContainer = new LinearLayout(context);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.MATCH_PARENT);
		statementContainer.setOrientation(LinearLayout.VERTICAL);
		statementContainer.setLayoutParams(param);
		addView(statementContainer);
		
		//隐藏scrollBar
		setVerticalScrollBarEnabled(false);
		loadLrc();
		
		paint = new Paint();
		paint.setColor(context.getResources().getColor(R.color.red_light));
		paint.setAntiAlias(true);
		paint.setTextSize(30);
		
		setPadding(0, 0, 0, 60);
		
		//屏幕常亮
		setKeepScreenOn(true);
	}
	
	private void loadLrc(){
		statements = mController.getCurrentLrcTreeMap();
		addLrcStatementToView();
	}
	
	private void addLrcStatementToView(){
		statementContainer.removeAllViews();
		if (statements != null && statements.size() > 0) {
			for (Map.Entry<Long, String> entry : statements.entrySet()) {
				TextView tv = new TextView(context);
				tv.setText(entry.getValue());
				tv.setTag(entry.getKey());
				tv.setTextColor(Color.WHITE);
				tv.setTextSize(17);
				tv.setPadding(2, 2, 2, 2);
				if (mTypeface != null) {
					tv.setTypeface(mTypeface);
				}
				ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				tv.setGravity(Gravity.CENTER_HORIZONTAL);
				statementContainer.addView(tv, params);
			}

		} else {
			TextView tv = new TextView(context);
			tv.setText("NO LRC");
			ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			addView(tv, params);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(timelineY == Integer.MAX_VALUE && getHeight() != 0){
			timelineY  = 0;
		}
		if(timelineY < 0){
			timelineY = 0;
		}
		if (!autoScroll) {
			canvas.drawText("我爱的音乐", 0, timelineY + getScrollY() - 10, paint);
			canvas.drawLine(0, timelineY + getScrollY(), getWidth(), timelineY + getScrollY(), paint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return super.onTouchEvent(ev);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int scrollY = getScrollY();
		if (getChildAt(0) != null) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				autoScroll = false;
				lastY = (int) ev.getY();
				// if(scrollY == 0){
				// return true;
				// }
				lastDownMotionEvent = ev;
			case MotionEvent.ACTION_MOVE:
				int currentY = (int) ev.getY();
				deltaY = currentY - lastY;
				System.out.println("ScrollY:" + scrollY + " lastY:" + lastY + " currentY:" +currentY+ " deltaY:" + deltaY);
				lastY = currentY;
				try {
					if (scrollY == 0) {
						timelineY -= deltaY;
						if (timelineY < getHeight() / 2) {
							invalidate();
							return true;
						}else{
							timelineY = getHeight() / 2;
							if(isFirstDown){
								isFirstDown = false;
								lastDownMotionEvent.setLocation(ev.getX(), currentY - deltaY);
								return super.onTouchEvent(lastDownMotionEvent);
							}
							return super.dispatchTouchEvent(ev);
						}
					} else {
						timelineY = getHeight() / 2;
					}
//					if (timelineY == Integer.MAX_VALUE && getHeight() != 0) {
//						timelineY = getHeight() / 2;
//					}
					if (timelineY < 0) {
						timelineY = 0;
					}
				} finally {
					System.out.println(timelineY);
					invalidate();
				}
				break;
			}
		}
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			autoScroll = true;
		}
		return super.dispatchTouchEvent(ev);
	}

	private class LrcListener implements OnLrcUpdateListener{
		
		private Handler handler;
		
		public LrcListener(Handler handler){
			this.handler = handler;
		}
		@Override
		public void onUpdate(long time, String statement) {
			Message msg = Message.obtain();
			msg.what = LRC_UPDATE;
			Bundle data= new Bundle();
			data.putLong("time", time);
			data.putString("statement", statement);
			msg.setData(data);
			mLrcHandler.sendMessage(msg);
		}
		@Override
		public void onStart() {
			mLrcHandler.sendEmptyMessage(LRC_START);
		}
		
	}
	
	private class LRCHandler extends Handler{
		TextView tv;
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case LRC_START:
				scrollTo(0, 0);
				loadLrc();
				break;
			case LRC_UPDATE:
				Bundle data = msg.getData();
				Long time = (Long) data.get("time");
				if(statementContainer!=null){
					if(tv!= null){
						tv.getPaint().setFakeBoldText(false);
						tv.setTextColor(Color.WHITE);
						tv.setTextSize(17);
						tv.setShadowLayer(0, 0, 0, Color.WHITE);
						//TODO 应该将当前歌词粗体改为正常
					}
					tv = (TextView) statementContainer.findViewWithTag(time);
					tv.setTextSize(20);
					tv.setTextColor(Color.WHITE);
//					Paint mPaint = tv.getPaint();
//					mPaint.setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
//					mPaint.setAntiAlias(true);
					tv.getPaint().setFakeBoldText(true);
					tv.setShadowLayer(3, 2, 2, context.getResources().getColor(R.color.purple_light));
					int height = getHeight();
					if(tv.getTop() > (height / 2) && autoScroll){
						smoothScrollTo(0, tv.getTop() - height/2 + tv.getHeight());
					}
				}
				break;
			}
		}
	}
	
	public void setLrcTypeface(Typeface typeface){
		mTypeface = typeface;
		addLrcStatementToView();
	}
	
}
