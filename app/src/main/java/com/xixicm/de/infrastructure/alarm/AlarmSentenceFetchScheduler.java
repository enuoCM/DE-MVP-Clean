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

import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.base.util.LogUtils;
import com.xixicm.de.domain.interactor.SentenceFetchScheduler;

import org.greenrobot.greendao.annotation.NotNull;

/**
 * Use Android Alarm scheme to make the schedule.
 *
 * @author mc
 */
public class AlarmSentenceFetchScheduler implements SentenceFetchScheduler {
    private static AlarmSentenceFetchScheduler sInstance;
    private FetchAlarmManager mFetchAlarmManager;

    AlarmSentenceFetchScheduler(FetchAlarmManager fetchAlarmManager) {
        mFetchAlarmManager = fetchAlarmManager;
    }

    public static synchronized AlarmSentenceFetchScheduler getInstance(@NotNull FetchAlarmManager fetchAlarmManager) {
        if (sInstance == null) {
            sInstance = new AlarmSentenceFetchScheduler(fetchAlarmManager);
        }
        return sInstance;
    }

    @Override
    public void nextFetchScheduleAt(long time) {
        LogUtils.v(Constants.TAG, AlarmSentenceFetchScheduler.class.getCanonicalName() + " nextFetchScheduleAt:" + time);
        mFetchAlarmManager.setNextFetchAlarmAt(time);
    }
}
