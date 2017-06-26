/*
 * Copyright (C) 2015 
 *
 */

package com.bamboosoft.spirittreeapp.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bamboosoft.spirittreeapp.Injection;
import com.bamboosoft.spirittreeapp.R;
import com.bamboosoft.spirittreeapp.viewmodel.ViewModelHolder;
import com.bamboosoft.spirittreeapp.ui.user.AddEditUserActivity;
import com.bamboosoft.spirittreeapp.ui.user.AddEditUserFragment;
import com.bamboosoft.spirittreeapp.util.ActivityUtils;

import static com.bamboosoft.spirittreeapp.ui.user.AddEditUserActivity.ADD_EDIT_RESULT_OK;
import static com.bamboosoft.spirittreeapp.ui.user.UserDetailFragment.REQUEST_EDIT_USER;

/**
 * Displays user details screen.
 * 屏幕显示用户详细信息。
 */
public class UserDetailActivity extends AppCompatActivity implements UserDetailNavigator {

    public static final String EXTRA_USER_ID = "USER_ID";

    public static final String USERDETAIL_VIEWMODEL_TAG = "USERDETAIL_VIEWMODEL_TAG";

    public static final int DELETE_RESULT_OK = RESULT_FIRST_USER + 2;

    public static final int EDIT_RESULT_OK = RESULT_FIRST_USER + 3;

    private UserDetailViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.userdetail_act);

        setupToolbar();

        UserDetailFragment userDetailFragment = findOrCreateViewFragment();

        mUserViewModel = findOrCreateViewModel();
        mUserViewModel.setNavigator(this);

        // Link View and ViewModel
		// 链接视图和视图模型
        userDetailFragment.setViewModel(mUserViewModel);
    }

    @Override
    protected void onDestroy() {
        mUserViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    @NonNull
    private UserDetailViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
		// 在配置更改中，我们可能会有一个视图模型。它使用片段管理器保留。
        @SuppressWarnings("unchecked")
        ViewModelHolder<UserDetailViewModel> retainedViewModel =
                (ViewModelHolder<UserDetailViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(USERDETAIL_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
			// 如果模型被保留，返回它。
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
			// 现在还没有ViewModel，创建它。
            UserDetailViewModel viewModel = new UserDetailViewModel(
                    getApplicationContext(),
                    Injection.provideUsersRepository(getApplicationContext()));

            // and bind it to this Activity's lifecycle using the Fragment Manager.
			// 使用片段管理器将其绑定到这个活动的生命周期。
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    USERDETAIL_VIEWMODEL_TAG);
            return viewModel;
        }
    }

    @NonNull
    private UserDetailFragment findOrCreateViewFragment() {
        // Get the requested user id
		// 获取请求的用户id

        String userId = getIntent().getStringExtra(EXTRA_USER_ID);

        UserDetailFragment userDetailFragment = (UserDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (userDetailFragment == null) {
            userDetailFragment = UserDetailFragment.newInstance(userId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    userDetailFragment, R.id.contentFrame);
        }
        return userDetailFragment;
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_USER) {
            // If the user was edited successfully, go back to the list.
			// 如果用户成功编辑，返回到列表。

            if (resultCode == ADD_EDIT_RESULT_OK) {
                // If the result comes from the add/edit screen, it's an edit.
				// 如果结果来自添加/编辑屏幕，它是一个编辑。
                setResult(EDIT_RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onUserDeleted() {
        setResult(DELETE_RESULT_OK);
        // If the user was deleted successfully, go back to the list.
		// 如果用户被成功删除，返回到列表。
        finish();
    }

    @Override
    public void onStartEditUser() {
        String userId = getIntent().getStringExtra(EXTRA_USER_ID);
        Intent intent = new Intent(this, AddEditUserActivity.class);
        intent.putExtra(AddEditUserFragment.ARGUMENT_EDIT_USER_ID, userId);
        startActivityForResult(intent, REQUEST_EDIT_USER);
    }
}
