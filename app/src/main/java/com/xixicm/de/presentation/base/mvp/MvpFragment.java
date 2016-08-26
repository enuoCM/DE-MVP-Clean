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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

/**
 * A Fragment that uses an {@link MvpPresenter} to implement a Model-View-Presenter architecture
 *
 * @author mc
 */
public abstract class MvpFragment<M, V extends MvpView, P extends MvpPresenter<V, M>> extends Fragment implements MvpView {
    protected P mPresenter;
    private static String VIEW_MODE_KEY;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VIEW_MODE_KEY = getClass().getCanonicalName() + "$ViewModel";
        onCreatePresenter(savedInstanceState);
    }

    /**
     * Called in {@link #onCreate}
     *
     * @param savedInstanceState
     */
    protected void onCreatePresenter(Bundle savedInstanceState) {
        // if set as retainInstance, mPresenter may be not null
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitializePresenter(savedInstanceState);
    }

    /**
     * Called in {@link #onViewCreated}
     *
     * @param savedInstanceState
     */
    protected void onInitializePresenter(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mPresenter.attachView((V) this, null);
        } else {
            M viewModel;
            if (getRetainInstance() && mPresenter.getViewModel() != null) {
                // if it's retainInstance, no need to restore from the savedInstanceState
                viewModel = mPresenter.getViewModel();
            } else {
                viewModel = (M) savedInstanceState.get(VIEW_MODE_KEY);
            }
            mPresenter.attachView((V) this, viewModel);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Even it's retainInstance, still save to outState.
        // For runtime permission change or kill process case. retainInstance doesn't work.
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
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @NonNull
    protected abstract P createPresenter();
}
