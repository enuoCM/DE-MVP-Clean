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
package com.xixicm.de.domain.interactor;

import com.xixicm.de.domain.Constants;
import com.xixicm.ca.domain.usecase.AbstractUseCase;
import com.xixicm.ca.domain.util.LogUtils;
import com.xixicm.de.domain.model.event.ScheduleFetchEvent;

/**
 * A trigger use case to schedule next fetch.
 *
 * @author mc
 */
public class ScheduleFetchTodaysSentenceUC extends AbstractUseCase<ScheduleFetchEvent, Void, Void> {
    static final long NORMAL_FETCHING_INTERVAL = 8 * 60 * 60 * 1000;
    static final long RETRY_FETCHING_INTERVAL = 5 * 60 * 1000;
    SentenceFetchScheduler mSentenceFetchScheduler;

    public ScheduleFetchTodaysSentenceUC(SentenceFetchScheduler sentenceFetchScheduler) {
        mSentenceFetchScheduler = sentenceFetchScheduler;
    }

    private void scheduleNextFetch() {
        long nextFetchTime = getRequestValue().getStartTime()
                + ((getRequestValue().getType() == ScheduleFetchEvent.TYPE.NORMAL) ? NORMAL_FETCHING_INTERVAL : RETRY_FETCHING_INTERVAL);
        LogUtils.v(Constants.TAG, "schedule next fetch at " + nextFetchTime);
        mSentenceFetchScheduler.nextFetchScheduleAt(nextFetchTime);
    }

    @Override
    public void run() {
        scheduleNextFetch();
    }
}
