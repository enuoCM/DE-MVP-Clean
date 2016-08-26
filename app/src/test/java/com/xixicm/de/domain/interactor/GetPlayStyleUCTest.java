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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author mc
 */
public class GetPlayStyleUCTest {
    private GetPlayStyleUC mGetPlayStyleUC;
    @Mock
    private PreferenceRepository mPreferences;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mGetPlayStyleUC = new GetPlayStyleUC(mPreferences);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mGetPlayStyleUC);
    }

    @Test
    public void testGetPlayStyleRepeat() {
        mGetPlayStyleUC.setUseCaseCallback(new GetPlayStyleUC.GetPlayStyleCallback() {
            @Override
            public void onPlayStyleGet(int playStyle) {
                assertEquals(playStyle, Constants.PLAY_REPEAT);
            }
        });
        when(mPreferences.getPlayStyle(anyInt())).thenReturn(Constants.PLAY_REPEAT);
        mGetPlayStyleUC.run();
        // getPlayStyle is called
        verify(mPreferences).getPlayStyle(anyInt());
    }

    @Test
    public void testGetPlayStyleOnce() {
        mGetPlayStyleUC.setUseCaseCallback(new GetPlayStyleUC.GetPlayStyleCallback() {
            @Override
            public void onPlayStyleGet(int playStyle) {
                assertEquals(playStyle, Constants.PLAY_ONCE);
            }
        });
        when(mPreferences.getPlayStyle(anyInt())).thenReturn(Constants.PLAY_ONCE);
        mGetPlayStyleUC.run();
        // getPlayStyle is called
        verify(mPreferences).getPlayStyle(anyInt());
    }
}
