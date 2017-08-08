/*
 * Copyright 2016, 
 *
 */

package com.bamboosoft.spirittreeapp.ui.main;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.bamboosoft.spirittreeapp.Injection;
import com.bamboosoft.spirittreeapp.R;
import com.bamboosoft.spirittreeapp.databinding.MainFragBinding;
import com.bamboosoft.spirittreeapp.ui.ScrollChildSwipeRefreshLayout;
import com.bamboosoft.spirittreeapp.domain.user.User;
import com.bamboosoft.spirittreeapp.repository.UsersRepository;
import com.bamboosoft.spirittreeapp.databinding.UserItemBinding;
import com.bamboosoft.spirittreeapp.databinding.UsersFragBinding;
import com.bamboosoft.spirittreeapp.util.SnackbarUtils;
import com.bamboosoft.spirittreeapp.viewmodel.user.UsersViewModel;
import com.bamboosoft.spirittreeapp.viewmodel.user.UserItemViewModel;
import com.bamboosoft.spirittreeapp.common.UsersFilterType;
import com.bamboosoft.spirittreeapp.ui.user.UserItemNavigator;
import java.util.ArrayList;
import java.util.List;

/**
 * Display a grid of {@link User}s. User can choose to view all, active or completed users.
 * 显示一个网格的{ @ link用户}。用户可以选择查看所有、活动或已完成的用户。
 */
public class MainFragment extends Fragment {

    private UsersViewModel mUsersViewModel;

    private MainFragBinding mMainFragBinding;


    private Observable.OnPropertyChangedCallback mSnackbarCallback;

    public MainFragment() {
        // Requires empty public constructor
		// 需要空公共构造函数
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupSnackbar();

        setupRefreshLayout();
    }

    //region Override Fragment
    @Override
    public void onResume() {
        super.onResume();
        mUsersViewModel.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainFragBinding = MainFragBinding.inflate(inflater, container, false);

        mMainFragBinding.setView(this);

        mMainFragBinding.setViewmodel(mUsersViewModel);

        setHasOptionsMenu(true);

        View root = mMainFragBinding.getRoot();

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mUsersViewModel.clearCompletedUsers();
                break;
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                mUsersViewModel.loadUsers(true);
                break;
        }
        return true;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.users_fragment_menu, menu);
    }
    @Override
    public void onDestroy() {
        if (mSnackbarCallback != null) {
            mUsersViewModel.snackbarText.removeOnPropertyChangedCallback(mSnackbarCallback);
        }
        super.onDestroy();
    }
    //endregion


    public void setViewModel(UsersViewModel viewModel) {
        mUsersViewModel = viewModel;
    }


    private void setupSnackbar() {
        mSnackbarCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                SnackbarUtils.showSnackbar(getView(), mUsersViewModel.getSnackbarText());
            }
        };
        mUsersViewModel.snackbarText.addOnPropertyChangedCallback(mSnackbarCallback);
    }

    private void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_users, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.active:
                        mUsersViewModel.setFiltering(UsersFilterType.ACTIVE_USERS);
                        break;
                    case R.id.completed:
                        mUsersViewModel.setFiltering(UsersFilterType.COMPLETED_USERS);
                        break;
                    default:
                        mUsersViewModel.setFiltering(UsersFilterType.ALL_USERS);
                        break;
                }
                mUsersViewModel.loadUsers(false);
                return true;
            }
        });

        popup.show();
    }
    /*
    * 刷新Layout
    * */
    private void setupRefreshLayout() {
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = mMainFragBinding.refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
		// 在自定义滑动刷新布局中设置滚动视图。
        //swipeRefreshLayout.setScrollUpChild(listView);
    }
}
