package com.cupfish.music.test;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;

import com.cupfish.music.utils.MyImageUtils;
import com.cupfish.music.utils.MyImageUtils.ImageCallback;

public class TestImageUtil extends AndroidTestCase {

	public void testLoadImage(){
		String imageUrl = "http://c.hiphotos.baidu.com/ting/pic/item/bd3eb13533fa828b1eb25047fd1f4134960a5a85.jpg";
		String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
		MyImageUtils.loadImage(imageName, imageUrl, new ImageCallback() {
			@Override
			public void loadImage(Bitmap bitmap, String imagePath) {
				System.out.println(imagePath);
			}
		});
	}
	
	public void testMd5(){
		System.out.println(MyImageUtils.md5("皇上吉祥"));
	}
}
