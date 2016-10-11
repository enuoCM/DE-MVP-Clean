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

import android.net.Uri;

import com.xixicm.de.R;
import com.xixicm.de.data.entity.SentenceEntity;
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
import com.xixicm.de.presentation.contract.SentenceDetail;
import com.xixicm.de.presentation.model.view.SentenceDetailViewModel;

import org.greenrobot.eventbus.EventBus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * @author mc
 */
@RunWith(MockitoJUnitRunner.class)
public class SentenceDetailPresenterTest {
    @Mock
    private SentenceDetail.View mView;
    private SentenceDetailViewModel mModel;
    @Mock
    private UseCaseHandler mLoadSentencesUseCaseHandler;
    @Mock
    private LoadSentencesUC mLoadSentencesUC;
    @Captor
    private ArgumentCaptor<LoadSentencesUC.LoadSentencesCallback> mLoadSentencesCallbackCaptor;
    @Captor
    private ArgumentCaptor<LoadSentenceUC.LoadSentenceCallback> mLoadSentenceCallbackCaptor;
    @Captor
    private ArgumentCaptor<LoadSentencesUC.LoadSentencesRequestParms> mLoadSentencesRequestParmsCaptor;
    @Captor
    private ArgumentCaptor<List<? extends Sentence>> mSentenceListCaptor;
    @Mock
    private UseCaseHandler mUpdateFavoriteSentenceUseCaseHandler;
    @Mock
    private UpdateFavoriteSentenceUC mUpdateFavoriteSentenceUC;
    @Mock
    private UseCaseHandler mSetPlayStyleUseCaseHandler;
    @Mock
    private SetPlayStyleUC mSetPlayStyleUC;
    @Captor
    private ArgumentCaptor<Integer> mSetPlayStyleParmsCaptor;
    @Mock
    private UseCaseHandler mGetPlayStyleUseCaseHandler;
    @Mock
    private GetPlayStyleUC mGetPlayStyleUCForPrepareMenu;
    @Mock
    private GetPlayStyleUC mGetPlayStyleUCForPlayAudio;
    @Captor
    private ArgumentCaptor<GetPlayStyleUC.GetPlayStyleCallback> mGetPlayStyleCallbackCaptor;
    @Mock
    private UseCaseHandler mFetchSentenceAudioUseCaseHander;
    @Mock
    private FetchSentenceAudioUC mFetchSentenceAudioUC;
    @Captor
    private ArgumentCaptor<FetchSentenceAudioUC.FetchSentenceAudioRequestParms> mFetchSentenceAudioRequestParmsCaptor;
    @Mock
    private AudioPlayer mAudioPlayer;
    @Mock
    private SentenceDataRepository mSentenceDataRepository;

    @Mock
    private EventBus mEventBus;

    @Captor
    private ArgumentCaptor<FocusedSentenceEvent> mFocusedSentenceEventCaptor;

