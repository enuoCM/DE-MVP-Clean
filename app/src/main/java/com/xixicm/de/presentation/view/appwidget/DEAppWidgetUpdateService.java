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
package com.xixicm.de.presentation.view.appwidget;

import android.app.IntentService;
import android.content.Intent;

import com.xixicm.de.R;
import com.xixicm.de.data.entity.SentenceEntity;
import com.xixicm.de.data.storage.SentenceDataRepository;
import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.base.handler.DefaultUseCaseHandler;
import com.xixicm.de.domain.interactor.AutoFetchTodaysSentenceUC;
import com.xixicm.de.domain.interactor.LoadLatestSentenceUC;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.infrastructure.service.ServiceSentenceFetchExecutor;

import java.util.Date;

/**
 * @author mc
 */
public class DEAppWidgetUpdateService extends IntentService {

    public DEAppWidgetUpdateService() {
        super("DEAppWidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // query latest sentence
        final int widgetType = intent.getIntExtra(WidgetManager.EXTRA_WIDGET_TYPE, 0);
        if (widgetType > 0) {
            LoadLatestSentenceUC loadLatestSentenceUC = new LoadLatestSentenceUC(SentenceDataRepository.getInstance());
            loadLatestSentenceUC.setUseCaseCallback(new LoadLatestSentenceUC.LoadLatestSentenceCallback() {
                @Override
                public void onLatestSentenceGot(Sentence sentence) {
                    if (sentence == null) {
                        // trigger to auto fetch in service
                        AutoFetchTodaysSentenceUC autoFetchTodaysSentenceUC
                                = new AutoFetchTodaysSentenceUC(ServiceSentenceFetchExecutor.getInstance(DEAppWidgetUpdateService.this));
                        DefaultUseCaseHandler.createParallelUCHandler().execute(autoFetchTodaysSentenceUC);
                        sentence = new SentenceEntity();
                        // fake sentence
                        final String today = Constants.SHORT_DATEFORMAT.format(new Date());
                        sentence.setDateline(today);
                        sentence.setContent(getString(R.string.no_sentence));
                    }
                    if ((widgetType & WidgetManager.TYPE_4_1) != 0) {
                        WidgetManager.getInstance().bindDEAppWidget(sentence, WidgetManager.TYPE_4_1);
                    }
                    if ((widgetType & WidgetManager.TYPE_5_1) != 0) {
                        WidgetManager.getInstance().bindDEAppWidget(sentence, WidgetManager.TYPE_5_1);
                    }
                }
            });
            DefaultUseCaseHandler.createSyncUCHandler().execute(loadLatestSentenceUC);
        }
    }
}
