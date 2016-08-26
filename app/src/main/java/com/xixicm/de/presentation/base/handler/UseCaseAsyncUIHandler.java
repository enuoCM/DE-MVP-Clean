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
package com.xixicm.de.presentation.base.handler;

import com.xixicm.de.domain.base.handler.DefaultUseCaseHandler;
import com.xixicm.de.domain.base.scheduler.UseCaseAsyncRequestScheduler;
import com.xixicm.de.presentation.base.scheduler.UseCaseUIResponseScheduler;

/**
 * Runs the useCase Async and Posts <Response, Error> on UI main thread.
 * For the no call back UseCase, still can use this UseCaseHandler.
 *
 * @author mc
 */
public class UseCaseAsyncUIHandler extends DefaultUseCaseHandler {
    private static UseCaseAsyncUIHandler sInstance;

    public synchronized static UseCaseAsyncUIHandler getInstance() {
        if (sInstance == null) {
            sInstance = new UseCaseAsyncUIHandler();
        }
        return sInstance;
    }

    /**
     * Runs the useCase using the {@link UseCaseAsyncRequestScheduler#getParallelInstance()}.
     * Posts <Response, Error> using {@link UseCaseUIResponseScheduler#getInstance()}.
     */
    UseCaseAsyncUIHandler() {
        super(UseCaseAsyncRequestScheduler.getParallelInstance(), UseCaseUIResponseScheduler.getInstance());
    }
}
