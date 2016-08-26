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
package com.xixicm.de.domain.model.event;

/**
 * @author mc
 */
public class ScheduleFetchEvent {
    public enum TYPE {NORMAL, RETRY}

    private TYPE mType;
    private long mStartTime;

    public ScheduleFetchEvent(long startTime, TYPE type) {
        mStartTime = startTime;
        mType = type;
    }

    public TYPE getType() {
        return mType;
    }

    public long getStartTime() {
        return mStartTime;
    }

    @Override
    public String toString() {
        return "ScheduleFetchEvent{" +
                "mType=" + mType +
                ", mStartTime=" + mStartTime +
                '}';
    }
}
