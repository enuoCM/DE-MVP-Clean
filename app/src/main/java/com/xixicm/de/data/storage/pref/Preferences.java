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
package com.xixicm.de.data.storage.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.repository.PreferenceRepository;

/**
 * @author mc
 */
public class Preferences implements PreferenceRepository {
    private static Preferences sInstance;
    private Context mContext;
    private SharedPreferences mPrefs;
    private static final String KEY_PLAY_STYLE = "key_play_style";

    private Preferences(@NonNull Context context) {
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static void init(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new Preferences(context);
        }
    }

    public static Preferences getInstance() {
        return sInstance;
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return mPrefs;
    }

    // for test
    public void setSharedPreferences(SharedPreferences preferences) {
        mPrefs = preferences;
    }

    @Override
    public int getPlayStyle(int defaultStyle) {
        return mPrefs.getInt(KEY_PLAY_STYLE, defaultStyle);
    }

    @Override
    public void setPlayStyle(@PlayStyle int style) {
        if (Constants.PLAY_REPEAT != style && Constants.PLAY_ONCE != style) {
            throw new IllegalArgumentException("Unsupported play style:" + style);
        }
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(KEY_PLAY_STYLE, style);
        editor.apply();
    }
}
