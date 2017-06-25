/*
 * Copyright 2016, 
 */

package com.bamboosoft.spirittreeapp.viewmodel.user;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;

import com.example.android.architecture.blueprints.todoapp.data.User;
import com.example.android.architecture.blueprints.todoapp.data.source.UsersDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.UsersRepository;


/**
 * Abstract class for View Models that expose a single {@link User}.
 * 用于显示单个{ @ link用户}的视图模型的抽象类。
 */
public abstract class UserViewModel extends BaseObservable
        implements UsersDataSource.GetUserCallback {

    public final ObservableField<String> snackbarText = new ObservableField<>();

    public final ObservableField<String> title = new ObservableField<>();

    public final ObservableField<String> description = new ObservableField<>();

    private final ObservableField<User> mUserObservable = new ObservableField<>();

    private final UsersRepository mUsersRepository;

    private final Context mContext;

    private boolean mIsDataLoading;

    public UserViewModel(Context context, UsersRepository usersRepository) {
		
		//强制使用应用程序上下文。
        mContext = context.getApplicationContext(); // Force use of Application Context.
        mUsersRepository = usersRepository;

        // Exposed observables depend on the mUserObservable observable:
		// 可观察到的可观察到的可观察到的可见性:
        mUserObservable.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                User user = mUserObservable.get();
                if (user != null) {
                    title.set(user.getTitle());
                    description.set(user.getDescription());
                } else {
                    title.set(mContext.getString(R.string.no_data));
                    description.set(mContext.getString(R.string.no_data_description));
                }
            }
        });
    }

    public void start(String userId) {
        if (userId != null) {
            mIsDataLoading = true;
            mUsersRepository.getUser(userId, this);
        }
    }

    public void setUser(User user) {
        mUserObservable.set(user);
    }

    // "completed" is two-way bound, so in order to intercept the new value, use a @Bindable
    // annotation and process it in the setter.
	// “完成”是双向绑定，因此为了拦截新值，
	// 使用@ bindable注释并在setter中处理它。

    @Bindable
    public boolean getCompleted() {
        return mUserObservable.get().isCompleted();
    }

    public void setCompleted(boolean completed) {
        if (mIsDataLoading) {
            return;
        }
        User user = mUserObservable.get();
        // Update the entity
		// 更新的实体
        user.setCompleted(completed);

        // Notify repository and user
		// 通知仓库和用户
        if (completed) {
            mUsersRepository.completeUser(user);
            snackbarText.set(mContext.getResources().getString(R.string.user_marked_complete));
        } else {
            mUsersRepository.activateUser(user);
            snackbarText.set(mContext.getResources().getString(R.string.user_marked_active));
        }
    }

    @Bindable
    public boolean isDataAvailable() {
        return mUserObservable.get() != null;
    }

    @Bindable
    public boolean isDataLoading() {
        return mIsDataLoading;
    }

    // This could be an observable, but we save a call to User.getTitleForList() if not needed.
	// 这可以是可观察的，但是我们可以为用户保存一个调用。
    @Bindable
    public String getTitleForList() {
        if (mUserObservable.get() == null) {
            return "No data";
        }
        return mUserObservable.get().getTitleForList();
    }

    @Override
    public void onUserLoaded(User user) {
        mUserObservable.set(user);
        mIsDataLoading = false;

		// @Bindable属性
        notifyChange(); // For the @Bindable properties
    }

    @Override
    public void onDataNotAvailable() {
        mUserObservable.set(null);
        mIsDataLoading = false;
    }

    public void deleteUser() {
        if (mUserObservable.get() != null) {
            mUsersRepository.deleteUser(mUserObservable.get().getId());
        }
    }

    public void onRefresh() {
        if (mUserObservable.get() != null) {
            start(mUserObservable.get().getId());
        }
    }

    public String getSnackbarText() {
        return snackbarText.get();
    }

    @Nullable
    protected String getUserId() {
        return mUserObservable.get().getId();
    }
}
