package com.cupfish.musicplayer.test;

import java.util.Date;

import android.test.AndroidTestCase;
import android.text.format.DateFormat;

import com.cupfish.musicplayer.utils.TextFormatUtils;

public class TestTextUtil extends AndroidTestCase {

	public void testFormatDuration(){
		System.out.println(TextFormatUtils.getPrettyFormatDuration(4 * 60 * 60 * 1000 + 15 * 60 * 1000 + 30 * 1000));;
	}
	
	public void test(){
		System.out.println(DateFormat.format("yyyy_MM_dd_hh_MM_ss", new Date(System.currentTimeMillis())).toString());
	}
}
