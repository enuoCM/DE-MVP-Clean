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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.base.handler.UseCaseHandler;
import com.xixicm.de.domain.base.util.LogUtils;
import com.xixicm.de.domain.interactor.LoadSentencesUC;
import com.xixicm.de.domain.interactor.UpdateFavoriteSentenceUC;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.domain.model.event.FocusedSentenceEvent;
import com.xixicm.de.domain.model.event.SentenceChangedEvent;
import com.xixicm.de.domain.model.event.UpdateManualFetchFabEvent;
import com.xixicm.de.presentation.base.mvp.AbstractMvpPresenter;
import com.xixicm.de.presentation.contract.SentenceList;
import com.xixicm.de.presentation.model.view.SentenceListViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * @author mc
 */
public class SentenceListPresenter extends AbstractMvpPresenter<SentenceList.View, SentenceListViewModel>
        implements SentenceList.Presenter<SentenceList.View, SentenceListViewModel> {
    private UseCaseHandler mLoadSentencesUseCaseHandler;
    private LoadSentencesUC mLoadSentencesUC;
    private LoadSentencesUC.LoadSentencesCallback mLoadSentenceCallback;
    private UseCaseHandler mUpdateFavoriteSentenceUseCaseHandler;
    private UpdateFavoriteSentenceUC mUpdateFavoriteSentenceUC;

    public SentenceListPresenter() {
    }

    public void setLoadSentencesUCAndHandler(LoadSentencesUC loadSentencesUC, UseCaseHandler useCaseHandler) {
        mLoadSentencesUC = loadSentencesUC;
        mLoadSentencesUseCaseHandler = useCaseHandler;
    }

    public void setUpdateFavoriteSentenceUCAndHandler(UpdateFavoriteSentenceUC updateFavoriteSentenceUC, UseCaseHandler useCaseHandler) {
        mUpdateFavoriteSentenceUC = updateFavoriteSentenceUC;
        mUpdateFavoriteSentenceUseCaseHandler = useCaseHandler;
    }

    @Nullable
    @Override
    protected SentenceListViewModel createViewMode() {
        return new SentenceListViewModel();
    }

    @Override
    public void loadSentences(final boolean firstLoad) {
        mLoadSentencesUC.setRequestValue(new LoadSentencesUC.LoadSentencesRequestParms(firstLoad, getViewModel().isFavoriteList()));
        if (firstLoad) {
            mView.setLoadingIndicator(true);
            mLoadSentenceCallback = new LoadSentencesUC.LoadSentencesCallback() {
                @Override
                public void onSentencesGet(List<? extends Sentence> sentences) {
                    if (mView != null) {
                        if (sentences == null || sentences.isEmpty()) {
                            mView.showNoSentences(getViewModel().isFavoriteList());
                        } else {
                            mView.showSentenceList(sentences);
                            if (getViewModel().isNeedAutoFocused()) {
                                int needFocusedPosition = findNeedFocusedPosition(sentences);
                                if (needFocusedPosition >= 0) {
                                    mView.setSelection(needFocusedPosition);
                                }
                                setNeedAutoFocused(false);
                            }
                        }
                    }
                }
            };
        }
        mLoadSentencesUC.setUseCaseCallback(mLoadSentenceCallback);
        mLoadSentencesUseCaseHandler.execute(mLoadSentencesUC);
    }

    private int findNeedFocusedPosition(List<? extends Sentence> sentences) {
        int position = -1;
        long needFocusedSentenceId = getViewModel().getNeedFocusedSentenceId();
        if (needFocusedSentenceId >= 0) {
            for (int i = 0; i < sentences.size(); i++) {
                Sentence s = sentences.get(i);
                if (s.getId() == needFocusedSentenceId) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    @Override
    public void openSentence(long sentenceId) {
        mView.showSentence(sentenceId);
        setNeedAutoFocused(true);
        getEventBus().post(new UpdateManualFetchFabEvent(false));
    }

    @Override
    public void setFavorite(@NonNull Sentence sentence, boolean favorite) {
        sentence.setIsStar(favorite);
        mUpdateFavoriteSentenceUC.setRequestValue(sentence);
        mUpdateFavoriteSentenceUseCaseHandler.execute(mUpdateFavoriteSentenceUC);
    }

    @Override
    public void onCreate() {
        // register in onCreate for background SentenceChangedEvent
        getEventBus().register(this);
    }

    @Override
    public void onActivityCreated() {
        boolean isFavorite = getViewModel().isFavoriteList();
        mView.showTitle(isFavorite);
        if (!isFavorite) {
            getEventBus().post(new UpdateManualFetchFabEvent(true));
        }
        FocusedSentenceEvent focusedSentenceEvent = getEventBus().removeStickyEvent(FocusedSentenceEvent.class);
        if (focusedSentenceEvent != null) {
            getViewModel().setNeedFocusedSentenceId(focusedSentenceEvent.getSentenceId());
        }
        mView.setFocusedSentenceId(getViewModel().getNeedFocusedSentenceId());
        loadSentences(true);
    }

    @Subscribe
    @Override
    public void onSentenceChanged(SentenceChangedEvent event) {
        LogUtils.v(Constants.TAG, "sentence changed, reload");
        loadSentences(false);
    }

    @Override
    public void setNeedAutoFocused(boolean needAutoFocused) {
        getViewModel().setNeedAutoFocused(needAutoFocused);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getEventBus().unregister(this);
        getEventBus().removeStickyEvent(FocusedSentenceEvent.class);
    }

    @VisibleForTesting
    public EventBus getEventBus() {
        return EventBus.getDefault();
    }
}
