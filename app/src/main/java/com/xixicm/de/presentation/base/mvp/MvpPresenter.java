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
package com.xixicm.de.presentation.base.mvp;

import android.support.annotation.Nullable;

/**
 * Every presenter in the app must either implement this interface or extend
 * AbstractMVPPresenter indicating the MvpView type that wants to be attached with.
 *
 * @author mc
 */
public interface MvpPresenter<V extends MvpView, M> {
    /**
     * Attach to current view and current view model.
     *
     * @param mvpView
     * @param viewModel If the presenter is being re-created from a previous saved state, this is the state.
     *                  Usually this is restored from savedInstanceState, so it should be Serializable or Parcelable.
     *                  Otherwise can not use savedInstanceState to restore it. Instead if it's Activity, can use NonConfigurationInstance.
     *                  If it's fragment, can use retainInstance.
     */
    void attachView(V mvpView, M viewModel);

    @Nullable
    M getViewModel();

    void detachView();

    void onDestroy();
}
