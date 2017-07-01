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
package com.xixicm.de.presentation.view.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xixicm.ca.domain.handler.DefaultUseCaseHandler;
import com.xixicm.ca.presentation.handler.AndroidHandlers;
import com.xixicm.ca.presentation.mvp.MvpFragment;
import com.xixicm.de.R;
import com.xixicm.de.data.storage.SentenceDataRepository;
import com.xixicm.de.data.storage.pref.Preferences;
import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.interactor.FetchSentenceAudioUC;
import com.xixicm.de.domain.interactor.GetPlayStyleUC;
import com.xixicm.de.domain.interactor.LoadSentencesUC;
import com.xixicm.de.domain.interactor.SetPlayStyleUC;
import com.xixicm.de.domain.interactor.UpdateFavoriteSentenceUC;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.infrastructure.loader.AsyncTaskSentenceLoadExecutor;
import com.xixicm.de.infrastructure.media.AudioPlayer;
import com.xixicm.de.presentation.contract.SentenceDetail;
import com.xixicm.de.presentation.model.view.SentenceDetailViewModel;
import com.xixicm.de.presentation.presenter.SentenceDetailPresenter;
import com.xixicm.de.presentation.view.adapter.SentenceDetailPageAdapter;
import com.xixicm.de.presentation.view.adapter.SentencesAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author mc.
 */
