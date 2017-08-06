/*
 * Copyright 2016, 
 *
 */

package com.bamboosoft.spirittreeapp.ui.main.;

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
import com.bamboosoft.spirittreeapp.ui.ScrollChildSwipeRefreshLayout;
import com.bamboosoft.spirittreeapp.domain.user.User;
import com.bamboosoft.spirittreeapp.repository.UsersRepository;
import com.bamboosoft.spirittreeapp.databinding.UserItemBinding;
import com.bamboosoft.spirittreeapp.databinding.UsersFragBinding;
import com.bamboosoft.spirittreeapp.util.SnackbarUtils;
import com.bamboosoft.spirittreeapp.viewmodel.user.UsersViewModel;
import com.bamboosoft.spirittreeapp.viewmodel.user.UserItemViewModel;
import com.bamboosoft.spirittreeapp.common.UsersFilterType;

import java.util.ArrayList;
import java.util.List;

/**
 * Display a grid of {@link User}s. User can choose to view all, active or completed users.
 * 显示一个网格的{ @ link用户}。用户可以选择查看所有、活动或已完成的用户。
 */
public class MainFragment extends Fragment {

    private UsersViewModel mUsersViewModel;

    private UsersFragBinding mUsersFragBinding;

    private UsersAdapter mListAdapter;

    private Observable.OnPropertyChangedCallback mSnackbarCallback;

    public MainFragment() {
        // Requires empty public constructor
		// 需要空公共构造函数
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mUsersViewModel.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUsersFragBinding = UsersFragBinding.inflate(inflater, container, false);

        mUsersFragBinding.setView(this);

        mUsersFragBinding.setViewmodel(mUsersViewModel);

        setHasOptionsMenu(true);

        View root = mUsersFragBinding.getRoot();

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

    public void setViewModel(UsersViewModel viewModel) {
        mUsersViewModel = viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupSnackbar();

        setupFab();

        setupListAdapter();

        setupRefreshLayout();
    }

    @Override
    public void onDestroy() {
        mListAdapter.onDestroy();
        if (mSnackbarCallback != null) {
            mUsersViewModel.snackbarText.removeOnPropertyChangedCallback(mSnackbarCallback);
        }
        super.onDestroy();
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

    private void setupFab() {
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_user);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsersViewModel.addNewUser();
            }
        });
    }

    private void setupListAdapter() {
        ListView listView =  mUsersFragBinding.usersList;

        mListAdapter = new UsersAdapter(
                new ArrayList<User>(0),
                (UsersActivity) getActivity(),
                Injection.provideUsersRepository(getContext().getApplicationContext()),
                mUsersViewModel);
        listView.setAdapter(mListAdapter);
    }

    private void setupRefreshLayout() {
        ListView listView =  mUsersFragBinding.usersList;
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = mUsersFragBinding.refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
		// 在自定义滑动刷新布局中设置滚动视图。
        swipeRefreshLayout.setScrollUpChild(listView);
    }

    public static class UsersAdapter extends BaseAdapter {

        @Nullable private UserItemNavigator mUserItemNavigator;

        private final UsersViewModel mUsersViewModel;

        private List<User> mUsers;

        private UsersRepository mUsersRepository;

        public UsersAdapter(List<User> users, UsersActivity userItemNavigator,
                            UsersRepository usersRepository,
                            UsersViewModel usersViewModel) {
            mUserItemNavigator = userItemNavigator;
            mUsersRepository = usersRepository;
            mUsersViewModel = usersViewModel;
            setList(users);

        }

        public void onDestroy() {
            mUserItemNavigator = null;
        }

        public void replaceData(List<User> users) {
            setList(users);
        }

        @Override
        public int getCount() {
            return mUsers != null ? mUsers.size() : 0;
        }

        @Override
        public User getItem(int i) {
            return mUsers.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            User user = getItem(i);
            UserItemBinding binding;
            if (view == null) {
                // Inflate
				// 膨胀
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

                // Create the binding
				// 创建绑定
                binding = UserItemBinding.inflate(inflater, viewGroup, false);
            } else {
                // Recycling view
				// 回收的观点
                binding = DataBindingUtil.getBinding(view);
            }

            final UserItemViewModel viewmodel = new UserItemViewModel(
                    viewGroup.getContext().getApplicationContext(),
                    mUsersRepository
            );

            viewmodel.setNavigator(mUserItemNavigator);

            binding.setViewmodel(viewmodel);
            // To save on PropertyChangedCallbacks, wire the item's snackbar text observable to the
            // fragment's.
			// 为了节省PropertyChangedCallbacks,线项目间小吃店的文本片段的可观察到的。
            viewmodel.snackbarText.addOnPropertyChangedCallback(
                    new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable observable, int i) {
                    mUsersViewModel.snackbarText.set(viewmodel.getSnackbarText());
                }
            });
            viewmodel.setUser(user);

            return binding.getRoot();
        }


        private void setList(List<User> users) {
            mUsers = users;
            notifyDataSetChanged();
        }
    }
}
