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
package com.xixicm.de.domain.interactor;

import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.repository.PreferenceRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

/**
 * @author mc
 */
public class SetPlayStyleUCTest {
    private SetPlayStyleUC mSetPlayStyleUC;
    @Mock
    private PreferenceRepository mPreferences;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mSetPlayStyleUC = new SetPlayStyleUC(mPreferences);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mSetPlayStyleUC);
    }

    @Test
    public void testSetPlayStyleRepeat() {
        mSetPlayStyleUC.setRequestValue(Constants.PLAY_REPEAT);
        mSetPlayStyleUC.run();
        // SetPlayStyle is called
        verify(mPreferences).setPlayStyle(Constants.PLAY_REPEAT);
    }

    @Test
    public void testSetPlayStyleOnce() {
        mSetPlayStyleUC.setRequestValue(Constants.PLAY_ONCE);
        mSetPlayStyleUC.run();
        // SetPlayStyle is called
        verify(mPreferences).setPlayStyle(Constants.PLAY_ONCE);
    }
}
