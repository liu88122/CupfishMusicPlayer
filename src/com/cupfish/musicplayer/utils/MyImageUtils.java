package com.cupfish.musicplayer.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.cupfish.musicplayer.global.Constants;

public class MyImageUtils {

	private static final String TAG = "MyImageUtils";

	/**
	 * 按指定高宽放大缩小图片
	 * 
	 * @param bitmap
	 *            源图
	 * @param w
	 *            目标宽度
	 * @param h
	 *            目标高度
	 * @return Bitmap
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = (float) (w / width);
		float scaleHeight = (float) (h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return result;
	}

	/**
	 * * 按指定比例放大缩小图片
	 * 
	 * @param bitmap
	 *            源图
	 * @param scale
	 *            缩放因子
	 * @return Bitmap
	 * 
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, float scale) {
		if(bitmap == null){
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return result;
	}

	public static Bitmap getFitableBitmapWithReflection(Context context, Bitmap bitmap) {
		if(bitmap == null){
			return null;
		}
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		float scale = 1.0f;
		if (width > 400) {
			scale = 1.8f;
		} else if (width > 300) {
			scale = 1.2f;
		} else {
			scale = 1.0f;
		}
		Log.i(TAG, "" + scale);
		Bitmap scaleBitmap = zoomBitmap(bitmap, scale);
		return createReflectionImageWithOrigin(scaleBitmap);
	}

	public static Bitmap composeBitmap(Bitmap backBitmap, Bitmap foreBitmap) {
		if (backBitmap == null) {
			return null;
		}

		int backWidth = backBitmap.getWidth();
		int backHeight = backBitmap.getHeight();
		int foreWidth = foreBitmap.getWidth();
		int foreHeight = foreBitmap.getHeight();
		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(backWidth, backHeight, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(backBitmap, 0, 0, null);// 在 0，0坐标开始画入src
		cv.drawBitmap(foreBitmap, 0, 0, null);// 画在左上角
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
		return newb;
	}

	/**
	 * 将Drawable转换为Bitmap
	 * 
	 * @param drawable
	 *            要转换的Drawable
	 * @return 转换后的Bitmap
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap result = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(result);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return result;
	}

	/**
	 * 将图片转换为圆角图片
	 * 
	 * @param bitmap
	 *            源图
	 * @param cornerRadius
	 *            圆角半径
	 * @return 转换后的Bitmap
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float cornerRadius) {
		if(bitmap == null){
			return null;
		}
		Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(result);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(result, rect, rect, paint);

		return result;
	}

	/**
	 * 生成带倒影的图片，该图是原图和倒影的合成体
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		if(bitmap == null){
			return null;
		}
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height * 2 / 3, width, height / 3, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 3), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint defaultPaint = new Paint();
		defaultPaint.setColor(Color.TRANSPARENT);
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x88ffffff, 0x00ffffff,
				TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight(), paint);

		return bitmapWithReflection;
	}

	/**
	 * 保存图片到SDCard
	 * 
	 * @param imagePath
	 *            图片保存的路径
	 * @param buffer
	 *            图片数据
	 * @throws IOException
	 */
	public static void saveImage(String imagePath, byte[] buffer) throws IOException {

		// 判断图片是否存在
		File file = new File(Constants.SDCARD_CACHE_IMG_PATH + "/" + imagePath);
		if (file.exists()) {
			return;
		} else {
			// 创建文件
			File parentFile = file.getParentFile();
			Log.i(TAG, parentFile.getAbsolutePath());
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(imagePath);
			fos.write(buffer);
			fos.flush();
			fos.close();
		}
	}

	public static void saveImage(String imageName, Bitmap bitmap) throws IOException {

		// 判断图片是否存在
		File file = new File(Constants.SDCARD_CACHE_IMG_PATH + "/" + imageName);
		if (file.exists()) {
			return;
		} else {
			// 创建文件
			File parentFile = file.getParentFile();
			Log.i(TAG, parentFile.getAbsolutePath());
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		}
	}

	/**
	 * 从SDCard读取图片
	 * 
	 * @param imagePath
	 * @return
	 */
	public static Bitmap getImageFromLocal(String imageName) {
		// 判断图片是否存在
		File file = new File(Constants.SDCARD_CACHE_IMG_PATH + "/" + imageName);
		if (file.exists()) {
			// 若存在，读取图片并返回
			Bitmap bitmap = BitmapFactory.decodeFile(Constants.SDCARD_CACHE_IMG_PATH + "/" + imageName);
			file.setLastModified(System.currentTimeMillis());
			return bitmap;
		}
		return null;
	}

	public static Bitmap loadImage(final String imageName, final String imageUrl, final ImageCallback callback) {
		Bitmap bitmap = getImageFromLocal(imageName);
		if (bitmap != null) {
			return bitmap;
		} else {
			// 从服务器下载图片
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.obj != null) {
						Bitmap bitmap = (Bitmap) msg.obj;
						callback.loadImage(bitmap, Constants.SDCARD_CACHE_IMG_PATH + "/" + imageName);
					}
				}
			};
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						if(TextUtils.isEmpty(imageUrl)){
							return;
						}
						Bitmap bitmap = null;
						URL url = new URL(imageUrl);
						System.out.println(imageUrl);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						if(conn.getResponseCode() == 200){
							BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
							bitmap = BitmapFactory.decodeStream(bis);
						}

						//String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
						if(bitmap != null){
							saveImage(imageName, bitmap);
						}

						Message msg = Message.obtain();
						msg.obj = bitmap;
						handler.sendMessage(msg);
					} catch (IOException e) {
						Log.i(MyImageUtils.class.getSimpleName(), e.getLocalizedMessage(), e);
					}
				}
			};

			ThreadPoolManager.getInstance().addTask(runnable);

		}
		return null;
	}

	public static String md5(String value) {
		String result;
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			result = byteToHexString(md.digest(value.getBytes("utf-8")));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public static String byteToHexString(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}

	public interface ImageCallback {
		void loadImage(Bitmap bitmap, String imagePath);
	}

}
