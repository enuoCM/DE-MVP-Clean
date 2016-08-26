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

import com.xixicm.de.domain.repository.SentenceRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

/**
 * @author mc
 */
public class FetchSentenceAudioUCTest {
    private static final String SAMPLE_AUDIO_URL = "http:\\/\\/news.iciba.com\\/admin\\/tts\\/2016-08-12-day.mp3";
    private FetchSentenceAudioUC mFetchSentenceAudioUC;
    @Mock
    private SentenceRepository mSentenceRepository;

    private long mToken;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mFetchSentenceAudioUC = new FetchSentenceAudioUC(mSentenceRepository);
        mToken = System.currentTimeMillis();
        mFetchSentenceAudioUC.setRequestValue(new FetchSentenceAudioUC.FetchSentenceAudioRequestParms(SAMPLE_AUDIO_URL, mToken));
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mFetchSentenceAudioUC);
    }

    @Test
    public void testFetchSentenceAudio() {
        mFetchSentenceAudioUC.run();
        // fetchSentenceAudio is called
        verify(mSentenceRepository).fetchSentenceAudio(SAMPLE_AUDIO_URL, mToken);
    }
}
