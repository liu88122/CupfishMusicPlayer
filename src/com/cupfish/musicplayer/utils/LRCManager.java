package com.cupfish.musicplayer.utils;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cupfish.musicplayer.domain.LRC;

public class LRCManager {
	private ExecutorService service;
	private static ArrayList<LRC> lrcList = new ArrayList<LRC>();

	private LRCManager() {
		service = Executors.newSingleThreadExecutor();
	}

	private static final LRCManager manager = new LRCManager();

	public static LRCManager getInstance() {
		return manager;
	}

	public void addLRC(LRC lrc) {
		lrcList.add(lrc);
		service.execute(lrc);
	}

	public void removeLRC() {
		service.shutdownNow();
		for (LRC lrc : lrcList) {
			lrc.unregisterReceiver();
		}
		lrcList.clear();
		service = Executors.newSingleThreadExecutor();
	}
}
