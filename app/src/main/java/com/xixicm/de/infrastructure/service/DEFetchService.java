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
package com.xixicm.de.infrastructure.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.VisibleForTesting;

import com.xixicm.de.data.storage.SentenceDataRepository;

/**
 * @author mc
 */
public class DEFetchService extends IntentService {
    public static final String BOOLEAN_EXTRAL_IS_MANUAL = "boolean_extral_is_manual";

    public DEFetchService() {
        super("DeFetchService");
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            getSentenceDataRepository().fetchTodaysSentence(intent.getBooleanExtra(BOOLEAN_EXTRAL_IS_MANUAL, false));
        }
    }

    @VisibleForTesting
    public SentenceDataRepository getSentenceDataRepository() {
        return SentenceDataRepository.getInstance();
    }
}
