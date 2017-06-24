/*
 * Copyright (C) 2015 
 *
 */

package com.example.android.architecture.blueprints.todoapp.userdetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.architecture.blueprints.todoapp.Injection;
import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.ViewModelHolder;
import com.example.android.architecture.blueprints.todoapp.addedituser.AddEditUserActivity;
import com.example.android.architecture.blueprints.todoapp.addedituser.AddEditUserFragment;
import com.example.android.architecture.blueprints.todoapp.util.ActivityUtils;

import static com.example.android.architecture.blueprints.todoapp.addedituser.AddEditUserActivity.ADD_EDIT_RESULT_OK;
import static com.example.android.architecture.blueprints.todoapp.userdetail.UserDetailFragment.REQUEST_EDIT_USER;

/**
 * Displays user details screen.
 * ��Ļ��ʾ�û���ϸ��Ϣ��
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
		// ������ͼ����ͼģ��
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
		// �����ø����У����ǿ��ܻ���һ����ͼģ�͡���ʹ��Ƭ�ι�����������
        @SuppressWarnings("unchecked")
        ViewModelHolder<UserDetailViewModel> retainedViewModel =
                (ViewModelHolder<UserDetailViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(USERDETAIL_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
			// ���ģ�ͱ���������������
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
			// ���ڻ�û��ViewModel����������
            UserDetailViewModel viewModel = new UserDetailViewModel(
                    getApplicationContext(),
                    Injection.provideUsersRepository(getApplicationContext()));

            // and bind it to this Activity's lifecycle using the Fragment Manager.
			// ʹ��Ƭ�ι���������󶨵��������������ڡ�
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
		// ��ȡ������û�id

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
			// ����û��ɹ��༭�����ص��б�

            if (resultCode == ADD_EDIT_RESULT_OK) {
                // If the result comes from the add/edit screen, it's an edit.
				// �������������/�༭��Ļ������һ���༭��
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
		// ����û����ɹ�ɾ�������ص��б�
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
