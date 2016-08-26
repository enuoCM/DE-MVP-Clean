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
package com.xixicm.de.infrastructure.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.xixicm.de.domain.base.usecase.UseCase;
import com.xixicm.de.domain.model.Sentence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author mc
 */
@RunWith(MockitoJUnitRunner.class)
public class AsyncTaskSentenceLoadExecutorTest {
    @Mock
    private LoaderManager mLoaderManager;
    @Mock
    private Loader mLoader;
    @Mock
    private Context mContext;
    // mark as mock first
    @Mock
    AsyncTaskSentenceLoadExecutor mAsyncTaskSentenceLoadExecutor;
    @Mock
    private List<Sentence> mMockSentences;
    @Mock
    private UseCase.UseCaseCallback<List<? extends Sentence>, Void> mCallback;

    @Captor
    private ArgumentCaptor<Bundle> mBundleCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mAsyncTaskSentenceLoadExecutor = new AsyncTaskSentenceLoadExecutor(mContext, mLoaderManager);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mAsyncTaskSentenceLoadExecutor);
    }

    @Test
    public void testLoadSentencesFirst() {
        mAsyncTaskSentenceLoadExecutor.loadSentences(true, true, null);
        verify(mLoaderManager).getLoader(anyInt());
        verify(mLoaderManager).initLoader(anyInt(), mBundleCaptor.capture(), eq(mAsyncTaskSentenceLoadExecutor));
        Bundle bundle = mBundleCaptor.getValue();
        // Bundle is not real mocked
        //assertTrue(bundle.getBoolean("IS_FAVORITE"));
        assertTrue(bundle != null);
    }

    @Test
    public void testLoadSentencesNotFirst1() {
        mAsyncTaskSentenceLoadExecutor.loadSentences(false, false, null);
        // still no existed loader
        verify(mLoaderManager).getLoader(anyInt());
        verify(mLoaderManager).initLoader(anyInt(), mBundleCaptor.capture(), eq(mAsyncTaskSentenceLoadExecutor));
        Bundle bundle = mBundleCaptor.getValue();
        assertFalse(bundle.getBoolean("IS_FAVORITE"));
    }

    @Test
    public void testLoadSentencesNotFirst2() {
        when(mLoaderManager.getLoader(anyInt())).thenReturn(mLoader);
        mAsyncTaskSentenceLoadExecutor.loadSentences(false, true, null);
        verify(mLoaderManager).getLoader(anyInt());
        verify(mLoaderManager, atMost(0)).initLoader(anyInt(), mBundleCaptor.capture(), eq(mAsyncTaskSentenceLoadExecutor));
        verify(mLoader).onContentChanged();
    }

    @Test
    public void testOnLoadFinished() {
        mAsyncTaskSentenceLoadExecutor.loadSentences(true, true, mCallback);
        mAsyncTaskSentenceLoadExecutor.onLoadFinished(mLoader, mMockSentences);
        verify(mCallback).onSuccess(mMockSentences);
    }
}
