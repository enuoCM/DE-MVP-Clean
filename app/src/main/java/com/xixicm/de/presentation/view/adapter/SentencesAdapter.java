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
package com.xixicm.de.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import com.xixicm.de.R;
import com.xixicm.de.domain.model.Sentence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author mc
 */
public class SentencesAdapter extends RecyclerView.Adapter<SentencesAdapter.ViewHolder> {
    List<? extends Sentence> mSentences;
    Context mContext;
    LayoutInflater mInflater;
    private long mFocusedSentenceId;
    FavoriteClickListener mFavoriteClickListener;
    OnItemClickListener mOnItemClickListener;

    public SentencesAdapter(Context context, List<? extends Sentence> sentences,
                            FavoriteClickListener favoriteClickListener,
                            OnItemClickListener onItemClickListener) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mFavoriteClickListener = favoriteClickListener;
        mOnItemClickListener = onItemClickListener;
        mSentences = sentences;
        if (sentences == null) {
            mSentences = Collections.emptyList();
        }
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mSentences.get(position).getId();
    }

    public void setSentences(List<? extends Sentence> sentences) {
        mSentences = sentences;
        if (sentences == null) {
            mSentences = Collections.emptyList();
        }
        notifyDataSetChanged();
    }

    public void setFocusedSentenceId(long id) {
        mFocusedSentenceId = id;
    }

    public long getFocusedSentenceId() {
        return mFocusedSentenceId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.sentence_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Sentence sentence = mSentences.get(position);
        holder.mCheckable.setChecked(sentence.getIsStar());
        holder.mIndicatorView.setVisibility(mFocusedSentenceId == sentence.getId() ? View.VISIBLE : View.INVISIBLE);
        holder.mContentTextView.setText(sentence.getContent());
        holder.mDayTextView.setText(sentence.getDateline());
        final Checkable checkable = holder.mCheckable;
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkable.toggle();
                if (mFavoriteClickListener != null) {
                    mFavoriteClickListener.onFavoriteClick(sentence, checkable.isChecked());
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onSentenceItemClicked(sentence);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSentences.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkable)
        Checkable mCheckable;
        @BindView(R.id.indicator)
        View mIndicatorView;
        @BindView(R.id.favorite)
        ImageView mImageView;
        @BindView(R.id.content)
        TextView mContentTextView;
        @BindView(R.id.day)
        TextView mDayTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface FavoriteClickListener {
        void onFavoriteClick(Sentence sentence, boolean favorite);
    }

    public interface OnItemClickListener {
        void onSentenceItemClicked(Sentence sentence);
    }
}
