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

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.xixicm.de.R;
import com.xixicm.de.data.storage.SentenceDataRepository;
import com.xixicm.de.domain.Constants;
import com.xixicm.ca.domain.handler.UseCaseHandler;
import com.xixicm.de.domain.interactor.FetchSentenceAudioUC;
import com.xixicm.de.domain.interactor.GetPlayStyleUC;
import com.xixicm.de.domain.interactor.LoadSentenceUC;
import com.xixicm.de.domain.interactor.LoadSentencesUC;
import com.xixicm.de.domain.interactor.SetPlayStyleUC;
import com.xixicm.de.domain.interactor.UpdateFavoriteSentenceUC;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.domain.model.event.FetchingAudioEvent;
import com.xixicm.de.domain.model.event.FetchingEvent;
import com.xixicm.de.domain.model.event.FocusedSentenceEvent;
import com.xixicm.de.infrastructure.media.AudioPlayer;
import com.xixicm.ca.presentation.handler.UseCaseAsyncUIHandler;
import com.xixicm.ca.presentation.mvp.AbstractMvpPresenter;
import com.xixicm.de.presentation.contract.SentenceDetail;
import com.xixicm.de.presentation.model.view.SentenceDetailViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mc
 */
public class SentenceDetailPresenter extends AbstractMvpPresenter<SentenceDetail.View, SentenceDetailViewModel>
        implements SentenceDetail.Presenter<SentenceDetail.View, SentenceDetailViewModel> {
    private UseCaseHandler mLoadSentencesUseCaseHandler;
    private LoadSentencesUC mLoadSentencesUC;
    private LoadSentencesUC.LoadSentencesCallback mLoadSentenceCallback;
    private UseCaseHandler mUpdateFavoriteSentenceUseCaseHandler;
    private UpdateFavoriteSentenceUC mUpdateFavoriteSentenceUC;
    private UseCaseHandler mSetPlayStyleUseCaseHandler;
    private SetPlayStyleUC mSetPlayStyleUC;
    private UseCaseHandler mGetPlayStyleUseCaseHandler;
    private GetPlayStyleUC mGetPlayStyleUCForPrepareMenu;
    private GetPlayStyleUC mGetPlayStyleUCForPlayAudio;
    private UseCaseHandler mFetchSentenceAudioUseCaseHander;
    private FetchSentenceAudioUC mFetchSentenceAudioUC;

    private AudioPlayer mAudioPlayer;
    private SentenceDataRepository mSentenceDataRepository;

    public SentenceDetailPresenter(SentenceDataRepository sentenceDataRepository, AudioPlayer audioPlayer) {
        mSentenceDataRepository = sentenceDataRepository;
        mAudioPlayer = audioPlayer;
        mAudioPlayer.setPlayCallback(new AudioPlayer.PlayCallback() {
            @Override
            public void onPrepareError() {
                getViewModel().setAudioStatus(SentenceDetailViewModel.AUDIO_STATUS.DOWNLOADING_FAIL);
                updateAudioFabStatusIfNeed();
            }

            @Override
            public void onPlayCompletion() {
                getViewModel().setAudioStatus(SentenceDetailViewModel.AUDIO_STATUS.NORMAL);
                updateAudioFabStatusIfNeed();
            }

            @Override
            public void onPlayError() {
                onPlayCompletion();
            }

            @Override
            public void onPlayFocusLoss() {
                onPlayCompletion();
            }

            @Override
            public void onCannotGetPlayFocus() {
                onPlayCompletion();
            }
        });
    }

    public void setLoadSentencesUCAndHandler(LoadSentencesUC loadSentencesUC, UseCaseHandler useCaseHandler) {
        mLoadSentencesUC = loadSentencesUC;
        mLoadSentencesUseCaseHandler = useCaseHandler;
    }

    public void setUpdateFavoriteSentenceUCAndHandler(UpdateFavoriteSentenceUC updateFavoriteSentenceUC, UseCaseHandler useCaseHandler) {
        mUpdateFavoriteSentenceUC = updateFavoriteSentenceUC;
        mUpdateFavoriteSentenceUseCaseHandler = useCaseHandler;
    }

    public void setSetPlayStyleUCAndHandler(SetPlayStyleUC setPlayStyleUC, UseCaseHandler useCaseHandler) {
        mSetPlayStyleUC = setPlayStyleUC;
        mSetPlayStyleUseCaseHandler = useCaseHandler;
    }

    public void setGetPlayStyleUCAndHandler(GetPlayStyleUC getPlayStyleUCForPrepareMenu,
                                            GetPlayStyleUC getPlayStyleUCForPlayAudio,
                                            UseCaseHandler useCaseHandler) {
        mGetPlayStyleUCForPrepareMenu = getPlayStyleUCForPrepareMenu;
        mGetPlayStyleUCForPlayAudio = getPlayStyleUCForPlayAudio;
        mGetPlayStyleUseCaseHandler = useCaseHandler;
    }

    public void setFetchSentenceAudioUCAndHandler(FetchSentenceAudioUC fetchSentenceAudioUC, UseCaseHandler useCaseHandler) {
        mFetchSentenceAudioUC = fetchSentenceAudioUC;
        mFetchSentenceAudioUseCaseHander = useCaseHandler;
    }

    @Override
    public void loadSentences(boolean firstLoad) {
        mLoadSentencesUC.setRequestValue(new LoadSentencesUC.LoadSentencesRequestParms(firstLoad, getViewModel().isFavoriteList()));
        if (firstLoad) {
            mLoadSentenceCallback = new LoadSentencesUC.LoadSentencesCallback() {
                @Override
                public void onSentencesGet(List<? extends Sentence> sentences) {
                    if (mView != null) {
                        if ((sentences == null || sentences.isEmpty())
                                && getViewModel().getCurrentSentenseId() >= 0 && getViewModel().isFavoriteList()) {
                            // the sentence may change to unstar. avoid showing nothing
                            // try to load the sentences
                            loadCurrentSentense(new LoadSentenceUC.LoadSentenceCallback() {
                                @Override
                                public void onSentenceGot(Sentence sentence) {
                                    List<Sentence> sentences1 = new ArrayList<Sentence>();
                                    if (sentence != null) {
                                        sentences1.add(sentence);
                                    }
                                    showSentenceList(sentences1);
                                }
                            });
                        } else {
                            showSentenceList(sentences);
                        }
                    }
                }
            };
        }
        mLoadSentencesUC.setUseCaseCallback(mLoadSentenceCallback);
        mLoadSentencesUseCaseHandler.execute(mLoadSentencesUC);
    }

    public void loadCurrentSentense(LoadSentenceUC.LoadSentenceCallback callback) {
        LoadSentenceUC loadSentenceUC = new LoadSentenceUC(SentenceDataRepository.getInstance());
        loadSentenceUC.setRequestValue(getViewModel().getCurrentSentenseId());
        loadSentenceUC.setUseCaseCallback(callback);
        UseCaseAsyncUIHandler.getInstance().execute(loadSentenceUC);
    }

    private void showSentenceList(List<? extends Sentence> sentences) {
        if (mView != null) {
            mView.showSentenceList(sentences);
            //get initial position
            if (getViewModel().getCurrentSentenseId() >= 0) {
                int position = findInitialPosition(sentences);
                if (position > 0) {
                    mView.navigateToSentence(position);
                }
            }
        }
    }

    private int findInitialPosition(List<? extends Sentence> sentences) {
        int position = 0;
        for (int i = 0; i < sentences.size(); i++) {
            Sentence s = sentences.get(i);
            if (s.getId() == getViewModel().getCurrentSentenseId()) {
                getViewModel().setCurrentAudioUrl(s.getAudioUrl());
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    public void fetchSentenceAudio() {
        mFetchSentenceAudioUC.setRequestValue(new FetchSentenceAudioUC.FetchSentenceAudioRequestParms(getViewModel().getCurrentAudioUrl(), getViewModel().getPlayToken()));
        mFetchSentenceAudioUseCaseHander.execute(mFetchSentenceAudioUC);
    }

    @Override
    public void setPlayStyle(int style) {
        getViewModel().setPlayStyle(style);
        mSetPlayStyleUC.setRequestValue(style);
        mSetPlayStyleUseCaseHandler.execute(mSetPlayStyleUC);
        mAudioPlayer.setLooping(style == Constants.PLAY_REPEAT);
    }

    @Override
    public void setFavorite(@NonNull Sentence sentence, boolean favorite) {
        sentence.setIsStar(favorite);
        mUpdateFavoriteSentenceUC.setRequestValue(sentence);
        mUpdateFavoriteSentenceUseCaseHandler.execute(mUpdateFavoriteSentenceUC);
    }

    @Override
    public void onAudioFabButtonClicked() {
        String currentAudioUrl = getViewModel().getCurrentAudioUrl();
        if (currentAudioUrl == null) {
            return;
        }
        if (getViewModel().getAudioStatus() == SentenceDetailViewModel.AUDIO_STATUS.PLAYING) {
            // stop playing
            getViewModel().setAudioStatus(SentenceDetailViewModel.AUDIO_STATUS.NORMAL);
            updateAudioFabStatusAndPlayIfNeed();
        } else {
            fetchSentenceAudio();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onFetchingAudioEvent(FetchingAudioEvent fetchingAudioEvent) {
        if (mView == null) {
            return;
        }
        if (fetchingAudioEvent.getFechingResult() == FetchingEvent.RESULT.REFRESHING) {
            mView.showFetchingResult(R.string.loading);
            return;
        }
        FetchingAudioEvent stickyEvent = getEventBus().getStickyEvent(FetchingAudioEvent.class);
        if (stickyEvent != null) {
            if (getViewModel().getCurrentAudioUrl() == null || !getViewModel().getCurrentAudioUrl().equals(stickyEvent.getAudioUrl())
                    || getViewModel().getPlayToken() != stickyEvent.getToken()) {
                // not current sentence's state
                // clear the result, set to any other status
                stickyEvent.setFechingResult(FetchingEvent.RESULT.EXISTED);
                return;
            }

            switch (stickyEvent.getFechingResult()) {
                case FAIL:
                    mView.showFetchingResult(R.string.download_fail);
                    getViewModel().setAudioStatus(SentenceDetailViewModel.AUDIO_STATUS.DOWNLOADING_FAIL);
                    break;
                case SUCCESS:
                    getViewModel().setAudioStatus(SentenceDetailViewModel.AUDIO_STATUS.PLAYING);
                    break;
                case NONE:
                    // mean start to download
                    getViewModel().setAudioStatus(SentenceDetailViewModel.AUDIO_STATUS.DOWNLOADING);
                    break;
            }
            // clear the result, set to any other status
            stickyEvent.setFechingResult(FetchingEvent.RESULT.EXISTED);
            updateAudioFabStatusAndPlayIfNeed();
        }
    }

    @Override
    public void onDisplaySentence(Sentence sentence) {
        if (sentence == null) {
            return;
        }
        long oldId = getViewModel().getCurrentSentenseId();
        if (oldId != sentence.getId()) {
            getViewModel().setPlayToken(System.currentTimeMillis());
            getViewModel().setCurrentSentenseId(sentence.getId());
            getViewModel().setCurrentAudioUrl(sentence.getAudioUrl());
            getViewModel().setAudioStatus(SentenceDetailViewModel.AUDIO_STATUS.NORMAL);
            updateAudioFabStatusAndPlayIfNeed();
        }
    }

    @Override
    public void onCreate() {
        getEventBus().register(this);
    }

    @Override
    public void onViewCreated() {
        updateAudioFabStatusAndPlayIfNeed();
    }

    @Override
    public void onPrepareOptionsMenu() {
        if (getViewModel().getPlayStyle() == Constants.PLAY_UNKNOWN) {
            mGetPlayStyleUCForPrepareMenu.setUseCaseCallback(new GetPlayStyleUC.GetPlayStyleCallback() {
                @Override
                public void onPlayStyleGet(int playStyle) {
                    getViewModel().setPlayStyle(playStyle);
                    updateOptionsMenu();
                }
            });
            mGetPlayStyleUseCaseHandler.execute(mGetPlayStyleUCForPrepareMenu);
        } else {
            updateOptionsMenu();
        }
    }

    private void updateOptionsMenu() {
        if (mView != null) {
            if (getViewModel().getPlayStyle() == Constants.PLAY_ONCE) {
                mView.checkPlayOnceMenuItem();
            } else {
                mView.checkPlayRepeatMenuItem();
            }
        }
    }

    private void updateAudioFabStatusIfNeed() {
        if (mView != null) {
            mView.updateAudioFabAnimator(getViewModel().getAudioStatus());
        }
    }

    private void updateAudioFabStatusAndPlayIfNeed() {
        updateAudioFabStatusIfNeed();
        refreshMediaPlayer();
    }

    private void refreshMediaPlayer() {
        SentenceDetailViewModel.AUDIO_STATUS status = getViewModel().getAudioStatus();
        switch (status) {
            case NORMAL:
                releaseMediaPlayer();
                break;
            case DOWNLOADING:
                releaseMediaPlayer();
                break;
            case DOWNLOADING_FAIL:
                releaseMediaPlayer();
                break;
            case PLAYING:
                startMediaPlayer();
                break;
        }
    }

    private void releaseMediaPlayer() {
        mAudioPlayer.releaseMediaPlayer();
    }

    private void startMediaPlayer() {
        if (getViewModel().getPlayStyle() == Constants.PLAY_UNKNOWN) {
            // have not got the play style.
            mGetPlayStyleUCForPlayAudio.setUseCaseCallback(new GetPlayStyleUC.GetPlayStyleCallback() {
                @Override
                public void onPlayStyleGet(int playStyle) {
                    getViewModel().setPlayStyle(playStyle);
                    startMediaPlayer();
                }
            });
            mGetPlayStyleUseCaseHandler.execute(mGetPlayStyleUCForPlayAudio);
        } else {
            mAudioPlayer.setLooping(getViewModel().getPlayStyle() == Constants.PLAY_REPEAT);
            mAudioPlayer.startMediaPlayer(mSentenceDataRepository.getAudioFile(getViewModel().getCurrentAudioUrl()));
        }
    }

    @Override
    public void onActivityCreated() {
        loadSentences(true);
    }

    @Override
    public void onResume() {
        if (getViewModel().getPlayStyle() != Constants.PLAY_UNKNOWN) {
            mAudioPlayer.setLooping(getViewModel().getPlayStyle() == Constants.PLAY_REPEAT);
        }
    }

    @Override
    public void onStop() {
        if (getViewModel().getPlayStyle() != Constants.PLAY_UNKNOWN) {
            // only play once in background.
            mAudioPlayer.setLooping(false);
        }
    }

    @Override
    public void onDestroy() {
        releaseMediaPlayer();
        getEventBus().unregister(this);
        getEventBus().postSticky(new FocusedSentenceEvent(getViewModel().getCurrentSentenseId()));
    }

    @Nullable
    @Override
    protected SentenceDetailViewModel createViewMode() {
        return new SentenceDetailViewModel();
    }

    @VisibleForTesting
    public EventBus getEventBus() {
        return EventBus.getDefault();
    }
}
