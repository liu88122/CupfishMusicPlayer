package com.cupfish.musicplayer.utils;

public class TextFormatUtils {

	public static String getPrettyFormatDuration(long duration) {
		StringBuilder result = new StringBuilder();

		int hh = (int) (duration / (60 * 60 * 1000));
		if (duration >= (60 * 60 * 1000)) {
			duration = duration % (60 * 60 * 1000);
			if (hh < 10) {
				result.append("0");
			}

			result.append(hh + ":");
		}

		int mm = (int) (duration / (60 * 1000));
		if (mm == 0) {
			result.append("00:");
		} else

		if (duration >= (60 * 1000)) {

			duration = duration % (60 * 1000);
			if (mm < 10) {
				result.append("0");
			}
			result.append(mm + ":");
		} else {
			result.append("00:");
		}

		int ss = (int) (duration / 1000);
		if (ss == 0) {
			result.append("00");
		} else if (duration >= 1000) {
			if (ss < 10) {
				result.append("0");
			}
			result.append(ss);
		}
		return result.toString();
	}
}
