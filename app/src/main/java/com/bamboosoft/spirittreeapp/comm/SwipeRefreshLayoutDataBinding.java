/*
 * Copyright 2017, 
 *
 */

package com.example.android.architecture.blueprints.todoapp;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import com.example.android.architecture.blueprints.todoapp.tasks.TasksViewModel;

public class SwipeRefreshLayoutDataBinding {

    /**
     * Reloads the data when the pull-to-refresh is triggered.
	 * 在触发到刷新时重新加载数据。
     * <p>
     * 产生 {@code android:onRefresh} 为 {@link SwipeRefreshLayout}.
     */
    @BindingAdapter("android:onRefresh")
    public static void setSwipeRefreshLayoutOnRefreshListener(ScrollChildSwipeRefreshLayout view,
            final TasksViewModel viewModel) {
        view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.loadTasks(true);
            }
        });
    }

}
