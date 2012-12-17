package com.cupfish.musicplayer.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.text.TextUtils;

public class PinyinUtil {

	public static String toPinyinString(String str){
		
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		
		StringBuilder sb = new StringBuilder();
		if(!TextUtils.isEmpty(str)){
			char[] chars = str.toCharArray();
			for(char c : chars){
				if(isEnglish(c)){
					sb.append(Character.toLowerCase(c));
				}else if(isChinese(c)){
					try {
						String[] pys = PinyinHelper.toHanyuPinyinStringArray(c, format);
						if (pys != null) {
							for (String s : pys) {
								sb.append(s);
							}
							System.out.println();
						}
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						e.printStackTrace();
					}
				} else {
					sb.append("#");
				}
			}
		}
		if(sb.length() == 0){
			sb.append("#");
		}
		return sb.toString();
	}
	
	private static boolean isChinese(char c){
		if(c>=0x4e00 && c<=0x9fff){
			return true;
		}
		return false;
	}
	
	private static boolean isEnglish(char c){
		if(c >='a' && c<='z' || c>='A' && c<='Z'){
			return true;
		}
		return false;
	}

	private static boolean isNumber(char c){
		if(c >= '0' && c <= '9'){
			return true;
		}
		return false;
	}
	
}
