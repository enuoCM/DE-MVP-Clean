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

/**
 * A manually trigger use case to fetch today's sentence.  No request parameter, No response,
 * implementation should base on other notification scheme, such as EventBus
 *
 * @author mc
 */
public class ManuallyFetchTodaysSentenceUC extends AbstractUseCase<Void, Void, Void> {
    SentenceFetchExecutor mSentenceFetchExecutor;

    public ManuallyFetchTodaysSentenceUC(SentenceFetchExecutor sentenceFetchExecutor) {
        mSentenceFetchExecutor = sentenceFetchExecutor;
    }

    private void fetchTodaysSentence() {
        LogUtils.v(Constants.TAG, "manually start to fetch today's sentence.");
        mSentenceFetchExecutor.fetchTodaysSentence(true);
    }

    @Override
    public void run() {
        fetchTodaysSentence();
    }
}
