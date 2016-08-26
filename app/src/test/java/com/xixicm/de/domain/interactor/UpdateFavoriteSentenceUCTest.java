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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

/**
 * @author mc
 */
public class UpdateFavoriteSentenceUCTest {
    private UpdateFavoriteSentenceUC mUpdateFavoriteSentenceUC;
    @Mock
    private SentenceRepository mSentenceRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mUpdateFavoriteSentenceUC = new UpdateFavoriteSentenceUC(mSentenceRepository);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mUpdateFavoriteSentenceUC);
    }

    @Test
    public void testUpdateFavoriteSentence() {
        final Sentence sentence = new SentenceEntity();
        sentence.setIsStar(true);
        mUpdateFavoriteSentenceUC.setRequestValue(sentence);
        mUpdateFavoriteSentenceUC.run();
        // updateSentence is called
        verify(mSentenceRepository).updateSentence(sentence);
    }
}
