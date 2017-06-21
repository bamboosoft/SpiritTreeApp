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

package com.example.android.architecture.blueprints.todoapp.user;

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
import com.example.android.architecture.blueprints.todoapp.data.source.UserDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.UserRepository;
import com.example.android.architecture.blueprints.todoapp.userdetail.UserDetailActivity;
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Exposes the data to be used in the user list screen.
 * <p>
 * {@link BaseObservable} implements a listener registration mechanism which is notified when a
 * property changes. This is done by assigning a {@link Bindable} annotation to the property's
 * getter method.
 */
public class UserViewModel extends BaseObservable {

    // These observable fields will update Views automatically
    public final ObservableList<User> items = new ObservableArrayList<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> currentFilteringLabel = new ObservableField<>();

    public final ObservableField<String> noUserLabel = new ObservableField<>();

    public final ObservableField<Drawable> noUserIconRes = new ObservableField<>();

    public final ObservableBoolean userAddViewVisible = new ObservableBoolean();

    final ObservableField<String> snackbarText = new ObservableField<>();

    private UserFilterType mCurrentFiltering = UserFilterType.ALL_USER;

    private final UserRepository mUserRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    private Context mContext; // To avoid leaks, this must be an Application Context.

    private UserNavigator mNavigator;

    public UserViewModel(
            UserRepository repository,
            Context context) {
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mUserRepository = repository;

        // Set initial state
        setFiltering(UserFilterType.ALL_USER);
    }

    void setNavigator(UserNavigator navigator) {
        mNavigator = navigator;
    }

    void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null;
    }

    public void start() {
        loadUser(false);
    }

    @Bindable
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void loadUser(boolean forceUpdate) {
        loadUser(forceUpdate, true);
    }

    /**
     * Sets the current user filtering type.
     *
     * @param requestType Can be {@link UserFilterType#ALL_USER},
     *                    {@link UserFilterType#COMPLETED_USER}, or
     *                    {@link UserFilterType#ACTIVE_USER}
     */
    public void setFiltering(UserFilterType requestType) {
        mCurrentFiltering = requestType;

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        switch (requestType) {
            case ALL_USER:
                currentFilteringLabel.set(mContext.getString(R.string.label_all));
                noUserLabel.set(mContext.getResources().getString(R.string.no_user_all));
                noUserIconRes.set(mContext.getResources().getDrawable(
                        R.drawable.ic_assignment_turned_in_24dp));
                userAddViewVisible.set(true);
                break;
            case ACTIVE_USER:
                currentFilteringLabel.set(mContext.getString(R.string.label_active));
                noUserLabel.set(mContext.getResources().getString(R.string.no_user_active));
                noUserIconRes.set(mContext.getResources().getDrawable(
                        R.drawable.ic_check_circle_24dp));
                userAddViewVisible.set(false);
                break;
            case COMPLETED_USER:
                currentFilteringLabel.set(mContext.getString(R.string.label_completed));
                noUserLabel.set(mContext.getResources().getString(R.string.no_user_completed));
                noUserIconRes.set(mContext.getResources().getDrawable(
                        R.drawable.ic_verified_user_24dp));
                userAddViewVisible.set(false);
                break;
        }
    }

    public void clearCompletedUser() {
        mUserRepository.clearCompletedUser();
        snackbarText.set(mContext.getString(R.string.completed_user_cleared));
        loadUser(false, false);
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
     * @param forceUpdate   Pass in true to refresh the data in the {@link UserDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadUser(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            dataLoading.set(true);
        }
        if (forceUpdate) {

            mUserRepository.refreshUser();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mUserRepository.getUser(new UserDataSource.LoadUserCallback() {
            @Override
            public void onUserLoaded(List<User> user) {
                List<User> userToShow = new ArrayList<User>();

                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); // Set app as idle.
                }

                // We filter the user based on the requestType
                for (User user : user) {
                    switch (mCurrentFiltering) {
                        case ALL_USER:
                            userToShow.add(user);
                            break;
                        case ACTIVE_USER:
                            if (user.isActive()) {
                                userToShow.add(user);
                            }
                            break;
                        case COMPLETED_USER:
                            if (user.isCompleted()) {
                                userToShow.add(user);
                            }
                            break;
                        default:
                            userToShow.add(user);
                            break;
                    }
                }
                if (showLoadingUI) {
                    dataLoading.set(false);
                }
                mIsDataLoadingError.set(false);

                items.clear();
                items.addAll(userToShow);
                notifyPropertyChanged(BR.empty); // It's a @Bindable so update manually
            }

            @Override
            public void onDataNotAvailable() {
                mIsDataLoadingError.set(true);
            }
        });
    }

}
