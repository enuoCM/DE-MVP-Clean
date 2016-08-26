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
import com.xixicm.de.domain.base.usecase.UseCase;
import com.xixicm.de.domain.model.Sentence;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * @author mc
 */
public class LoadSentencesUCTest {
    private LoadSentencesUC mLoadSentencesUC;
    @Mock
    private SentenceLoadExecutor mSentenceLoadExecutor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mLoadSentencesUC = new LoadSentencesUC(mSentenceLoadExecutor);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mLoadSentencesUC);
    }

    @Test
    public void testLoadSentencesSuccess() {
        Sentence sentence1 = new SentenceEntity();
        sentence1.setDateline("2016-08-16");
        Sentence sentence2 = new SentenceEntity();
        sentence2.setDateline("2016-08-17");
        final List<Sentence> mockSentences = new ArrayList<>();
        mockSentences.add(sentence1);
        mockSentences.add(sentence2);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((UseCase.UseCaseCallback) invocation.getArguments()[2]).onSuccess(mockSentences);
                return null;
            }
        }).when(mSentenceLoadExecutor).loadSentences(anyBoolean(), anyBoolean(), any(UseCase.UseCaseCallback.class));


        mLoadSentencesUC.setRequestValue(new LoadSentencesUC.LoadSentencesRequestParms(true, true));
        LoadSentencesUC.LoadSentencesCallback callback = new LoadSentencesUC.LoadSentencesCallback() {
            @Override
            public void onSentencesGet(List<? extends Sentence> sentences) {
                assertEquals(sentences, mockSentences);
            }

            @Override
            public void onError(Void aVoid) {
                fail("should be success, no error");
            }
        };

        mLoadSentencesUC.setUseCaseCallback(callback);
        mLoadSentencesUC.run();
        // loadSentences is called
        verify(mSentenceLoadExecutor).loadSentences(true, true, callback);
    }

    @Test
    public void testLoadSentencesFail() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((UseCase.UseCaseCallback) invocation.getArguments()[2]).onError(null);
                return null;
            }
        }).when(mSentenceLoadExecutor).loadSentences(anyBoolean(), anyBoolean(), any(UseCase.UseCaseCallback.class));

        mLoadSentencesUC.setRequestValue(new LoadSentencesUC.LoadSentencesRequestParms(true, true));
        LoadSentencesUC.LoadSentencesCallback callback = new LoadSentencesUC.LoadSentencesCallback() {
            @Override
            public void onSentencesGet(List<? extends Sentence> sentences) {
                fail("should be fail, not success");
            }
        };

        mLoadSentencesUC.setUseCaseCallback(callback);
        mLoadSentencesUC.run();
        // loadSentences is called
        verify(mSentenceLoadExecutor).loadSentences(true, true, callback);
    }
}
