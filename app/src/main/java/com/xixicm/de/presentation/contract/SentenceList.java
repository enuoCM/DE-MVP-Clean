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
package com.xixicm.de.presentation.contract;

import android.support.annotation.NonNull;

import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.domain.model.event.SentenceChangedEvent;
import com.xixicm.de.presentation.base.mvp.MvpPresenter;
import com.xixicm.de.presentation.base.mvp.MvpView;

import java.util.List;

/**
 * @author mc
 */
public interface SentenceList {
    interface View extends MvpView {
        void setLoadingIndicator(boolean active);

        void showNoSentences(boolean favorite);

        void showTitle(boolean favorite);

        void showSentenceList(List<? extends Sentence> sentences);

        void showSentence(long sentenceId);

        void setSelection(int position);

        void setFocusedSentenceId(long sentenceId);

        boolean isFavoriteList();
    }

    interface Presenter<V extends MvpView, M> extends MvpPresenter<V, M> {
        void loadSentences(boolean firstLoad);

        void openSentence(long sentenceId);

        void setFavorite(@NonNull Sentence sentence, boolean favorite);

        void onCreate();

        void onActivityCreated();

        void onSentenceChanged(SentenceChangedEvent event);

        void setNeedAutoFocused(boolean needAutoFocused);
    }
}
