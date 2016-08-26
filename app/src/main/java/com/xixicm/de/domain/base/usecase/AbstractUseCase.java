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
 * @author mc
 */
public abstract class AbstractUseCase<Request, Response, Error> implements UseCase<Request, Response, Error> {
    private Request mRequestValue;
    private UseCaseCallback<Response, Error> mUseCaseCallback;

    @Override
    public void setRequestValue(Request requestValue) {
        mRequestValue = requestValue;
    }

    @Override
    public Request getRequestValue() {
        return mRequestValue;
    }

    @Override
    public void setUseCaseCallback(UseCaseCallback<Response, Error> useCaseCallback) {
        mUseCaseCallback = useCaseCallback;
    }

    @Override
    public UseCaseCallback<Response, Error> getUseCaseCallback() {
        return mUseCaseCallback;
    }
}
