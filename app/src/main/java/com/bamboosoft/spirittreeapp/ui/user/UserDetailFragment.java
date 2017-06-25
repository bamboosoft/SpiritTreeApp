/*
 * Copyright (C) 2015 
 *
 */

package com.bamboosoft.spirittreeapp.ui.user;

import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.databinding.UserdetailFragBinding;
import com.example.android.architecture.blueprints.todoapp.util.SnackbarUtils;


/**
 * Main UI for the user detail screen.
 * 主UI用于用户细节屏幕。
 */
public class UserDetailFragment extends Fragment {

    public static final String ARGUMENT_USER_ID = "USER_ID";

    public static final int REQUEST_EDIT_USER = 1;

    private UserDetailViewModel mViewModel;
    private Observable.OnPropertyChangedCallback mSnackbarCallback;

    public static UserDetailFragment newInstance(String userId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_USER_ID, userId);
        UserDetailFragment fragment = new UserDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public void setViewModel(UserDetailViewModel userViewModel) {
        mViewModel = userViewModel;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupFab();

        setupSnackbar();
    }

    @Override
    public void onDestroy() {
        if (mSnackbarCallback != null) {
            mViewModel.snackbarText.removeOnPropertyChangedCallback(mSnackbarCallback);
        }
        super.onDestroy();
    }

    private void setupSnackbar() {
        mSnackbarCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                SnackbarUtils.showSnackbar(getView(), mViewModel.getSnackbarText());
            }
        };
        mViewModel.snackbarText.addOnPropertyChangedCallback(mSnackbarCallback);
    }

    private void setupFab() {
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_user);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.startEditUser();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.start(getArguments().getString(ARGUMENT_USER_ID));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.userdetail_frag, container, false);

        UserdetailFragBinding viewDataBinding = UserdetailFragBinding.bind(view);
        viewDataBinding.setViewmodel(mViewModel);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mViewModel.deleteUser();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.userdetail_fragment_menu, menu);
    }
}
