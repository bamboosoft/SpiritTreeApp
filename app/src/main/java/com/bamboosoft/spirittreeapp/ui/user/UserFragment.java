/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.tasks;

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

import com.example.android.architecture.blueprints.todoapp.Injection;
import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.ScrollChildSwipeRefreshLayout;
import com.example.android.architecture.blueprints.todoapp.data.User;
import com.example.android.architecture.blueprints.todoapp.data.source.UsersRepository;
import com.example.android.architecture.blueprints.todoapp.databinding.UserItemBinding;
import com.example.android.architecture.blueprints.todoapp.databinding.UsersFragBinding;
import com.example.android.architecture.blueprints.todoapp.util.SnackbarUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Display a grid of {@link User}s. User can choose to view all, active or completed tasks.
 */
public class UsersFragment extends Fragment {

    private UsersViewModel mUsersViewModel;

    private UsersFragBinding mUsersFragBinding;

    private UsersAdapter mListAdapter;

    private Observable.OnPropertyChangedCallback mSnackbarCallback;

    public UsersFragment() {
        // Requires empty public constructor
    }

    public static UsersFragment newInstance() {
        return new UsersFragment();
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
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
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
        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.active:
                        mUsersViewModel.setFiltering(UsersFilterType.ACTIVE_TASKS);
                        break;
                    case R.id.completed:
                        mUsersViewModel.setFiltering(UsersFilterType.COMPLETED_TASKS);
                        break;
                    default:
                        mUsersViewModel.setFiltering(UsersFilterType.ALL_TASKS);
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
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_task);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsersViewModel.addNewUser();
            }
        });
    }

    private void setupListAdapter() {
        ListView listView =  mUsersFragBinding.tasksList;

        mListAdapter = new UsersAdapter(
                new ArrayList<User>(0),
                (UsersActivity) getActivity(),
                Injection.provideUsersRepository(getContext().getApplicationContext()),
                mUsersViewModel);
        listView.setAdapter(mListAdapter);
    }

    private void setupRefreshLayout() {
        ListView listView =  mUsersFragBinding.tasksList;
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = mUsersFragBinding.refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);
    }

    public static class UsersAdapter extends BaseAdapter {

        @Nullable private UserItemNavigator mUserItemNavigator;

        private final UsersViewModel mUsersViewModel;

        private List<User> mUsers;

        private UsersRepository mUsersRepository;

        public UsersAdapter(List<User> tasks, UsersActivity taskItemNavigator,
                            UsersRepository tasksRepository,
                            UsersViewModel tasksViewModel) {
            mUserItemNavigator = taskItemNavigator;
            mUsersRepository = tasksRepository;
            mUsersViewModel = tasksViewModel;
            setList(tasks);

        }

        public void onDestroy() {
            mUserItemNavigator = null;
        }

        public void replaceData(List<User> tasks) {
            setList(tasks);
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
            User task = getItem(i);
            UserItemBinding binding;
            if (view == null) {
                // Inflate
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

                // Create the binding
                binding = UserItemBinding.inflate(inflater, viewGroup, false);
            } else {
                // Recycling view
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
            viewmodel.snackbarText.addOnPropertyChangedCallback(
                    new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable observable, int i) {
                    mUsersViewModel.snackbarText.set(viewmodel.getSnackbarText());
                }
            });
            viewmodel.setUser(task);

            return binding.getRoot();
        }


        private void setList(List<User> tasks) {
            mUsers = tasks;
            notifyDataSetChanged();
        }
    }
}
