package com.cupfish.musicplayer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectivityHelper {
	public static void showNetworkType(Context context){
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = manager.getActiveNetworkInfo();
		if(mNetworkInfo == null){
			Toast.makeText(context, "当前网络连接不可用，若在线听歌请打开数据连接", 1).show();
		}else{
			String networkType = mNetworkInfo.getTypeName();
			if("mobile".equals(networkType)){
				Toast.makeText(context, "当前网络不是WIFI连接，在线听歌会消耗大量移动流量", 1).show();
			}
		}
	}
	
	
	public static boolean isNetworkActivie(Context context){
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = manager.getActiveNetworkInfo();
		if(mNetworkInfo != null && mNetworkInfo.isConnected()){
			return true;
		}
		return false;
	}
}
