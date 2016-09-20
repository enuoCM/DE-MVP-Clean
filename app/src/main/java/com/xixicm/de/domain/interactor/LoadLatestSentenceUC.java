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
import com.xixicm.ca.domain.usecase.AbstractUseCase;
import com.xixicm.ca.domain.util.LogUtils;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.domain.repository.SentenceRepository;

/**
 * A use case to get the latest sentence.
 *
 * @author mc
 */
public class LoadLatestSentenceUC extends AbstractUseCase<Void, Sentence, Void> {
    SentenceRepository mSentenceRepository;

    public LoadLatestSentenceUC(SentenceRepository sentenceDataRepository) {
        mSentenceRepository = sentenceDataRepository;
    }

    private void loadLatestSentence() {
        LogUtils.v(Constants.TAG, "start to load latest sentence.");
        Sentence sentence = mSentenceRepository.getLatestSentence();
        if (getUseCaseCallback() != null) {
            getUseCaseCallback().onSuccess(sentence);
        }
    }

    @Override
    public void run() {
        loadLatestSentence();
    }

    public static abstract class LoadLatestSentenceCallback implements UseCaseCallback<Sentence, Void> {
        public abstract void onLatestSentenceGot(Sentence sentence);

        @Override
        public void onSuccess(Sentence sentence) {
            onLatestSentenceGot(sentence);
        }

        @Override
        public void onError(Void aVoid) {
        }
    }
}
