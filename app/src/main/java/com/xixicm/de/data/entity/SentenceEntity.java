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
package com.xixicm.de.data.entity;

import com.xixicm.de.domain.model.Sentence;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Let it extend from SentenceMode.
 */
import org.greenrobot.greendao.annotation.Generated;

/**
 * Entity mapped to table "SENTENCE".
 *
 * @author mc
 */
@Entity(nameInDb = "SENTENCE")
public class SentenceEntity implements Sentence {

    @Id(autoincrement = true)
    @Property(nameInDb = "_id")
    private Long id;
    private String sid;
    private String dateline;
    private String content;
    private String allContent;
    private Boolean isStar;

    @Transient
    private transient JSONObject mJSONObject;

    @Generated(hash = 1727968701)
    public SentenceEntity(Long id, String sid, String dateline, String content,
                          String allContent, Boolean isStar) {
        this.id = id;
        this.sid = sid;
        this.dateline = dateline;
        this.content = content;
        this.allContent = allContent;
        this.isStar = isStar;
    }

    @Generated(hash = 856064646)
    public SentenceEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAllContent() {
        return allContent;
    }

    public void setAllContent(String allContent) {
        this.allContent = allContent;
    }

    public Boolean getIsStar() {
        return isStar;
    }

    public void setIsStar(Boolean isStar) {
        this.isStar = isStar;
    }

    // for business and presentation(performance)

    /**
     * {
     * 'sid':'' #每日一句ID
     * 'tts': '' #音频地址
     * 'content':'' #英文内容
     * 'note': '' #中文内容
     * 'translation':'' #词霸小编
     * 'picture': '' #图片地址
     * 'picture2': '' #大图片地址
     * 'caption':'' #标题
     * 'dateline':'' #时间
     * 's_pv':'' #浏览数
     * 'sp_pv':'' #语音评测浏览数
     * 'tags':'' #相关标签
     * 'fenxiang_img':'' #合成图片，建议分享微博用的
     * }
     *
     * @return
     */
    public JSONObject getJsonObjectContent() {
        if (mJSONObject == null) {
            JSONObject o = new JSONObject();
            try {
                o = new JSONObject(allContent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mJSONObject = o;
        }
        return mJSONObject;
    }

    public String getAudioUrl() {
        JSONObject o = getJsonObjectContent();
        if (o != null) {
            return o.optString("tts");
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SentenceEntity that = (SentenceEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (sid != null ? !sid.equals(that.sid) : that.sid != null) return false;
        if (dateline != null ? !dateline.equals(that.dateline) : that.dateline != null)
            return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (allContent != null ? !allContent.equals(that.allContent) : that.allContent != null)
            return false;
        if (isStar != null ? !isStar.equals(that.isStar) : that.isStar != null) return false;
        return mJSONObject != null ? mJSONObject.equals(that.mJSONObject) : that.mJSONObject == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (sid != null ? sid.hashCode() : 0);
        result = 31 * result + (dateline != null ? dateline.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (allContent != null ? allContent.hashCode() : 0);
        result = 31 * result + (isStar != null ? isStar.hashCode() : 0);
        result = 31 * result + (mJSONObject != null ? mJSONObject.hashCode() : 0);
        return result;
    }
}
