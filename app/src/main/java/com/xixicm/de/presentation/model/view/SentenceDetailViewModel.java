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
package com.xixicm.de.presentation.model.view;

import com.xixicm.de.domain.Constants;

/**
 * @author mc
 */
public class SentenceDetailViewModel {
    private boolean mIsFavoriteList;
    private long mCurrentSentenseId = -1L;
    private String mCurrentAudioUrl = null;
    private long mPlayToken;
    private int mPlayStyle = Constants.PLAY_UNKNOWN;
    private AUDIO_STATUS mAudioStatus = AUDIO_STATUS.NORMAL;

    public enum AUDIO_STATUS {
        NORMAL, DOWNLOADING, DOWNLOADING_FAIL, PLAYING
    }

    public SentenceDetailViewModel() {
    }

    public boolean isFavoriteList() {
        return mIsFavoriteList;
    }

    public void setIsFavoriteList(boolean isFavoriteList) {
        mIsFavoriteList = isFavoriteList;
    }

    public long getCurrentSentenseId() {
        return mCurrentSentenseId;
    }

    public void setCurrentSentenseId(long currentSentenseId) {
        mCurrentSentenseId = currentSentenseId;
    }

    public String getCurrentAudioUrl() {
        return mCurrentAudioUrl;
    }

    public void setCurrentAudioUrl(String currentAudioUrl) {
        mCurrentAudioUrl = currentAudioUrl;
    }

    public long getPlayToken() {
        return mPlayToken;
    }

    public void setPlayToken(long playToken) {
        mPlayToken = playToken;
    }

    public AUDIO_STATUS getAudioStatus() {
        return mAudioStatus;
    }

    public void setAudioStatus(AUDIO_STATUS audioStatus) {
        mAudioStatus = audioStatus;
    }

    public int getPlayStyle() {
        return mPlayStyle;
    }

    public void setPlayStyle(int playStyle) {
        mPlayStyle = playStyle;
    }
}
