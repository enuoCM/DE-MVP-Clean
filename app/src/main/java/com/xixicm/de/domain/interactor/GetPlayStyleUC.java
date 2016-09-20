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
import com.xixicm.ca.domain.usecase.AbstractUseCase;
import com.xixicm.ca.domain.util.LogUtils;
import com.xixicm.de.domain.repository.PreferenceRepository;

/**
 * A use case to get current play style setting.
 *
 * @author mc
 */
public class GetPlayStyleUC extends AbstractUseCase<Void, Integer, Void> {
    PreferenceRepository mPreferenceRepository;

    public GetPlayStyleUC(PreferenceRepository preferences) {
        mPreferenceRepository = preferences;
    }

    private void getPlayStyle() {
        LogUtils.v(Constants.TAG, "get current play style.");
        UseCaseCallback<Integer, Void> callback = getUseCaseCallback();
        if (callback != null) {
            callback.onSuccess(mPreferenceRepository.getPlayStyle(Constants.PLAY_ONCE));
        }
    }

    @Override
    public void run() {
        getPlayStyle();
    }

    public static abstract class GetPlayStyleCallback implements UseCaseCallback<Integer, Void> {
        public abstract void onPlayStyleGet(int playStyle);

        @Override
        public void onSuccess(Integer integer) {
            onPlayStyleGet(integer);
        }

        @Override
        public void onError(Void aVoid) {
        }
    }
}
