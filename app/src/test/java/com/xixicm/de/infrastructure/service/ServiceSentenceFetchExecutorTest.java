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
package com.xixicm.de.infrastructure.service;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author mc
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceSentenceFetchExecutorTest {
    private ServiceSentenceFetchExecutor mServiceSentenceFetchExecutor;
    @Mock
    private Context mContext;
    @Captor
    private ArgumentCaptor<Intent> mIntentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mContext.getApplicationContext()).thenReturn(mContext);
        mServiceSentenceFetchExecutor = new ServiceSentenceFetchExecutor(mContext);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mServiceSentenceFetchExecutor);
    }

    @Test
    public void testFetchTodaysSentence() {
        mServiceSentenceFetchExecutor.fetchTodaysSentence(false);
        verify(mContext).startService(mIntentCaptor.capture());
        Intent intent = mIntentCaptor.getValue();
        assertTrue(intent != null);
        // assertTrue(intent.getComponent().getClassName().equals(DEFetchService.class.getCanonicalName()));
    }
}
