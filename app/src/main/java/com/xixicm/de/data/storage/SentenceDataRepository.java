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

import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.VisibleForTesting;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.cache.DiskBasedCache;
import com.android.volley.error.VolleyError;
import com.android.volley.request.DownloadRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.VolleyTickle;
import com.xixicm.de.data.entity.SentenceEntity;
import com.xixicm.de.data.net.DEVolley;
import com.xixicm.de.data.storage.dao.DaoManager;
import com.xixicm.de.data.storage.dao.SentenceEntityDao;
import com.xixicm.de.domain.Constants;
import com.xixicm.ca.domain.util.LogUtils;
import com.xixicm.ca.domain.util.NetworkUtils;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.domain.model.event.FetchingAudioEvent;
import com.xixicm.de.domain.model.event.FetchingEvent;
import com.xixicm.de.domain.model.event.RefreshWidgetEvent;
import com.xixicm.de.domain.model.event.ScheduleFetchEvent;
import com.xixicm.de.domain.model.event.SentenceChangedEvent;
import com.xixicm.de.domain.repository.SentenceRepository;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author mc
 */
public class SentenceDataRepository implements SentenceRepository {
    private static String URL_ICIBA = "http://open.iciba.com/dsapi/";
    private static SentenceDataRepository sInstance;
    private Object mFetchingLock = new Object();
    private boolean mIsFetchingSentence;
    private boolean mIsFetchingAudio;

    public static synchronized SentenceDataRepository getInstance() {
        if (sInstance == null) {
            sInstance = new SentenceDataRepository();
        }
        return sInstance;
    }

    SentenceDataRepository() {
    }

    /**
     * Get local latest sentence.
     * Note: Query in current thread
     *
     * @return latest sentence
     */
    @Override
    public Sentence getLatestSentence() {
        return DaoManager.getInstance().getSentenceEntityDao().queryBuilder().orderDesc(SentenceEntityDao.Properties.Dateline).limit(1).unique();
    }

    /**
     * Get local sentence with specific id.
     * Note: Query in current thread
     *
     * @return latest sentence
     */
    @Override
    public Sentence getSentence(long sentenceId) {
        return DaoManager.getInstance().getSentenceEntityDao().load(sentenceId);
    }

    /**
     * Get local sentences.
     * Note: Query in current thread
     *
     * @param favorite if true return all favorite sentence, if false return all not favorite sentences
     * @return
     */
    @Override
    public List<? extends Sentence> getSentences(boolean favorite) {
        return DaoManager.getInstance().getSentenceEntityDao().queryBuilder().where(SentenceEntityDao.Properties.IsStar.eq(favorite ? Boolean.TRUE : Boolean.FALSE))
                .orderDesc(SentenceEntityDao.Properties.Dateline).list();
    }


    /**
     * Get all local sentences.
     *
     * @return
     */
    @Override
    public List<? extends Sentence> getAllSentences() {
        return DaoManager.getInstance().getSentenceEntityDao().queryBuilder().orderDesc(SentenceEntityDao.Properties.Dateline).list();
    }

    @Override
    public void fetchSentenceAudio(final String audioUrl, final long playToken) {
        synchronized (mFetchingLock) {
            if (isFetchingAudio()) {
                updateFetchingAudioStatusAndNotify(true, FetchingEvent.RESULT.REFRESHING, audioUrl, playToken, false);
                return;
            }
        }
        if (hasDownloadedAudio(audioUrl)) {
            updateFetchingAudioStatusAndNotify(false, FetchingEvent.RESULT.SUCCESS, audioUrl, playToken, true);
            return;
        }
        // mean starting
        updateFetchingAudioStatusAndNotify(true, FetchingEvent.RESULT.NONE, audioUrl, playToken, true);
        LogUtils.d(Constants.TAG, "try to fetchSentenceAudio:" + audioUrl);
        // start downloading, may get from the cache
        // Note, can use sync download here. but keep to use callback.
        startDownloadAudio(audioUrl, playToken, new DownloadAudioCallback());
    }

