package com.cupfish.music.test;

import com.cupfish.music.parser.JsonParser;

import android.test.AndroidTestCase;

public class TestJsonParser extends AndroidTestCase {

	public void testGenerateJsonFile(){
		String downloadUrl = "http://www.cupfish.com/apk/CupfishMusicPlayer2.0.apk";
		String updateLog = "修复在线听失效问题#增加自动更新功能#更多功能，正在开发...";
		JsonParser.generateCurrentAppUpdateInfoJsonFile(getContext(), downloadUrl, updateLog);
	}
	
}
