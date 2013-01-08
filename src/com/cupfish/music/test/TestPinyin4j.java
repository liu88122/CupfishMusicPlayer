package com.cupfish.music.test;

import com.cupfish.music.utils.PinyinUtil;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.test.AndroidTestCase;

public class TestPinyin4j extends AndroidTestCase {

	public void test(){
		String str = "刘#liu.Sir忠德";
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		char[] chars = str.toCharArray();
		for(char c : chars){
			String[] result;
			try {
				if (isChinese(c)) {
					result = PinyinHelper.toHanyuPinyinStringArray(c, format);
					for (String s : result) {
						System.out.print(s);
					}
					System.out.println();
				}else if(isEnglish(c)){
					System.out.print(c);
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void testPinyinUtil(){
		
		String str ="《How You Remind Me #(海贼王电影主题歌)》";
		String result = PinyinUtil.toPinyinString(str);
		System.out.println(result);
	}
	
	
	
	
	
	
	
	
	
	
	
	public boolean isChinese(char c){
		if(c>0x4e00 && c<0x9fbb){
			return true;
		}
		return false;
	}
	
	public boolean isEnglish(char c){
		if(c >='a' && c<='z' || c>='A' && c<='Z'){
			return true;
		}
		return false;
	}
	
}
