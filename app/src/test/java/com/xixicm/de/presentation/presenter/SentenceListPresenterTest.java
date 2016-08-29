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

import com.xixicm.de.data.entity.SentenceEntity;
import com.xixicm.de.domain.base.handler.UseCaseHandler;
import com.xixicm.de.domain.interactor.LoadSentencesUC;
import com.xixicm.de.domain.interactor.UpdateFavoriteSentenceUC;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.domain.model.event.FocusedSentenceEvent;
import com.xixicm.de.domain.model.event.SentenceChangedEvent;
import com.xixicm.de.domain.model.event.UpdateManualFetchFabEvent;
import com.xixicm.de.presentation.contract.SentenceList;
import com.xixicm.de.presentation.model.view.SentenceListViewModel;

import org.greenrobot.eventbus.EventBus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author mc
 */
@RunWith(MockitoJUnitRunner.class)
public class SentenceListPresenterTest {
    @Mock
    private SentenceList.View mView;
    private SentenceListViewModel mModel;
    @Mock
    private UseCaseHandler mLoadSentencesUseCaseHandler;
    @Mock
    private LoadSentencesUC mLoadSentencesUC;
    @Captor
    private ArgumentCaptor<LoadSentencesUC.LoadSentencesCallback> mLoadSentencesCallbackCaptor;
    @Captor
    private ArgumentCaptor<LoadSentencesUC.LoadSentencesRequestParms> mLoadSentencesRequestParmsCaptor;
    @Mock
    private UseCaseHandler mUpdateFavoriteSentenceUseCaseHandler;
    @Mock
    private UpdateFavoriteSentenceUC mUpdateFavoriteSentenceUC;
    @Mock
    private EventBus mEventBus;
    @Captor
    private ArgumentCaptor<UpdateManualFetchFabEvent> mUpdateManualFetchFabEventCaptor;

