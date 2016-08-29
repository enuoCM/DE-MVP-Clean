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
package com.xixicm.de.presentation.presenter;

import android.support.annotation.VisibleForTesting;

import com.xixicm.de.R;
import com.xixicm.de.data.storage.SentenceDataRepository;
import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.base.handler.UseCaseHandler;
import com.xixicm.de.domain.interactor.ManuallyFetchTodaysSentenceUC;
import com.xixicm.de.domain.interactor.RepositorySentenceFetchExecutor;
import com.xixicm.de.domain.model.event.FetchingEvent;
import com.xixicm.de.domain.model.event.FocusedSentenceEvent;
import com.xixicm.de.domain.model.event.UpdateManualFetchFabEvent;
import com.xixicm.de.presentation.base.handler.UseCaseAsyncUIHandler;
import com.xixicm.de.presentation.base.mvp.AbstractMvpPresenter;
import com.xixicm.de.presentation.contract.Main;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

/**
 * @author mc
 */
public class MainPresenter extends AbstractMvpPresenter<Main.View, Void> implements Main.Presenter<Main.View, Void> {
    private UseCaseHandler mUseCaseHandler;
    private ManuallyFetchTodaysSentenceUC mFetchTodaysSentenceUseCase;

    public MainPresenter() {
        this(UseCaseAsyncUIHandler.getInstance(),
                new ManuallyFetchTodaysSentenceUC(new RepositorySentenceFetchExecutor(SentenceDataRepository.getInstance())));
    }

    public MainPresenter(UseCaseHandler useCaseHandler, ManuallyFetchTodaysSentenceUC fetchTodaysSentenceUseCase) {
        mUseCaseHandler = useCaseHandler;
        mFetchTodaysSentenceUseCase = fetchTodaysSentenceUseCase;
    }

    @Override
    public void fetchTodaysSentence() {
        // It's OK to reuse the same use case here. Because this use case has no callback.
        mUseCaseHandler.execute(mFetchTodaysSentenceUseCase);
    }

    @Override
    public void onCreate(long widgetSentenceId) {
        // clear previous need focused event
        getEventBus().removeStickyEvent(FocusedSentenceEvent.class);
        if (widgetSentenceId >= 0) {
            // default detail
            mView.showSentenceDetailFragment(widgetSentenceId);
            mView.updateManualFetchFabStatus(false);
        } else {
            // default all list
            onNavigationItemSelected(R.id.nav_all);
        }
    }

    @Override
    public void onStart() {
        getEventBus().register(this);
        FetchingEvent stickyEvent = getEventBus().getStickyEvent(FetchingEvent.class);
        if (stickyEvent != null) {
            // BUG of eventbus: new created activity can not got the stick event
            // workaround: manual call back
            refreshFetchingStatus(stickyEvent);
        }
    }

    @Override
    public void updateManualFetchFabStatus(boolean isDisplayingList, boolean isFavoriteList) {
        if (isDisplayingList) {
            mView.updateManualFetchFabStatus(!isFavoriteList);
        } else {
            mView.updateManualFetchFabStatus(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateManualFetchFabStatus(UpdateManualFetchFabEvent event) {
        if (mView != null) {
            mView.updateManualFetchFabStatus(event.getVisible());
        }
    }

    @Override
    public void onResume() {
        mView.refreshDateView(Constants.SHORT_DATEFORMAT.format(new Date()));
    }

    @Override
    public void onStop() {
        getEventBus().unregister(this);
    }

    @Override
    public void onBackPressed(boolean isDrawerOpen, boolean isDetailFragment, int backStackEntryCount) {
        if (isDrawerOpen) {
            mView.closeDrawer();
        } else {
            if (isDetailFragment) {
                if (backStackEntryCount == 0) {
                    // open default all list
                    onNavigationItemSelected(R.id.nav_all);
                    mView.updateManualFetchFabStatus(true);
                    return;
                }
            }
            mView.onSuperBackPressed();
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshFetchingStatus(FetchingEvent fetchingEvent) {
        FetchingEvent stickyEvent = getEventBus().getStickyEvent(FetchingEvent.class);
        if (mView != null) {
            boolean needToEnd = stickyEvent == null || !stickyEvent.isFetching();
            boolean needToStart = !needToEnd && stickyEvent.getFechingResult() != FetchingEvent.RESULT.REFRESHING;
            mView.updateManualFetchFabAnimator(needToEnd, needToStart);
            if (stickyEvent != null) {
                switch (stickyEvent.getFechingResult()) {
                    case REFRESHING:
                        mView.showFetchingResult(R.string.refreshing);
                        break;
                    case NO_NETWORK:
                        mView.showFetchingResult(R.string.refresh_no_network);
                        break;
                    case FAIL:
                    case ABORT:
                        mView.showFetchingResult(R.string.refresh_fail);
                        break;
                    case SUCCESS:
                        mView.showFetchingResult(R.string.refresh_finish);
                        break;
                    case GOT_NEW:
                        mView.showFetchingResult(R.string.got_new);
                        break;
                    case EXISTED:
                        mView.showFetchingResult(R.string.already_got);
                        break;
                }
            }
        }
        if (stickyEvent != null) {
            // clear the result
            stickyEvent.setFechingResult(FetchingEvent.RESULT.NONE);
        }
    }

    @Override
    public void onNavigationItemSelected(int menuId) {
        mView.updateManualFetchFabStatus(false);
        mView.clearAllFragment();
        switch (menuId) {
            case R.id.nav_all:
                mView.updateManualFetchFabStatus(true);
                mView.showSentenceListFragment(false);
                break;
            case R.id.nav_favorite:
                mView.showSentenceListFragment(true);
                break;
            case R.id.nav_about:
                mView.showAboutFragment();
                break;
        }
        mView.closeDrawer();
    }

    @VisibleForTesting
    public EventBus getEventBus() {
        return EventBus.getDefault();
    }
}
