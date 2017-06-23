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

package com.bamboosoft.spirittreeapp.viewmodel.user;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.graphics.drawable.Drawable;

import com.example.android.architecture.blueprints.todoapp.BR;
import com.example.android.architecture.blueprints.todoapp.R;
import com.example.android.architecture.blueprints.todoapp.addedituser.AddEditUserActivity;
import com.example.android.architecture.blueprints.todoapp.data.User;
import com.example.android.architecture.blueprints.todoapp.data.source.UsersDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.UsersRepository;
import com.example.android.architecture.blueprints.todoapp.userdetail.UserDetailActivity;
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示用户列表数据.
 * <p>
 * {@link BaseObservable} implements a listener registration mechanism which is notified when a
 * property changes. This is done by assigning a {@link Bindable} annotation to the property's
 * getter method.
 */
public class UsersViewModel extends BaseObservable {

    // These observable fields will update Views automatically
    public final ObservableList<User> items = new ObservableArrayList<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> currentFilteringLabel = new ObservableField<>();

    public final ObservableField<String> noUsersLabel = new ObservableField<>();

    public final ObservableField<Drawable> noUserIconRes = new ObservableField<>();

    public final ObservableBoolean UsersAddViewVisible = new ObservableBoolean();

    final ObservableField<String> snackbarText = new ObservableField<>();

    private UsersFilterType mCurrentFiltering = UsersFilterType.ALL_Users;

    private final UsersRepository mUsersRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    private Context mContext; // To avoid leaks, this must be an Application Context.

    private UsersNavigator mNavigator;

    public UsersViewModel(
            UsersRepository repository,
            Context context) {
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mUsersRepository = repository;

        // Set initial state
        setFiltering(UsersFilterType.ALL_USERS);
    }

    void setNavigator(UsersNavigator navigator) {
        mNavigator = navigator;
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    public void start() {
        loadUsers(false);
    }

    @Bindable
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void loadUsers(boolean forceUpdate) {
        loadUsers(forceUpdate, true);
    }

    /**
     * Sets the current user filtering type.
     *
     * @param requestType Can be {@link UsersFilterType#ALL_USERS},
     *                    {@link UsersFilterType#COMPLETED_USERS}, or
     *                    {@link UsersFilterType#ACTIVE_USERS}
     */
    public void setFiltering(UsersFilterType requestType) {
        mCurrentFiltering = requestType;

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        switch (requestType) {
            case ALL_USERS:
                currentFilteringLabel.set(mContext.getString(R.string.label_all));
                noUsersLabel.set(mContext.getResources().getString(R.string.no_users_all));
                noUserIconRes.set(mContext.getResources().getDrawable(
                        R.drawable.ic_assignment_turned_in_24dp));
                usersAddViewVisible.set(true);
                break;
            case ACTIVE_USERS:
                currentFilteringLabel.set(mContext.getString(R.string.label_active));
                noUsersLabel.set(mContext.getResources().getString(R.string.no_users_active));
                noUserIconRes.set(mContext.getResources().getDrawable(
                        R.drawable.ic_check_circle_24dp));
                usersAddViewVisible.set(false);
                break;
            case COMPLETED_USERS:
                currentFilteringLabel.set(mContext.getString(R.string.label_completed));
                noUsersLabel.set(mContext.getResources().getString(R.string.no_users_completed));
                noUserIconRes.set(mContext.getResources().getDrawable(
                        R.drawable.ic_verified_user_24dp));
                usersAddViewVisible.set(false);
                break;
        }
    }

    public void clearCompletedUsers() {
        mUsersRepository.clearCompletedUsers();
        snackbarText.set(mContext.getString(R.string.completed_users_cleared));
        loadUsers(false, false);
    }

    public String getSnackbarText() {
        return snackbarText.get();
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    public void addNewUser() {
        if (mNavigator != null) {
            mNavigator.addNewUser();
        }
    }

    void handleActivityResult(int requestCode, int resultCode) {
        if (AddEditUserActivity.REQUEST_CODE == requestCode) {
            switch (resultCode) {
                case UserDetailActivity.EDIT_RESULT_OK:
                    snackbarText.set(
                            mContext.getString(R.string.successfully_saved_user_message));
                    break;
                case AddEditUserActivity.ADD_EDIT_RESULT_OK:
                    snackbarText.set(
                            mContext.getString(R.string.successfully_added_user_message));
                    break;
                case UserDetailActivity.DELETE_RESULT_OK:
                    snackbarText.set(
                            mContext.getString(R.string.successfully_deleted_user_message));
                    break;
            }
        }
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link UsersDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadUsers(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            dataLoading.set(true);
        }
        if (forceUpdate) {

            mUsersRepository.refreshUsers();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mUsersRepository.getUsers(new UsersDataSource.LoadUsersCallback() {
            @Override
            public void onUsersLoaded(List<User> users) {
                List<User> usersToShow = new ArrayList<User>();

                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); // Set app as idle.
                }

                // We filter the users based on the requestType
                for (User user : users) {
                    switch (mCurrentFiltering) {
                        case ALL_USERS:
                            usersToShow.add(user);
                            break;
                        case ACTIVE_USERS:
                            if (user.isActive()) {
                                usersToShow.add(user);
                            }
                            break;
                        case COMPLETED_USERS:
                            if (user.isCompleted()) {
                                usersToShow.add(user);
                            }
                            break;
                        default:
                            usersToShow.add(user);
                            break;
                    }
                }
                if (showLoadingUI) {
                    dataLoading.set(false);
                }
                mIsDataLoadingError.set(false);

                items.clear();
                items.addAll(usersToShow);
                notifyPropertyChanged(BR.empty); // It's a @Bindable so update manually
            }

            @Override
            public void onDataNotAvailable() {
                mIsDataLoadingError.set(true);
            }
        });
    }

}
