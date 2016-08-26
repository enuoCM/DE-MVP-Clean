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
package com.xixicm.de.presentation.base.scheduler;

import android.os.Handler;
import android.os.Looper;

import com.xixicm.de.domain.base.scheduler.UseCaseResponseScheduler;

/**
 * Post response task to UI main thread..
 *
 * @author mc
 */
public class UseCaseUIResponseScheduler implements UseCaseResponseScheduler {
    private static UseCaseUIResponseScheduler sInstance;
    private final Handler mHandler;

    public synchronized static UseCaseUIResponseScheduler getInstance() {
        if (sInstance == null) {
            sInstance = new UseCaseUIResponseScheduler();
        }
        return sInstance;
    }

    UseCaseUIResponseScheduler() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }
}
