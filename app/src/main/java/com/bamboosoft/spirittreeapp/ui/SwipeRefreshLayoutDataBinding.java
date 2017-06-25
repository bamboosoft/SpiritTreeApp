/*
 * Copyright 2017, 
 *
 */

package com.bamboosoft.spirittreeapp.ui;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

import com.bamboosoft.spirittreeapp.viewmodel.user.UsersViewModel;

public class SwipeRefreshLayoutDataBinding {

    /**
     * Reloads the data when the pull-to-refresh is triggered.
	 * 在触发到刷新时重新加载数据。
     * <p>
     * 产生 {@code android:onRefresh} 为 {@link SwipeRefreshLayout}.
     */
    @BindingAdapter("android:onRefresh")
    public static void setSwipeRefreshLayoutOnRefreshListener(ScrollChildSwipeRefreshLayout view,
            final UsersViewModel viewModel) {
        view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.loadUsers(true);
            }
        });
    }

}
