/*
 * Copyright 2016, 
 *
 */

package com.bamboosoft.spirittreeapp.viewmodel.user;

import android.content.Context;
import android.support.annotation.Nullable;

import com.bamboosoft.spirittreeapp.viewmodel.user.UserViewModel;
import com.bamboosoft.spirittreeapp.repository.UsersRepository;
import com.bamboosoft.spirittreeapp.ui.user.UsersFragment;

/**
 * Listens to user actions from the list item in ({@link UsersFragment}) and redirects them to the
 * Fragment's actions listener.
 * 从列表项({ @ link UsersFragment })中侦听用户操作，
 * 并将它们重定向到片段的操作侦听器。
 */
public class UserDetailViewModel extends UserViewModel {

    @Nullable
    private UsersDetailNavigator mUserDetailNavigator;

    public UserDetailViewModel(Context context, UsersRepository usersRepository) {
        super(context, usersRepository);
    }

    public void setNavigator(UserDetailNavigator userDetailNavigator) {
        mUserDetailNavigator = userDetailNavigator;
    }

    public void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
		// 明确的引用以避免潜在的内存泄漏。
        mUserDetailNavigator = null;
    }

    /**
     * Can be called by the Data Binding Library or the delete menu item.
	 * 可以通过数据绑定库或删除菜单项调用。
     */
    public void deleteUser() {
        super.deleteUser();
        if (mUserDetailNavigator != null) {
            mUserDetailNavigator.onUserDeleted();
        }
    }

    public void startEditUser() {
        if (mUserDetailNavigator != null) {
            mUserDetailNavigator.onStartEditUser();
        }
    }
}
