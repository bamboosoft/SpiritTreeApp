/*
 * Copyright 2016, 
 *
 */

package com.example.android.architecture.blueprints.todoapp.addedituser;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.architecture.blueprints.todoapp.Injection;
import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.ViewModelHolder;
import com.example.android.architecture.blueprints.todoapp.util.ActivityUtils;
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource;

/**
 * Displays an add or edit user screen.
 * ��ʾ���ӻ�༭�û����档
 */
public class AddEditUserActivity extends AppCompatActivity implements AddEditUserNavigator {

    public static final int REQUEST_CODE = 1;

    public static final int ADD_EDIT_RESULT_OK = RESULT_FIRST_USER + 1;

    public static final String ADD_EDIT_VIEWMODEL_TAG = "ADD_EDIT_VIEWMODEL_TAG";
    private AddEditUserViewModel mViewModel;

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
    public void onUserSaved() {
        setResult(ADD_EDIT_RESULT_OK);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adduser_act);

        // Set up the toolbar.
		// ���ù�������
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        AddEditUserFragment addEditUserFragment = findOrCreateViewFragment();

        mViewModel = findOrCreateViewModel();

        // Link View and ViewModel
		// ������ͼ����ͼģ�� 
        addEditUserFragment.setViewModel(mViewModel);

        mViewModel.onActivityCreated(this);
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    @NonNull
    private AddEditUserFragment findOrCreateViewFragment() {
        // View Fragment
		// ��ͼ��Ƭ��
        AddEditUserFragment addEditUserFragment = (AddEditUserFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (addEditUserFragment == null) {
            addEditUserFragment = AddEditUserFragment.newInstance();

            // Send the user ID to the fragment
			// ���û�ID���͸���Ƭ
            Bundle bundle = new Bundle();
            bundle.putString(AddEditUserFragment.ARGUMENT_EDIT_USER_ID,
                    getIntent().getStringExtra(AddEditUserFragment.ARGUMENT_EDIT_USER_ID));
            addEditUserFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditUserFragment, R.id.contentFrame);
        }
        return addEditUserFragment;
    }

    private AddEditUserViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
		// �����ø����У����ǿ��ܻ���һ����ͼģ�͡���ʹ��Ƭ�ι�����������
        @SuppressWarnings("unchecked")
        ViewModelHolder<AddEditUserViewModel> retainedViewModel =
                (ViewModelHolder<AddEditUserViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(ADD_EDIT_VIEWMODEL_TAG);

        if (retainedViewModel != null && retainedViewModel.getViewmodel() != null) {
            // If the model was retained, return it.
			// ���ģ�ͱ���������������
            return retainedViewModel.getViewmodel();
        } else {
            // There is no ViewModel yet, create it.
			// ���ڻ�û��ViewModel����������
            AddEditUserViewModel viewModel = new AddEditUserViewModel(
                    getApplicationContext(),
                    Injection.provideUsersRepository(getApplicationContext()));

            // and bind it to this Activity's lifecycle using the Fragment Manager.
			// ʹ��Ƭ�ι���������󶨵����Activity���������ڡ�

            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    ADD_EDIT_VIEWMODEL_TAG);
            return viewModel;
        }
    }
}