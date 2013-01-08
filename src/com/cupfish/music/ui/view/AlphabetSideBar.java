package com.cupfish.music.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.cupfish.music.R;

public class AlphabetSideBar extends View {
	
	private char[] alphabet;
	private SectionIndexer mSectionIndexer;
	private ListView mListView;
	private Paint mPaint;
	private int mCurrentFontColor;
	private int mNormalFontColor;
	private int mPressedFontColor;
	private int mNormalBackgroundColor;
	private int mPressedBackgroundColor;
	private int mPaddingBottom;
	private int curAlphabetPosition  = -1;
	
	private AlphabetClickListener mClickListener;

	public AlphabetSideBar(Context context){
		super(context);
		init(context);
	}
	
	public AlphabetSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AlphabetSideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context){
		alphabet = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G',
								'H', 'I', 'J', 'K', 'L', 'M', 'N',
								'O', 'P', 'Q', 'R', 'S', 'T',
								'U', 'V', 'W', 'X', 'Y', 'Z', '#'};
		
		mPressedBackgroundColor = context.getResources().getColor(R.color.gray_light);
		mNormalBackgroundColor = context.getResources().getColor(R.color.translucent_gray);
		setBackgroundColor(mNormalBackgroundColor);
		
		mNormalFontColor = Color.WHITE;
		mPressedFontColor = context.getResources().getColor(R.color.main_blue_light);
		mCurrentFontColor = mNormalFontColor;
		
		mPaint = new Paint(mCurrentFontColor);
		mPaint.setColor(mNormalFontColor);
		mPaint.setTextSize(20);
		//设置下划线
		mPaint.setUnderlineText(false);
		//设置删除线，不知道为什么默认会显示
		mPaint.setStrikeThruText(false);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaddingBottom = 5;
		
	}
	
	public void setListView(ListView listView){
		mListView = listView;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int y = (int) event.getY();
		curAlphabetPosition = y / ((getMeasuredHeight()- mPaddingBottom) / alphabet.length);
		if(curAlphabetPosition >= alphabet.length){
			curAlphabetPosition = alphabet.length - 1;
		}else if(curAlphabetPosition < 0){
			curAlphabetPosition = 0;
		}
		System.out.println(curAlphabetPosition);
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		
		switch(action){
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			setBackgroundColor(mPressedBackgroundColor);
			if(mSectionIndexer == null){
				mSectionIndexer = (SectionIndexer) mListView.getAdapter();
			}
			int position = mSectionIndexer.getPositionForSection(alphabet[curAlphabetPosition]);
			if(position == -1){
				return true;
			}
			mListView.setSelection(position);
			if(mClickListener != null){
				mClickListener.onPressed(alphabet[curAlphabetPosition]);
			}
			break;
		case MotionEvent.ACTION_UP:
			setBackgroundColor(mNormalBackgroundColor);
			mCurrentFontColor = mNormalFontColor;
			curAlphabetPosition = -1;
			if(mClickListener != null){
				mClickListener.onReleased();
			}
			break;
		}
		invalidate();
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		float widthCenter = getMeasuredWidth() / 2;
		float itemHeight = (getMeasuredHeight() - mPaddingBottom) / alphabet.length;
		for(int i=0; i<alphabet.length; i++){
			if(curAlphabetPosition == i){
				mCurrentFontColor = mPressedFontColor;
			} else {
				mCurrentFontColor = mNormalFontColor;
			}
			mPaint.setColor(mCurrentFontColor);
			canvas.drawText(Character.toString(alphabet[i]), widthCenter, itemHeight + itemHeight * i, mPaint);
		}
		super.onDraw(canvas);
	}
	
	public void setAlphabetClickListener(AlphabetClickListener listener){
		this.mClickListener = listener;
	}
	
	public interface AlphabetClickListener{
		void onPressed(char c);
		void onReleased();
	}
}
