package com.cupfish.music.service;

import com.cupfish.music.utils.LocalMusicScanner;

import android.app.IntentService;
import android.content.Intent;

public class MusicScanService extends IntentService {

	public MusicScanService() {
		super("MusicScanService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		LocalMusicScanner scanner = LocalMusicScanner.getInstance(this);
		scanner.start();
	}

}
