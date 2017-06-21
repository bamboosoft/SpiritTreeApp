/*
 * Copyright 2016, The Android Open Source Project
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

package com.example.android.architecture.blueprints.todoapp.user;

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

import com.example.android.architecture.blueprints.todoapp.Injection;
import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.ViewModelHolder;
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditUserActivity;
import com.example.android.architecture.blueprints.todoapp.statistics.StatisticsActivity;
import com.example.android.architecture.blueprints.todoapp.taskdetail.UserDetailActivity;
import com.example.android.architecture.blueprints.todoapp.util.ActivityUtils;
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource;


public class UserActivity extends AppCompatActivity implements UserItemNavigator, UserNavigator {

    private DrawerLayout mDrawerLayout;

    public static final String USER_VIEWMODEL_TAG = "USER_VIEWMODEL_TAG";

    private UserViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_act);

        setupToolbar();

        setupNavigationDrawer();

        UserFragment userFragment = findOrCreateViewFragment();

        mViewModel = findOrCreateViewModel();
        mViewModel.setNavigator(this);

        // Link View and ViewModel
        userFragment.setViewModel(mViewModel);
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    private UserViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
        @SuppressWarnings("unchecked")
        ViewModelHolder<UserViewModel> retainedViewModel =
                (ViewModelHolder<UserViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(USER_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
            UserViewModel viewModel = new UserViewModel(
                    Injection.provideUserRepository(getApplicationContext()),
                    getApplicationContext());
            // and bind it to this Activity's lifecycle using the Fragment Manager.
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    USER_VIEWMODEL_TAG);
            return viewModel;
        }
    }

    @NonNull
    private UserFragment findOrCreateViewFragment() {
        UserFragment userFragment =
                (UserFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (userFragment == null) {
            // Create the fragment
            userFragment = UserFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), userFragment, R.id.contentFrame);
        }
        return userFragment;
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
                                break;
                            case R.id.statistics_navigation_menu_item:
                                Intent intent =
                                        new Intent(UserActivity.this, StatisticsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
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
    public void openUserDetails(String taskId) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra(UserDetailActivity.EXTRA_TASK_ID, taskId);
        startActivityForResult(intent, AddEditUserActivity.REQUEST_CODE);

    }

    @Override
    public void addNewUser() {
        Intent intent = new Intent(this, AddEditUserActivity.class);
        startActivityForResult(intent, AddEditUserActivity.REQUEST_CODE);
    }
}
