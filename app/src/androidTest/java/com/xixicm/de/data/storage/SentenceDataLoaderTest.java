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
package com.xixicm.de.data.storage;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.xixicm.de.domain.model.Sentence;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author mc
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class SentenceDataLoaderTest extends DataBaseTest {
    private SentenceDataLoader mSentenceDataLoader;

    @Override
    public void setUp() {
        super.setUp();
        Sentence newSentence1 = generateNewSentence(false);
        Sentence newSentence2 = generateNewSentence(true);
        Sentence newSentence3 = generateNewSentence(true);
        mSentenceDataRepository.saveSentence(newSentence1);
        mSentenceDataRepository.saveSentence(newSentence2);
        mSentenceDataRepository.saveSentence(newSentence3);
    }

    @Test
    public void testLoadFavorite() {
        mSentenceDataLoader = new SentenceDataLoader(getRenamingDelegatingContext(), true);
        List<? extends Sentence> sentences = mSentenceDataLoader.loadInBackground();
        assertTrue(sentences.size() == 2);
    }

    @Test
    public void testLoadAll() {
        mSentenceDataLoader = new SentenceDataLoader(getRenamingDelegatingContext(), false);
        List<? extends Sentence> sentences = mSentenceDataLoader.loadInBackground();
        assertTrue(sentences.size() == 3);
    }
}
