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
import com.xixicm.de.domain.repository.SentenceRepository;

/**
 * A use case to get sentence with specific id.
 *
 * @author mc
 */
public class LoadSentenceUC extends AbstractUseCase<Long, Sentence, Void> {
    SentenceRepository mSentenceRepository;

    public LoadSentenceUC(SentenceRepository sentenceDataRepository) {
        mSentenceRepository = sentenceDataRepository;
    }

    private void loadSentence() {
        LogUtils.v(Constants.TAG, "start to load sentence[" + getRequestValue() + "]");
        Sentence sentence = mSentenceRepository.getSentence(getRequestValue());
        if (getUseCaseCallback() != null) {
            getUseCaseCallback().onSuccess(sentence);
        }
    }

    @Override
    public void run() {
        loadSentence();
    }

    public static abstract class LoadSentenceCallback implements UseCaseCallback<Sentence, Void> {
        public abstract void onSentenceGot(Sentence sentence);

        @Override
        public void onSuccess(Sentence sentence) {
            onSentenceGot(sentence);
        }

        @Override
        public void onError(Void aVoid) {
        }
    }
}
