/**
 * Copyright 2011, Felix Palmer
 *
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
package com.cupfish.music.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.media.audiofx.Visualizer;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;

import com.cupfish.music.R;
import com.cupfish.music.common.Constants;
import com.cupfish.music.utils.visualizer.AudioData;
import com.cupfish.music.utils.visualizer.BarGraphRenderer;
import com.cupfish.music.utils.visualizer.FFTData;
import com.cupfish.music.utils.visualizer.Renderer;
import com.cupfish.music.utils.visualizer.SolidBarGraphRenderer;
import com.cupfish.music.utils.visualizer.WaveformRenderer;

public class VisualizerView extends View {

  private byte[] mBytes;
  private byte[] mFFTBytes;
  private Rect mRect = new Rect();
  
  private Renderer mRenderer;
  String type = null;

  public VisualizerView(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs);

    mBytes = null;
    mFFTBytes = null;

    //TODO 暂时先不用保存设置
//    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
//    type = sp.getString(Constants.VISUALIZATION_TYPE, getResources().getString(R.string.visual_none));
//    
//    if( type.equals(getResources().getString(R.string.visual_solidbargraph)) ){
//    	
//    	mRenderer = new SolidBarGraphRenderer(context);
//    	
//    }
//    else if ( type.equals(getResources().getString(R.string.visual_waveform)) ){
//
//        mRenderer = new WaveformRenderer(context);
//    	
//    }
//    else if ( type.equals(getResources().getString(R.string.visual_bargraph)) ){
//
//    	mRenderer = new BarGraphRenderer(context);
//    	
//    }    
    
    mRenderer = new BarGraphRenderer(context);
    
  }

  public VisualizerView(Context context, AttributeSet attrs)
  {
    this(context, attrs, 0);
  }

  public VisualizerView(Context context)
  {
    this(context, null, 0);
  }

  /**
   * Pass data to the visualizer. Typically this will be obtained from the
   * Android Visualizer.OnDataCaptureListener call back. See
   * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
   * @param bytes
   */
  public void updateVisualizer(byte[] bytes) {
    mBytes = bytes;
    invalidate();
  }

  /**
   * Pass FFT data to the visualizer. Typically this will be obtained from the
   * Android Visualizer.OnDataCaptureListener call back. See
   * {@link Visualizer.OnDataCaptureListener#onFftDataCapture }
   * @param bytes
   */
  public void updateVisualizerFFT(byte[] bytes) {
    mFFTBytes = bytes;
    invalidate();
  }

  Bitmap mCanvasBitmap;
  Canvas mCanvas;


  @SuppressLint("DrawAllocation")
@Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
//    if(type.equals("None"))
//    	return;
    // Create canvas once we're ready to draw
    mRect.set(0, 0, getWidth(), getHeight());

    if(mCanvasBitmap == null)
    {
      mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
    }
    if(mCanvas == null)
    {
      mCanvas = new Canvas(mCanvasBitmap);
    }
    
    //Clear canvas
    mCanvas.drawColor(0, Mode.CLEAR);


    if (mBytes != null) {
      // Render all audio renderers
      AudioData audioData = new AudioData(mBytes);
      mRenderer.render(mCanvas, audioData, mRect);
    }

    if (mFFTBytes != null) {
      // Render all FFT renderers
      FFTData fftData = new FFTData(mFFTBytes);
      mRenderer.render(mCanvas, fftData, mRect);
    }

    canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
  }
}