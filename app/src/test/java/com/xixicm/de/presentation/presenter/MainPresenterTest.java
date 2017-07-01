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

import com.xixicm.de.R;
import com.xixicm.de.domain.Constants;
import com.xixicm.ca.domain.handler.UseCaseHandler;
import com.xixicm.de.domain.interactor.ManuallyFetchTodaysSentenceUC;
import com.xixicm.de.domain.model.event.FetchingEvent;
import com.xixicm.de.domain.model.event.FocusedSentenceEvent;
import com.xixicm.de.domain.model.event.UpdateManualFetchFabEvent;
import com.xixicm.de.presentation.contract.Main;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author mc
 */
@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {
    @Mock
    private Main.View mView;
    @Mock
    private UseCaseHandler mUseCaseHandler;
    @Mock
    private ManuallyFetchTodaysSentenceUC mFetchTodaysSentenceUseCase;
    @Mock
    private EventBus mEventBus;
    @Mock
    private FetchingEvent mFetchingEvent;
    @Captor
    private ArgumentCaptor<Class> mEventClassCaptor;

    private MainPresenter mMainPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mMainPresenter = new MainPresenter(mUseCaseHandler, mFetchTodaysSentenceUseCase);
        mMainPresenter.attachView(mView, null);
        mMainPresenter = spy(mMainPresenter);
        when(mMainPresenter.getEventBus()).thenReturn(mEventBus);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mMainPresenter);
    }

    @Test
    public void testFetchTodaysSentence() {
        mMainPresenter.fetchTodaysSentence();
        verify(mFetchTodaysSentenceUseCase).execute(mUseCaseHandler);
    }

    @Test
    public void testOnCreateShowDetailSentence() {
        mMainPresenter.onCreate(1);
        // remove FocusedSentenceEvent
        verify(mEventBus).removeStickyEvent(mEventClassCaptor.capture());
        assertTrue(mEventClassCaptor.getValue().getName().equals(FocusedSentenceEvent.class.getName()));

        // show detail
        verify(mView).showSentenceDetailFragment(1);
        // hide manual fetch button
        verify(mView).updateManualFetchFabStatus(false);
    }

    @Test
    public void testOnCreateShowList() {
        mMainPresenter.onCreate(-1);
        // remove FocusedSentenceEvent
        verify(mEventBus).removeStickyEvent(mEventClassCaptor.capture());
        assertTrue(mEventClassCaptor.getValue().getName().equals(FocusedSentenceEvent.class.getName()));

        // show manual fetch button
        verify(mView).updateManualFetchFabStatus(false);
        verify(mView).updateManualFetchFabStatus(true);
        // clear other fragment in stack
        verify(mView).clearAllFragment();
        // show all sentence list
        verify(mView).showSentenceListFragment(false);
        // close drawer
        verify(mView).closeDrawer();
    }

    @Test
    public void testOnStartWithoutFetchingEvent() {
        mMainPresenter.onStart();
        // register as event listener
        verify(mEventBus).register(mMainPresenter);
        verify(mMainPresenter, times(0)).refreshFetchingStatus(any(FetchingEvent.class));
    }

    @Test
    public void testOnStartWithFetchingEvent() {
        when(mFetchingEvent.getFechingResult()).thenReturn(FetchingEvent.RESULT.SUCCESS);
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(mFetchingEvent);
        mMainPresenter.onStart();
        // register as event listener
        verify(mEventBus).register(mMainPresenter);
        // refresh fetching status
        verify(mMainPresenter).refreshFetchingStatus(mFetchingEvent);
    }

    @Test
    public void testUpdateManualFetchFabStatus_FavoriteList() {
        mMainPresenter.updateManualFetchFabStatus(true, true);
        // displaying favorite list, no fetch button
        verify(mView).updateManualFetchFabStatus(false);
    }

    @Test
    public void testUpdateManualFetchFabStatus_AllList() {
        mMainPresenter.updateManualFetchFabStatus(true, false);
        // displaying all list, has fetch button
        verify(mView).updateManualFetchFabStatus(true);
    }

    @Test
    public void testUpdateManualFetchFabStatus_FavoriteDetail() {
        mMainPresenter.updateManualFetchFabStatus(false, true);
        // displaying detail, no fetch button
        verify(mView).updateManualFetchFabStatus(false);
    }

    @Test
    public void testUpdateManualFetchFabStatus_NoneFavoriteDetail() {
        mMainPresenter.updateManualFetchFabStatus(false, false);
        // displaying detail, no fetch button
        verify(mView).updateManualFetchFabStatus(false);
    }

    @Test
    public void testUpdateManualFetchFabStatus_UpdateEventTrue() {
        UpdateManualFetchFabEvent event = new UpdateManualFetchFabEvent(true);
        mMainPresenter.updateManualFetchFabStatus(event);
        // show fetch button
        verify(mView).updateManualFetchFabStatus(true);
    }

    @Test
    public void testUpdateManualFetchFabStatus_UpdateEventFalse() {
        UpdateManualFetchFabEvent event = new UpdateManualFetchFabEvent(false);
        mMainPresenter.updateManualFetchFabStatus(event);
        // hide fetch button
        verify(mView).updateManualFetchFabStatus(false);
    }

    @Test
    public void testOnResume() {
        String today = Constants.SHORT_DATEFORMAT.format(new Date());
        mMainPresenter.onResume();
        // update date string
        verify(mView).refreshDateView(today);
    }

    @Test
    public void testOnStop() {
        mMainPresenter.onStop();
        // unregister event listener
        verify(mEventBus).unregister(mMainPresenter);
    }

    @Test
    public void testOnBackPressed_DrawerOpened() {
        mMainPresenter.onBackPressed(true, false, 0);
        mMainPresenter.onBackPressed(true, true, 0);
        // unregister event listener
        verify(mView, times(2)).closeDrawer();

        // do not go back
        verify(mView, times(0)).onSuperBackPressed();
    }

    @Test
    public void testOnBackPressed_DrawerClosed_List() {
        mMainPresenter.onBackPressed(false, false, 0);
        // directly back
        verify(mView).onSuperBackPressed();
    }

    @Test
    public void testOnBackPressed_DrawerClosed_Detail_NeedOpenAllList() {
        mMainPresenter.onBackPressed(false, true, 0);

        // show manual fetch button
        verify(mView).updateManualFetchFabStatus(false);
        verify(mView, times(2)).updateManualFetchFabStatus(true);
        // clear other fragment in stack
        verify(mView).clearAllFragment();
        // show all sentence list
        verify(mView).showSentenceListFragment(false);

        // do not go back
        verify(mView, times(0)).onSuperBackPressed();
    }

    @Test
    public void testOnBackPressed_DrawerClosed_Detail_NoNeedOpenAllList() {
        mMainPresenter.onBackPressed(false, true, 1);
        // directly back
        verify(mView).onSuperBackPressed();
    }

    @Test
    public void testRefreshFetchingStatus_IsFetching_REFRESHING() {
        when(mFetchingEvent.isFetching()).thenReturn(true);
        when(mFetchingEvent.getFechingResult()).thenReturn(FetchingEvent.RESULT.REFRESHING);
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(mFetchingEvent);
        mMainPresenter.refreshFetchingStatus(mFetchingEvent);
        // keep do animation
        verify(mView).updateManualFetchFabAnimator(false, false);
        // show refreshing result
        verify(mView).showFetchingResult(R.string.refreshing);
        // reset fetching result
        verify(mFetchingEvent).setFechingResult(FetchingEvent.RESULT.NONE);
    }

    @Test
    public void testRefreshFetchingStatus_IsFetching_START() {
        when(mFetchingEvent.isFetching()).thenReturn(true);
        when(mFetchingEvent.getFechingResult()).thenReturn(FetchingEvent.RESULT.NONE);
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(mFetchingEvent);
        mMainPresenter.refreshFetchingStatus(mFetchingEvent);
        // need to start animation
        verify(mView).updateManualFetchFabAnimator(false, true);
        // not show result
        verify(mView, times(0)).showFetchingResult(anyInt());
        // reset fetching result
        verify(mFetchingEvent).setFechingResult(FetchingEvent.RESULT.NONE);
    }


    @Test
    public void testRefreshFetchingStatus_NotFetching_NO_NETWORK() {
        when(mFetchingEvent.isFetching()).thenReturn(false);
        when(mFetchingEvent.getFechingResult()).thenReturn(FetchingEvent.RESULT.NO_NETWORK);
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(mFetchingEvent);
        mMainPresenter.refreshFetchingStatus(mFetchingEvent);
        // need to stop animation
        verify(mView).updateManualFetchFabAnimator(true, false);
        // show no network result
        verify(mView).showFetchingResult(R.string.refresh_no_network);
        // reset fetching result
        verify(mFetchingEvent).setFechingResult(FetchingEvent.RESULT.NONE);
    }

    @Test
    public void testRefreshFetchingStatus_NotFetching_FAIL() {
        when(mFetchingEvent.isFetching()).thenReturn(false);
        when(mFetchingEvent.getFechingResult()).thenReturn(FetchingEvent.RESULT.FAIL);
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(mFetchingEvent);
        mMainPresenter.refreshFetchingStatus(mFetchingEvent);
        // need to stop animation
        verify(mView).updateManualFetchFabAnimator(true, false);
        // show fail result
        verify(mView).showFetchingResult(R.string.refresh_fail);
        // reset fetching result
        verify(mFetchingEvent).setFechingResult(FetchingEvent.RESULT.NONE);
    }

    @Test
    public void testRefreshFetchingStatus_NotFetching_ABORT() {
        when(mFetchingEvent.isFetching()).thenReturn(false);
        when(mFetchingEvent.getFechingResult()).thenReturn(FetchingEvent.RESULT.ABORT);
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(mFetchingEvent);
        mMainPresenter.refreshFetchingStatus(mFetchingEvent);
        // need to stop animation
        verify(mView).updateManualFetchFabAnimator(true, false);
        // show fail result
        verify(mView).showFetchingResult(R.string.refresh_fail);
        // reset fetching result
        verify(mFetchingEvent).setFechingResult(FetchingEvent.RESULT.NONE);
    }

    @Test
    public void testRefreshFetchingStatus_NotFetching_SUCCESS() {
        when(mFetchingEvent.isFetching()).thenReturn(false);
        when(mFetchingEvent.getFechingResult()).thenReturn(FetchingEvent.RESULT.SUCCESS);
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(mFetchingEvent);
        mMainPresenter.refreshFetchingStatus(mFetchingEvent);
        // need to stop animation
        verify(mView).updateManualFetchFabAnimator(true, false);
        // show finish result
        verify(mView).showFetchingResult(R.string.refresh_finish);
        // reset fetching result
        verify(mFetchingEvent).setFechingResult(FetchingEvent.RESULT.NONE);
    }

    @Test
    public void testRefreshFetchingStatus_NotFetching_GOT_NEW() {
        when(mFetchingEvent.isFetching()).thenReturn(false);
        when(mFetchingEvent.getFechingResult()).thenReturn(FetchingEvent.RESULT.GOT_NEW);
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(mFetchingEvent);
        mMainPresenter.refreshFetchingStatus(mFetchingEvent);
        // need to stop animation
        verify(mView).updateManualFetchFabAnimator(true, false);
        // show got new result
        verify(mView).showFetchingResult(R.string.got_new);
        // reset fetching result
        verify(mFetchingEvent).setFechingResult(FetchingEvent.RESULT.NONE);
    }

    @Test
    public void testRefreshFetchingStatus_NotFetching_EXISTED() {
        when(mFetchingEvent.isFetching()).thenReturn(false);
        when(mFetchingEvent.getFechingResult()).thenReturn(FetchingEvent.RESULT.EXISTED);
        when(mEventBus.getStickyEvent(any(Class.class))).thenReturn(mFetchingEvent);
        mMainPresenter.refreshFetchingStatus(mFetchingEvent);
        // need to stop animation
        verify(mView).updateManualFetchFabAnimator(true, false);
        // show already got result
        verify(mView).showFetchingResult(R.string.already_got);
        // reset fetching result
        verify(mFetchingEvent).setFechingResult(FetchingEvent.RESULT.NONE);
    }

    @Test
    public void testOnNavigationItemSelected_ALLList(){
        mMainPresenter.onNavigationItemSelected(R.id.nav_all);
        // show manual fetch button
        verify(mView).updateManualFetchFabStatus(false);
        verify(mView).updateManualFetchFabStatus(true);
        // clear other fragment in stack
        verify(mView).clearAllFragment();
        // show all sentence list
        verify(mView).showSentenceListFragment(false);
        // close drawer
        verify(mView).closeDrawer();
    }

    @Test
    public void testOnNavigationItemSelected_FavoriteList(){
        mMainPresenter.onNavigationItemSelected(R.id.nav_favorite);
        // hide manual fetch button
        verify(mView).updateManualFetchFabStatus(false);
        // clear other fragment in stack
        verify(mView).clearAllFragment();
        // show favorite sentence list
        verify(mView).showSentenceListFragment(true);
        // close drawer
        verify(mView).closeDrawer();
    }

    @Test
    public void testOnNavigationItemSelected_About(){
        mMainPresenter.onNavigationItemSelected(R.id.nav_about);
        // hide manual fetch button
        verify(mView).updateManualFetchFabStatus(false);
        // clear other fragment in stack
        verify(mView).clearAllFragment();
        // show about frame
        verify(mView).showAboutFragment();
        // close drawer
        verify(mView).closeDrawer();
    }
}
