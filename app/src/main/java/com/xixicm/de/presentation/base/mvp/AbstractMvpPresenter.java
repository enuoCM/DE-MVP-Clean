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
 * Base class that implements the MvpPresenter interface and provides a base implementation
 * for attachView() and detachView().
 *
 * @author mc
 */
public abstract class AbstractMvpPresenter<V extends MvpView, M> implements MvpPresenter<V, M> {
    protected V mView;
    private M mViewModel;

    @Override
    public void attachView(V mvpView, M viewMode) {
        mView = mvpView;
        if (viewMode == null) {
            mViewModel = createViewMode();
        } else {
            mViewModel = viewMode;
        }
    }

    /**
     * Create a new view mode if need.
     *
     * @return
     */
    @Nullable
    protected M createViewMode() {
        return null;
    }

    /**
     * Get attached view mode.
     *
     * @return
     */
    @Nullable
    @Override
    public M getViewModel() {
        return mViewModel;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    /**
     * Default do nothing. sub class can override this method and clean all unused objects.
     */
    @Override
    public void onDestroy() {
    }
}
