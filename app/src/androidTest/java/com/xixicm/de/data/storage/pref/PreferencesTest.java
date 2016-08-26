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
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.xixicm.de.TestUtil;
import com.xixicm.de.domain.Constants;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author mc
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class PreferencesTest {
    private Preferences mPreferences;

    @Before
    public void setUp() {
        mPreferences = Preferences.getInstance();
        mPreferences.setSharedPreferences(InstrumentationRegistry.getTargetContext().getSharedPreferences(TestUtil.TEST_PREFERENCES, Context.MODE_PRIVATE));
    }

    @After
    public void cleanUp() {
        // clean  preference
        SharedPreferences.Editor editor = mPreferences.getDefaultSharedPreferences().edit();
        editor.clear();
        editor.commit();
    }

    @AfterClass
    public static void tearDownClass() {
        // delete test db
        File testPreference = new File(InstrumentationRegistry.getTargetContext().getFilesDir().getParent() + "/shared_prefs/" + TestUtil.TEST_PREFERENCES + ".xml");
        assertTrue(testPreference.exists());
        testPreference.delete();
        assertFalse(testPreference.exists());
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mPreferences);
        assertNotNull(mPreferences.getDefaultSharedPreferences());
    }

    @Rule
    public final ExpectedException mExpectedException = ExpectedException.none();

    @Test
    public void testSet_GetPlayStyle() {
        assertTrue(mPreferences.getPlayStyle(Constants.PLAY_UNKNOWN) == Constants.PLAY_UNKNOWN);
        mPreferences.setPlayStyle(Constants.PLAY_REPEAT);
        assertTrue(mPreferences.getPlayStyle(Constants.PLAY_UNKNOWN) == Constants.PLAY_REPEAT);
        mPreferences.setPlayStyle(Constants.PLAY_ONCE);
        assertTrue(mPreferences.getPlayStyle(Constants.PLAY_UNKNOWN) == Constants.PLAY_ONCE);

        mExpectedException.expect(IllegalArgumentException.class);
        mPreferences.setPlayStyle(Constants.PLAY_UNKNOWN);
    }
}
