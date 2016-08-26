/*
 * Copyright (C) 2016 mc
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
package com.xixicm.de.data.net;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.RequestTickle;
import com.android.volley.cache.DiskBasedCache;
import com.android.volley.cache.SimpleImageLoader;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.VolleyTickle;

import java.io.File;

/**
 * @author mc
 */
public class DEVolley {
    public static final int HTTP_CONNECTION_TIMEOUT = 30000;
    /**
     * maximum disk usage in bytes.
     */
    private static final int MAX_DISK_USAGE_BYTES = 50 * 1024 * 1024; // 50M
    private static DEVolley sInstance;
    private Context mContext;
    //async. call back will be in main thread
    private RequestQueue mRequestQueue;
    //sync. call back will be in the same thread
    private RequestTickle mRequestTickle;
    private SimpleImageLoader mImageLoader;

    public File mTempAudioDownloadFile;

    private DiskBasedCache mDiskBasedCache;

    private DEVolley(Context context) {
        mContext = context;
        mTempAudioDownloadFile = new File(mContext.getCacheDir(), ".tmp.mp3");

        File cacheDir = new File(context.getCacheDir(), "volley");
        mDiskBasedCache = new DiskBasedCache(cacheDir, MAX_DISK_USAGE_BYTES);
        // will use cache first
        mRequestQueue = newCustomRequestQueue(mDiskBasedCache);
        // only write cache, never use
        mRequestTickle = VolleyTickle.newRequestTickle(mContext.getApplicationContext());
        mImageLoader = new SimpleImageLoader(mRequestQueue);
        mImageLoader.setMaxImageSize(getMaxImageSize(context));
    }

    public static DEVolley getInstance() {
        return sInstance;
    }

    public DiskBasedCache getCache() {
        return mDiskBasedCache;
    }

    public File getTempAudioDownloadFile() {
        return mTempAudioDownloadFile;
    }

    private static RequestQueue newCustomRequestQueue(DiskBasedCache cache) {
        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();
        return queue;
    }

    public static void init(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new DEVolley(context);
        }
    }

    private int getMaxImageSize(Context context) {
        int width = Math.min(context.getResources().getDisplayMetrics().widthPixels,
                context.getResources().getDisplayMetrics().heightPixels);
        return width;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    public RequestTickle getRequestTickle() {
        if (mRequestTickle != null) {
            return mRequestTickle;
        } else {
            throw new IllegalStateException("RequestTickle not initialized");
        }
    }

    public SimpleImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }

    public DefaultRetryPolicy getDefaultRetryPolicy(int maxNumRetries) {
        return new DefaultRetryPolicy(HTTP_CONNECTION_TIMEOUT, maxNumRetries,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }
}
