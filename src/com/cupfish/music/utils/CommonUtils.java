package com.cupfish.music.utils;

import android.os.Build;

public class CommonUtils {
	
	 public static boolean hasFroyo() {
	        // Can use static final constants like FROYO, declared in later versions
	        // of the OS since they are inlined at compile time. This is guaranteed behavior.
	        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	    }

	    public static boolean hasGingerbread() {
	        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	    }

	    public static boolean hasHoneycomb() {
	        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	    }

	    public static boolean hasHoneycombMR1() {
	        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	    }

//	    public static boolean hasJellyBean() {
//	        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
//	    }
}
