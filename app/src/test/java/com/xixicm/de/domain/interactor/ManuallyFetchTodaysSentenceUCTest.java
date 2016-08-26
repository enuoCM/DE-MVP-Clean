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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

/**
 * @author mc
 */
public class ManuallyFetchTodaysSentenceUCTest {
    private ManuallyFetchTodaysSentenceUC mManuallyFetchTodaysSentenceUC;
    @Mock
    private SentenceFetchExecutor mSentenceFetchExecutor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mManuallyFetchTodaysSentenceUC = new ManuallyFetchTodaysSentenceUC(mSentenceFetchExecutor);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mManuallyFetchTodaysSentenceUC);
    }

    @Test
    public void testFetchTodaysSentence() {
        mManuallyFetchTodaysSentenceUC.run();
        // fetchTodaysSentence(true) is called
        verify(mSentenceFetchExecutor).fetchTodaysSentence(true);
    }
}
