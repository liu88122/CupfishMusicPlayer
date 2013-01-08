package com.cupfish.music.test;

import com.cupfish.music.utils.ConnectivityHelper;

import android.test.AndroidTestCase;

public class TestConnectivityHelper extends AndroidTestCase {

	public void test(){
		ConnectivityHelper.showNetworkType(getContext());
	}
}

