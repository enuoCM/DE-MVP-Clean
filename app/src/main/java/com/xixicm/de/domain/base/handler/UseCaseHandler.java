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

import com.xixicm.de.domain.base.scheduler.UseCaseRequestScheduler;
import com.xixicm.de.domain.base.scheduler.UseCaseResponseScheduler;
import com.xixicm.de.domain.base.usecase.UseCase;

/**
 * Runs {@link UseCase}s using a {@link UseCaseRequestScheduler}.
 * Posts <Response, Error> using a {@link UseCaseResponseScheduler}.
 *
 * @author mc
 */
public interface UseCaseHandler {
    <Request, Response, Error> void execute(final UseCase<Request, Response, Error> useCase);

    <Response, Error> void notifyResponse(final Response response, final UseCase.UseCaseCallback<Response, Error> useCaseCallback);

    <Response, Error> void notifyError(final Error error, final UseCase.UseCaseCallback<Response, Error> useCaseCallback);

    /**
     * A wrapper callback class to delivery <Response, Error> to {@link UseCaseResponseScheduler}.
     * Usually after create such UseCaseCallbackWrapper, should set it as the call back of UseCase.
     *
     * @param <Response>
     * @param <Error>
     * @see {@link UseCase#setUseCaseCallback(UseCase.UseCaseCallback)}
     */
    final class UseCaseCallbackWrapper<Request, Response, Error> implements
            UseCase.UseCaseCallback<Response, Error> {
        private final UseCase<Request, Response, Error> mUseCase;
        // original call back
        private final UseCase.UseCaseCallback<Response, Error> mUseCaseCallback;
        private final UseCaseHandler mUseCaseHandler;

        public UseCaseCallbackWrapper(UseCase<Request, Response, Error> useCase,
                                      UseCaseHandler useCaseHandler) {
            mUseCase = useCase;
            mUseCaseCallback = useCase.getUseCaseCallback();
            mUseCaseHandler = useCaseHandler;
        }

        /**
         * Only delivery the Response to {@link UseCaseResponseScheduler},
         * if current wrapper call back is still attached to original use case.
         *
         * @param response
         */
        @Override
        public void onSuccess(Response response) {
            // for case the mUseCase is re-executed, abort previous callback
            if (mUseCase.getUseCaseCallback() == this) {
                mUseCaseHandler.notifyResponse(response, mUseCaseCallback);
            }
        }

        /**
         * Only delivery the Error to {@link UseCaseResponseScheduler},
         * if current wrapper call back is still attached to original use case.
         *
         * @param error
         */
        @Override
        public void onError(Error error) {
            // for case the mUseCase is re-executed, abort previous callback
            if (mUseCase.getUseCaseCallback() == this) {
                mUseCaseHandler.notifyError(error, mUseCaseCallback);
            }
        }
    }
}
