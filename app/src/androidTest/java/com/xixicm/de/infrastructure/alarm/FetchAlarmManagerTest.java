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
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.xixicm.de.domain.model.event.ScheduleFetchEvent;
import com.xixicm.de.infrastructure.service.DEFetchService;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author mc
 */
@RunWith(AndroidJUnit4.class)
public class FetchAlarmManagerTest {
    private FetchAlarmManager mFetchAlarmManager;

    @Mock
    private AlarmManager mAlarmManager;

    @Captor
    private ArgumentCaptor<Integer> mIntegerCaptor;
    @Captor
    private ArgumentCaptor<Long> mLongCaptor;
    @Captor
    private ArgumentCaptor<PendingIntent> mPendingIntentCaptor;
    private ScheduleFetchEvent mScheduleFetchEvent;

    @Before
    public void setUp() {
        // workaround for NPE; mockito 1.10.19
        mFetchAlarmManager = FetchAlarmManager.getInstance();
        mScheduleFetchEvent = new ScheduleFetchEvent(System.currentTimeMillis(), ScheduleFetchEvent.TYPE.RETRY);
        MockitoAnnotations.initMocks(this);
        mFetchAlarmManager = spy(mFetchAlarmManager);
        when(mFetchAlarmManager.getAlarmManager()).thenReturn(mAlarmManager);
        //reset to null;
        mScheduleFetchEvent = null;
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mFetchAlarmManager);
    }

    @Test
    public void testSetNextFetchAlarmAt() {
        long time = System.currentTimeMillis();
        mFetchAlarmManager.setNextFetchAlarmAt(time);
        verify(mAlarmManager).set(mIntegerCaptor.capture(), mLongCaptor.capture(), mPendingIntentCaptor.capture());
        assertEquals(mIntegerCaptor.getValue().intValue(), AlarmManager.ELAPSED_REALTIME);
        assertEquals(mLongCaptor.getValue().longValue(), time);
        assertThat(mPendingIntentCaptor.getValue(), is(getPendingIntent()));
    }

    @Test
    public void testCancelFetchAlarm() {
        mFetchAlarmManager.cancelFetchAlarm();
        verify(mAlarmManager).cancel(mPendingIntentCaptor.capture());
        assertThat(mPendingIntentCaptor.getValue(), is(getPendingIntent()));
    }

    @Test
    public void testOnScheduleNextFetch() {
        EventBus.getDefault().register(mFetchAlarmManager);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                assertNotNull(invocation.getArguments()[0]);
                mScheduleFetchEvent = (ScheduleFetchEvent) invocation.getArguments()[0];
                return null;
            }
        }).when(mFetchAlarmManager).onScheduleNextFetch(any(ScheduleFetchEvent.class));

        ScheduleFetchEvent postEvent = new ScheduleFetchEvent(System.currentTimeMillis(), ScheduleFetchEvent.TYPE.NORMAL);
        EventBus.getDefault().post(postEvent);

        assertNotNull(mScheduleFetchEvent);
        assertThat(mScheduleFetchEvent, is(postEvent));
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getService(InstrumentationRegistry.getTargetContext(), 0,
                new Intent(InstrumentationRegistry.getTargetContext(), DEFetchService.class),
                PendingIntent.FLAG_ONE_SHOT);
    }
}
