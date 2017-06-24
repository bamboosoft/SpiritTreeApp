/*
 * Copyright 2016, 
 *
 */

package com.example.android.architecture.blueprints.todoapp.users;

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
import com.example.android.architecture.blueprints.todoapp.addedituser.AddEditUserActivity;
import com.example.android.architecture.blueprints.todoapp.statistics.StatisticsActivity;
import com.example.android.architecture.blueprints.todoapp.userdetail.UserDetailActivity;
import com.example.android.architecture.blueprints.todoapp.util.ActivityUtils;
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource;


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
		// ������ͼ����ͼģ��
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
		// �����ø����У����ǿ��ܻ���һ����ͼģ�͡���ʹ��Ƭ�ι�����������
        @SuppressWarnings("unchecked")
        ViewModelHolder<UsersViewModel> retainedViewModel =
                (ViewModelHolder<UsersViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(USERS_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
			// ���ڻ�û��ViewModel����������
            UsersViewModel viewModel = new UsersViewModel(
                    Injection.provideUsersRepository(getApplicationContext()),
                    getApplicationContext());
            // and bind it to this Activity's lifecycle using the Fragment Manager.
			// ʹ��Ƭ�ι���������󶨵��������������ڡ�
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
			// ����Ƭ��
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
				// �ӹ�������ѡ��homeͼ��ʱ�򿪵������롣
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
								// ʲô�������������Ѿ�����Ļ����
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
						// ѡ��һ����Ŀʱ�رյ������롣
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
