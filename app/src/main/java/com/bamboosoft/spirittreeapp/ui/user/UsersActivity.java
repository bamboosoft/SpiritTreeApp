/*
 * Copyright 2016, 
 *
 */

package com.bamboosoft.spirittreeapp.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.test.espresso.IdlingResource;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bamboosoft.spirittreeapp.Injection;
import com.bamboosoft.spirittreeapp.R;
import com.bamboosoft.spirittreeapp.viewmodel.ViewModelHolder;
import com.bamboosoft.spirittreeapp.ui.user.AddEditUserActivity;
import com.bamboosoft.spirittreeapp.ui.user.StatisticsActivity;
import com.bamboosoft.spirittreeapp.ui.user.UserDetailActivity;


import com.bamboosoft.spirittreeapp.util.ActivityUtils;
import com.bamboosoft.spirittreeapp.util.EspressoIdlingResource;


public class UsersActivity extends AppCompatActivity implements UserItemNavigator, UsersNavigator {

    private DrawerLayout mDrawerLayout;

    public static final String USERS_VIEWMODEL_TAG = "USERS_VIEWMODEL_TAG";

    private UsersViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_act);

        setupToolbar();

        setupNavigationDrawer();

        UsersFragment usersFragment = findOrCreateViewFragment();

        mViewModel = findOrCreateViewModel();
        mViewModel.setNavigator(this);

        // Link View and ViewModel
		// 链接视图和视图模型
        usersFragment.setViewModel(mViewModel);
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    private UsersViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
		// 在配置更改中，我们可能会有一个视图模型。它使用片段管理器保留。
        @SuppressWarnings("unchecked")
        ViewModelHolder<UsersViewModel> retainedViewModel =
                (ViewModelHolder<UsersViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(USERS_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
			// 现在还没有ViewModel，创建它。
            UsersViewModel viewModel = new UsersViewModel(
                    Injection.provideUsersRepository(getApplicationContext()),
                    getApplicationContext());
            // and bind it to this Activity's lifecycle using the Fragment Manager.
			// 使用片段管理器将其绑定到这个活动的生命周期。
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    USERS_VIEWMODEL_TAG);
            return viewModel;
        }
    }

    @NonNull
    private UsersFragment findOrCreateViewFragment() {
        UsersFragment usersFragment =
                (UsersFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (usersFragment == null) {
            // Create the fragment
			// 创建片段
            usersFragment = UsersFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), usersFragment, R.id.contentFrame);
        }
        return usersFragment;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

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

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.list_navigation_menu_item:
                                // Do nothing, we're already on that screen
								// 什么都不做，我们已经在屏幕上了
                                break;
                            case R.id.statistics_navigation_menu_item:
                                Intent intent =
                                        new Intent(UsersActivity.this, StatisticsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_USER
                                        | Intent.FLAG_ACTIVITY_CLEAR_USER);
                                startActivity(intent);
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

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mViewModel.handleActivityResult(requestCode, resultCode);
    }

    @Override
    public void openUserDetails(String userId) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra(UserDetailActivity.EXTRA_USER_ID, userId);
        startActivityForResult(intent, AddEditUserActivity.REQUEST_CODE);

    }

    @Override
    public void addNewUser() {
        Intent intent = new Intent(this, AddEditUserActivity.class);
        startActivityForResult(intent, AddEditUserActivity.REQUEST_CODE);
    }
}
