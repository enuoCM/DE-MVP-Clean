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
package com.xixicm.de.domain.base.util;

/**
 * @author mc
 */
public class NetworkUtils {
    private static NetworkChecker sNetworkChecker;

    public static void injectNetworkChecker(NetworkChecker networkChecker) {
        sNetworkChecker = networkChecker;
    }

    public static boolean isNetworkAvailable() {
        if (sNetworkChecker == null) {
            new IllegalArgumentException("sNetworkChecker is not injected!!!");
        }
        return sNetworkChecker.isNetworkAvailable();
    }
}
