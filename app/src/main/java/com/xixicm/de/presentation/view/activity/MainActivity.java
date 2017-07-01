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
package com.xixicm.de.presentation.view.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xixicm.de.R;
import com.xixicm.de.data.entity.SentenceEntity;
import com.xixicm.de.data.storage.dao.DaoManager;
import com.xixicm.de.data.storage.dao.SentenceEntityDao;
import com.xixicm.de.domain.Constants;
import com.xixicm.de.domain.model.Sentence;
import com.xixicm.de.domain.model.event.SentenceChangedEvent;
import com.xixicm.ca.presentation.mvp.MvpActivity;
import com.xixicm.de.presentation.contract.Main;
import com.xixicm.de.presentation.presenter.MainPresenter;
import com.xixicm.de.presentation.view.fragment.AboutFragment;
import com.xixicm.de.presentation.view.fragment.SentenceDetailFragment;
import com.xixicm.de.presentation.view.fragment.SentenceListFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author mc
 */
public class MainActivity extends MvpActivity<Void, Main.View, Main.Presenter<Main.View, Void>>
        implements NavigationView.OnNavigationItemSelectedListener, Main.View {
    public final static String MAIN_TAG = "main";
    public final static String BACK_STACK_NAME = "sentence_list";
    @BindView(R.id.fab)
    FloatingActionButton mManualFetchFab;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    private TextView mDayTextView;
    private ObjectAnimator mManualFetchFabAnimator;
    FragmentManager mFragmentManager;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(1, 1, 1, "add");
//        menu.add(2, 2, 2, "delete");
//        return true;
//
//    }

    public void duplicateSentences() {
        List<SentenceEntity> se = DaoManager.getInstance().getSentenceEntityDao().queryBuilder().orderDesc(SentenceEntityDao.Properties.Dateline).list();
        for (Sentence s : se) {
            Sentence newS = new SentenceEntity();
            newS.setAllContent(s.getAllContent());
            newS.setContent(s.getContent());
            newS.setDateline(s.getDateline().replace("2016", "2017"));
            newS.setSid("AAAA");
            newS.setIsStar(false);
            DaoManager.getInstance().getDaoSession().insert(newS);
            break;
        }
        EventBus.getDefault().post(new SentenceChangedEvent());
    }

    private void deleateFakeSentences() {
        List<SentenceEntity> se = DaoManager.getInstance().getSentenceEntityDao().queryBuilder().where(SentenceEntityDao.Properties.Sid.eq("AAAA")).list();
        DaoManager.getInstance().getSentenceEntityDao().deleteInTx(se);
        EventBus.getDefault().post(new SentenceChangedEvent());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            duplicateSentences();
            return true;
        } else if (item.getItemId() == 2) {
            deleateFakeSentences();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        mManualFetchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.fetchTodaysSentence();
            }
        });

        DrawerLayout drawer = ButterKnife.findById(this, R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        View headerView = mNavigationView.inflateHeaderView(R.layout.nav_header_main);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDayTextView = ButterKnife.findById(headerView, R.id.textView);
        if (savedInstanceState == null) {
            mNavigationView.setCheckedItem(R.id.nav_all);
            long widgetSentenceId = getIntent().getLongExtra(Constants.KEY_WIDGET_SENTENCE_ID, -1L);
            mPresenter.onCreate(widgetSentenceId);
        }
    }

    @NonNull
    @Override
    protected Main.Presenter<Main.View, Void> createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        updateFabStatus();
    }

    private void updateFabStatus() {
        // for recreate activity
        Fragment fragment = mFragmentManager.findFragmentByTag(MAIN_TAG);
        boolean isDisplayingList = fragment instanceof SentenceListFragment;
        boolean isFavoriteList = false;
        if (isDisplayingList) {
            isFavoriteList = ((SentenceListFragment) fragment).isFavoriteList();
        }
        mPresenter.updateManualFetchFabStatus(isDisplayingList, isFavoriteList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // For day change
        mPresenter.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        boolean isDrawerOpen = drawer.isDrawerOpen(GravityCompat.START);
        int backStackEntryCount = 0;
        boolean isDetailFragment = false;
        if (!isDrawerOpen) {
            Fragment fragment = mFragmentManager.findFragmentByTag(MAIN_TAG);
            isDetailFragment = fragment instanceof SentenceDetailFragment;
            if (isDetailFragment) {
                backStackEntryCount = mFragmentManager.getBackStackEntryCount();
            }
        }
        mPresenter.onBackPressed(isDrawerOpen, isDetailFragment, backStackEntryCount);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        mPresenter.onNavigationItemSelected(item.getItemId());
        return true;
    }

    @Override
    public void showSentenceDetailFragment(long sentenceId) {
        mFragmentManager.beginTransaction()
                .add(R.id.content_main, SentenceDetailFragment.newInstance(false, sentenceId), MAIN_TAG).commit();
    }

    @Override
    public void showSentenceListFragment(boolean favorite) {
        mFragmentManager.beginTransaction()
                .replace(R.id.content_main, SentenceListFragment.newInstance(favorite), MAIN_TAG)
                .commit();
    }

    @Override
    public void showAboutFragment() {
        mFragmentManager.beginTransaction()
                .replace(R.id.content_main, AboutFragment.newInstance(), MAIN_TAG)
                .commit();
    }

    @Override
    public void clearAllFragment() {
        mFragmentManager.popBackStackImmediate(BACK_STACK_NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void updateManualFetchFabStatus(boolean visiable) {
        if (mManualFetchFab != null) {
            mManualFetchFab.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void refreshDateView(String date) {
        mDayTextView.setText(date);
    }

    @Override
    public void updateManualFetchFabAnimator(boolean needToEnd, boolean needToStart) {
        if (mManualFetchFabAnimator == null) {
            mManualFetchFabAnimator = ObjectAnimator.ofInt(mManualFetchFab.getDrawable(), "level", 0, 10000);
            mManualFetchFabAnimator.setDuration(1000);
            mManualFetchFabAnimator.setRepeatCount(ValueAnimator.INFINITE);
        }
        if (needToEnd) {
            mManualFetchFabAnimator.end();
        } else if (needToStart && !mManualFetchFabAnimator.isStarted()) {
            mManualFetchFabAnimator.start();
        }
    }

    @Override
    public void showFetchingResult(int stringResource) {
        Toast.makeText(this, stringResource, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onSuperBackPressed() {
        super.onBackPressed();
    }
}
