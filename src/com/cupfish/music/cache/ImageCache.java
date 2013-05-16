package com.cupfish.music.cache;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;


public final class ImageCache {

    private static final String TAG = ImageCache.class.getSimpleName();

    private LruCache<String, Bitmap> mLruCache;

    private static ImageCache sInstance;

    public ImageCache(final Context context) {
        init(context);
    }

    public final static ImageCache getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new ImageCache(context.getApplicationContext());
        }
        return sInstance;
    }

    public void init(final Context context) {
        final ActivityManager activityManager = (ActivityManager)context
                .getSystemService(Context.ACTIVITY_SERVICE);
        final int lruCacheSize = Math.round(0.25f * activityManager.getMemoryClass()
                * 1024 * 1024);
        mLruCache = new LruCache<String, Bitmap>(lruCacheSize) {
            @Override
            protected int sizeOf(final String paramString, final Bitmap paramBitmap) {
            	
                return paramBitmap.getRowBytes();
            }

        };
    }

    public static final ImageCache findOrCreateCache(final Activity activity) {    	
    	
           ImageCache cache = getInstance(activity);
       
        return cache;
    }

    public void add(final String data, final Bitmap bitmap) {
        if (data == null || bitmap == null) {
            return;
        }
        if (get(data) == null) {
            mLruCache.put(data, bitmap);
        }
    }

    public final Bitmap get(final String data) {
        if (data == null) {
            return null;
        }
        if (mLruCache != null) {
            final Bitmap mBitmap = mLruCache.get(data);
            if (mBitmap != null) {
                return mBitmap;
            }
        }
        return null;
    }

    public void remove(final String key) {
        if (mLruCache != null) {
            mLruCache.remove(key);
        }
    }
    
    public void clearMemCache() {
        if (mLruCache != null) {
            mLruCache.evictAll();
        }
        System.gc();
    }

    public static final class RetainFragment extends Fragment {

        private Object mObject;

        public RetainFragment() {
        }
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Make sure this Fragment is retained over a configuration change
            setRetainInstance(true);
        }
        public void setObject(final Object object) {
            mObject = object;
        }
        public Object getObject() {
            return mObject;
        }
    }
}
