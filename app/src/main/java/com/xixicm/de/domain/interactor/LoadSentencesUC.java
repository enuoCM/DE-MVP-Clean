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

import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.base.usecase.AbstractUseCase;
import com.xixicm.de.domain.base.util.LogUtils;
import com.xixicm.de.domain.model.Sentence;

import java.util.List;

/**
 * A use case to get sentences with special request parameters.
 *
 * @author mc
 */
public class LoadSentencesUC extends AbstractUseCase<LoadSentencesUC.LoadSentencesRequestParms, List<? extends Sentence>, Void> {
    SentenceLoadExecutor mSentenceLoadExecutor;

    public LoadSentencesUC(SentenceLoadExecutor sentenceLoadExecutor) {
        mSentenceLoadExecutor = sentenceLoadExecutor;
    }

    private void loadSentences() {
        LogUtils.v(Constants.TAG, "start to load " + (getRequestValue().isFavorite() ? "favorite " : "") + "sentences.");
        mSentenceLoadExecutor.loadSentences(getRequestValue().isFirstLoad(), getRequestValue().isFavorite(), getUseCaseCallback());
    }

    @Override
    public void run() {
        loadSentences();
    }

    public static class LoadSentencesRequestParms {
        private boolean mIsFavorite;
        private boolean mIsFirstLoad;

        public LoadSentencesRequestParms(boolean isFirstLoad, boolean isFavorite) {
            mIsFirstLoad = isFirstLoad;
            mIsFavorite = isFavorite;
        }

        public boolean isFavorite() {
            return mIsFavorite;
        }

        public boolean isFirstLoad() {
            return mIsFirstLoad;
        }
    }

    public static abstract class LoadSentencesCallback implements UseCaseCallback<List<? extends Sentence>, Void> {
        public abstract void onSentencesGet(List<? extends Sentence> sentences);

        @Override
        public void onSuccess(List<? extends Sentence> sentences) {
            onSentencesGet(sentences);
        }

        @Override
        public void onError(Void aVoid) {
        }
    }
}
