package com.cupfish.musicplayer.test;

import android.test.AndroidTestCase;

import com.cupfish.musicplayer.domain.AppUpdateInfo;
import com.cupfish.musicplayer.utils.UpdateManager;

public class TestUpdateManager extends AndroidTestCase {

	public void testCheckUpdate(){
		UpdateManager updateManager = UpdateManager.getInstance();
		AppUpdateInfo info = updateManager.checkUpdate(getContext());
		System.out.println(info.downloadUrl);
	}
	
}
