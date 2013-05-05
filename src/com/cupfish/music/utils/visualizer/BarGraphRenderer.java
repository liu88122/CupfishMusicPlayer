/**
 * Copyright 2011, Felix Palmer
 *
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
package com.cupfish.music.utils.visualizer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;

import com.cupfish.music.R;

public class BarGraphRenderer extends Renderer
{
  private Context mContext = null;
  
  private int[] mData = null;
  
  private int[] mLastMax = null;
  
  private Handler mHandler;
  
  private long mDropInterval = 500;
  
  private Runnable mMaxBarCalc = new Runnable() {
	@Override
	public void run() {
		for(int i =0; i<mLastMax.length; i++){
			if((mLastMax[i] - 1) <=  0){
				mLastMax[i] = 0;
			} else {
				mLastMax[i] = mLastMax[i] - 1;
			}
		}
		mHandler.postDelayed(this, mDropInterval);
	}
};

  public BarGraphRenderer(Context context)
  {
    super();
    mContext = context;
    mData = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    mLastMax = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    mHandler = new Handler();
    mHandler.postDelayed(mMaxBarCalc, mDropInterval);
  }

  @Override
  public void onRender(Canvas canvas, AudioData data, Rect rect)
  {
    // Do nothing, we only display FFT data
  }

  /**
   * Renders a 14 line bar graph/ histogram of the FFT data
   */
  @Override
  public void onRender(Canvas canvas, FFTData data, Rect rect)
  {
	  //space between lines of graph  
	  float space = 4f;

	  Resources resources = mContext.getResources();
	  NinePatchDrawable bg =  (NinePatchDrawable) resources.getDrawable(R.drawable.bar_graph);
	  DisplayMetrics metrics = resources.getDisplayMetrics();
	  //margin from left/right edges
	  int margin = (int) ( ( 16 * (metrics.densityDpi/160f) ) + 0.5f );
  
	  //Calculate width of each bar
	  float bar_width = ( ( rect.width() - ((13 * space) + (margin * 2)) ) / 14 );
	  //calculate length between the start of each bar
	  float next_start = bar_width + space;
	  
	  for (int i = 0; i < 14; i++) {
			//set x start of bar
			float x1 = margin + (i * next_start);
		
			//calculate height of bar based on sampling 4 data points
			byte rfk = data.bytes[ (10 * i)];
			byte ifk = data.bytes[ (10 * i + 1)];
			float magnitude = (rfk * rfk + ifk * ifk);
			int dbValue = (int) (10 * Math.log10(magnitude));
			rfk = data.bytes[ (10 * i + 2)];
			ifk = data.bytes[ (10 * i + 3)];
			magnitude = (rfk * rfk + ifk * ifk);
			dbValue = (int) ( (10 * Math.log10(magnitude)) + dbValue) / 2;
		
			//Average with previous bars value(reduce spikes / smoother transitions)
			dbValue =( mData[i] +  ((dbValue < 0) ? 0 : dbValue) ) / 2;
			mData[i] = dbValue;
		
			//only jump height on multiples of 5
			if(dbValue >= 5)
				dbValue = (int) Math.floor(dbValue/5) * 5;
		
			//bottom edge of canvas
			float y1 = rect.height();
			int blockHeight = 10;
			int numBlocks = (int) Math.floor((dbValue * 8) / blockHeight);
			if(numBlocks > mLastMax[i]){
				mLastMax[i] = numBlocks;
			}
			//cycle through and render individual blocks
			for( int j = 0; j < numBlocks; j++ ){
					int yEnd = (int)( y1 - ( blockHeight * j ));
					Rect nRect = new Rect((int)x1, yEnd - blockHeight, (int)(x1+bar_width), yEnd);
					bg.setBounds(nRect);
					bg.draw(canvas);
			}	
			
			int yEnd = (int)( y1 - ( blockHeight * mLastMax[i] ));
			Rect nRect = new Rect((int)x1,  yEnd - blockHeight, (int)(x1+bar_width), yEnd);
//			ColorDrawable bg1 = new ColorDrawable(R.color.main_blue_light);
			bg.setBounds(nRect);
			bg.draw(canvas);
	  }
  }
}
