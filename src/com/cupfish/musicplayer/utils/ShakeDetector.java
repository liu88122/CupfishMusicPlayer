package com.cupfish.musicplayer.utils;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;

public class ShakeDetector implements SensorEventListener {

	//刷新时间间隔
	private static final int UPDATE_TIME = 100;
	//上次检测时间
	private long mLastUpdateTime;
	//上次检测的加速度在x, y , z方向上的分量
	private float mLastX, mLastY, mLastZ;
	private Context mContext;
	private SensorManager mSensorManager;
	private ArrayList<OnShakeListener> mListeners;
	//Shake敏感度
	public int shakeSen = 2000;
	
	public ShakeDetector(Context context){
		mContext = context;
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		mListeners = new ArrayList<OnShakeListener>();
	}
	
	/**
	 * 摇晃时触发事件接口
	 * @author Liu88122
	 *
	 */
	public interface OnShakeListener{
		void onShake();
	}
	
	//注册摇晃事件
	public void registerOnShakeListener(OnShakeListener listener){
		if(mListeners.contains(listener)){
			return;
		}
		mListeners.add(listener);
	}
	//取消注册
	public void unregisterOnShakeListener(OnShakeListener listener){
		mListeners.remove(listener);
	}
	
	public void start(){
		if(mSensorManager == null){
			throw new UnsupportedOperationException();
		}
		Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if(sensor == null){
			throw new UnsupportedOperationException();
		}
		boolean success = mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
		if(!success){
			throw new UnsupportedOperationException();
		}
	}
	
	public void stop(){
		if(mSensorManager != null){
			mSensorManager.unregisterListener(this);
		}
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		long currentTime = System.currentTimeMillis();
		long diffTime = currentTime - mLastUpdateTime;
		if(diffTime < UPDATE_TIME){
			return;
		}
		mLastUpdateTime = currentTime;
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		float deltaX = x - mLastX;
		float deltaY = y - mLastY;
		float deltaZ = z - mLastZ;
		mLastX = x;
		mLastY = y;
		mLastZ = z;
		float delta = FloatMath.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / diffTime * 10000;
		if(delta > shakeSen){
			this.notifyListeners();
		}
	}

	private void notifyListeners(){
		for(OnShakeListener listener : mListeners){
			listener.onShake();
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

}
