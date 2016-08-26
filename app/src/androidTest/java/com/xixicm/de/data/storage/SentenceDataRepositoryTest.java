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
package com.xixicm.de.data.storage;

import android.os.SystemClock;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.xixicm.de.TestUtil;
import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.base.util.NetworkChecker;
import com.xixicm.de.domain.base.util.NetworkUtils;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.domain.model.event.FetchingAudioEvent;
import com.xixicm.de.domain.model.event.FetchingEvent;
import com.xixicm.de.domain.model.event.RefreshWidgetEvent;
import com.xixicm.de.domain.model.event.ScheduleFetchEvent;
import com.xixicm.de.domain.model.event.SentenceChangedEvent;
import com.xixicm.de.infrastructure.alarm.FetchAlarmManager;
import com.xixicm.de.infrastructure.base.util.AndroidNetworkChecker;

import org.greenrobot.eventbus.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author mc
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SentenceDataRepositoryTest extends DataBaseTest {
    private NetworkChecker mMockNetworkChecker;
    private NetworkChecker mNetworkChecker;

    @Mock
    EventBus mDefaultEventBus;
    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<Object> mPostEventCaptor;

    @Captor
    private ArgumentCaptor<Object> mPostStickyEventCaptor;

    private void prepareMockItems(boolean isNetworkOK) {
        // workaround for NPE; mockito 1.10.19
        mNetworkChecker = new AndroidNetworkChecker(getTargetContext());
        mMockNetworkChecker = mNetworkChecker;
        MockitoAnnotations.initMocks(this);
        mSentenceDataRepository = spy(mSentenceDataRepository);
        // getEventBus return the mock object
        when(mSentenceDataRepository.getEventBus()).thenReturn(mDefaultEventBus);

        mMockNetworkChecker = spy(mNetworkChecker);
        when(mMockNetworkChecker.isNetworkAvailable()).thenReturn(isNetworkOK);
        NetworkUtils.injectNetworkChecker(mMockNetworkChecker);
    }

    // other way to mock response:
    // https://github.com/mcxiaoke/android-volley/blob/master/src/test/java/com/android/volley/RequestQueueIntegrationTest.java
    private void prepareNoResponse() {
        when(mSentenceDataRepository.getTodaysSentenceResponse()).thenReturn(null);
    }

    private void prepareBadResponse() {
        NetworkResponse response = new NetworkResponse(201, MOCK_SENTENCE_CONTENT.getBytes(), null, true, System.currentTimeMillis());
        when(mSentenceDataRepository.getTodaysSentenceResponse()).thenReturn(response);
    }

    private void prepareGoodResponse() {
        NetworkResponse response = new NetworkResponse(200, MOCK_SENTENCE_CONTENT.getBytes(),
                new HashMap<String, String>(), true, System.currentTimeMillis());
        when(mSentenceDataRepository.getTodaysSentenceResponse()).thenReturn(response);
    }

    private void prepareDuplicateResponse() {
        NetworkResponse response = new NetworkResponse(200, MOCK_SENTENCE_CONTENT.getBytes(),
                new HashMap<String, String>(), true, System.currentTimeMillis());
        when(mSentenceDataRepository.getTodaysSentenceResponse()).thenReturn(response);
        Sentence existedSentence = generateNewSentence(false);
        mSentenceDataRepository.saveSentence(existedSentence);
    }

    private void prepareIsFetchingSentence() {
        when(mSentenceDataRepository.isFetchingSentence()).thenReturn(true);
    }

    private void prepareIsFetchingAudio() {
        when(mSentenceDataRepository.isFetchingAudio()).thenReturn(true);
    }

    private void prepareLocalAudio() {
        when(mSentenceDataRepository.hasDownloadedAudio(anyString())).thenReturn(true);
    }

    private void prepareNoLocalAudio() {
        when(mSentenceDataRepository.hasDownloadedAudio(anyString())).thenReturn(false);
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        // cancel fetch alarm
        FetchAlarmManager.getInstance().cancelFetchAlarm();
    }

    @Test
    public void testSaveSentence_GetSentence_DeleteSentence() {
        Sentence newSentence = generateNewSentence(false);
        // not saved
        assertNull(newSentence.getId());
        mSentenceDataRepository.saveSentence(newSentence);
        // has been saved
        assertNotNull(newSentence.getId());

        Sentence restoredSentence = mSentenceDataRepository.getSentence(newSentence.getId());
        // the sentence can be retrieved from the persistent repository
        assertThat(restoredSentence, is(newSentence));

        mSentenceDataRepository.deleteSentence(newSentence.getId());
        restoredSentence = mSentenceDataRepository.getSentence(newSentence.getId());
        // not exists in persistent repository
        assertNull(restoredSentence);
    }

    @Test
    public void testGetAllSentences_DeleteAllSentences() {
        Sentence newSentence1 = generateNewSentence(false);
        Sentence newSentence2 = generateNewSentence(true);
        mSentenceDataRepository.saveSentence(newSentence1);
        mSentenceDataRepository.saveSentence(newSentence2);
        List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
        // total two saved sentences.
        assertEquals(2, sentences.size());

        mSentenceDataRepository.deleteAllSentences();
        sentences = mSentenceDataRepository.getAllSentences();
        // no saved sentences.
        assertEquals(0, sentences.size());

    }

    @Test
    public void testUpdateSentence_GetFavoriteSentence() {
        Sentence newSentence1 = generateNewSentence(false);
        Sentence newSentence2 = generateNewSentence(true);
        mSentenceDataRepository.saveSentence(newSentence1);
        mSentenceDataRepository.saveSentence(newSentence2);
        Sentence restoredSentence1 = mSentenceDataRepository.getSentence(newSentence1.getId());
        assertFalse(restoredSentence1.getIsStar());
        // update to stared
        newSentence1.setIsStar(true);
        mSentenceDataRepository.updateSentence(newSentence1);
        restoredSentence1 = mSentenceDataRepository.getSentence(newSentence1.getId());
        // already stared
        assertTrue(restoredSentence1.getIsStar());

        List<? extends Sentence> sentences = mSentenceDataRepository.getSentences(true);
        // total two stared sentences now
        assertEquals(2, sentences.size());
        sentences = mSentenceDataRepository.getSentences(false);
        // no not stared sentences now
        assertEquals(0, sentences.size());
    }

    @Test
    public void testGetLatestSentence() {
        Sentence newSentence1 = generateNewSentence(false);
        Sentence newSentence2 = generateNewSentence(true);
        newSentence1.setDateline("2016-08-12");
        newSentence2.setDateline("2016-08-11");
        mSentenceDataRepository.saveSentence(newSentence1);
        mSentenceDataRepository.saveSentence(newSentence2);

        Sentence latestSentence = mSentenceDataRepository.getLatestSentence();
        assertThat(latestSentence, is(newSentence1));
    }

    private void waitNotFetchingSentence(int maxSeconds) {
        int i = 1;
        while (i <= maxSeconds && mSentenceDataRepository.isFetchingSentence()) {
            i++;
            SystemClock.sleep(1000);
        }
        if (mSentenceDataRepository.isFetchingSentence()) {
            fail("waiting for not fetching sentence, time out!");
        }
    }

    private void waitNotFetchingAudio(int maxSeconds) {
        int i = 1;
        while (i <= maxSeconds && mSentenceDataRepository.isFetchingAudio()) {
            i++;
            SystemClock.sleep(1000);
        }
        if (mSentenceDataRepository.isFetchingAudio()) {
            fail("waiting for not fetching audio, time out!");
        }
    }

    private void prepareTodaysSentence() {
        Sentence newSentence = generateNewSentence(false);
        // set as today
        newSentence.setDateline(Constants.SHORT_DATEFORMAT.format(new Date()));
        mSentenceDataRepository.saveSentence(newSentence);
    }

    private void verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE type) {
        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, atLeastOnce()).post(mPostEventCaptor.capture());
        List<Object> events = mPostEventCaptor.getAllValues();
        assertTrue(events.size() > 0);
        Log.d(Constants.TAG, "\n expected type: " + type);
        boolean isContain = false;
        for (Object event : events) {
            Log.d(Constants.TAG, "::::::" + event);
            if (event instanceof ScheduleFetchEvent && ((ScheduleFetchEvent) event).getType() == type) {
                isContain = true;
            }
        }
        if (!isContain) {
            fail("Do not have FetchAlarm: " + type);
        }
    }

    private void verifyNotFetchingSentence() {
        assertFalse(mSentenceDataRepository.isFetchingSentence());
    }

    private void prepareForFetch(boolean withLocalSentence, boolean isNetworkOK) {
        // avoid auto fetch in background
        waitNotFetchingSentence(30);
        mSentenceDataRepository.deleteAllSentences();
        if (withLocalSentence) {
            prepareTodaysSentence();
        }
        prepareMockItems(isNetworkOK);
        if (mSentenceDataRepository.isFetchingSentence()) {
            fail("Is fetching, precondition is not OK!");
        }
    }

    @Test
    public void testFetchTodaysSentence_NotManual_AlreadyInLocal() {
        prepareForFetch(true, true);

        mSentenceDataRepository.fetchTodaysSentence(false);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // no Event
        verify(mDefaultEventBus, times(0)).postSticky(mPostStickyEventCaptor.capture());

        verifyNotFetchingSentence();
    }

    @Test
    public void testFetchTodaysSentence_NotManual_NoLocal_NoNetwork() {
        prepareForFetch(false, false);

        mSentenceDataRepository.fetchTodaysSentence(false);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // no Event
        verify(mDefaultEventBus, times(0)).postSticky(mPostStickyEventCaptor.capture());

        verifyNotFetchingSentence();
    }

    @Test
    public void testFetchTodaysSentence_NotManual_NoLocal_WithNetwork_NoResponse() {
        prepareForFetch(false, true);
        prepareNoResponse();

        mSentenceDataRepository.fetchTodaysSentence(false);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingEvent);
        // start fetch
        assertTrue(((FetchingEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);

        // no response
        // still be NONE status event
        assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.NONE);
        List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
        assertEquals(0, sentences.size());
        // should have retry
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.RETRY);

        verifyNotFetchingSentence();
    }

    @Test
    public void testFetchTodaysSentence_NotManual_NoLocal_WithNetwork_BadResponse() {
        prepareForFetch(false, true);
        prepareBadResponse();

        mSentenceDataRepository.fetchTodaysSentence(false);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingEvent);
        // start fetch
        assertTrue(((FetchingEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);

        // bad response
        // still be NONE status event
        assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.NONE);
        List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
        assertEquals(0, sentences.size());
        // should have retry
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.RETRY);

        verifyNotFetchingSentence();
    }

    @Test
    public void testFetchTodaysSentence_NotManual_NoLocal_WithNetwork_GoodResponse() {
        prepareForFetch(false, true);
        prepareGoodResponse();

        mSentenceDataRepository.fetchTodaysSentence(false);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingEvent);
        // start fetch
        assertTrue(((FetchingEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);

        // has response
        // still be NONE status event
        assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.NONE);
        List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
        assertEquals(1, sentences.size());

        // should have RefreshWidgetEvent and SentenceChangedEvent
        verify(mDefaultEventBus, atLeast(3)).post(mPostEventCaptor.capture());
        events = mPostEventCaptor.getAllValues();
        assertTrue(events.get(events.size() - 1) instanceof RefreshWidgetEvent);
        assertTrue(events.get(events.size() - 2) instanceof SentenceChangedEvent);

        verifyNotFetchingSentence();
    }

    // this test may be fail
    @Test
    public void testFetchTodaysSentence_NotManual_NoLocal_WithNetwork_RealResponse() {
        prepareForFetch(false, true);

        mSentenceDataRepository.fetchTodaysSentence(false);
        waitNotFetchingSentence(60);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingEvent);
        // start fetch
        assertTrue(((FetchingEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);

        if (mNetworkChecker.isNetworkAvailable()) {
            // has response
            // still be NONE status event
            assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.NONE);
            List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
            assertEquals(1, sentences.size());
            // should have RefreshWidgetEvent and SentenceChangedEvent
            verify(mDefaultEventBus, atLeast(3)).post(mPostEventCaptor.capture());
            events = mPostEventCaptor.getAllValues();
            assertTrue(events.get(events.size() - 1) instanceof RefreshWidgetEvent);
            assertTrue(events.get(events.size() - 2) instanceof SentenceChangedEvent);
        } else {
            // bad response
            // still be NONE status event
            assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.NONE);
            List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
            assertEquals(0, sentences.size());
            // should have retry
            verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.RETRY);
        }

        verifyNotFetchingSentence();
    }

    @Test
    public void testFetchTodaysSentence_NotManual_NoLocal_WithNetwork_DuplicateResponse() {
        prepareForFetch(false, true);
        prepareDuplicateResponse();

        // already existed one local sentence
        List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
        assertEquals(1, sentences.size());

        mSentenceDataRepository.fetchTodaysSentence(false);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingEvent);
        // start fetch
        assertTrue(((FetchingEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);

        // has response
        // still be NONE status event
        assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.NONE);

        // duplicate sentence, no new sentence
        sentences = mSentenceDataRepository.getAllSentences();
        assertEquals(1, sentences.size());

        // should have no RefreshWidgetEvent and SentenceChangedEvent
        verify(mDefaultEventBus, atMost(1)).post(mPostEventCaptor.capture());

        verifyNotFetchingSentence();
    }

    @Test
    public void testFetchTodaysSentence_Manual_AlreadyInLocal() {
        prepareForFetch(true, true);

        mSentenceDataRepository.fetchTodaysSentence(true);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // already has the sentence
        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, times(2)).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.size() == 2);
        assertTrue(events.get(0) instanceof FetchingEvent);
        assertTrue(events.get(1) instanceof FetchingEvent);
        // start fetch
        assertTrue(((FetchingEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);
        // already existed event
        assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.EXISTED);

        verifyNotFetchingSentence();
    }

    @Test
    public void testFetchTodaysSentence_Manual_NoLocal_NoNetwork() {
        prepareForFetch(false, false);

        mSentenceDataRepository.fetchTodaysSentence(true);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // already has the sentence
        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, times(2)).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.size() == 2);
        assertTrue(events.get(0) instanceof FetchingEvent);
        assertTrue(events.get(1) instanceof FetchingEvent);
        assertTrue(((FetchingEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);
        // no network event
        assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.NO_NETWORK);

        verifyNotFetchingSentence();
    }

    @Test
    public void testFetchTodaysSentence_Manual_NoLocal_WithNetwork_NoResponse() {
        prepareForFetch(false, true);
        prepareNoResponse();

        mSentenceDataRepository.fetchTodaysSentence(true);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingEvent);
        // start fetch
        assertTrue(((FetchingEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);

        // no response
        // FAIL status event
        assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.FAIL);
        List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
        assertEquals(0, sentences.size());
        // should have retry
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.RETRY);

        verifyNotFetchingSentence();
    }

    @Test
    public void testFetchTodaysSentence_Manual_NoLocal_WithNetwork_BadResponse() {
        prepareForFetch(false, true);
        prepareBadResponse();

        mSentenceDataRepository.fetchTodaysSentence(true);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingEvent);
        // start fetch
        assertTrue(((FetchingEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);

        // bad response
        // FAIL status event
        assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.FAIL);
        List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
        assertEquals(0, sentences.size());
        // should have retry
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.RETRY);

        verifyNotFetchingSentence();
    }

    @Test
    public void testFetchTodaysSentence_Manual_NoLocal_WithNetwork_GoodResponse() {
        prepareForFetch(false, true);
        prepareGoodResponse();

        mSentenceDataRepository.fetchTodaysSentence(true);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingEvent);
        // start fetch
        assertTrue(((FetchingEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);

        // has response
        // still be NONE status event
        assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.GOT_NEW);
        List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
        assertEquals(1, sentences.size());

        // should have RefreshWidgetEvent and SentenceChangedEvent
        verify(mDefaultEventBus, atLeast(3)).post(mPostEventCaptor.capture());
        events = mPostEventCaptor.getAllValues();
        assertTrue(events.get(events.size() - 1) instanceof RefreshWidgetEvent);
        assertTrue(events.get(events.size() - 2) instanceof SentenceChangedEvent);

        verifyNotFetchingSentence();
    }

    // this test may be fail
    @Test
    public void testFetchTodaysSentence_Manual_NoLocal_WithNetwork_RealResponse() {
        prepareForFetch(false, true);

        mSentenceDataRepository.fetchTodaysSentence(true);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingEvent);
        // start fetch
        assertTrue(((FetchingEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);

        if (mNetworkChecker.isNetworkAvailable()) {
            // has response
            // still be NONE status event
            assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.GOT_NEW);
            List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
            assertEquals(1, sentences.size());
            // should have RefreshWidgetEvent and SentenceChangedEvent
            verify(mDefaultEventBus, atLeast(3)).post(mPostEventCaptor.capture());
            events = mPostEventCaptor.getAllValues();
            assertTrue(events.get(events.size() - 1) instanceof RefreshWidgetEvent);
            assertTrue(events.get(events.size() - 2) instanceof SentenceChangedEvent);
        } else {
            // bad response
            // FAIL status event
            assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.FAIL);
            List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
            assertEquals(0, sentences.size());
            // should have retry
            verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.RETRY);
        }

        verifyNotFetchingSentence();
    }

    @Test
    public void testFetchTodaysSentence_Manual_NoLocal_WithNetwork_DuplicateResponse() {
        prepareForFetch(false, true);
        prepareDuplicateResponse();

        // already existed one local sentence
        List<? extends Sentence> sentences = mSentenceDataRepository.getAllSentences();
        assertEquals(1, sentences.size());

        mSentenceDataRepository.fetchTodaysSentence(true);

        // reset alarm
        verifyContainsFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        // Event is captured and invoked with mDefaultEventBus
        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingEvent);
        // start fetch
        assertTrue(((FetchingEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);

        // has response
        // SUCCESS status event
        assertTrue(((FetchingEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.SUCCESS);

        // duplicate sentence, no new sentence
        sentences = mSentenceDataRepository.getAllSentences();
        assertEquals(1, sentences.size());

        // should have no RefreshWidgetEvent and SentenceChangedEvent
        verify(mDefaultEventBus, atMost(1)).post(mPostEventCaptor.capture());

        verifyNotFetchingSentence();
    }

    @Test
    public void testQuickManuallyFetchSentence() {
        prepareForFetch(false, false);
        prepareIsFetchingSentence();
        mSentenceDataRepository.fetchTodaysSentence(true);
        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        Object events = mPostStickyEventCaptor.getValue();
        assertTrue(events instanceof FetchingEvent);
        assertTrue(((FetchingEvent) events).getFechingResult() == FetchingEvent.RESULT.REFRESHING);
    }

    @Test
    public void testFetchSentenceAudio_AlreadyInLocal() {
        prepareMockItems(false);
        prepareLocalAudio();

        mSentenceDataRepository.fetchSentenceAudio(TestUtil.FAKE_AUDIO_URL, System.currentTimeMillis());
        verify(mDefaultEventBus).postSticky(mPostStickyEventCaptor.capture());
        Object event = mPostStickyEventCaptor.getValue();
        assertTrue(event instanceof FetchingAudioEvent);
        assertTrue(((FetchingAudioEvent) event).getAudioUrl().equals(TestUtil.FAKE_AUDIO_URL));
        assertTrue(((FetchingAudioEvent) event).getFechingResult() == FetchingEvent.RESULT.SUCCESS);
    }

    @Test
    public void testFetchSentenceAudio_NoLocal_BadResponse_InvalidUrl() {
        prepareForFetch(false, true);
        prepareNoLocalAudio();

        mSentenceDataRepository.fetchSentenceAudio(TestUtil.FAKE_AUDIO_URL, System.currentTimeMillis());
        waitNotFetchingAudio(60);
        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingAudioEvent);
        assertTrue(events.get(1) instanceof FetchingAudioEvent);
        // start fetch
        assertTrue(((FetchingAudioEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);
        // result fail
        assertTrue(((FetchingAudioEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.FAIL);
    }

    @Test
    public void testFetchSentenceAudio_NoLocal_BadResponse_ValidUrl() {
        prepareForFetch(false, true);
        mSentenceDataRepository.removeDownloadedAudio(TestUtil.SAMPLE_AUDIO_URL);
        // mock bad response
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((SentenceDataRepository.DownloadAudioCallback) invocation.getArguments()[2]).onFail(TestUtil.SAMPLE_AUDIO_URL, System.currentTimeMillis());
                return null;
            }
        }).when(mSentenceDataRepository).startDownloadAudio(anyString(), anyLong(), any(SentenceDataRepository.DownloadAudioCallback.class));

        mSentenceDataRepository.fetchSentenceAudio(TestUtil.SAMPLE_AUDIO_URL, System.currentTimeMillis());

        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingAudioEvent);
        // start fetch
        assertTrue(((FetchingAudioEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);
        // result fail
        assertTrue(((FetchingAudioEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.FAIL);
    }

    @Test
    public void testFetchSentenceAudio_NoLocal_GoodResponse_ValidUrl() {
        prepareForFetch(false, true);
        mSentenceDataRepository.removeDownloadedAudio(TestUtil.SAMPLE_AUDIO_URL);
        // mock good response
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((SentenceDataRepository.DownloadAudioCallback) invocation.getArguments()[2]).onSuccess(TestUtil.SAMPLE_AUDIO_URL, System.currentTimeMillis());
                return null;
            }
        }).when(mSentenceDataRepository).startDownloadAudio(anyString(), anyLong(), any(SentenceDataRepository.DownloadAudioCallback.class));

        mSentenceDataRepository.fetchSentenceAudio(TestUtil.SAMPLE_AUDIO_URL, System.currentTimeMillis());

        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingAudioEvent);
        // start fetch
        assertTrue(((FetchingAudioEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);
        // result success
        assertTrue(((FetchingAudioEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.SUCCESS);
    }

    // this test may be fail
    @Test
    public void testFetchSentenceAudio_NoLocal_RealResponse_ValidUrl() {
        prepareForFetch(false, true);
        mSentenceDataRepository.removeDownloadedAudio(TestUtil.SAMPLE_AUDIO_URL);

        mSentenceDataRepository.fetchSentenceAudio(TestUtil.SAMPLE_AUDIO_URL, System.currentTimeMillis());
        waitNotFetchingAudio(60);

        verify(mDefaultEventBus, atLeastOnce()).postSticky(mPostStickyEventCaptor.capture());
        List<Object> events = mPostStickyEventCaptor.getAllValues();
        assertTrue(events.get(0) instanceof FetchingAudioEvent);
        // start fetch
        assertTrue(((FetchingAudioEvent) events.get(0)).getFechingResult() == FetchingEvent.RESULT.NONE);
        if (mNetworkChecker.isNetworkAvailable()) {
            // result success
            assertTrue(((FetchingAudioEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.SUCCESS);
            assertTrue(mSentenceDataRepository.hasDownloadedAudio(TestUtil.SAMPLE_AUDIO_URL));
            assertNotNull(mSentenceDataRepository.getAudioFile(TestUtil.SAMPLE_AUDIO_URL));
        } else {
            // result fail
            assertTrue(((FetchingAudioEvent) events.get(1)).getFechingResult() == FetchingEvent.RESULT.FAIL);
        }
    }

    @Test
    public void testQuickManuallyFetchSentenceAudio() {
        prepareForFetch(false, true);
        prepareIsFetchingAudio();
        mSentenceDataRepository.fetchSentenceAudio(TestUtil.SAMPLE_AUDIO_URL, System.currentTimeMillis());
        verify(mDefaultEventBus, atLeastOnce()).post(mPostEventCaptor.capture());
        Object events = mPostEventCaptor.getValue();
        assertTrue(events instanceof FetchingEvent);
        assertTrue(((FetchingEvent) events).getFechingResult() == FetchingEvent.RESULT.REFRESHING);
    }
}
