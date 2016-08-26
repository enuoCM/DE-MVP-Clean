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

import android.content.Intent;

import com.xixicm.de.data.storage.SentenceDataRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author mc
 */
@RunWith(MockitoJUnitRunner.class)
public class DEFetchServiceTest {
    private DEFetchService mDEFetchService;
    @Mock
    private SentenceDataRepository mSentenceDataRepository;
    @Mock
    private Intent mIntent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mDEFetchService = spy(new DEFetchService());
        when(mDEFetchService.getSentenceDataRepository()).thenReturn(mSentenceDataRepository);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mDEFetchService);
    }

    @Test
    public void testOnNullIntent() {
        mDEFetchService.onHandleIntent(null);
        verify(mSentenceDataRepository, atMost(0)).fetchTodaysSentence(anyBoolean());
    }

    @Test
    public void testOnIntent1() {
        mDEFetchService.onHandleIntent(mIntent);
        verify(mSentenceDataRepository).fetchTodaysSentence(false);
    }

    @Test
    public void testOnIntent2() {
        doReturn(true).when(mIntent).getBooleanExtra("boolean_extral_is_manual", false);
        mDEFetchService.onHandleIntent(mIntent);
        verify(mSentenceDataRepository).fetchTodaysSentence(true);
    }
}
