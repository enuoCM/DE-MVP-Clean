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
package com.xixicm.de.domain.base.handler;

import com.xixicm.de.domain.base.scheduler.UseCaseAsyncRequestScheduler;
import com.xixicm.de.domain.base.scheduler.UseCaseRequestScheduler;
import com.xixicm.de.domain.base.scheduler.UseCaseResponseScheduler;
import com.xixicm.de.domain.base.scheduler.UseCaseSyncRequestScheduler;
import com.xixicm.de.domain.base.scheduler.UseCaseSyncResponseScheduler;
import com.xixicm.de.domain.base.usecase.UseCase;

/**
 * @author mc
 */
public class DefaultUseCaseHandler implements UseCaseHandler {
    protected UseCaseRequestScheduler mUseCaseRequestScheduler;
    protected UseCaseResponseScheduler mUseCaseResponseScheduler;

    /**
     * Runs the useCase using the useCaseRequestScheduler.
     * Posts <Response, Error> using the useCaseResponseScheduler.
     *
     * @param useCaseRequestScheduler  if null, default to use {@link UseCaseAsyncRequestScheduler#getParallelInstance()}
     * @param useCaseResponseScheduler if null, default to use a {@link UseCaseSyncResponseScheduler}
     */
    public DefaultUseCaseHandler(UseCaseRequestScheduler useCaseRequestScheduler,
                                 UseCaseResponseScheduler useCaseResponseScheduler) {
        if (useCaseRequestScheduler == null) {
            useCaseRequestScheduler = UseCaseAsyncRequestScheduler.getParallelInstance();
        }
        mUseCaseRequestScheduler = useCaseRequestScheduler;
        if (useCaseResponseScheduler == null) {
            useCaseResponseScheduler = new UseCaseSyncResponseScheduler();
        }
        mUseCaseResponseScheduler = useCaseResponseScheduler;
    }

    private DefaultUseCaseHandler() {
        this(null, null);
    }

    /**
     * @return a new DefaultUseCaseHandler with {@link UseCaseAsyncRequestScheduler#getParallelInstance()}
     * and {@link UseCaseSyncResponseScheduler}
     */
    public static DefaultUseCaseHandler createParallelUCHandler() {
        return new DefaultUseCaseHandler();
    }

    /**
     * @return a new DefaultUseCaseHandler with {@link UseCaseAsyncRequestScheduler#getSerialInstance()}
     * and {@link UseCaseSyncResponseScheduler}
     */
    public static DefaultUseCaseHandler createSerialUCHandler() {
        return new DefaultUseCaseHandler(UseCaseAsyncRequestScheduler.getSerialInstance(), null);
    }

    /**
     * @return a new DefaultUseCaseHandler with {@link UseCaseSyncRequestScheduler}
     * and {@link UseCaseSyncResponseScheduler}
     */
    public static DefaultUseCaseHandler createSyncUCHandler() {
        return new DefaultUseCaseHandler(new UseCaseSyncRequestScheduler(), null);
    }

    @Override
    public <Request, Response, Error> void execute(UseCase<Request, Response, Error> useCase) {
        useCase.setUseCaseCallback(new UseCaseCallbackWrapper<Request, Response, Error>(useCase, this));
        mUseCaseRequestScheduler.execute(useCase);
    }

    @Override
    public <Response, Error> void notifyResponse(final Response response, final UseCase.UseCaseCallback<Response, Error> useCaseCallback) {
        if (useCaseCallback != null) {
            mUseCaseResponseScheduler.post(new Runnable() {
                @Override
                public void run() {
                    useCaseCallback.onSuccess(response);
                }
            });
        }
    }

    @Override
    public <Response, Error> void notifyError(final Error error, final UseCase.UseCaseCallback<Response, Error> useCaseCallback) {
        if (useCaseCallback != null) {
            mUseCaseResponseScheduler.post(new Runnable() {
                @Override
                public void run() {
                    useCaseCallback.onError(error);
                }
            });
        }
    }
}
