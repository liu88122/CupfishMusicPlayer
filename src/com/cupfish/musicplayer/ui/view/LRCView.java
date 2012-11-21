package com.cupfish.musicplayer.ui.view;

import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
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
	private LRC2 mLrc;
	private LrcController mController;
	private LrcListener mLRCListener;
	private Handler mLrcHandler;
	private TreeMap<Long, String> statements;
	private LinearLayout statementContainer;
	
	public LRCView(Context context) {
		super(context);
		this.context = context;
		mLrcHandler = new LRCHandler();
		mLRCListener = new LrcListener(mLrcHandler);
		mController = LrcController.getInstance();
		mController.addOnLrcUpdateListener(mLRCListener);
		
		//隐藏scrollBar
		setVerticalScrollBarEnabled(false);
		load();
		
	}
	
	private void load(){
		mLrc = mController.getCurrentLRC();
		addLrcStatementToView();
	}
	
	private void addLrcStatementToView(){
		removeAllViews();
		if(mLrc != null){
			statements = mLrc.getLrcsMap();
			statementContainer = new LinearLayout(context);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 
					LinearLayout.LayoutParams.MATCH_PARENT);
			statementContainer.setOrientation(LinearLayout.VERTICAL);
			statementContainer.setLayoutParams(param);
			if (statements != null && statements.size() > 0) {
				for (Map.Entry<Long, String> entry : statements.entrySet()) {
					TextView tv = new TextView(context);
					tv.setText(entry.getValue());
					tv.setTag(entry.getKey());
					tv.setTextColor(Color.WHITE);
					ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					tv.setGravity(Gravity.CENTER_HORIZONTAL);
					statementContainer.addView(tv, params);
				}
			}
			
			addView(statementContainer);
		}else{
			TextView tv = new TextView(context);
			tv.setText("NO LRC");
			ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			addView(tv, params);
		}
	}
	
	private class LrcListener implements OnLrcUpdateListener{
		
		private Handler handler;
		private int lineNum;
		
		public LrcListener(Handler handler){
			this.handler = handler;
		}
		@Override
		public void onUpdate(long time, String statement) {
			lineNum ++;
			Message msg = Message.obtain();
			msg.what = LRC_UPDATE;
			msg.arg1 = lineNum;
			Bundle data= new Bundle();
			data.putLong("time", time);
			data.putString("statement", statement);
			msg.setData(data);
			mLrcHandler.sendMessage(msg);
		}
		@Override
		public void onStart() {
			lineNum = 0;
			mLrcHandler.sendEmptyMessage(LRC_START);
		}
		
	}
	
	private class LRCHandler extends Handler{
		TextView tv ;
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case LRC_START:
				scrollTo(0, 0);
				load();
				break;
			case LRC_UPDATE:
				Bundle data = msg.getData();
				int lineNum = msg.arg1;
				if(statementContainer!=null){
					if(tv!= null){
						tv.setTextColor(Color.WHITE);
					}
					tv = (TextView) statementContainer.getChildAt(lineNum);
					tv.setTextColor(context.getResources().getColor(R.color.green_light));
					int height = getHeight();
					if(tv.getTop() > (height / 2)){
						scrollBy(0, tv.getHeight());
					}
				}
				break;
			}
			
		}
	}
}
