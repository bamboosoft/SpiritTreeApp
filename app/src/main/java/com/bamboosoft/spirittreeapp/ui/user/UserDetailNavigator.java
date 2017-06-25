/*
 * Copyright 2016, 
 *
 */

package com.bamboosoft.spirittreeapp.ui.user;

/**
 * Defines the navigation actions that can be called from the Details screen.
 * 定义可以从Details屏幕调用的导航操作。
 */
public interface UserDetailNavigator {

    void onUserDeleted();

    void onStartEditUser();
}