    private SentenceListPresenter mSentenceListPresenter;
    @Mock
    private List<? extends Sentence> mSentences;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mSentenceListPresenter = new SentenceListPresenter();
        mModel = new SentenceListViewModel();
        mModel.setIsFavoriteList(false);
        mSentenceListPresenter.attachView(mView, mModel);
        mSentenceListPresenter.setLoadSentencesUCAndHandler(mLoadSentencesUC, mLoadSentencesUseCaseHandler);
        mSentenceListPresenter.setUpdateFavoriteSentenceUCAndHandler(mUpdateFavoriteSentenceUC, mUpdateFavoriteSentenceUseCaseHandler);
        mSentenceListPresenter = spy(mSentenceListPresenter);
        when(mSentenceListPresenter.getEventBus()).thenReturn(mEventBus);
    }

    @Test
    public void testPreConditions() {
        Assert.assertNotNull(mSentenceListPresenter);
    }

    @Test
    public void testCreateViewMode() {
        assertNotNull(mSentenceListPresenter.createViewMode());
    }

    @Test
    public void testLoadSentences_First_EmptyList() {
        mSentenceListPresenter.loadSentences(true);
        // show loading
        verify(mView).setLoadingIndicator(true);
        verify(mLoadSentencesUC).setRequestValue(mLoadSentencesRequestParmsCaptor.capture());
        assertFalse(mLoadSentencesRequestParmsCaptor.getValue().isFavorite());
        assertTrue(mLoadSentencesRequestParmsCaptor.getValue().isFirstLoad());
        verify(mLoadSentencesUC).setUseCaseCallback(mLoadSentencesCallbackCaptor.capture());
        LoadSentencesUC.LoadSentencesCallback callback = mLoadSentencesCallbackCaptor.getValue();
        assertNotNull(callback);
        verify(mLoadSentencesUseCaseHandler).execute(mLoadSentencesUC);

        // empty list
        callback.onSentencesGet(new ArrayList<Sentence>());
        // show no sentence
        verify(mView).showNoSentences(false);
    }

    @Test
    public void testLoadSentences_First_NotEmptyList_NoNeedAutoFocused() {
        mSentenceListPresenter.loadSentences(true);
        // show loading
        verify(mView).setLoadingIndicator(true);
        verify(mLoadSentencesUC).setRequestValue(mLoadSentencesRequestParmsCaptor.capture());
        assertFalse(mLoadSentencesRequestParmsCaptor.getValue().isFavorite());
        assertTrue(mLoadSentencesRequestParmsCaptor.getValue().isFirstLoad());
        verify(mLoadSentencesUC).setUseCaseCallback(mLoadSentencesCallbackCaptor.capture());
        LoadSentencesUC.LoadSentencesCallback callback = mLoadSentencesCallbackCaptor.getValue();
        assertNotNull(callback);
        verify(mLoadSentencesUseCaseHandler).execute(mLoadSentencesUC);

        // not empty list
        when(mSentences.size()).thenReturn(3);
        callback.onSentencesGet(mSentences);
        // show list
        verify(mView).showSentenceList(mSentences);
    }

    @Test
    public void testLoadSentences_First_NotEmptyList_NeedAutoFocused() {
        mSentenceListPresenter.loadSentences(true);
        // show loading
        verify(mView).setLoadingIndicator(true);
        verify(mLoadSentencesUC).setRequestValue(mLoadSentencesRequestParmsCaptor.capture());
        assertFalse(mLoadSentencesRequestParmsCaptor.getValue().isFavorite());
        assertTrue(mLoadSentencesRequestParmsCaptor.getValue().isFirstLoad());
        verify(mLoadSentencesUC).setUseCaseCallback(mLoadSentencesCallbackCaptor.capture());
        LoadSentencesUC.LoadSentencesCallback callback = mLoadSentencesCallbackCaptor.getValue();
        assertNotNull(callback);
        verify(mLoadSentencesUseCaseHandler).execute(mLoadSentencesUC);

        // need auto focused
        mModel.setNeedAutoFocused(true);
        mModel.setNeedFocusedSentenceId(1);
        // not empty list
        when(mSentences.size()).thenReturn(2);
        Sentence sentence1 = mock(Sentence.class);
        when(sentence1.getId()).thenReturn(2L);
        when(mSentences.get(0)).thenReturn(sentence1);
        Sentence sentence2 = mock(Sentence.class);
        when(sentence2.getId()).thenReturn(1L);
        when(mSentences.get(1)).thenReturn(sentence2);
        callback.onSentencesGet(mSentences);
        // show list
        verify(mView).showSentenceList(mSentences);
        // focused on second sentence
        verify(mView).setSelection(1);
        // auto focused is reset
        assertFalse(mModel.isNeedAutoFocused());
    }

    @Test
    public void testLoadSentences_NotFirst() {
        mSentenceListPresenter.loadSentences(false);
        // don not show loading
        verify(mView, times(0)).setLoadingIndicator(true);
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
    public void testOpenSentence() {
        mSentenceListPresenter.openSentence(1L);
        // display sentence detail
        verify(mView).showSentence(1L);
        // need auto focused
        assertTrue(mModel.isNeedAutoFocused());
        // hide manual fetch button
        verify(mEventBus).post(mUpdateManualFetchFabEventCaptor.capture());
        assertFalse(mUpdateManualFetchFabEventCaptor.getValue().getVisible());
    }

    @Test
    public void testSetFavorite_True() {
        SentenceEntity sentence = new SentenceEntity();
        mSentenceListPresenter.setFavorite(sentence, true);
        assertTrue(sentence.getIsStar());
        // execute update
        verify(mUpdateFavoriteSentenceUC).setRequestValue(sentence);
        verify(mUpdateFavoriteSentenceUseCaseHandler).execute(mUpdateFavoriteSentenceUC);
    }

    @Test
    public void testSetFavorite_False() {
        SentenceEntity sentence = new SentenceEntity();
        mSentenceListPresenter.setFavorite(sentence, false);
        assertFalse(sentence.getIsStar());
        // execute update
        verify(mUpdateFavoriteSentenceUC).setRequestValue(sentence);
        verify(mUpdateFavoriteSentenceUseCaseHandler).execute(mUpdateFavoriteSentenceUC);
    }

    @Test
    public void testOnCreate() {
        mSentenceListPresenter.onCreate();
        // register as event listener
        verify(mEventBus).register(mSentenceListPresenter);
    }

    @Test
    public void testOnActivityCreated_Favorite_WithFocusedSentenceEvent() {
        mModel.setIsFavoriteList(true);
        FocusedSentenceEvent event = new FocusedSentenceEvent(1L);
        when(mEventBus.removeStickyEvent(any(Class.class))).thenReturn(event);

        mSentenceListPresenter.onActivityCreated();
        // remove FocusedSentenceEvent
        verify(mEventBus).removeStickyEvent(FocusedSentenceEvent.class);
        // show favorite title
        verify(mView).showTitle(true);
        // do not change manual fetch button's status
        verify(mEventBus, times(0)).post(any(UpdateManualFetchFabEvent.class));
        // need focused to sentence with id 1L
        assertEquals(mModel.getNeedFocusedSentenceId(), 1L);
        verify(mView).setFocusedSentenceId(1L);
        // first load sentences
        verify(mSentenceListPresenter).loadSentences(true);
    }

    @Test
    public void testOnActivityCreated_Favorite_WithoutFocusedSentenceEvent() {
        mModel.setIsFavoriteList(true);
        when(mEventBus.removeStickyEvent(any(Class.class))).thenReturn(null);

        mSentenceListPresenter.onActivityCreated();
        // remove FocusedSentenceEvent
        verify(mEventBus).removeStickyEvent(FocusedSentenceEvent.class);
        // show favorite title
        verify(mView).showTitle(true);
        // do not change manual fetch button's status
        verify(mEventBus, times(0)).post(any(UpdateManualFetchFabEvent.class));
        // no need focused sentence
        assertEquals(mModel.getNeedFocusedSentenceId(), -1L);
        verify(mView).setFocusedSentenceId(-1L);
        // first load sentences
        verify(mSentenceListPresenter).loadSentences(true);
    }

    @Test
    public void testOnActivityCreated_NotFavorite_WithFocusedSentenceEvent() {
        mModel.setIsFavoriteList(false);
        FocusedSentenceEvent event = new FocusedSentenceEvent(1L);
        when(mEventBus.removeStickyEvent(any(Class.class))).thenReturn(event);

        mSentenceListPresenter.onActivityCreated();
        // remove FocusedSentenceEvent
        verify(mEventBus).removeStickyEvent(FocusedSentenceEvent.class);
        // show not favorite title
        verify(mView).showTitle(false);
        // show manual fetch button
        verify(mEventBus).post(mUpdateManualFetchFabEventCaptor.capture());
        assertTrue(mUpdateManualFetchFabEventCaptor.getValue().getVisible());
        // need focused to sentence with id 1L
        assertEquals(mModel.getNeedFocusedSentenceId(), 1L);
        verify(mView).setFocusedSentenceId(1L);
        // first load sentences
        verify(mSentenceListPresenter).loadSentences(true);
    }

    @Test
    public void testOnActivityCreated_NotFavorite_WithoutFocusedSentenceEvent() {
        mModel.setIsFavoriteList(false);
        when(mEventBus.removeStickyEvent(any(Class.class))).thenReturn(null);

        mSentenceListPresenter.onActivityCreated();
        // remove FocusedSentenceEvent
        verify(mEventBus).removeStickyEvent(FocusedSentenceEvent.class);
        // show not favorite title
        verify(mView).showTitle(false);
        // show manual fetch button
        verify(mEventBus).post(mUpdateManualFetchFabEventCaptor.capture());
        assertTrue(mUpdateManualFetchFabEventCaptor.getValue().getVisible());
        // no need focused sentence
        assertEquals(mModel.getNeedFocusedSentenceId(), -1L);
        verify(mView).setFocusedSentenceId(-1L);
        // first load sentences
        verify(mSentenceListPresenter).loadSentences(true);
    }

    @Test
    public void testOnSentenceChanged() {
        mSentenceListPresenter.onSentenceChanged(new SentenceChangedEvent());
        // reload sentences
        verify(mSentenceListPresenter).loadSentences(false);
    }

    @Test
    public void testSetNeedAutoFocused_True() {
        mSentenceListPresenter.setNeedAutoFocused(true);
        assertTrue(mModel.isNeedAutoFocused());
    }

    @Test
    public void testSetNeedAutoFocused_False() {
        mSentenceListPresenter.setNeedAutoFocused(false);
        assertFalse(mModel.isNeedAutoFocused());
    }

    @Test
    public void testOnDestroy() {
        mSentenceListPresenter.onDestroy();
        // unregister event listener
        verify(mEventBus).unregister(mSentenceListPresenter);
        // remove FocusedSentenceEvent
        verify(mEventBus).removeStickyEvent(FocusedSentenceEvent.class);
    }
}
