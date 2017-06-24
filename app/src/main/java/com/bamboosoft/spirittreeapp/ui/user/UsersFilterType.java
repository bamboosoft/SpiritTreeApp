/*
 * Copyright 2016, 
 *
 */

package com.example.android.architecture.blueprints.todoapp.users;

/**
 * Used with the filter spinner in the users list.
 * 与用户列表中的过滤器spinner一起使用。
 */
public enum UsersFilterType {
    /**
     * Do not filter users.
	 * 不过滤用户。
     */
    ALL_USERS,

    /**
     * Filters only the active (not completed yet) users.
	 * 只过滤活动的(未完成的)用户。
     */
    ACTIVE_USERS,

    /**
     * Filters only the completed users.
	 * 只过滤已完成的用户。
     */
    COMPLETED_USERS
}
