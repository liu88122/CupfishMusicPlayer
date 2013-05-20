package com.cupfish.music.test;

import java.util.Date;

import android.test.AndroidTestCase;
import android.text.format.DateFormat;

import com.cupfish.music.utils.StringUtils;
import com.cupfish.music.utils.TextFormatUtils;

public class TestTextUtil extends AndroidTestCase {

	public void testFormatDuration(){
		System.out.println(StringUtils.getPrettyFormatDuration(4 * 60 * 60 * 1000 + 15 * 60 * 1000 + 30 * 1000));;
	}
	
	public void test(){
		System.out.println(DateFormat.format("yyyy_MM_dd_hh_MM_ss", new Date(System.currentTimeMillis())).toString());
	}
}
