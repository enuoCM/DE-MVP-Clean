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
package com.xixicm.de.domain.repository;

import android.net.Uri;

import com.xixicm.de.domain.base.Repository;
import com.xixicm.de.domain.model.Sentence;

import java.io.File;
import java.util.List;

/**
 * @author mc
 */
public interface SentenceRepository extends Repository {
    Sentence getLatestSentence();

    Sentence getSentence(long sentenceId);

    List<? extends Sentence> getSentences(boolean favorite);

    List<? extends Sentence> getAllSentences();

    void fetchSentenceAudio(final String audioUrl, final long playToken);

    Uri getAudioFile(String audioUrl);

    /**
     * Note: this will be not a async operation.
     *
     * @param manual
     */
    void fetchTodaysSentence(final boolean manual);

    void updateSentence(Sentence sentence);

    void saveSentence(Sentence sentence);

    void deleteSentence(long sentenceId);

    void deleteAllSentences();
}
