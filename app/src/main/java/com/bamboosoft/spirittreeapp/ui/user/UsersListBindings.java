/*
 * Copyright 2016, 
 *
 */

package com.bamboosoft.spirittreeapp.ui.user;

import android.databinding.BindingAdapter;
import android.widget.ListView;

import com.bamboosoft.spirittreeapp.domain.user.User;

import java.util.List;

/**
 * Contains {@link BindingAdapter}s for the {@link User} list.
 * 在{ @ link用户列表中包含{ @ link BindingAdapter } s。
 */
public class UsersListBindings {

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:items")
    public static void setItems(ListView listView, List<User> items) {
        UsersFragment.UsersAdapter adapter = (UsersFragment.UsersAdapter) listView.getAdapter();
        if (adapter != null)
        {
            adapter.replaceData(items);
        }
    }
}
