package com.cupfish.music.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;

import com.cupfish.music.utils.CommonUtils;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class NewImageCache {

	private static final String TAG = NewImageCache.class.getSimpleName();
	
	public static final int DEFAULT_MEM_CACHE_SIZE = 10 * 1024 * 1024;
	public static final int DEFAULT_DISK_CACHE_SIZE = 20 * 1024 * 1024;
	public static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG;
	public static final int DEFAULT_COMPRESS_QUALITY = 100;
	public static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
	public static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
	public static final boolean DEFAULT_CLEAR_DISK_CACHE_ON_START = false;
	public static final boolean DEFAULT_INIT_DISK_CACHE_ON_CREATE = false;
	public static final int DISK_CACHE_INDEX = 0;
	private DiskLruCache mDiskCache;
	private LruCache<String, Bitmap> mMemCache;
	private ImageCacheParams mCacheParams;
	private Object mDiskCacheLock = new Object();
	private boolean mDiskCacheStarting = true;
	
	public NewImageCache(ImageCacheParams cacheParams){
		init(cacheParams);
	}
	
	public NewImageCache(Context context, String name){
		init(new ImageCacheParams(context, name));
	}
	
	public static NewImageCache findOrCreateCache(FragmentManager fm, ImageCacheParams cacheParams){
		final RetainFragment mRetainFragment = findOrCreateFragment(fm);
		NewImageCache imageCache = (NewImageCache) mRetainFragment.getObject();
		if(imageCache == null){
			imageCache = new NewImageCache(cacheParams);
			mRetainFragment.setObject(imageCache);
		}
		return imageCache;
	}
	
	private void init(ImageCacheParams cacheParams){
		mCacheParams = cacheParams;
		if(mCacheParams.memoryCacheEnabled){
			mMemCache = new LruCache<String, Bitmap>(mCacheParams.memCacheSize){
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return getBitmapSize(bitmap);
				}
			};
		}
		if(mCacheParams.diskCacheEnabled){
			initDiskCache();
		}
	}
	
	public void initDiskCache() {
		synchronized (mDiskCacheLock) {
			if(mDiskCache == null || mDiskCache.isClosed()){
				File diskCacheDir = mCacheParams.diskCacheDir;
				if(mCacheParams.diskCacheEnabled && diskCacheDir != null){
					if(!diskCacheDir.exists()){
						diskCacheDir.mkdirs();
					}
					if(getUsableSpace(diskCacheDir) > mCacheParams.diskCacheSize){
						try {
							mDiskCache = DiskLruCache.open(diskCacheDir, 1, 1, mCacheParams.diskCacheSize);
						} catch (IOException e) {
							 mCacheParams.diskCacheDir = null;
							e.printStackTrace();
						}
					}
				}
				mDiskCacheStarting = false;
				mDiskCacheLock.notifyAll();
			}
		}
	}
	
	public void addBitmapToCache(String data, Bitmap bitmap){
		if(data == null || bitmap == null){
			return;
		}
		//add to memory cache
		if(mMemCache != null && mMemCache.get(data) == null){
			mMemCache.put(data, bitmap);
		}
		
		//add to disk cache
		synchronized (mDiskCacheLock) {
			if(mDiskCache != null){
				final String key = hashKeyForDisk(data);
				OutputStream out = null;
				try {
					DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
					if(snapshot == null){
						final DiskLruCache.Editor editor = mDiskCache.edit(key);
						if(editor != null){
							out = editor.newOutputStream(DISK_CACHE_INDEX);
							bitmap.compress(mCacheParams.compressFormat, mCacheParams.compressQuality, out);
							editor.commit();
							out.close();
						}
					} else {
						snapshot.getInputStream(DISK_CACHE_INDEX).close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try{
						if(out != null){
							out.close();
						}
					}catch(Exception e){
					
					}
				}
			}
		}
	}
	
	/**
	 * 从内存缓存中获取
	 * @param data
	 * @return
	 * Bitmap
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-5-16 下午2:56:29
	 */
	public Bitmap getBitmapFromMemCache(String data){
		if(mMemCache != null){
			return mMemCache.get(data);
		}
		return null;
	}
	
	/**
	 * 从硬盘缓存中获取
	 * @param data
	 * @return
	 * Bitmap
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2013-5-16 下午3:02:20
	 */
	public Bitmap getBitmapFromDiskCache(String data){
		final String key = hashKeyForDisk(data);
		synchronized (mDiskCacheLock) {
			while(mDiskCacheStarting){
				try {
					mDiskCache.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(mDiskCache != null){
				InputStream is = null;
				try {
					DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
					if(snapshot != null){
						is = snapshot.getInputStream(DISK_CACHE_INDEX);
						if(is != null){
							Bitmap bitmap = BitmapFactory.decodeStream(is);
							return bitmap;
						}
					}
				
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (is != null) {
							is.close();
						}
					} catch (Exception e) {

					}
				}
			}
			return null;
		}
	}
	
	public void clearCache(){
		if(mMemCache != null){
			mMemCache.evictAll();
		}
		synchronized (mDiskCacheLock) {
			mDiskCacheStarting = true;
			if(mDiskCache != null && !mDiskCache.isClosed()){
				try {
					mDiskCache.delete();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mDiskCache = null;
				initDiskCache();
			}
		}
	}
	
	public void flush(){
		synchronized (mDiskCacheLock) {
			if(mDiskCache != null){
				try {
					mDiskCache.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void close(){
		synchronized (mDiskCacheLock) {
			if(mDiskCache != null){
				if(!mDiskCache.isClosed()){
					try {
						mDiskCache.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mDiskCache = null;
				}
			}
		}
	}
	
	private static int getBitmapSize(Bitmap bitmap){
		if(CommonUtils.hasHoneycombMR1()){
			return bitmap.getByteCount();
		}
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
	
	public static long getUsableSpace(File path){
		if(CommonUtils.hasGingerbread()){
			return path.getUsableSpace();
		}
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getAvailableBlocks() * stats.getBlockSize();
	}
	
	public static String hashKeyForDisk(String key){
		String cacheKey = null;
		try {
			MessageDigest digist = MessageDigest.getInstance("MD5");
			digist.update(key.getBytes());
			cacheKey = bytesToHexString(digist.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}
	
	private static String bytesToHexString(byte[] bytes){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<bytes.length; i++){
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if(hex.length() == 1){
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
	
	public static class ImageCacheParams{
		public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
		public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
		public File diskCacheDir;
		public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
		public int compressQuality = DEFAULT_COMPRESS_QUALITY;
		public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
		public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
		public boolean clearDiskCacheOnStart = DEFAULT_CLEAR_DISK_CACHE_ON_START;
		public boolean initDiskCacheOnCreate = DEFAULT_INIT_DISK_CACHE_ON_CREATE;
	
		public ImageCacheParams(Context context, String name){
			diskCacheDir = getDiskCacheDir(context, name);
		}
		
		public ImageCacheParams(File diskCacheDir){
			this.diskCacheDir = diskCacheDir;
		}
		
		public void setMemCacheSizePercent(Context context, float percent){
			if(percent < 0.05f || percent > 0.8f){
				throw new IllegalArgumentException("setMemCacheSizePercent must between 0.05 and 0.8");
			}
			memCacheSize = Math.round(percent * getMemoryClass(context));
		}
	
		private static int getMemoryClass(Context context){
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			return am.getMemoryClass();
		}
	}
	
	public static File getDiskCacheDir(Context context, String dir){
		final String cachePath = 
				Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())||!isExternalStorageRomovable()
				?getExternalCacheDir(context).getPath() : context.getCacheDir().getPath();
		return new File(cachePath + File.separator + dir);
	}
	
	public static boolean isExternalStorageRomovable(){
		if(CommonUtils.hasGingerbread()){
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}
	
	public static File getExternalCacheDir(Context context) {
        if (CommonUtils.hasFroyo()) {
            return context.getExternalCacheDir();
        }
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }
	
	public static RetainFragment findOrCreateFragment(FragmentManager fm){
		RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
		if(fragment == null){
			fragment = new RetainFragment();
			fm.beginTransaction().add(fragment, TAG).commitAllowingStateLoss();
		}
		return fragment;
	}
	
	public static class RetainFragment extends Fragment{
		
		private Object mObj;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
		}
		
		public void setObject(Object obj){
			mObj = obj;
		}
		
		public Object getObject(){
			return mObj;
		}
	}
}
