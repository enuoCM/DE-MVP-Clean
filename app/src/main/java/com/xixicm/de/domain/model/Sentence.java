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
package com.xixicm.de.domain.model;

import org.json.JSONObject;

/**
 * Just for simple, this Mode is used for
 * the transport(get by json content from network), storage(dao layer), business(this layer) and presentation layer.
 * Usually each layer should have it's own mode
 *
 * @author mc
 */
public interface Sentence {
    Long getId();

    void setId(Long id);

    String getSid();

    void setSid(String sid);

    String getDateline();

    void setDateline(String dateline);

    String getContent();

    void setContent(String content);

    String getAllContent();

    void setAllContent(String allContent);

    Boolean getIsStar();

    void setIsStar(Boolean isStar);

    JSONObject getJsonObjectContent();

    String getAudioUrl();
}
