/*
 * Copyright 2016, 
 *
 */

package com.example.android.architecture.blueprints.todoapp.addedituser;

import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.databinding.AdduserFragBinding;
import com.example.android.architecture.blueprints.todoapp.util.SnackbarUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Main UI for the add user screen. Users can enter a user title and description.
 * ���������Ļ����UI���û�����������������������
 */
public class AddEditUserFragment extends Fragment {

    public static final String ARGUMENT_EDIT_USER_ID = "EDIT_USER_ID";

    private AddEditUserViewModel mViewModel;

    private AdduserFragBinding mViewDataBinding;

    private Observable.OnPropertyChangedCallback mSnackbarCallback;

    public static AddEditUserFragment newInstance() {
        return new AddEditUserFragment();
    }

    public AddEditUserFragment() {
        // Required empty public constructor
		// ��Ҫ�չ������캯��

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            mViewModel.start(getArguments().getString(ARGUMENT_EDIT_USER_ID));
        } else {
            mViewModel.start(null);
        }
    }

    public void setViewModel(@NonNull AddEditUserViewModel viewModel) {
        mViewModel = checkNotNull(viewModel);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupFab();

        setupSnackbar();

        setupActionBar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.adduser_frag, container, false);
        if (mViewDataBinding == null) {
            mViewDataBinding = AdduserFragBinding.bind(root);
        }

        mViewDataBinding.setViewmodel(mViewModel);

        setHasOptionsMenu(true);
        setRetainInstance(false);

        return mViewDataBinding.getRoot();
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
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_user_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.saveUser(mViewModel.title.get(), mViewModel.description.get());
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (getArguments() != null) {
            actionBar.setTitle(R.string.edit_user);
        } else {
            actionBar.setTitle(R.string.add_user);
        }
    }
}
