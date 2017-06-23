/*
 *
 */

package com.bamboosoft.spirittreeapp.repository;

import android.support.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.data.User;

import java.util.List;

/**
 * Main entry point for accessing users data.
 * �����û����ݵ���Ҫ��ڵ㡣
 */
public interface UserDao {

    interface LoadUserCallback {

        void onUserLoaded(List<User> users);

        void onDataNotAvailable();
    }

    interface GetUserCallback {

        void onUserLoaded(User user);

        void onDataNotAvailable();
    }

    void getUser(@NonNull LoadUserCallback callback);

    void getUser(@NonNull String userId, @NonNull GetUserCallback callback);

    void saveUser(@NonNull User user);

    void updateUser(@NonNull User user);

    void refreshUser();

    void deleteAllUser();

    void deleteUser(@NonNull String userId);
}
