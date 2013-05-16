/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cupfish.music.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.cupfish.music.utils.ImageUtils;

/**
 * A simple subclass of {@link ImageResizer} that fetches and resizes images fetched from a URL.
 */
public class ImageFetcher extends ImageWorker {
    private static final String TAG = "ImageFetcher";

    private Context context;

    public ImageFetcher(Context context) {
    	super(context);
    	this.context = context;
    }

    @Override
    protected Bitmap processBitmap(Object data) {
    	ImageInfo info = (ImageInfo) data;
    	Bitmap bitmap = null;
    	String imageUrl = ImageUtils.getImageUrlFromWeb( context, info );
    	if(imageUrl != null){
    		InputStream is = downloadUrlToStream(imageUrl);
    		if(is != null){
    			bitmap = BitmapFactory.decodeStream(is);
    		}
    	}
        return bitmap;
    }

    
    
    public InputStream downloadUrlToStream(String urlString) {
        disableConnectionReuseIfNecessary();
        HttpURLConnection urlConnection = null;
        InputStream in = null;

        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = urlConnection.getInputStream();
            return in;
        } catch (final IOException e) {
            Log.e(TAG, "Error in downloadBitmap - " + e);
        } 
        return null;
    }

    /**
     * Workaround for bug pre-Froyo, see here for more info:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     */
    public static void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }
}
