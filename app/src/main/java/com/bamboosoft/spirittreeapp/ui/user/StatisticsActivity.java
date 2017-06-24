/*
 * Copyright 2016, 
 * 
 */

package com.example.android.architecture.blueprints.todoapp.statistics;

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

import com.example.android.architecture.blueprints.todoapp.Injection;
import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.ViewModelHolder;
import com.example.android.architecture.blueprints.todoapp.users.UsersActivity;
import com.example.android.architecture.blueprints.todoapp.util.ActivityUtils;

/**
 * Show statistics for users.
 * ��ʾ�û�ͳ�����ݡ�
 */
public class StatisticsActivity extends AppCompatActivity {

    public static final String STATS_VIEWMODEL_TAG = "ADD_EDIT_VIEWMODEL_TAG";

    private DrawerLayout mDrawerLayout;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
				// �ӹ�������ѡ��homeͼ��ʱ�򿪵������롣
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
		// ������ͼ����ͼģ��
        statisticsFragment.setViewModel(statisticsViewModel);
    }

    @NonNull
    private StatisticsViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
		// �����ø����У����ǿ��ܻ���һ����ͼģ�͡���ʹ��Ƭ�ι�����������
        @SuppressWarnings("unchecked")
        ViewModelHolder<StatisticsViewModel> retainedViewModel =
                (ViewModelHolder<StatisticsViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(STATS_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
			// ���ģ�ͱ���������������
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
			// ���ڻ�û��ViewModel����������
            StatisticsViewModel viewModel = new StatisticsViewModel(getApplicationContext(),
                    Injection.provideUsersRepository(getApplicationContext()));

            // and bind it to this Activity's lifecycle using the Fragment Manager.
			// ʹ��Ƭ�ι���������󶨵��������������ڡ�
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
        ab.setTitle(R.string.statistics_title);
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
								// ʲô�������������Ѿ�����Ļ����
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
						// ѡ��һ����Ŀʱ�رյ������롣
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}
