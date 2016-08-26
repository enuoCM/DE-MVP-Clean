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

import java.io.Serializable;

/**
 * @author mc
 */
public class SentenceListViewModel implements Serializable {
    private boolean mIsFavoriteList;
    private long mNeedFocusedSentenceId = -1L;
    private boolean mNeedAutoFocused = true;

    public SentenceListViewModel() {
    }

    public boolean isFavoriteList() {
        return mIsFavoriteList;
    }

    public void setIsFavoriteList(boolean favorite) {
        mIsFavoriteList = favorite;
    }

    public long getNeedFocusedSentenceId() {
        return mNeedFocusedSentenceId;
    }

    public void setNeedFocusedSentenceId(long needFocusedSentenceId) {
        mNeedFocusedSentenceId = needFocusedSentenceId;
    }

    public boolean isNeedAutoFocused() {
        return mNeedAutoFocused;
    }

    public void setNeedAutoFocused(boolean needAutoFocused) {
        mNeedAutoFocused = needAutoFocused;
    }
}
