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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

/**
 * @author mc
 */
@RunWith(MockitoJUnitRunner.class)
public class AlarmSentenceFetchSchedulerTest {
    @Mock
    private FetchAlarmManager mFetchAlarmManager;
    // mark as mock first
    @Mock
    AlarmSentenceFetchScheduler mAlarmSentenceFetchScheduler;

    @Captor
    private ArgumentCaptor<Long> mLongCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mAlarmSentenceFetchScheduler = new AlarmSentenceFetchScheduler(mFetchAlarmManager);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mAlarmSentenceFetchScheduler);
    }

    @Test
    public void testNextFetchScheduleAt() {
        long time = System.currentTimeMillis();
        mAlarmSentenceFetchScheduler.nextFetchScheduleAt(time);
        verify(mFetchAlarmManager).setNextFetchAlarmAt(mLongCaptor.capture());
        assertEquals(mLongCaptor.getValue(), (Long) time);
    }
}
