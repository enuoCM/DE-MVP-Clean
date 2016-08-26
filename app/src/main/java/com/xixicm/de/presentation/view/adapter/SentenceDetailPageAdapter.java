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
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.TextView;

import com.android.volley.ui.NetworkImageView;
import com.xixicm.de.R;
import com.xixicm.de.data.net.DEVolley;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.presentation.view.component.CheckableImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author mc
 */
public class SentenceDetailPageAdapter extends PagerAdapter {
    List<? extends Sentence> mSentences;
    Context mContext;
    LayoutInflater mInflater;
    SentencesAdapter.FavoriteClickListener mFavoriteClickListener;

    public SentenceDetailPageAdapter(Context context, List<? extends Sentence> sentences,
                                     SentencesAdapter.FavoriteClickListener favoriteClickListener) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mFavoriteClickListener = favoriteClickListener;
        mSentences = sentences;
        if (sentences == null) {
            mSentences = Collections.emptyList();
        }
    }

    public void setSentences(List<? extends Sentence> sentences) {
        mSentences = sentences;
        if (sentences == null) {
            mSentences = Collections.emptyList();
        }
        notifyDataSetChanged();
    }

    public Sentence getSentenceByPosition(int position) {
        if (position >= 0 && position < mSentences.size()) {
            return mSentences.get(position);
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mInflater.inflate(R.layout.sentence_detail, null);
        final Sentence sentence = mSentences.get(position);
        JSONObject content = sentence.getJsonObjectContent();
        final Checkable checkable = (Checkable) view.findViewById(R.id.content);
        checkable.setChecked(sentence.getIsStar());
        final CheckableImageView favoriteBtn = (CheckableImageView) view.findViewById(R.id.favorite);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkable.toggle();
                if (mFavoriteClickListener != null) {
                    mFavoriteClickListener.onFavoriteClick(sentence, checkable.isChecked());
                }
            }
        });
        ((TextView) view.findViewById(R.id.day)).setText(sentence.getDateline());
        ((TextView) view.findViewById(R.id.en_content)).setText("  " + sentence.getContent());
        ((TextView) view.findViewById(R.id.cn_content)).setText("  " + content.optString("note"));
        loadImg(sentence, (NetworkImageView) view.findViewById(R.id.image));
        container.addView(view);
        return view;
    }

    private void loadImg(Sentence sentence, NetworkImageView imageView) {
        String imgUrl = sentence.getJsonObjectContent().optString("picture2");
        if (TextUtils.isEmpty(imgUrl)) {
            return;
        }
        imageView.setImageUrl(imgUrl, DEVolley.getInstance().getImageLoader());
        imageView.setDefaultImageResId(R.drawable.ic_default);
        imageView.setFadeInImage(true);
    }

    /**
     * Destroy the item from the {@link android.support.v4.view.ViewPager}. In our case this is simply removing the
     * {@link View}.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        NetworkImageView nv = (NetworkImageView) ((View) object).findViewById(R.id.image);
        if (nv != null) {
            nv.setImageUrl(null, DEVolley.getInstance().getImageLoader());
        }
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mSentences.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public int getItemPosition(Object object) {
        int position = mSentences.indexOf(object);
        if (position < 0) {
            return POSITION_NONE;
        }
        return position;
    }
}
