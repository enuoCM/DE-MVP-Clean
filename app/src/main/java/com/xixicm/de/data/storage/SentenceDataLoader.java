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
package com.xixicm.de.data.storage;

import android.content.Context;

import com.xixicm.de.data.base.loader.DataAsyncLoader;
import com.xixicm.de.domain.model.Sentence;

import java.util.List;

/**
 * @author mc
 */
public class SentenceDataLoader extends DataAsyncLoader<List<? extends Sentence>> {
    private boolean mIsFavorite;

    public SentenceDataLoader(Context context, boolean isFavorite) {
        super(context);
        mIsFavorite = isFavorite;
    }

    @Override
    public List<? extends Sentence> loadInBackground() {
        // This method is called on a background thread and should generate a
        // new set of data to be delivered back to the client.
        if (mIsFavorite) {
            return SentenceDataRepository.getInstance().getSentences(mIsFavorite);
        } else {
            return SentenceDataRepository.getInstance().getAllSentences();
        }
    }
}
