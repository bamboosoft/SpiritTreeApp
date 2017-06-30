/*
 * Copyright 2016, 
 */

package com.bamboosoft.spirittreeapp.viewmodel.user;

import android.content.Context;
import android.support.annotation.Nullable;

import com.bamboosoft.spirittreeapp.viewmodel.user.UserViewModel;
import com.bamboosoft.spirittreeapp.ui.user.UsersFragment;
import com.bamboosoft.spirittreeapp.ui.user.UserItemNavigator;
import com.bamboosoft.spirittreeapp.repository.UsersRepository;


import java.lang.ref.WeakReference;


/**
 * Listens to user actions from the list item in ({@link UsersFragment}) and redirects them to the
 * Fragment's actions listener.
 * 从列表项中侦听用户操作({ @ link users片段})，
 * 并将它们重定向到片段的操作侦听器。
 */
public class UserItemViewModel extends UserViewModel {

    // This navigator is s wrapped in a WeakReference to avoid leaks because it has references to an
    // activity. There's no straightforward way to clear it for each item in a list adapter.
	// 这个导航器被封装在一个弱引用中，以避免泄漏，因为它引用了一个活动。
	// 对于列表适配器中的每一个条目，没有简单的方法来清除它。
    @Nullable
    private WeakReference<UserItemNavigator> mNavigator;

    public UserItemViewModel(Context context, UsersRepository usersRepository) {
        super(context, usersRepository);
    }

    public void setNavigator(UserItemNavigator navigator) {
        mNavigator = new WeakReference<>(navigator);
    }

    /**
     * Called by the Data Binding library when the row is clicked.
	 * 当单击行时，由数据绑定库调用。
     */
    public void userClicked() {
        String userId = getUserId();
        if (userId == null) {
            // Click happened before user was loaded, no-op.
			// 在任务加载之前单击发生，不操作。
            return;
        }
        if (mNavigator != null && mNavigator.get() != null) {
            mNavigator.get().openUserDetails(userId);
        }
    }
}
