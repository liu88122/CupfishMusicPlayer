package com.cupfish.musicplayer.test;

import com.cupfish.musicplayer.utils.ConnectivityHelper;

import android.test.AndroidTestCase;

public class TestConnectivityHelper extends AndroidTestCase {

	public void test(){
		ConnectivityHelper.showNetworkType(getContext());
	}
}

