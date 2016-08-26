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
package com.xixicm.de.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author mc
 */
public class Constants {
    public static final String TAG = "DE";
    public static final DateFormat SHORT_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final String KEY_WIDGET_SENTENCE_ID = "KEY_WIDGET_SENTENCE_ID";
    public static final int PLAY_ONCE = 0;
    public static final int PLAY_REPEAT = 1;
    public static final int PLAY_UNKNOWN = -1;
}
