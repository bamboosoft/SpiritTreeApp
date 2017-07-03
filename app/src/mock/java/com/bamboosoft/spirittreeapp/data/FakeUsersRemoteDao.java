/*
 * Copyright 2016, 
 *
 */

package com.bamboosoft.spirittreeapp.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.bamboosoft.spirittreeapp.repository.UsersDao;
import com.google.common.collect.Lists;
import com.bamboosoft.spirittreeapp.domain.user.User;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 * 使用静态访问数据的远程数据源实现简单的测试。
 */
public class FakeUsersRemoteDao implements UsersDao {

    private static FakeUsersRemoteDao INSTANCE;

    private static final Map<String, User> USERS_SERVICE_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation.
    // 防止直接实例化。
    private FakeUsersRemoteDao() {}

    public static FakeUsersRemoteDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeUsersRemoteDao();
        }
        return INSTANCE;
    }

    @Override
    public void getUsers(@NonNull LoadUsersCallback callback) {
        callback.onUsersLoaded(Lists.newArrayList(USERS_SERVICE_DATA.values()));
    }

    @Override
    public void getUser(@NonNull String userId, @NonNull GetUserCallback callback) {
        User user = USERS_SERVICE_DATA.get(userId);
        callback.onUserLoaded(user);
    }

    @Override
    public void saveUser(@NonNull User user) {
        USERS_SERVICE_DATA.put(user.getId(), user);
    }

    @Override
    public void updateUser(@NonNull User user) {
        USERS_SERVICE_DATA.put(user.getId(), user);
    }



    @Override
    public void completeUser(@NonNull User user) {
        User completedUser = new User(user.getAccount(), user.getPassword(),user.getMobile(), user.getEmail(), user.getId());
        USERS_SERVICE_DATA.put(user.getId(), completedUser);
    }

    @Override
    public void completeUser(@NonNull String userId) {
        // Not required for the remote data source.
        // 不需要远程数据源。
    }

    @Override
    public void activateUser(@NonNull User user) {
        User activeUser = new User(user.getAccount(), user.getPassword(),user.getMobile(), user.getEmail(), user.getId());
        USERS_SERVICE_DATA.put(user.getId(), activeUser);
    }

    @Override
    public void activateUser(@NonNull String userId) {
        // Not required for the remote data source.
        // 不需要远程数据源。

    }

    @Override
    public void clearCompletedUsers() {
        Iterator<Map.Entry<String, User>> it = USERS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, User> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    public void refreshUsers() {
        // Not required because the {@link UsersRepository} handles the logic of refreshing the
        // users from all the available data sources.
        // 不需要，因为{ @ link UsersRepository }处理从所有可用数据源中刷新用户的逻辑。
    }

    @Override
    public void deleteUser(@NonNull String userId) {
        USERS_SERVICE_DATA.remove(userId);
    }

    @Override
    public void deleteAllUsers() {
        USERS_SERVICE_DATA.clear();
    }

    @VisibleForTesting
    public void addUsers(User... users) {
        if (users != null) {
            for (User user : users) {
                USERS_SERVICE_DATA.put(user.getId(), user);
            }
        }
    }
}
