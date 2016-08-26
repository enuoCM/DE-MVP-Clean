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
package com.xixicm.de;

import android.content.Context;

import com.xixicm.de.data.net.DEVolley;
import com.xixicm.de.data.storage.dao.DaoManager;
import com.xixicm.de.data.storage.pref.Preferences;
import com.xixicm.de.domain.base.handler.DefaultUseCaseHandler;
import com.xixicm.de.domain.base.util.LogUtils;
import com.xixicm.de.domain.base.util.NetworkUtils;
import com.xixicm.de.domain.interactor.AutoFetchTodaysSentenceUC;
import com.xixicm.de.infrastructure.alarm.FetchAlarmManager;
import com.xixicm.de.infrastructure.base.util.AndroidLogger;
import com.xixicm.de.infrastructure.base.util.AndroidNetworkChecker;
import com.xixicm.de.infrastructure.service.ServiceSentenceFetchExecutor;
import com.xixicm.de.presentation.view.appwidget.WidgetManager;

import org.greenrobot.eventbus.EventBus;

/**
 * @author mc
 */
public class Initializer {
    private Initializer() {
    }

    public static void init(Context context) {
        // domain
        LogUtils.injectLogger(new AndroidLogger());
        NetworkUtils.injectNetworkChecker(new AndroidNetworkChecker(context));
        // data
        DaoManager.init(context);
        DEVolley.init(context);
        Preferences.init(context);
        // presentation
        //build an EventBus that keeps quiet in case a posted event has no subscribers:
        EventBus.builder().logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false).installDefaultEventBus();
        WidgetManager.init(context);
        // infrastructure
        FetchAlarmManager.init(context);

        // trigger to auto fetch in service
        AutoFetchTodaysSentenceUC autoFetchTodaysSentenceUC = new AutoFetchTodaysSentenceUC(ServiceSentenceFetchExecutor.getInstance(context));
        DefaultUseCaseHandler.createParallelUCHandler().execute(autoFetchTodaysSentenceUC);
    }
}
