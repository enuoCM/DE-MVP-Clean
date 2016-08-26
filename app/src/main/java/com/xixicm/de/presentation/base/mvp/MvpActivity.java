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

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

/**
 * An Activity that uses an {@link MvpPresenter} to implement a Model-View-Presenter architecture.
 *
 * @author mc
 */
public abstract class MvpActivity<M, V extends MvpView, P extends MvpPresenter<V, M>> extends AppCompatActivity implements MvpView {
    protected P mPresenter;
    private static String VIEW_MODE_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VIEW_MODE_KEY = getClass().getCanonicalName() + "$ViewModel";
        onCreatePresenter(savedInstanceState);
        onInitializePresenter(savedInstanceState);
    }

    /**
     * Called in {@link #onCreate} before {@link #onInitializePresenter}
     *
     * @param savedInstanceState
     */
    protected void onCreatePresenter(Bundle savedInstanceState) {
        if (isRetainPresenterByNonConfigurationInstance()) {
            mPresenter = getPresenterFromNonConfigurationInstance();
        }
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
    }

    /**
     * Called in {@link #onCreate} after {@link #onCreatePresenter}
     *
     * @param savedInstanceState
     */
    protected void onInitializePresenter(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mPresenter.attachView((V) this, null);
        } else {
            M viewModel;
            if (isRetainPresenterByNonConfigurationInstance() && mPresenter.getViewModel() != null) {
                // if it's RetainPresenterByNonConfigurationInstance, no need to restore from the savedInstanceState
                viewModel = mPresenter.getViewModel();
            } else {
                viewModel = (M) savedInstanceState.get(VIEW_MODE_KEY);
            }
            mPresenter.attachView((V) this, viewModel);
        }
    }

    /**
     * Whether retain mPresenter by NonConfigurationInstance.
     *
     * @return default false
     */
    protected boolean isRetainPresenterByNonConfigurationInstance() {
        return false;
    }

    /**
     * If isRetainPresenterByNonConfigurationInstance return true, should override this function to get mPresenter.
     *
     * @return default null
     */
    protected P getPresenterFromNonConfigurationInstance() {
        return null;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Even it's isRetainPresenterByNonConfigurationInstance, still save to outState.
        // For runtime permission change or kill process case. RetainNonConfigurationInstance doesn't work.
        M viewModel = mPresenter.getViewModel();
        if (viewModel != null) {
            if (viewModel instanceof Serializable) {
                outState.putSerializable(VIEW_MODE_KEY, (Serializable) viewModel);
            } else if (viewModel instanceof Parcelable) {
                outState.putParcelable(VIEW_MODE_KEY, (Parcelable) viewModel);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        if (!isRetainPresenterByNonConfigurationInstance()) {
            mPresenter.onDestroy();
        } else if (isFinishing()) {
            mPresenter.onDestroy();
        }
    }

    @NonNull
    protected abstract P createPresenter();
}
