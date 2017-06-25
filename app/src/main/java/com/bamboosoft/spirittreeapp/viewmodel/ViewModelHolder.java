/*
 * Copyright 2017,
 *
 */

package com.bamboosoft.spirittreeapp.viewmodel;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;


/**
 * Non-UI Fragment used to retain ViewModels.
 * 用于保留viewmodel的非ui片段。
 */
public class ViewModelHolder<VM> extends Fragment {

    private VM mViewModel;

    public ViewModelHolder() { }

    public static <M> ViewModelHolder createContainer(@NonNull M viewModel) {
        ViewModelHolder<M> viewModelContainer = new ViewModelHolder<>();
        viewModelContainer.setViewModel(viewModel);
        return viewModelContainer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable public VM getViewodel() {
        return mViewModel;
    }

    public void setViewModel(@NonNull VM viewModel) {
        mViewModel = viewModel;
    }
}