    private SentenceDetailPresenter mSentenceDetailPresenter;
    @Mock
    private List<? extends Sentence> mSentences;
    @Mock
    private Sentence mCurrentSentence;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mSentenceDetailPresenter = new SentenceDetailPresenter(mSentenceDataRepository, mAudioPlayer);
        mModel = new SentenceDetailViewModel();
        mModel.setIsFavoriteList(false);
        mModel.setCurrentSentenseId(1L);
        mSentenceDetailPresenter.attachView(mView, mModel);
        mSentenceDetailPresenter.setLoadSentencesUCAndHandler(mLoadSentencesUC, mLoadSentencesUseCaseHandler);
        mSentenceDetailPresenter.setUpdateFavoriteSentenceUCAndHandler(mUpdateFavoriteSentenceUC, mUpdateFavoriteSentenceUseCaseHandler);
        mSentenceDetailPresenter.setFetchSentenceAudioUCAndHandler(mFetchSentenceAudioUC, mFetchSentenceAudioUseCaseHander);
        mSentenceDetailPresenter.setGetPlayStyleUCAndHandler(mGetPlayStyleUCForPrepareMenu, mGetPlayStyleUCForPlayAudio, mGetPlayStyleUseCaseHandler);
        mSentenceDetailPresenter.setSetPlayStyleUCAndHandler(mSetPlayStyleUC, mSetPlayStyleUseCaseHandler);
        mSentenceDetailPresenter = spy(mSentenceDetailPresenter);
        when(mSentenceDetailPresenter.getEventBus()).thenReturn(mEventBus);
    }

    @Test
    public void testPreConditions() {
        Assert.assertNotNull(mSentenceDetailPresenter);
    }

    @Test
    public void testCreateViewMode() {
        assertNotNull(mSentenceDetailPresenter.createViewModel());
    }


    @Test
    public void testLoadSentences_First_EmptyList_NoNeedLoadCurrent() {
        mSentenceDetailPresenter.loadSentences(true);
        verify(mLoadSentencesUC).setRequestValue(mLoadSentencesRequestParmsCaptor.capture());
        assertFalse(mLoadSentencesRequestParmsCaptor.getValue().isFavorite());
        assertTrue(mLoadSentencesRequestParmsCaptor.getValue().isFirstLoad());
        verify(mLoadSentencesUC).setUseCaseCallback(mLoadSentencesCallbackCaptor.capture());
        LoadSentencesUC.LoadSentencesCallback callback = mLoadSentencesCallbackCaptor.getValue();
        assertNotNull(callback);
        verify(mLoadSentencesUseCaseHandler).execute(mLoadSentencesUC);

        // empty list
        when(mSentences.size()).thenReturn(0);
        when(mSentences.isEmpty()).thenReturn(true);
        callback.onSentencesGet(mSentences);
        verify(mView).showSentenceList(mSentences);
        // show no sentence
        verify(mView, times(0)).navigateToSentence(anyInt());
    }

    @Test
    public void testLoadSentences_First_EmptyList_NeedLoadCurrent_Success() {
        mModel.setIsFavoriteList(true);
        mSentenceDetailPresenter.loadSentences(true);
        verify(mLoadSentencesUC).setRequestValue(mLoadSentencesRequestParmsCaptor.capture());
        assertTrue(mLoadSentencesRequestParmsCaptor.getValue().isFavorite());
        assertTrue(mLoadSentencesRequestParmsCaptor.getValue().isFirstLoad());
        verify(mLoadSentencesUC).setUseCaseCallback(mLoadSentencesCallbackCaptor.capture());
        LoadSentencesUC.LoadSentencesCallback callback = mLoadSentencesCallbackCaptor.getValue();
        assertNotNull(callback);
        verify(mLoadSentencesUseCaseHandler).execute(mLoadSentencesUC);

        // empty list
        when(mSentences.size()).thenReturn(0);
        when(mSentences.isEmpty()).thenReturn(true);
        when(mCurrentSentence.getId()).thenReturn(1L);
        when(mCurrentSentence.getAudioUrl()).thenReturn("audio1");
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                LoadSentenceUC.LoadSentenceCallback callback2 = mLoadSentenceCallbackCaptor.getValue();
                assertNotNull(callback2);
                callback2.onSentenceGot(mCurrentSentence);
                return null;
            }
        }).when(mSentenceDetailPresenter).loadCurrentSentense(mLoadSentenceCallbackCaptor.capture());
        callback.onSentencesGet(mSentences);

        // show the sentence
        verify(mView).showSentenceList(mSentenceListCaptor.capture());
        assertThat(mSentenceListCaptor.getValue().get(0), is(mCurrentSentence));
    }


    @Test
    public void testLoadSentences_First_EmptyList_NeedLoadCurrent_Fail() {
        mModel.setIsFavoriteList(true);
        mSentenceDetailPresenter.loadSentences(true);
        verify(mLoadSentencesUC).setRequestValue(mLoadSentencesRequestParmsCaptor.capture());
        assertTrue(mLoadSentencesRequestParmsCaptor.getValue().isFavorite());
        assertTrue(mLoadSentencesRequestParmsCaptor.getValue().isFirstLoad());
        verify(mLoadSentencesUC).setUseCaseCallback(mLoadSentencesCallbackCaptor.capture());
        LoadSentencesUC.LoadSentencesCallback callback = mLoadSentencesCallbackCaptor.getValue();
        assertNotNull(callback);
        verify(mLoadSentencesUseCaseHandler).execute(mLoadSentencesUC);

        // empty list
        when(mSentences.size()).thenReturn(0);
        when(mSentences.isEmpty()).thenReturn(true);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                LoadSentenceUC.LoadSentenceCallback callback2 = mLoadSentenceCallbackCaptor.getValue();
                assertNotNull(callback2);
                callback2.onSentenceGot(null);
                return null;
            }
        }).when(mSentenceDetailPresenter).loadCurrentSentense(mLoadSentenceCallbackCaptor.capture());
        callback.onSentencesGet(mSentences);

        // show no sentence
        verify(mView).showSentenceList(mSentenceListCaptor.capture());
        assertThat(mSentenceListCaptor.getValue().size(), is(0));
    }

    @Test
    public void testLoadSentences_First_NotEmptyList() {
        mSentenceDetailPresenter.loadSentences(true);
        verify(mLoadSentencesUC).setRequestValue(mLoadSentencesRequestParmsCaptor.capture());
        assertFalse(mLoadSentencesRequestParmsCaptor.getValue().isFavorite());
        assertTrue(mLoadSentencesRequestParmsCaptor.getValue().isFirstLoad());
        verify(mLoadSentencesUC).setUseCaseCallback(mLoadSentencesCallbackCaptor.capture());
        LoadSentencesUC.LoadSentencesCallback callback = mLoadSentencesCallbackCaptor.getValue();
        assertNotNull(callback);
        verify(mLoadSentencesUseCaseHandler).execute(mLoadSentencesUC);

        // not empty list
        when(mSentences.size()).thenReturn(2);
        Sentence sentence1 = mock(Sentence.class);
        when(sentence1.getId()).thenReturn(2L);
        when(mSentences.get(0)).thenReturn(sentence1);
        Sentence sentence2 = mock(Sentence.class);
        when(sentence2.getId()).thenReturn(1L);
        when(mSentences.get(1)).thenReturn(sentence2);
        callback.onSentencesGet(mSentences);
        verify(mView).showSentenceList(mSentences);
        // navigate to the target sentence
        verify(mView).navigateToSentence(1);
    }

    @Test
    public void testLoadSentences_NotFirst() {
        mSentenceDetailPresenter.loadSentences(false);

        verify(mLoadSentencesUC).setRequestValue(mLoadSentencesRequestParmsCaptor.capture());
        assertFalse(mLoadSentencesRequestParmsCaptor.getValue().isFavorite());
        assertFalse(mLoadSentencesRequestParmsCaptor.getValue().isFirstLoad());
        verify(mLoadSentencesUC).setUseCaseCallback(mLoadSentencesCallbackCaptor.capture());
        LoadSentencesUC.LoadSentencesCallback callback = mLoadSentencesCallbackCaptor.getValue();
        // mLoadSentencesUC#mLoadSentenceCallback is not initialized under this test
        assertNull(callback);
        verify(mLoadSentencesUseCaseHandler).execute(mLoadSentencesUC);
    }

    @Test
    public void testFetchSentenceAudio() {
        mModel.setCurrentAudioUrl("audio1");
        mModel.setPlayToken(System.currentTimeMillis());
        mSentenceDetailPresenter.fetchSentenceAudio();
        verify(mFetchSentenceAudioUC).setRequestValue(mFetchSentenceAudioRequestParmsCaptor.capture());
        assertEquals(mFetchSentenceAudioRequestParmsCaptor.getValue().getAudioUrl(), mModel.getCurrentAudioUrl());
        assertEquals(mFetchSentenceAudioRequestParmsCaptor.getValue().getToken(), mModel.getPlayToken());
        verify(mFetchSentenceAudioUseCaseHander).execute(mFetchSentenceAudioUC);
    }

    @Test
    public void testSetPlayStyle_Once() {
        mSentenceDetailPresenter.setPlayStyle(Constants.PLAY_ONCE);
        verify(mSetPlayStyleUC).setRequestValue(mSetPlayStyleParmsCaptor.capture());
        assertEquals(mSetPlayStyleParmsCaptor.getValue().intValue(), mModel.getPlayStyle());
        verify(mSetPlayStyleUseCaseHandler).execute(mSetPlayStyleUC);
        // not loop
        verify(mAudioPlayer).setLooping(false);
    }

    @Test
    public void testSetPlayStyle_Repeat() {
        mSentenceDetailPresenter.setPlayStyle(Constants.PLAY_REPEAT);
        verify(mSetPlayStyleUC).setRequestValue(mSetPlayStyleParmsCaptor.capture());
        assertEquals(mSetPlayStyleParmsCaptor.getValue().intValue(), mModel.getPlayStyle());
        verify(mSetPlayStyleUseCaseHandler).execute(mSetPlayStyleUC);
        // not loop
        verify(mAudioPlayer).setLooping(true);
    }


    @Test
    public void testSetFavorite_True() {
        SentenceEntity sentence = new SentenceEntity();
        mSentenceDetailPresenter.setFavorite(sentence, true);
        assertTrue(sentence.getIsStar());
        // execute update
        verify(mUpdateFavoriteSentenceUC).setRequestValue(sentence);
        verify(mUpdateFavoriteSentenceUseCaseHandler).execute(mUpdateFavoriteSentenceUC);
    }

    @Test
    public void testSetFavorite_False() {
        SentenceEntity sentence = new SentenceEntity();
        mSentenceDetailPresenter.setFavorite(sentence, false);
        assertFalse(sentence.getIsStar());
        // execute update
        verify(mUpdateFavoriteSentenceUC).setRequestValue(sentence);
        verify(mUpdateFavoriteSentenceUseCaseHandler).execute(mUpdateFavoriteSentenceUC);
    }

    @Test
    public void testOnAudioFabButtonClicked_NoAudioUrl() {
        mSentenceDetailPresenter.onAudioFabButtonClicked();
        // not start fetch
        verify(mSentenceDetailPresenter, times(0)).fetchSentenceAudio();
        // not update fab button status
        verify(mView, times(0)).updateAudioFabAnimator(any(SentenceDetailViewModel.AUDIO_STATUS.class));
    }

    @Test
    public void testOnAudioFabButtonClicked_IsPlayingAudio() {
        mModel.setCurrentAudioUrl("audio1");
        mModel.setAudioStatus(SentenceDetailViewModel.AUDIO_STATUS.PLAYING);
        mSentenceDetailPresenter.onAudioFabButtonClicked();
        // not start fetch
        verify(mSentenceDetailPresenter, times(0)).fetchSentenceAudio();
        // stop playing
        assertTrue(mModel.getAudioStatus() == SentenceDetailViewModel.AUDIO_STATUS.NORMAL);
        verify(mView).updateAudioFabAnimator(SentenceDetailViewModel.AUDIO_STATUS.NORMAL);
        verify(mAudioPlayer).releaseMediaPlayer();
    }

    @Test
    public void testOnAudioFabButtonClicked_NotPlayingAudio() {
        mModel.setCurrentAudioUrl("audio1");
        mSentenceDetailPresenter.onAudioFabButtonClicked();
        // start fetch
        verify(mSentenceDetailPresenter).fetchSentenceAudio();
    }

    @Test
    public void testOnFetchingAudioEvent_Refreshing() {
        FetchingAudioEvent event = new FetchingAudioEvent(true, FetchingEvent.RESULT.REFRESHING, "audio1", System.currentTimeMillis());
        mSentenceDetailPresenter.onFetchingAudioEvent(event);
        // show loading result
        verify(mView).showFetchingResult(R.string.loading);
    }

    @Test
    public void testOnFetchingAudioEvent_NotCurrent_DifferentUrl() {
        mModel.setCurrentAudioUrl("audio1");
        FetchingAudioEvent event = new FetchingAudioEvent(true, FetchingEvent.RESULT.SUCCESS, "audio2", System.currentTimeMillis());
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(event);
        mSentenceDetailPresenter.onFetchingAudioEvent(event);
        // not update fab button status
        verify(mView, times(0)).updateAudioFabAnimator(any(SentenceDetailViewModel.AUDIO_STATUS.class));
    }

    @Test
    public void testOnFetchingAudioEvent_NotCurrent_DifferentToken() {
        mModel.setCurrentAudioUrl("audio1");
        mModel.setPlayToken(System.currentTimeMillis() / 2);
        FetchingAudioEvent event = new FetchingAudioEvent(true, FetchingEvent.RESULT.SUCCESS, "audio1", System.currentTimeMillis());
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(event);
        mSentenceDetailPresenter.onFetchingAudioEvent(event);
        // not update fab button status
        verify(mView, times(0)).updateAudioFabAnimator(any(SentenceDetailViewModel.AUDIO_STATUS.class));
    }

    @Test
    public void testOnFetchingAudioEvent_Current_Fail() {
        mModel.setCurrentAudioUrl("audio1");
        mModel.setPlayToken(System.currentTimeMillis());
        FetchingAudioEvent event = new FetchingAudioEvent(true, FetchingEvent.RESULT.FAIL, mModel.getCurrentAudioUrl(), mModel.getPlayToken());
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(event);
        mSentenceDetailPresenter.onFetchingAudioEvent(event);
        // show fail result
        verify(mView).showFetchingResult(R.string.download_fail);
        assertTrue(mModel.getAudioStatus() == SentenceDetailViewModel.AUDIO_STATUS.DOWNLOADING_FAIL);
        // clear result
        assertTrue(event.getFechingResult() == FetchingEvent.RESULT.EXISTED);
        // update fab button status
        verify(mView).updateAudioFabAnimator(SentenceDetailViewModel.AUDIO_STATUS.DOWNLOADING_FAIL);
        // release player
        verify(mAudioPlayer).releaseMediaPlayer();
    }

    @Test
    public void testOnFetchingAudioEvent_Current_Success_PlayOnce() {
        mModel.setCurrentAudioUrl("audio1");
        mModel.setPlayToken(System.currentTimeMillis());
        mModel.setPlayStyle(Constants.PLAY_ONCE);
        FetchingAudioEvent event = new FetchingAudioEvent(true, FetchingEvent.RESULT.SUCCESS, mModel.getCurrentAudioUrl(), mModel.getPlayToken());
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(event);
        mSentenceDetailPresenter.onFetchingAudioEvent(event);

        //playing
        assertTrue(mModel.getAudioStatus() == SentenceDetailViewModel.AUDIO_STATUS.PLAYING);
        // clear result
        assertTrue(event.getFechingResult() == FetchingEvent.RESULT.EXISTED);
        // update fab button status
        verify(mView).updateAudioFabAnimator(SentenceDetailViewModel.AUDIO_STATUS.PLAYING);
        // start player
        verify(mAudioPlayer).setLooping(false);
        verify(mSentenceDataRepository).getAudioFile("audio1");
        Uri uri = Uri.fromFile(new File("audio1"));
        when(mSentenceDataRepository.getAudioFile("audio1")).thenReturn(uri);
        verify(mAudioPlayer).startMediaPlayer(uri);
    }

    @Test
    public void testOnFetchingAudioEvent_Current_Success_PlayRepeat() {
        mModel.setCurrentAudioUrl("audio1");
        mModel.setPlayToken(System.currentTimeMillis());
        mModel.setPlayStyle(Constants.PLAY_REPEAT);
        FetchingAudioEvent event = new FetchingAudioEvent(true, FetchingEvent.RESULT.SUCCESS, mModel.getCurrentAudioUrl(), mModel.getPlayToken());
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(event);
        mSentenceDetailPresenter.onFetchingAudioEvent(event);

        //playing
        assertTrue(mModel.getAudioStatus() == SentenceDetailViewModel.AUDIO_STATUS.PLAYING);
        // clear result
        assertTrue(event.getFechingResult() == FetchingEvent.RESULT.EXISTED);
        // update fab button status
        verify(mView).updateAudioFabAnimator(SentenceDetailViewModel.AUDIO_STATUS.PLAYING);
        // start player
        verify(mAudioPlayer).setLooping(true);
        verify(mSentenceDataRepository).getAudioFile("audio1");
        Uri uri = Uri.fromFile(new File("audio1"));
        when(mSentenceDataRepository.getAudioFile("audio1")).thenReturn(uri);
        verify(mAudioPlayer).startMediaPlayer(uri);
    }


    @Test
    public void testOnFetchingAudioEvent_Current_Success_PlayUnknown() {
        mModel.setCurrentAudioUrl("audio1");
        mModel.setPlayToken(System.currentTimeMillis());
        mModel.setPlayStyle(Constants.PLAY_UNKNOWN);
        FetchingAudioEvent event = new FetchingAudioEvent(true, FetchingEvent.RESULT.SUCCESS, mModel.getCurrentAudioUrl(), mModel.getPlayToken());
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(event);
        mSentenceDetailPresenter.onFetchingAudioEvent(event);

        //playing
        assertTrue(mModel.getAudioStatus() == SentenceDetailViewModel.AUDIO_STATUS.PLAYING);
        // clear result
        assertTrue(event.getFechingResult() == FetchingEvent.RESULT.EXISTED);
        // update fab button status
        verify(mView).updateAudioFabAnimator(SentenceDetailViewModel.AUDIO_STATUS.PLAYING);

        // get play style first
        verify(mGetPlayStyleUCForPlayAudio).setUseCaseCallback(mGetPlayStyleCallbackCaptor.capture());
        verify(mGetPlayStyleUseCaseHandler).execute(mGetPlayStyleUCForPlayAudio);
        GetPlayStyleUC.GetPlayStyleCallback callback = mGetPlayStyleCallbackCaptor.getValue();
        assertNotNull(callback);
        // result is repeat
        callback.onPlayStyleGet(Constants.PLAY_REPEAT);
        assertTrue(mModel.getPlayStyle() == Constants.PLAY_REPEAT);
        // start player
        verify(mAudioPlayer).setLooping(true);
        verify(mSentenceDataRepository).getAudioFile("audio1");
        Uri uri = Uri.fromFile(new File("audio1"));
        when(mSentenceDataRepository.getAudioFile("audio1")).thenReturn(uri);
        verify(mAudioPlayer).startMediaPlayer(uri);
    }

    @Test
    public void testOnFetchingAudioEvent_Current_None() {
        mModel.setCurrentAudioUrl("audio1");
        mModel.setPlayToken(System.currentTimeMillis());
        FetchingAudioEvent event = new FetchingAudioEvent(true, FetchingEvent.RESULT.NONE, mModel.getCurrentAudioUrl(), mModel.getPlayToken());
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(event);
        mSentenceDetailPresenter.onFetchingAudioEvent(event);

        //downloading
        assertTrue(mModel.getAudioStatus() == SentenceDetailViewModel.AUDIO_STATUS.DOWNLOADING);
        // clear result
        assertTrue(event.getFechingResult() == FetchingEvent.RESULT.EXISTED);
        // update fab button status
        verify(mView).updateAudioFabAnimator(SentenceDetailViewModel.AUDIO_STATUS.DOWNLOADING);

        // release player
        verify(mAudioPlayer).releaseMediaPlayer();
    }

    @Test
    public void testOnDisplaySentence_SameSentence() {
        when(mCurrentSentence.getId()).thenReturn(1L);
        mModel.setCurrentSentenseId(1L);
        mSentenceDetailPresenter.onDisplaySentence(mCurrentSentence);
        // do nothing
        verify(mView, times(0)).updateAudioFabAnimator(any(SentenceDetailViewModel.AUDIO_STATUS.class));
    }

    @Test
    public void testOnDisplaySentence_DifferentSentence() {
        when(mCurrentSentence.getId()).thenReturn(2L);
        when(mCurrentSentence.getAudioUrl()).thenReturn("audio2");
        mModel.setCurrentSentenseId(1L);
        mModel.setPlayToken(System.currentTimeMillis() / 2);
        long originToken = mModel.getPlayToken();
        mSentenceDetailPresenter.onDisplaySentence(mCurrentSentence);
        // change to display new sentence
        assertTrue(mModel.getCurrentSentenseId() == 2L);
        assertEquals("audio2", mModel.getCurrentAudioUrl());
        assertEquals(SentenceDetailViewModel.AUDIO_STATUS.NORMAL, mModel.getAudioStatus());
        assertNotEquals(originToken, mModel.getPlayToken());
        // update fab status
        verify(mView).updateAudioFabAnimator(SentenceDetailViewModel.AUDIO_STATUS.NORMAL);
        // stop play
        verify(mAudioPlayer).releaseMediaPlayer();
    }

    @Test
    public void testOnCreate() {
        mSentenceDetailPresenter.onCreate();
        // register as event listener
        verify(mEventBus).register(mSentenceDetailPresenter);
    }

    @Test
    public void testOnViewCreated() {
        mModel.setAudioStatus(SentenceDetailViewModel.AUDIO_STATUS.NORMAL);
        mSentenceDetailPresenter.onViewCreated();
        // update fab status
        verify(mView).updateAudioFabAnimator(SentenceDetailViewModel.AUDIO_STATUS.NORMAL);
        // release player
        verify(mAudioPlayer).releaseMediaPlayer();
    }

    @Test
    public void testOnPrepareOptionsMenu_PlayOnce() {
        mModel.setPlayStyle(Constants.PLAY_ONCE);
        mSentenceDetailPresenter.onPrepareOptionsMenu();
        // check play once menu item
        verify(mView).checkPlayOnceMenuItem();
    }

    @Test
    public void testOnPrepareOptionsMenu_PlayRepeat() {
        mModel.setPlayStyle(Constants.PLAY_REPEAT);
        mSentenceDetailPresenter.onPrepareOptionsMenu();
        // check play repeat menu item
        verify(mView).checkPlayRepeatMenuItem();
    }

    @Test
    public void testOnPrepareOptionsMenu_PlayUnknown() {
        mModel.setPlayStyle(Constants.PLAY_UNKNOWN);
        mSentenceDetailPresenter.onPrepareOptionsMenu();
        // get play style first
        verify(mGetPlayStyleUCForPrepareMenu).setUseCaseCallback(mGetPlayStyleCallbackCaptor.capture());
        verify(mGetPlayStyleUseCaseHandler).execute(mGetPlayStyleUCForPrepareMenu);
        GetPlayStyleUC.GetPlayStyleCallback callback = mGetPlayStyleCallbackCaptor.getValue();
        assertNotNull(callback);
        // result is repeat
        callback.onPlayStyleGet(Constants.PLAY_REPEAT);
        assertTrue(mModel.getPlayStyle() == Constants.PLAY_REPEAT);
        // check play repeat menu item
        verify(mView).checkPlayRepeatMenuItem();
    }

    @Test
    public void testOnActivityCreated() {
        mSentenceDetailPresenter.onActivityCreated();
        // start to load sentences
        verify(mSentenceDetailPresenter).loadSentences(true);
    }

    @Test
    public void testOnResume_PlayOnce() {
        mModel.setPlayStyle(Constants.PLAY_ONCE);
        mSentenceDetailPresenter.onResume();
        // reset to not loop
        verify(mAudioPlayer).setLooping(false);
    }

    @Test
    public void testOnResume_PlayRepeat() {
        mModel.setPlayStyle(Constants.PLAY_REPEAT);
        mSentenceDetailPresenter.onResume();
        // reset to loop
        verify(mAudioPlayer).setLooping(true);
    }

    @Test
    public void testOnResume_PlayUnknown() {
        mModel.setPlayStyle(Constants.PLAY_UNKNOWN);
        mSentenceDetailPresenter.onResume();
        // do not reset as play style is unknown
        verify(mAudioPlayer, times(0)).setLooping(anyBoolean());
    }

    @Test
    public void testOnStop_PlayOnce() {
        mModel.setPlayStyle(Constants.PLAY_ONCE);
        mSentenceDetailPresenter.onStop();
        // reset to not loop
        verify(mAudioPlayer).setLooping(false);
    }

    @Test
    public void testOnStop_PlayRepeat() {
        mModel.setPlayStyle(Constants.PLAY_REPEAT);
        mSentenceDetailPresenter.onStop();
        // reset to not loop
        verify(mAudioPlayer).setLooping(false);
    }

    @Test
    public void testOnStop_PlayUnknown() {
        mModel.setPlayStyle(Constants.PLAY_UNKNOWN);
        mSentenceDetailPresenter.onStop();
        // do not reset as play style is unknown
        verify(mAudioPlayer, times(0)).setLooping(anyBoolean());
    }


    @Test
    public void testOnDestroy() {
        mModel.setCurrentSentenseId(2L);
        mSentenceDetailPresenter.onDestroy();
        // release player
        verify(mAudioPlayer).releaseMediaPlayer();
        // unregister event listener
        verify(mEventBus).unregister(mSentenceDetailPresenter);
        // post FocusedSentenceEvent
        verify(mEventBus).postSticky(mFocusedSentenceEventCaptor.capture());
        FocusedSentenceEvent event = mFocusedSentenceEventCaptor.getValue();
        assertEquals(event.getSentenceId(), 2L);
    }
}
