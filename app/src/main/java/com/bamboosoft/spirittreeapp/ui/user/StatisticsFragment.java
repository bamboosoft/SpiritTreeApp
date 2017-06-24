/*
 * Copyright 2016, 
 *
 */

package com.example.android.architecture.blueprints.todoapp.statistics;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.databinding.StatisticsFragBinding;

/**
 * Main UI for the statistics screen.
 * ͳ����Ļ����UI��
 */
public class StatisticsFragment extends Fragment {

    private StatisticsFragBinding mViewDataBinding;

    private StatisticsViewModel mStatisticsViewModel;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewDataBinding = DataBindingUtil.inflate(
                inflater, R.layout.statistics_frag, container, false);
        return mViewDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewDataBinding.setStats(mStatisticsViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        mStatisticsViewModel.start();
    }

    public void setViewModel(StatisticsViewModel statisticsViewModel) {
        mStatisticsViewModel = statisticsViewModel;
    }

    public boolean isActive() {
        return isAdded();
    }
}