public class SentenceDetailFragment extends MvpFragment<SentenceDetailViewModel, SentenceDetail.View, SentenceDetail.Presenter<SentenceDetail.View, SentenceDetailViewModel>>
        implements SentenceDetail.View, SentencesAdapter.FavoriteClickListener {
    private static final String TYPE = "type";
    private static final String ID = "id";

    @BindView(R.id.fab_download_play)
    FloatingActionButton mAudioFab;
    private ObjectAnimator mAudioFabAnimator;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    MenuItem mMenuItemPlayOnce;
    MenuItem mMenuItemPlayRepeat;
    SentenceDetailPageAdapter mAdapter;


    public SentenceDetailFragment() {
        // Required empty public constructor
    }

    public static SentenceDetailFragment newInstance(boolean isFavorite, Long id) {
        SentenceDetailFragment fragment = new SentenceDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(TYPE, isFavorite);
        bundle.putLong(ID, id == null ? -1L : id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    protected SentenceDetail.Presenter<SentenceDetail.View, SentenceDetailViewModel> createPresenter() {
        SentenceDetailPresenter presenter = new SentenceDetailPresenter(SentenceDataRepository.getInstance(), new AudioPlayer(getActivity()));
        LoadSentencesUC loadSentencesUC = new LoadSentencesUC(new AsyncTaskSentenceLoadExecutor(getActivity(), getLoaderManager()));
        // because we use async task loader, we can use sync handler here
        presenter.setLoadSentencesUCAndHandler(loadSentencesUC, DefaultUseCaseHandler.createSyncUCHandler());
        UpdateFavoriteSentenceUC updateFavoriteSentenceUC = new UpdateFavoriteSentenceUC(SentenceDataRepository.getInstance());
        presenter.setUpdateFavoriteSentenceUCAndHandler(updateFavoriteSentenceUC, AndroidHandlers.asyncParallelReqSyncRes());
        SetPlayStyleUC setPlayStyleUC = new SetPlayStyleUC(Preferences.getInstance());
        presenter.setSetPlayStyleUCAndHandler(setPlayStyleUC, DefaultUseCaseHandler.createSerialUCHandler());
        GetPlayStyleUC getPlayStyleUCForPrepareMenu = new GetPlayStyleUC(Preferences.getInstance());
        GetPlayStyleUC getPlayStyleUCForPlayAudio = new GetPlayStyleUC(Preferences.getInstance());
        presenter.setGetPlayStyleUCAndHandler(getPlayStyleUCForPrepareMenu, getPlayStyleUCForPlayAudio, AndroidHandlers.asyncParallelReqSyncRes());
        FetchSentenceAudioUC fetchSentenceAudioUC = new FetchSentenceAudioUC(SentenceDataRepository.getInstance());
        presenter.setFetchSentenceAudioUCAndHandler(fetchSentenceAudioUC, AndroidHandlers.asyncParallelReqSyncRes());
        return presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPresenter.onCreate();
    }

    @Override
    protected void onInitializePresenter(@Nullable Bundle savedInstanceState) {
        super.onInitializePresenter(savedInstanceState);
        if (savedInstanceState == null) {
            mPresenter.getViewModel().setIsFavoriteList(getArguments().getBoolean(TYPE));
            mPresenter.getViewModel().setCurrentSentenseId(getArguments().getLong(ID));
        }
        mPresenter.getViewModel().setPlayToken(System.currentTimeMillis());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sentence_detail, null);
        ButterKnife.bind(this, view);
        //reset
        mAdapter = null;
        mAudioFabAnimator = null;
        mAudioFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    mPresenter.onAudioFabButtonClicked();
                }
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPresenter.onDisplaySentence(mAdapter.getSentenceByPosition(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.onViewCreated();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_de);
        setHasOptionsMenu(true);
        mPresenter.loadSentences(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mMenuItemPlayOnce = menu.findItem(R.id.once);
        mMenuItemPlayRepeat = menu.findItem(R.id.repeat);
        mPresenter.onPrepareOptionsMenu();
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.once) {
            item.setChecked(true);
            mPresenter.setPlayStyle(Constants.PLAY_ONCE);
            return true;
        }
        if (id == R.id.repeat) {
            item.setChecked(true);
            mPresenter.setPlayStyle(Constants.PLAY_REPEAT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFavoriteClick(Sentence sentence, boolean favorite) {
        mPresenter.setFavorite(sentence, favorite);
    }

    @Override
    public void showFetchingResult(int stringResource) {
        Toast.makeText(getActivity(), stringResource, Toast.LENGTH_SHORT).show();
    }


    private void initAnimatiorIfNeed() {
        if (mAudioFabAnimator == null) {
            mAudioFabAnimator = ObjectAnimator.ofInt(mAudioFab, "imageLevel", 0, 10000);
            mAudioFabAnimator.setDuration(1000);
            mAudioFabAnimator.setRepeatCount(ValueAnimator.INFINITE);
        }
    }

    private Drawable getDrawable(int drawableId) {
        if (getActivity() != null) {
            return getActivity().getResources().getDrawable(drawableId);
        }
        return null;
    }

    @Override
    public void updateAudioFabAnimator(SentenceDetailViewModel.AUDIO_STATUS status) {
        initAnimatiorIfNeed();
        switch (status) {
            case NORMAL:
                mAudioFab.setImageDrawable(getDrawable(R.drawable.ic_btn_play));
                mAudioFabAnimator.end();
                break;
            case DOWNLOADING:
                mAudioFab.setImageDrawable(getDrawable(R.drawable.ic_btn_downloading));
                if (!mAudioFabAnimator.isStarted()) {
                    mAudioFabAnimator.start();
                }
                break;
            case DOWNLOADING_FAIL:
                mAudioFab.setImageDrawable(getDrawable(R.drawable.ic_btn_downloading));
                mAudioFabAnimator.end();
                break;
            case PLAYING:
                mAudioFabAnimator.end();
                mAudioFab.setImageDrawable(getDrawable(R.drawable.ic_btn_playing));
                mAudioFabAnimator.start();
                break;
        }
    }

    @Override
    public void showSentenceList(List<? extends Sentence> sentences) {
        if (mAdapter == null) {
            mAdapter = new SentenceDetailPageAdapter(getActivity(), sentences, this);
            mViewPager.setAdapter(mAdapter);
        } else {
            mAdapter.setSentences(sentences);
        }
    }

    @Override
    public void navigateToSentence(int position) {
        if (mAdapter != null && position > 0) {
            if (mViewPager.getCurrentItem() != position) {
                mViewPager.setCurrentItem(position);
            }
        }
    }

    @Override
    public void checkPlayOnceMenuItem() {
        if (mMenuItemPlayOnce != null) {
            mMenuItemPlayOnce.setChecked(true);
        }
    }

    @Override
    public void checkPlayRepeatMenuItem() {
        if (mMenuItemPlayRepeat != null) {
            mMenuItemPlayRepeat.setChecked(true);
        }
    }
}
