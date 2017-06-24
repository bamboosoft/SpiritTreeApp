/*
 * Copyright 2016, 
 *
 */

package com.example.android.architecture.blueprints.todoapp.users;

/**
 * Used with the filter spinner in the users list.
 * ���û��б��еĹ�����spinnerһ��ʹ�á�
 */
public enum UsersFilterType {
    /**
     * Do not filter users.
	 * �������û���
     */
    ALL_USERS,

    /**
     * Filters only the active (not completed yet) users.
	 * ֻ���˻��(δ��ɵ�)�û���
     */
    ACTIVE_USERS,

    /**
     * Filters only the completed users.
	 * ֻ��������ɵ��û���
     */
    COMPLETED_USERS
}
