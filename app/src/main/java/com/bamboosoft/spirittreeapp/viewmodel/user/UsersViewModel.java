/*
 * Copyright 2016, 
 *
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

import com.bamboosoft.spirittreeapp.BR;
import com.bamboosoft.spirittreeapp.ui.user.AddEditUserActivity;
import com.bamboosoft.spirittreeapp.ui.user.UserDetailActivity;
import com.bamboosoft.spirittreeapp.util.EspressoIdlingResource;
import com.bamboosoft.spirittreeapp.R;
import com.bamboosoft.spirittreeapp.domain.user.User;
import com.bamboosoft.spirittreeapp.repository.UsersDao;
import com.bamboosoft.spirittreeapp.repository.UsersRepository;
import com.bamboosoft.spirittreeapp.common.UsersFilterType;
import com.bamboosoft.spirittreeapp.ui.user.UsersNavigator;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示用户列表数据.
 * <p>
 * {@link BaseObservable} implements a listener registration mechanism which is notified when a
 * property changes. This is done by assigning a {@link Bindable} annotation to the property's
 * getter method.
 * { @ link BaseObservable }实现了一个侦听器注册机制，当属性发生变化时，
 * 它会被通知。这是通过为属性的getter方法分配{ @ link可绑定}注释来完成的。
 */
public class UsersViewModel extends BaseObservable {

    // These observable fields will update Views automatically
	// 这些可观察的字段将自动更新视图

    public final ObservableList<User> items = new ObservableArrayList<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableField<String> currentFilteringLabel = new ObservableField<>();

    public final ObservableField<String> noUsersLabel = new ObservableField<>();

    public final ObservableField<Drawable> noUserIconRes = new ObservableField<>();

    public final ObservableBoolean usersAddViewVisible = new ObservableBoolean();

    public final ObservableField<String> snackbarText = new ObservableField<>();

    private UsersFilterType mCurrentFiltering = UsersFilterType.ALL_USERS;

    private final UsersRepository mUsersRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

	// To avoid leaks, this must be an Application Context.
	// 为了避免泄漏，这必须是应用程序上下文。

    private Context mContext; 

    private UsersNavigator mNavigator;

    public UsersViewModel(
            UsersRepository repository,
            Context context) {

		// 强制使用应用程序上下文。
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mUsersRepository = repository;

        // Set initial state
		// 设置初始状态
        setFiltering(UsersFilterType.ALL_USERS);
    }

    public void setNavigator(UsersNavigator navigator) {
        mNavigator = navigator;
    }

    public void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
		// 明确的引用以避免潜在的内存泄漏。
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
     * 设置当前的用户过滤类型。
	 *
     * @param requestType Can be {@link UsersFilterType#ALL_USERS},
     *                    {@link UsersFilterType#COMPLETED_USERS}, or
     *                    {@link UsersFilterType#ACTIVE_USERS}
     */
    public void setFiltering(UsersFilterType requestType) {
        mCurrentFiltering = requestType;

        // Depending on the filter type, set the filtering label, icon drawables, etc.
		// 根据筛选器类型，设置过滤标签、图标绘制器等。
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
	 * 由数据绑定库和FAB的单击侦听器调用。
     */
    public void addNewUser() {
        if (mNavigator != null) {
            mNavigator.addNewUser();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode) {
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
     * @param forceUpdate   Pass in true to refresh the data in the {@link UsersDao}
	 * @ param forceUpdate通过true来刷新{ @ link UsersDataSource }中的数据

     * @param showLoadingUI Pass in true to display a loading icon in the UI
	 * @ param showLoadingUI通过true来显示UI中的加载图标
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
		// 网络请求可能会以不同的线程处理，所以确保Espresso知道应用程序很忙，
		// 直到响应被处理。

		//应用程序一直忙到另行通知
        EspressoIdlingResource.increment(); // App is busy until further notice

        mUsersRepository.getUsers(new UsersDao.LoadUsersCallback() {
            @Override
            public void onUsersLoaded(List<User> users) {
                List<User> usersToShow = new ArrayList<User>();

                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
				// 这个回调可能被调用两次，一次是用于缓存，一次是用于从服务器API加载数据，
				// 所以我们在递减之前检查，否则它抛出“计数器已经被破坏了!”“例外。
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); // Set app as idle.
                }

                // We filter the users based on the requestType
				// 我们根据请求类型过滤用户
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

				//这是一个手动更新的@ bindable
                notifyPropertyChanged(BR.empty); // It's a @Bindable so update manually
            }

            @Override
            public void onDataNotAvailable() {
                mIsDataLoadingError.set(true);
            }
        });
    }

}
