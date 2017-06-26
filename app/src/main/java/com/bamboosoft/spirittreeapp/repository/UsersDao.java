/*
 *
 */

package com.bamboosoft.spirittreeapp.repository;

import android.support.annotation.NonNull;

import com.bamboosoft.spirittreeapp.domain.user.User;

import java.util.List;

/**
 * Main entry point for accessing users data.
 * 访问用户数据的主要入口点。
 */
public interface UsersDao {

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

	//-------------------------------------------

    void completeUser(@NonNull User user);

    void completeUser(@NonNull String userId);

    void activateUser(@NonNull User user);

    void activateUser(@NonNull String userId);

    void clearCompletedUsers();


}
