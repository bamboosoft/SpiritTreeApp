/*
 * Copyright 2016, 
 * 
 */

package com.bamboosoft.spirittreeapp.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bamboosoft.spirittreeapp.Injection;
import com.bamboosoft.spirittreeapp.R;

import com.bamboosoft.spirittreeapp.viewmodel.ViewModelHolder;
import com.bamboosoft.spirittreeapp.viewmodel.user.StatisticsViewModel;
import com.bamboosoft.spirittreeapp.ui.user.UsersActivity;

import com.bamboosoft.spirittreeapp.util.ActivityUtils;

/**
 * Show statistics for users.
 * 显示用户统计数据。
 */
public class StatisticsActivity extends AppCompatActivity {

    public static final String STATS_VIEWMODEL_TAG = "ADD_EDIT_VIEWMODEL_TAG";

    private DrawerLayout mDrawerLayout;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
				// 从工具栏中选择home图标时打开导航抽屉。
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.statistics_act);

        setupToolbar();

        setupNavigationDrawer();

        StatisticsFragment statisticsFragment = findOrCreateViewFragment();

        StatisticsViewModel statisticsViewModel = findOrCreateViewModel();

        // Link View and ViewModel
		// 链接视图和视图模型
        statisticsFragment.setViewModel(statisticsViewModel);
    }

    @NonNull
    private StatisticsViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
		// 在配置更改中，我们可能会有一个视图模型。它使用片段管理器保留。
        @SuppressWarnings("unchecked")
        ViewModelHolder<StatisticsViewModel> retainedViewModel =
                (ViewModelHolder<StatisticsViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(STATS_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewModel() != null) {
            // If the model was retained, return it.
			// 如果模型被保留，返回它。
            return retainedViewModel.getViewModel();
        } else {
            // There is no ViewModel yet, create it.
			// 现在还没有ViewModel，创建它。
            StatisticsViewModel viewModel = new StatisticsViewModel(getApplicationContext(),
                    Injection.provideUsersRepository(getApplicationContext()));

            // and bind it to this Activity's lifecycle using the Fragment Manager.
			// 使用片段管理器将其绑定到这个活动的生命周期。
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    STATS_VIEWMODEL_TAG);
            return viewModel;
        }
    }

    @NonNull
    private StatisticsFragment findOrCreateViewFragment() {
        StatisticsFragment statisticsFragment = (StatisticsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (statisticsFragment == null) {
            statisticsFragment = StatisticsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    statisticsFragment, R.id.contentFrame);
        }
        return statisticsFragment;
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.statistics_account);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.list_navigation_menu_item:
                                Intent intent =
                                        new Intent(StatisticsActivity.this, UsersActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.statistics_navigation_menu_item:
                                // Do nothing, we're already on that screen
								// 什么都不做，我们已经在屏幕上了
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
						// 选择一个项目时关闭导航抽屉。
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}
