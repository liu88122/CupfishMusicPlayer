package com.cupfish.musicplayer.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import android.test.AndroidTestCase;

import com.cupfish.musicplayer.bean.Song;
import com.cupfish.musicplayer.utils.BaiduTingHelper;
import com.cupfish.musicplayer.utils.LRCReader;

public class TestLRCReader extends AndroidTestCase {
	
	public void testLrcRead() throws Exception{

		Song song = BaiduTingHelper.getSongById("13869261");
		TreeMap<Long, String> result = LRCReader.getLRCTreeMap(song, getContext());
		Iterator timeIterator = result.keySet().iterator();
		while(timeIterator.hasNext()){
			System.out.println(timeIterator.next());
		}
	}
	
	public void testLrcReader2() throws Exception{
		TreeMap<Long, String> result = LRCReader.parseLRCFromFile(new File("/mnt/sdcard/cupfish/lrc/lanlianhua.lrc"));
		System.out.println(result.size());
		for(Map.Entry<Long, String> entry : result.entrySet()){
			System.out.println(entry.getKey() +  ":" + entry.getValue());
		}
	}
}
