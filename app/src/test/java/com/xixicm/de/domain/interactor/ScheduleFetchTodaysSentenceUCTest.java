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

import com.xixicm.de.domain.model.event.ScheduleFetchEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

/**
 * @author mc
 */
public class ScheduleFetchTodaysSentenceUCTest {
    private ScheduleFetchTodaysSentenceUC mScheduleFetchTodaysSentenceUC;
    @Mock
    private SentenceFetchScheduler mSentenceFetchScheduler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mScheduleFetchTodaysSentenceUC = new ScheduleFetchTodaysSentenceUC(mSentenceFetchScheduler);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mScheduleFetchTodaysSentenceUC);
    }

    @Test
    public void testNormalScheduleFetch() {
        long startTime = System.currentTimeMillis();
        ScheduleFetchEvent event = new ScheduleFetchEvent(startTime, ScheduleFetchEvent.TYPE.NORMAL);
        mScheduleFetchTodaysSentenceUC.setRequestValue(event);
        mScheduleFetchTodaysSentenceUC.run();
        // nextFetchScheduleAt(long) is called
        verify(mSentenceFetchScheduler).nextFetchScheduleAt(startTime + ScheduleFetchTodaysSentenceUC.NORMAL_FETCHING_INTERVAL);
    }


    @Test
    public void testRetryScheduleFetch() {
        long startTime = System.currentTimeMillis();
        ScheduleFetchEvent event = new ScheduleFetchEvent(startTime, ScheduleFetchEvent.TYPE.RETRY);
        mScheduleFetchTodaysSentenceUC.setRequestValue(event);
        mScheduleFetchTodaysSentenceUC.run();
        // nextFetchScheduleAt(long) is called
        verify(mSentenceFetchScheduler).nextFetchScheduleAt(startTime + ScheduleFetchTodaysSentenceUC.RETRY_FETCHING_INTERVAL);
    }
}
