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
package com.xixicm.de.infrastructure.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.VisibleForTesting;

import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.base.handler.DefaultUseCaseHandler;
import com.xixicm.de.domain.base.util.LogUtils;
import com.xixicm.de.domain.interactor.ScheduleFetchTodaysSentenceUC;
import com.xixicm.de.domain.model.event.ScheduleFetchEvent;
import com.xixicm.de.infrastructure.service.DEFetchService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * A manager to observe {@link ScheduleFetchEvent} to start a {@link ScheduleFetchTodaysSentenceUC} use case.
 * This manager lives in whole application life cycle.
 *
 * @author mc
 */
public class FetchAlarmManager {
    private static FetchAlarmManager sInstance;
    private Context mContext;

    public static FetchAlarmManager getInstance() {
        return sInstance;
    }

    private FetchAlarmManager(Context context) {
        mContext = context;
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onScheduleNextFetch(ScheduleFetchEvent event) {
        LogUtils.v(Constants.TAG, "onScheduleNextFetch: " + event);
        ScheduleFetchTodaysSentenceUC scheduleFetchTodaysSentenceUC = new ScheduleFetchTodaysSentenceUC(AlarmSentenceFetchScheduler.getInstance(this));
        scheduleFetchTodaysSentenceUC.setRequestValue(event);
        DefaultUseCaseHandler.createParallelUCHandler().execute(scheduleFetchTodaysSentenceUC);
    }

    public static void init(Context context) {
        sInstance = new FetchAlarmManager(context);
    }


    /**
     * Set the alarm
     *
     * @param time alarm trigger time
     */
    public void setNextFetchAlarmAt(long time) {
        LogUtils.v(Constants.TAG, FetchAlarmManager.class.getCanonicalName() + " nextFetchScheduleAt:" + time);
        getAlarmManager().set(AlarmManager.ELAPSED_REALTIME, time, getPendingIntent());
    }

    @VisibleForTesting
    public AlarmManager getAlarmManager() {
        return (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Cancel the alarm
     */
    public void cancelFetchAlarm() {
        getAlarmManager().cancel(getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getService(mContext, 0,
                new Intent(mContext, DEFetchService.class),
                PendingIntent.FLAG_ONE_SHOT);
    }
}
