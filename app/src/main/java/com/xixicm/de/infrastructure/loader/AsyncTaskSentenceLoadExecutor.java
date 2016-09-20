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

import com.xixicm.de.data.storage.SentenceDataLoader;
import com.xixicm.de.domain.Constants;
import com.xixicm.ca.domain.usecase.UseCase;
import com.xixicm.ca.domain.util.LogUtils;
import com.xixicm.de.domain.interactor.SentenceLoadExecutor;
import com.xixicm.de.domain.model.Sentence;

import java.util.List;

/**
 * @author mc
 */
public class AsyncTaskSentenceLoadExecutor implements SentenceLoadExecutor<List<? extends Sentence>, Void>, LoaderManager.LoaderCallbacks<List<? extends Sentence>> {
    private static final int SENTENCES_QUERY = 1;
    private static final String IS_FAVORITE = "IS_FAVORITE";
    private final LoaderManager mLoaderManager;
    private final Context mContext;
    private UseCase.UseCaseCallback<List<? extends Sentence>, Void> mCaseCallback;

    public AsyncTaskSentenceLoadExecutor(Context context, LoaderManager loaderManager) {
        mContext = context;
        mLoaderManager = loaderManager;
    }

    public void loadSentences(boolean isFirstLoad, boolean favorite, UseCase.UseCaseCallback<List<? extends Sentence>, Void> callback) {
        mCaseCallback = callback;
        Loader loader = mLoaderManager.getLoader(SENTENCES_QUERY);
        if (isFirstLoad || loader == null) {
            Bundle args = new Bundle();
            args.putBoolean(IS_FAVORITE, favorite);
            mLoaderManager.initLoader(SENTENCES_QUERY, args, this);
        } else {
            loader.onContentChanged();
        }
    }

    @Override
    public Loader<List<? extends Sentence>> onCreateLoader(int id, Bundle args) {
        return new SentenceDataLoader(mContext, args.getBoolean(IS_FAVORITE));
    }

    @Override
    public void onLoadFinished(Loader<List<? extends Sentence>> loader, List<? extends Sentence> data) {
        LogUtils.v(Constants.TAG, "load sentences done!");
        if (mCaseCallback != null) {
            mCaseCallback.onSuccess(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<? extends Sentence>> loader) {

    }
}
