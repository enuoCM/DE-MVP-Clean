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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xixicm.de.R;
import com.xixicm.de.data.storage.SentenceDataRepository;
import com.xixicm.de.domain.base.handler.DefaultUseCaseHandler;
import com.xixicm.de.domain.interactor.LoadSentencesUC;
import com.xixicm.de.domain.interactor.UpdateFavoriteSentenceUC;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.infrastructure.loader.AsyncTaskSentenceLoadExecutor;
import com.xixicm.de.presentation.base.handler.UseCaseAsyncUIHandler;
import com.xixicm.de.presentation.base.mvp.MvpFragment;
import com.xixicm.de.presentation.contract.SentenceList;
import com.xixicm.de.presentation.model.view.SentenceListViewModel;
import com.xixicm.de.presentation.presenter.SentenceListPresenter;
import com.xixicm.de.presentation.view.activity.MainActivity;
import com.xixicm.de.presentation.view.adapter.SentencesAdapter;
import com.xixicm.de.presentation.view.component.DividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author mc
 */
public class SentenceListFragment extends
        MvpFragment<SentenceListViewModel, SentenceList.View, SentenceList.Presenter<SentenceList.View, SentenceListViewModel>>
        implements SentenceList.View, SentencesAdapter.OnItemClickListener, SentencesAdapter.FavoriteClickListener {
    private static final String TYPE = "type";

    @BindView(R.id.sentences)
    RecyclerView mSentencesRecyclerView;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    @BindView(R.id.no_sentences)
    TextView mEmptyTextView;
    SentencesAdapter mSentencesAdapter;

    public SentenceListFragment() {
        // Required empty public constructor
    }

    public static SentenceListFragment newInstance(boolean isFavorite) {
        SentenceListFragment fragment = new SentenceListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(TYPE, isFavorite);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    protected SentenceList.Presenter<SentenceList.View, SentenceListViewModel> createPresenter() {
        SentenceListPresenter presenter = new SentenceListPresenter();
        LoadSentencesUC loadSentencesUC = new LoadSentencesUC(new AsyncTaskSentenceLoadExecutor(getActivity(), getLoaderManager()));
        // because we use async task loader, we can use sync handler here
        presenter.setLoadSentencesUCAndHandler(loadSentencesUC, DefaultUseCaseHandler.createSyncUCHandler());
        UpdateFavoriteSentenceUC updateFavoriteSentenceUC = new UpdateFavoriteSentenceUC(SentenceDataRepository.getInstance());
        presenter.setUpdateFavoriteSentenceUCAndHandler(updateFavoriteSentenceUC, UseCaseAsyncUIHandler.getInstance());
        return presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.onCreate();
    }

    @Override
    protected void onInitializePresenter(@Nullable Bundle savedInstanceState) {
        super.onInitializePresenter(savedInstanceState);
        if (savedInstanceState == null) {
            mPresenter.getViewModel().setIsFavoriteList(getArguments().getBoolean(TYPE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sentence_list, null);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        mSentencesAdapter = new SentencesAdapter(getActivity(), null, this, this);
        mSentencesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSentencesRecyclerView.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mSentencesRecyclerView.setAdapter(mSentencesAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.onActivityCreated();
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        mProgressBar.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showNoSentences(boolean favorite) {
        mProgressBar.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.VISIBLE);
        mEmptyTextView.setText(favorite ? R.string.no_favorite : R.string.no_sentence);
    }

    @Override
    public void showTitle(boolean favorite) {
        getActivity().setTitle(favorite ? R.string.title_favorite : R.string.app_s_name);
    }

    @Override
    public void showSentenceList(List<? extends Sentence> sentences) {
        mProgressBar.setVisibility(View.GONE);
        mEmptyTextView.setVisibility(View.GONE);
        mSentencesAdapter.setSentences(sentences);
    }

    @Override
    public void showSentence(long sentenceId) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack(MainActivity.BACK_STACK_NAME)
                .setCustomAnimations(R.anim.slide_left, R.anim.hold)
                .replace(R.id.content_main, SentenceDetailFragment.newInstance(mPresenter.getViewModel().isFavoriteList(), sentenceId), MainActivity.MAIN_TAG).commit();
    }

    @Override
    public void setSelection(final int position) {
        mSentencesRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                int firstVisiblePosition = ((LinearLayoutManager) mSentencesRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                int lastVisiblePosition = ((LinearLayoutManager) mSentencesRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                if (position < firstVisiblePosition || position > lastVisiblePosition) {
                    mSentencesRecyclerView.scrollToPosition(position);
                }
            }
        });
    }

    @Override
    public void setFocusedSentenceId(long sentenceId) {
        mSentencesAdapter.setFocusedSentenceId(sentenceId);
    }

    @Override
    public boolean isFavoriteList() {
        return mPresenter.getViewModel().isFavoriteList();
    }

    @Override
    public void onFavoriteClick(Sentence sentence, boolean favorite) {
        mPresenter.setFavorite(sentence, favorite);
    }

    @Override
    public void onSentenceItemClicked(Sentence sentence) {
        mPresenter.openSentence(sentence.getId());
    }
}
