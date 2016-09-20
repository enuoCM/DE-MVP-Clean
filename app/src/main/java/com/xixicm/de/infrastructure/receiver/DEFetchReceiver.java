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
package com.xixicm.de.infrastructure.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.VisibleForTesting;

import com.xixicm.de.domain.Constants;
import com.xixicm.ca.domain.handler.DefaultUseCaseHandler;
import com.xixicm.ca.domain.util.LogUtils;
import com.xixicm.ca.domain.util.NetworkUtils;
import com.xixicm.de.domain.interactor.AutoFetchTodaysSentenceUC;
import com.xixicm.de.infrastructure.service.ServiceSentenceFetchExecutor;

/**
 * @author mc
 */
public class DEFetchReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.v(Constants.TAG, "DEFetchReceiver: " + intent);
        // work on previous android N
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()) && !NetworkUtils.isNetworkAvailable()) {
            LogUtils.d(Constants.TAG, "Network is not available, abort fetching.");
            return;
        }

        DefaultUseCaseHandler.createParallelUCHandler().execute(getAutoFetchTodaysSentenceUC(context));
    }

    @VisibleForTesting
    public AutoFetchTodaysSentenceUC getAutoFetchTodaysSentenceUC(Context context) {
        return new AutoFetchTodaysSentenceUC(ServiceSentenceFetchExecutor.getInstance(context));
    }
}
