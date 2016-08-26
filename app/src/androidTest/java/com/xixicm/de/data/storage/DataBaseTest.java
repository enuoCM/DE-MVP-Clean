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
import android.support.test.InstrumentationRegistry;

import com.xixicm.de.TestUtil;
import com.xixicm.de.data.entity.SentenceEntity;
import com.xixicm.de.data.storage.dao.SentenceEntityDao;
import com.xixicm.de.domain.model.Sentence;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author mc
 */
public abstract class DataBaseTest {
    protected SentenceDataRepository mSentenceDataRepository;
    protected String MOCK_SENTENCE_CONTENT = "{\"sid\":\"2233\",\"tts\":\"http:\\/\\/news.iciba.com\\/admin\\/tts\\/2016-08-12-day.mp3\"," +
            "\"content\":\"If I should meet you.After long years.How should I greet you? With silence and tears.\\u2014When we two parted by George Gorden Byron \"," +
            "\"note\":\"\\u5047\\u4f7f\\u6211\\u53c8\\u89c1\\u4f60\\uff0c\\u9694\\u4e86\\u60a0\\u957f\\u7684\\u5c81\\u6708\\uff0c\\u6211\\u8be5\\u5982\\u4f55\\u81f4\\u610f\\uff1f\\u4ee5\\u6c89\\u9ed8\\uff0c\\u4ee5\\u773c\\u6cea\\u3002\\u2014\\u62dc\\u4f26\\u300a\\u5f53\\u6211\\u4fe9\\u5206\\u522b\\u300b\",\"love\":\"1047\"," +
            "\"translation\":\"\\u8bcd\\u9738\\u5c0f\\u7f16\\uff1a\\u62dc\\u4f26\\u7684\\u8fd9\\u9996\\u8bd7\\u6709\\u4e0d\\u540c\\u7684\\u7ffb\\u8bd1\\u7248\\u672c\\uff0c\\u8fd9\\u9996\\u8bd7\\u7684\\u540d\\u5b57\\u6709\\u5f88\\u591a,\\u300a\\u6625\\u901d\\u300b\\u3001\\u300a\\u6614\\u65e5\\u4f9d\\u4f9d\\u522b\\u300b\\u3001\\u300a\\u5f53\\u6211\\u4fe9\\u5206\\u522b\\u300b\\uff0c\\u5c0f\\u7f16\\u5f88\\u559c\\u6b22\\u300a\\u6625\\u901d\\u300b\\u8fd9\\u4e2a\\u540d\\u5b57\\uff0c\\u5c0f\\u4f19\\u4f34\\u4eec\\u4f60\\u4eec\\u89c9\\u5f97\\u5982\\u4f55\\u5462\\uff1f\\u5c0f\\u7f16\\u4e5f\\u5f88\\u559c\\u6b22\\u9648\\u9521\\u9e9f\\u7ffb\\u8bd1\\u7684\\u4e00\\u7248\\u2014\\u201c\\u591a\\u5e74\\u60dc\\u522b\\u540e\\uff0c\\u6291\\u6216\\u518d\\u76f8\\u9022\\uff0c\\u76f8\\u9022\\u4f55\\u6240\\u8bed\\uff1f\\u6cea\\u6d41\\u9ed8\\u65e0\\u58f0\\u3002\\u201d\\u4ece\\u5c0f\\u65f6\\u5019\\u8d77\\u5c0f\\u7f16\\u5c31\\u89c9\\u5f97\\u4e2d\\u6587\\u535a\\u5927\\u7cbe\\u6df1\\uff0c\\u6709\\u7684\\u65f6\\u5019\\u4e00\\u79cd\\u60c5\\u611f\\u3001\\u4e00\\u5904\\u666f\\u7269\\uff0c\\u7ecf\\u8fc7\\u8bed\\u8a00\\u7684\\u52a0\\u5de5\\u5c31\\u5316\\u8150\\u673d\\u4e3a\\u795e\\u5947\\u3002\\u5bf9\\u6bd4\\u56fd\\u5916\\u7684\\u8bd7\\u6b4c\\u4f60\\u4eec\\u662f\\u5426\\u89c9\\u5f97\\u4e2d\\u56fd\\u7684\\u8bd7\\u6b4c\\u66f4\\u610f\\u4e49\\u6df1\\u8fdc\\uff0c\\u4ee4\\u4eba\\u8d5e\\u53f9\\u5462\\uff1f\\u5f53\\u7136\\u4e0d\\u7ba1\\u662f\\u56fd\\u5916\\u8bd7\\u6b4c\\u8fd8\\u662f\\u4e2d\\u56fd\\u7684\\u8bd7\\u90fd\\u5404\\u6709\\u5343\\u79cb\\u54e6~\"," +
            "\"picture\":\"http:\\/\\/cdn.iciba.com\\/news\\/word\\/20160812.jpg\"," +
            "\"picture2\":\"http:\\/\\/cdn.iciba.com\\/news\\/word\\/big_20160812b.jpg\"," +
            "\"caption\":\"\\u8bcd\\u9738\\u6bcf\\u65e5\\u4e00\\u53e5\"," +
            "\"dateline\":\"2016-08-12\",\"s_pv\":\"0\",\"sp_pv\":\"0\"," +
            "\"tags\":[{\"id\":\"13\",\"name\":\"\\u540d\\u4eba\\u540d\\u8a00\"}]," +
            "\"fenxiang_img\":\"http:\\/\\/cdn.iciba.com\\/web\\/news\\/longweibo\\/imag\\/2016-08-12.jpg\"}";

    protected Sentence generateNewSentence(boolean isStar) {
        SentenceEntity newSentence = new SentenceEntity();
        try {
            JSONObject result = new JSONObject(MOCK_SENTENCE_CONTENT);
            newSentence.setDateline(result.optString(SentenceEntityDao.Properties.Dateline.name));
            // insert today's or first got sentence
            newSentence.setSid(result.optString(SentenceEntityDao.Properties.Sid.name));
            newSentence.setContent(result.optString(SentenceEntityDao.Properties.Content.name));
            newSentence.setAllContent(MOCK_SENTENCE_CONTENT);
            newSentence.setIsStar(isStar);
        } catch (JSONException e) {
            fail("generateNewSentence error");
        }
        return newSentence;
    }

    protected Context getTargetContext(){
        return InstrumentationRegistry.getTargetContext();
    }

    protected Context getRenamingDelegatingContext(){
        return TestUtil.getRenamingDelegatingContext();
    }

    @Before
    public void setUp() {
        mSentenceDataRepository = SentenceDataRepository.getInstance();
    }

    @After
    public void cleanUp() {
        // delete test db
        mSentenceDataRepository.deleteAllSentences();
    }

    @AfterClass
    public static void tearDownClass() {
        // delete test db
        InstrumentationRegistry.getTargetContext().deleteDatabase(TestUtil.TEST_DB);
        File testDb = InstrumentationRegistry.getTargetContext().getDatabasePath(TestUtil.TEST_DB);
        Assert.assertFalse(testDb.exists());
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mSentenceDataRepository);
    }
}
