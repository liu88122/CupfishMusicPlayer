package com.cupfish.music.test;

import android.test.AndroidTestCase;

import com.cupfish.music.bean.AppUpdateInfo;
import com.cupfish.music.utils.UpdateManager;

public class TestUpdateManager extends AndroidTestCase {

	public void testCheckUpdate(){
		UpdateManager updateManager = UpdateManager.getInstance();
		AppUpdateInfo info = updateManager.checkUpdate(getContext());
		System.out.println(info.downloadUrl);
	}
	
}
