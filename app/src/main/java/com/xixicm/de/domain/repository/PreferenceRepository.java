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
package com.xixicm.de.domain.repository;

import android.support.annotation.IntDef;

import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.base.Repository;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author mc
 */
public interface PreferenceRepository extends Repository {
    @IntDef({Constants.PLAY_REPEAT, Constants.PLAY_ONCE})
    @Retention(RetentionPolicy.SOURCE)
    @interface PlayStyle {
    }

    int getPlayStyle(int defaultStyle);

    void setPlayStyle(@PlayStyle int style);
}
