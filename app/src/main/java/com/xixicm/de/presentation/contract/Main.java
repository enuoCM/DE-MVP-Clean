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

import com.xixicm.de.domain.model.event.FetchingEvent;
import com.xixicm.de.domain.model.event.UpdateManualFetchFabEvent;
import com.xixicm.de.presentation.base.mvp.MvpPresenter;
import com.xixicm.de.presentation.base.mvp.MvpView;

/**
 * @author mc
 */
public interface Main {
    interface View extends MvpView {
        void showSentenceDetailFragment(long sentenceId);

        void showSentenceListFragment(boolean favorite);

        void showAboutFragment();

        void clearAllFragment();

        void updateManualFetchFabStatus(boolean visiable);

        void refreshDateView(String date);

        void updateManualFetchFabAnimator(boolean needToEnd, boolean needToStart);

        void showFetchingResult(int stringResource);

        void closeDrawer();

        void onSuperBackPressed();
    }

    interface Presenter<V extends MvpView, M> extends MvpPresenter<V, M> {
        void fetchTodaysSentence();

        void onCreate(long widgetSentenceId);

        void onStart();

        void updateManualFetchFabStatus(boolean isDisplayingList, boolean isFavoriteList);

        void updateManualFetchFabStatus(UpdateManualFetchFabEvent event);

        void onResume();

        void onStop();

        void onBackPressed(boolean isDrawerOpen, boolean isDetailFragment, int backStackEntryCount);

        void refreshFetchingStatus(FetchingEvent fetchingEvent);

        void onNavigationItemSelected(int menuId);
    }
}
