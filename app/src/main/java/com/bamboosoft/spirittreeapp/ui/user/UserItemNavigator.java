/*
 * Copyright 2016, 
 *
 */

package com.example.android.architecture.blueprints.todoapp.users;

/**
 * Defines the navigation actions that can be called from a list item in the user list.
 * 定义在用户列表中可以从列表项调用的导航操作。
 */
public interface UserItemNavigator {

    void openUserDetails(String userId);
}
