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
package com.xixicm.de.data.storage.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * @author mc
 */
public class DaoManager {
    public static final String DB_NAME = "de-db";
    private static DaoManager sInstance;
    // What’s a good place to store SQLiteDatabase/DaoMaster/DaoSession?
    // As a rule of thumb it’s a good idea to store database related objects in Application scope.
    // This way use can use the database/DAO objects across Activities.
    // Leaving the database open for the life time of the application’s process makes things simple and efficient.
    private SQLiteDatabase mDb;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private SentenceEntityDao mSentenceEntityDao;

    private DaoManager(@NonNull Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        mDb = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDb);
        mDaoSession = mDaoMaster.newSession();
        mSentenceEntityDao = mDaoSession.getSentenceEntityDao();
    }

    public static DaoManager getInstance() {
        return sInstance;
    }

    public static synchronized void init(@NonNull Context context) {
        if (sInstance == null) {
            sInstance = new DaoManager(context);
        }
    }

    public SQLiteDatabase getWritableDatabase() {
        return mDb;
    }

    public DaoMaster getDaoMaster() {
        return mDaoMaster;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SentenceEntityDao getSentenceEntityDao() {
        return mSentenceEntityDao;
    }
}
