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
package com.xixicm.de;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.RenamingDelegatingContext;

import com.xixicm.de.data.storage.dao.DaoManager;

/**
 * @author mc
 */
public class TestUtil {
    public static final String TEST_PREFIX = "test_";
    public static final String TEST_DB = TEST_PREFIX + DaoManager.DB_NAME;
    public static final String TEST_PREFERENCES = TEST_PREFIX + "preferences";
    public static final String FAKE_AUDIO_URL = "http:/x.x.x/2016-08-12-day.mp3";
    public static final String SAMPLE_AUDIO_URL = "http:\\/\\/news.iciba.com\\/admin\\/tts\\/2016-08-12-day.mp3";

    public static Context getRenamingDelegatingContext() {
        return new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(), TEST_PREFIX);
    }
}
