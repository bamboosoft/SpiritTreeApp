/*
 * Copyright 2016, 
 *
 */

package com.bamboosoft.spirittreeapp.ui.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bamboosoft.spirittreeapp.Injection;
import com.bamboosoft.spirittreeapp.R;
import com.bamboosoft.spirittreeapp.viewmodel.ViewModelHolder;
import com.bamboosoft.spirittreeapp.util.ActivityUtils;
import com.bamboosoft.spirittreeapp.util.EspressoIdlingResource;
import com.bamboosoft.spirittreeapp.viewmodel.user.RegisterUserViewModel;



/**
 * Displays an add or edit user screen.
 * 显示添加或编辑用户界面。
 */
public class RegisterUserActivity extends AppCompatActivity implements RegisterUserNavigator {

    public static final int REQUEST_CODE = 1;

    public static final int REGISTER_RESULT_OK = RESULT_FIRST_USER + 1;

    public static final String REGISTER_VIEWMODEL_TAG = "REGISTER_VIEWMODEL_TAG";
    private RegisterUserViewModel mViewModel;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

    @Override
    public void onUserRegisted() {
        setResult(REGISTER_RESULT_OK);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adduser_act);

        // Set up the toolbar.
		// 设置工具栏。
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        RegisterUserFragment addEditUserFragment = findOrCreateViewFragment();

        mViewModel = findOrCreateViewModel();

        // Link View and ViewModel
		// 链接视图和视图模型 
        addEditUserFragment.setViewModel(mViewModel);

        mViewModel.onActivityCreated(this);
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    @NonNull
    private RegisterUserFragment findOrCreateViewFragment() {
        // View Fragment
		// 视图的片段
        RegisterUserFragment registerUserFragment = (RegisterUserFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (registerUserFragment == null) {
            registerUserFragment = RegisterUserFragment.newInstance();

            // Send the user ID to the fragment
			// 将用户ID发送给碎片
            Bundle bundle = new Bundle();
            bundle.putString(RegisterUserFragment.ARGUMENT_EDIT_USER_ID,
                    getIntent().getStringExtra(RegisterUserFragment.ARGUMENT_EDIT_USER_ID));
            registerUserFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    registerUserFragment, R.id.contentFrame);
        }
        return registerUserFragment;
    }

    private RegisterUserViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
		// 在配置更改中，我们可能会有一个视图模型。它使用片段管理器保留。
        @SuppressWarnings("unchecked")
        ViewModelHolder<RegisterUserViewModel> retainedViewModel =
                (ViewModelHolder<RegisterUserViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(REGISTER_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewModel() != null) {
            // If the model was retained, return it.
			// 如果模型被保留，返回它。
            return retainedViewModel.getViewModel();
        } else {
            // There is no ViewModel yet, create it.
			// 现在还没有ViewModel，创建它。
            RegisterUserViewModel viewModel = new RegisterUserViewModel(
                    getApplicationContext(),
                    Injection.provideUsersRepository(getApplicationContext()));

            // and bind it to this Activity's lifecycle using the Fragment Manager.
			// 使用片段管理器将其绑定到这个Activity的生命周期。

            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    REGISTER_VIEWMODEL_TAG);
            return viewModel;
        }
    }
}
