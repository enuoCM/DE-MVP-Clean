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
import com.xixicm.de.domain.model.event.FetchingAudioEvent;
import com.xixicm.ca.presentation.mvp.MvpPresenter;
import com.xixicm.ca.presentation.mvp.MvpView;
import com.xixicm.de.presentation.model.view.SentenceDetailViewModel;

import java.util.List;

/**
 * @author mc
 */
public interface SentenceDetail {
    interface View extends MvpView {
        void showFetchingResult(int stringResource);

        void updateAudioFabAnimator(SentenceDetailViewModel.AUDIO_STATUS status);

        void showSentenceList(List<? extends Sentence> sentences);

        void navigateToSentence(int position);

        void checkPlayOnceMenuItem();

        void checkPlayRepeatMenuItem();
    }

    interface Presenter<V extends MvpView, M> extends MvpPresenter<V, M> {

        void loadSentences(boolean firstLoad);

        void fetchSentenceAudio();

        void setPlayStyle(int style);

        void setFavorite(@NonNull Sentence sentence, boolean favorite);

        void onAudioFabButtonClicked();

        void onFetchingAudioEvent(FetchingAudioEvent fetchingAudioEvent);

        void onDisplaySentence(Sentence sentence);

        void onCreate();

        void onViewCreated();

        void onPrepareOptionsMenu();

        void onActivityCreated();

        void onResume();

        void onStop();
    }
}
