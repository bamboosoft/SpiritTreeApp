/*
 * Copyright 2016, 
 *
 */

package com.example.android.architecture.blueprints.todoapp.users;

/**
 * Defines the navigation actions that can be called from a list item in the user list.
 * �������û��б��п��Դ��б�����õĵ���������
 */
public interface UserItemNavigator {

    void openUserDetails(String userId);
}
