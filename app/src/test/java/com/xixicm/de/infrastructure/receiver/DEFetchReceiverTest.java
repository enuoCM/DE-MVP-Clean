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
package com.xixicm.de.infrastructure.receiver;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.xixicm.ca.domain.util.NetworkChecker;
import com.xixicm.ca.domain.util.NetworkUtils;
import com.xixicm.de.domain.interactor.AutoFetchTodaysSentenceUC;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * @author mc
 */
@RunWith(MockitoJUnitRunner.class)
public class DEFetchReceiverTest {
    @Mock
    private DEFetchReceiver mDEFetchReceiver;
    @Mock
    private NetworkChecker mMockNetworkChecker;
    @Mock
    private AutoFetchTodaysSentenceUC mAutoFetchTodaysSentenceUC;
    @Mock
    private Intent mIntent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.doCallRealMethod().when(mDEFetchReceiver).onReceive(any(Context.class), any(Intent.class));
        doReturn(mAutoFetchTodaysSentenceUC).when(mDEFetchReceiver).getAutoFetchTodaysSentenceUC(any(Context.class));
        NetworkUtils.injectNetworkChecker(mMockNetworkChecker);
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mDEFetchReceiver);
    }

    @Test
    public void testOnReceive_CONNECTIVITY_ACTION_AvailableNetwork() {
        doReturn(true).when(mMockNetworkChecker).isNetworkAvailable();
        doReturn(ConnectivityManager.CONNECTIVITY_ACTION).when(mIntent).getAction();
        mDEFetchReceiver.onReceive(null, mIntent);

        // wait to execute
        waitToRun(100);
        verify(mAutoFetchTodaysSentenceUC).run();
    }

    @Test
    public void testOnReceive_CONNECTIVITY_ACTION_NotAvailableNetwork() {
        doReturn(false).when(mMockNetworkChecker).isNetworkAvailable();
        doReturn(ConnectivityManager.CONNECTIVITY_ACTION).when(mIntent).getAction();
        mDEFetchReceiver.onReceive(null, mIntent);

        // wait to execute
        waitToRun(100);
        verify(mAutoFetchTodaysSentenceUC, atMost(0)).run();
    }

    @Test
    public void testOnReceive_OTHER_ACTION_AvailableNetwork() {
        doReturn(true).when(mMockNetworkChecker).isNetworkAvailable();
        mDEFetchReceiver.onReceive(null, mIntent);

        // wait to execute
        waitToRun(100);
        verify(mAutoFetchTodaysSentenceUC).run();
    }

    private void waitToRun(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
