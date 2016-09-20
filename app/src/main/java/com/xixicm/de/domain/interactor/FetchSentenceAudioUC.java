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
import com.xixicm.de.domain.repository.SentenceRepository;

/**
 * A manually trigger use case to fetch sentence's audio.  No response,
 * implementation should base on other notification scheme, such as EventBus
 *
 * @author mc
 */
public class FetchSentenceAudioUC extends AbstractUseCase<FetchSentenceAudioUC.FetchSentenceAudioRequestParms, Void, Void> {
    SentenceRepository mSentenceRepository;

    public FetchSentenceAudioUC(SentenceRepository sentenceRepository) {
        mSentenceRepository = sentenceRepository;
    }

    private void fetchSentenceAudio() {
        LogUtils.v(Constants.TAG, "fetch audio: " + getRequestValue() + ".");
        mSentenceRepository.fetchSentenceAudio(getRequestValue().getAudioUrl(), getRequestValue().getToken());
    }

    @Override
    public void run() {
        fetchSentenceAudio();
    }

    public static class FetchSentenceAudioRequestParms {
        private String mAudioUrl;
        private long mToken;

        public FetchSentenceAudioRequestParms(String audioUrl, long token) {
            mAudioUrl = audioUrl;
            mToken = token;
        }

        public String getAudioUrl() {
            return mAudioUrl;
        }

        public long getToken() {
            return mToken;
        }

        @Override
        public String toString() {
            return "FetchSentenceAudioRequestParms{" +
                    "mAudioUrl='" + mAudioUrl + '\'' +
                    ", mToken=" + mToken +
                    '}';
        }
    }
}