    public void startDownloadAudio(final String audioUrl, final long playToken, final DownloadAudioCallback callback) {
        DownloadRequest request = new DownloadRequest(audioUrl, DEVolley.getInstance().getTempAudioDownloadFile().getPath(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(audioUrl, playToken);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFail(audioUrl, playToken);
            }
        });
        request.setRetryPolicy(DEVolley.getInstance().getDefaultRetryPolicy(2));
        request.setTag("fetchSentenceAudio");
        DEVolley.getInstance().getRequestQueue().add(request);
    }

    public class DownloadAudioCallback {
        public void onSuccess(final String audioUrl, final long playToken) {
            updateFetchingAudioStatusAndNotify(false, FetchingEvent.RESULT.SUCCESS, audioUrl, playToken, true);
        }

        public void onFail(final String audioUrl, final long playToken) {
            updateFetchingAudioStatusAndNotify(false, FetchingEvent.RESULT.FAIL, audioUrl, playToken, true);
        }
    }

    public boolean hasDownloadedAudio(String audioUrl) {
        Cache cache = DEVolley.getInstance().getCache();
        if (cache != null) {
            // ignore expired setting
            return cache.get(audioUrl) != null;
        }
        return false;
    }

    private void updateFetchingAudioStatusAndNotify(boolean isFetching, FetchingEvent.RESULT result,
                                                    String audioUrl, long token, boolean stick) {
        synchronized (mFetchingLock) {
            mIsFetchingAudio = isFetching;
        }
        if (stick) {
            getEventBus().postSticky(new FetchingAudioEvent(isFetching, result, audioUrl, token));
        } else {
            getEventBus().post(new FetchingAudioEvent(isFetching, result, audioUrl, token));
        }
    }

    @Override
    public Uri getAudioFile(String audioUrl) {
        DiskBasedCache cache = DEVolley.getInstance().getCache();
        if (audioUrl != null && cache != null) {
            File audioFile = cache.getFileForKey(audioUrl);
            if (audioFile != null && audioFile.exists()) {
                return Uri.fromFile(audioFile);
            }
        }
        return null;
    }

    /**
     * Get todays's sentence from local db first. If can not get, try to fetch it from network.
     * Will post fetch status event {@link FetchingEvent} during the fetching process. If got a new sentence and saved to db,
     * will post {@link SentenceChangedEvent} and  {@link RefreshWidgetEvent}
     *
     * @param manual
     */
    @Override
    public void fetchTodaysSentence(final boolean manual) {
        synchronized (mFetchingLock) {
            if (isFetchingSentence()) {
                LogUtils.d(Constants.TAG, "\nfetchTodaysSentence manual: " + manual + " IsFetchingSentence:" + mIsFetchingSentence);
                updateFetchingSentenceStatusAndNotify(true, manual, FetchingEvent.RESULT.REFRESHING);
                return;
            }
        }
        LogUtils.d(Constants.TAG, "\ntry to fetchTodaysSentence, and schedule next auto fetch alarm. manual: " + manual);
        updateFetchingSentenceStatusAndNotify(true, manual, FetchingEvent.RESULT.NONE);
        // set/reset auto fetch alarm
        setFetchAlarm(ScheduleFetchEvent.TYPE.NORMAL);

        String today = Constants.SHORT_DATEFORMAT.format(new Date());
        Sentence sentence = DaoManager.getInstance().getSentenceEntityDao().queryBuilder()
                .where(SentenceEntityDao.Properties.Dateline.eq(today)).unique();
        if (sentence != null) {
            // already existed
            LogUtils.d(Constants.TAG, "already has the sentence of " + today);
            updateFetchingSentenceStatusAndNotify(false, manual, manual ? FetchingEvent.RESULT.EXISTED : FetchingEvent.RESULT.NONE);
            return;
        }
        if (!NetworkUtils.isNetworkAvailable()) {
            // no network, abort fetching
            updateFetchingSentenceStatusAndNotify(false, manual, manual ? FetchingEvent.RESULT.NO_NETWORK : FetchingEvent.RESULT.NONE);
            LogUtils.d(Constants.TAG, "no network, abort fetching");
            return;
        }
        // start network fetching
        updateFetchingSentenceStatusAndNotify(true, !manual, FetchingEvent.RESULT.NONE);
        // not UI thread
        NetworkResponse response = getTodaysSentenceResponse();
        // background
        FetchingEvent.RESULT fetchResult = manual ? FetchingEvent.RESULT.FAIL : FetchingEvent.RESULT.NONE;
        if (response != null && response.statusCode == 200) {
            fetchResult = manual ? FetchingEvent.RESULT.SUCCESS : FetchingEvent.RESULT.NONE;
            String data = VolleyTickle.parseResponse(response);
            try {
                JSONObject result = new JSONObject(data);
                SentenceEntity newSentence = new SentenceEntity();
                newSentence.setDateline(result.optString(SentenceEntityDao.Properties.Dateline.name));
                if (today.equals(newSentence.getDateline())
                        || DaoManager.getInstance().getSentenceEntityDao().queryBuilder()
                        .where(SentenceEntityDao.Properties.Dateline.eq(newSentence.getDateline())).build().unique() == null) {
                    // insert today's or first got sentence
                    newSentence.setSid(result.optString(SentenceEntityDao.Properties.Sid.name));
                    newSentence.setContent(result.optString(SentenceEntityDao.Properties.Content.name));
                    newSentence.setAllContent(data);
                    newSentence.setIsStar(false);
                    saveSentence(newSentence);
                    LogUtils.d(Constants.TAG, "inserted one new sentence");
                    getEventBus().post(new SentenceChangedEvent());
                    LogUtils.d(Constants.TAG, "try to updateDEAppWidget");
                    getEventBus().post(new RefreshWidgetEvent());
                    fetchResult = manual ? FetchingEvent.RESULT.GOT_NEW : FetchingEvent.RESULT.NONE;
                }
            } catch (JSONException e) {
            }
        } else {
            // if fetching failed but has network
            if (NetworkUtils.isNetworkAvailable()) {
                // set/reset retry fetch alarm
                setFetchAlarm(ScheduleFetchEvent.TYPE.RETRY);
            }
        }
        updateFetchingSentenceStatusAndNotify(false, true, fetchResult);
    }

    public NetworkResponse getTodaysSentenceResponse() {
        StringRequest request = new StringRequest(Request.Method.GET, URL_ICIBA, null, null);
        request.setRetryPolicy(DEVolley.getInstance().getDefaultRetryPolicy(2));
        request.setTag("fetchTodaysSentence");
        DEVolley.getInstance().getRequestTickle().add(request);
        // sync get
        NetworkResponse response = DEVolley.getInstance().getRequestTickle().start();
        return response;
    }

    @Override
    public void updateSentence(Sentence sentence) {
        if (sentence == null) {
            return;
        }
        DaoManager.getInstance().getDaoSession().update(sentence);
    }

    @Override
    public void saveSentence(Sentence sentence) {
        if (sentence == null) {
            return;
        }
        DaoManager.getInstance().getDaoSession().insertOrReplace(sentence);
    }

    @Override
    public void deleteSentence(long sentenceId) {
        DaoManager.getInstance().getSentenceEntityDao().deleteByKey(sentenceId);
    }

    @Override
    public void deleteAllSentences() {
        DaoManager.getInstance().getSentenceEntityDao().deleteAll();
    }

    private void updateFetchingSentenceStatusAndNotify(boolean isFetching, boolean notify, FetchingEvent.RESULT result) {
        synchronized (mFetchingLock) {
            mIsFetchingSentence = isFetching;
        }
        if (notify) {
            FetchingEvent event = new FetchingEvent(isFetching, result);
            LogUtils.d(Constants.TAG, "postSticky:  FetchingEvent:" + event);
            getEventBus().postSticky(event);
        }
    }

    private void setFetchAlarm(ScheduleFetchEvent.TYPE type) {
        LogUtils.d(Constants.TAG, "setFetchAlarm: " + type);
        ScheduleFetchEvent scheduleFetchEvent = new ScheduleFetchEvent(SystemClock.elapsedRealtime(), type);
        getEventBus().post(scheduleFetchEvent);
    }

    @VisibleForTesting
    public EventBus getEventBus() {
        return EventBus.getDefault();
    }

    @VisibleForTesting
    public boolean isFetchingSentence() {
        synchronized (mFetchingLock) {
            return mIsFetchingSentence;
        }
    }

    @VisibleForTesting
    public boolean isFetchingAudio() {
        synchronized (mFetchingLock) {
            return mIsFetchingAudio;
        }
    }

    @VisibleForTesting
    void removeDownloadedAudio(String audioUrl) {
        Cache cache = DEVolley.getInstance().getCache();
        if (cache != null) {
            // ignore expired setting
            cache.remove(audioUrl);
        }
    }
}
