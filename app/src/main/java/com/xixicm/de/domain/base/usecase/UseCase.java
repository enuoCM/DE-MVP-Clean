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
package com.xixicm.de.domain.base.usecase;

/**
 * Use cases are the entry points to the domain layer.
 * Please note: UseCase is not thread safe. When reusing UseCase,
 * should consider changed Request parameter and wrong call back on previous call back.
 * Best practice: 1. Always reset a new call back for a re-used UseCase
 * 2. Or always create new UseCase.
 *
 * @param <Request>  Data passed to a request.
 * @param <Response> Data received from a request.
 * @param <Error>    Data received Error.
 * @author mc
 */
public interface UseCase<Request, Response, Error> extends Runnable {

    void setRequestValue(Request requestValue);

    Request getRequestValue();

    void setUseCaseCallback(UseCaseCallback<Response, Error> useCaseCallback);

    UseCaseCallback<Response, Error> getUseCaseCallback();

    /**
     * Data received Error.
     */
    interface ResponseError {
        Exception getException();

        String getErrorMessage();
    }

    /**
     * @param <Response> success response data
     * @param <Error>    error response data
     */
    interface UseCaseCallback<Response, Error> {
        void onSuccess(Response response);

        void onError(Error error);
    }
}
