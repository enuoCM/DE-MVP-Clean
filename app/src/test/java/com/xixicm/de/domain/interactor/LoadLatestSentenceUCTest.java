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

import com.xixicm.de.data.entity.SentenceEntity;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.domain.repository.SentenceRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author mc
 */
public class LoadLatestSentenceUCTest {
    private LoadLatestSentenceUC mLoadLatestSentenceUC;
    @Mock
    private SentenceRepository mSentenceRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mLoadLatestSentenceUC = new LoadLatestSentenceUC(mSentenceRepository);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mLoadLatestSentenceUC);
    }

    @Test
    public void testLoadLatestSentence() {
        final Sentence newSentence = new SentenceEntity();
        newSentence.setDateline("2016-08-16");
        when(mSentenceRepository.getLatestSentence()).thenReturn(newSentence);
        mLoadLatestSentenceUC.setUseCaseCallback(new LoadLatestSentenceUC.LoadLatestSentenceCallback() {
            @Override
            public void onLatestSentenceGot(Sentence sentence) {
                assertThat(sentence, is(newSentence));
            }
        });
        mLoadLatestSentenceUC.run();
        // getLatestSentence is called
        verify(mSentenceRepository).getLatestSentence();
    }
}
