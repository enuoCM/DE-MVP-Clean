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
package com.xixicm.de.presentation.view.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.xixicm.de.R;
import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.domain.model.event.RefreshWidgetEvent;
import com.xixicm.de.presentation.view.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author mc
 */
public class WidgetManager {
    private static WidgetManager sInstance;
    private Context mContext;
    public static final String EXTRA_WIDGET_TYPE = "extra_widget_type";
    public static final int TYPE_4_1 = 1;
    public static final int TYPE_5_1 = 2;

    private WidgetManager(Context context) {
        mContext = context;
        EventBus.getDefault().register(this);
    }

    public static WidgetManager getInstance() {
        return sInstance;
    }


    /**
     * @return If any {@link WidgetManager#TYPE_4_1} widget, return true
     */
    private boolean isAnyDEApp41Widget() {
        return AppWidgetManager.getInstance(mContext).getAppWidgetIds(new ComponentName(mContext,
                DEAppWidgetProvider41.class)).length > 0;
    }

    /**
     * @return If any {@link WidgetManager#TYPE_5_1} widget, return true
     */
    private boolean isAnyDEApp51Widget() {
        return AppWidgetManager.getInstance(mContext).getAppWidgetIds(new ComponentName(mContext,
                DEAppWidgetProvider51.class)).length > 0;
    }

    /**
     * Update widget with latest sentence.
     *
     * @param latestSentence
     * @param widgetType
     */
    public void bindDEAppWidget(Sentence latestSentence, int widgetType) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.content, latestSentence.getContent());
        views.setTextViewText(R.id.day, latestSentence.getDateline());

        Intent launchIntent = new Intent(mContext, MainActivity.class);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        launchIntent.setAction("launch list");
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, launchIntent, 0);
        views.setOnClickPendingIntent(R.id.launcher_icon, pendingIntent);
        if (latestSentence.getId() != null) {
            Intent detailIntent = new Intent(mContext, MainActivity.class);
            detailIntent.putExtra(Constants.KEY_WIDGET_SENTENCE_ID, latestSentence.getId());
            detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            detailIntent.setAction("launch detail");
            pendingIntent = PendingIntent.getActivity(mContext, 0, detailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        views.setOnClickPendingIntent(R.id.right_part, pendingIntent);
        AppWidgetManager.getInstance(mContext).updateAppWidget(new ComponentName(mContext,
                widgetType == TYPE_4_1 ? DEAppWidgetProvider41.class : DEAppWidgetProvider51.class), views);
    }

    /**
     * @param type {@link WidgetManager#TYPE_4_1}, {@link WidgetManager#TYPE_5_1}
     */
    public void updateDEAppWidget(int type) {
        Intent intent = new Intent(mContext, DEAppWidgetUpdateService.class);
        intent.putExtra(EXTRA_WIDGET_TYPE, type);
        mContext.startService(intent);
    }

    public void updateDEAppWidgetIfNeed() {
        int type = 0;
        if (isAnyDEApp41Widget()) {
            type |= TYPE_4_1;
        }
        if (isAnyDEApp51Widget()) {
            type |= TYPE_5_1;
        }
        updateDEAppWidget(type);
    }

    @Subscribe
    public void refreshWidget(RefreshWidgetEvent refreshWidgetEvent) {
        updateDEAppWidgetIfNeed();
    }

    public static void init(Context context) {
        sInstance = new WidgetManager(context);
        sInstance.updateDEAppWidgetIfNeed();
    }
}
