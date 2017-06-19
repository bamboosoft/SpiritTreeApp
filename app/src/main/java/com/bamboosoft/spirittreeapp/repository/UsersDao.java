/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.data.source;

import android.support.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.data.User;

import java.util.List;

/**
 * Main entry point for accessing users data.
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

    void completeUser(@NonNull User user);

    void completeUser(@NonNull String userId);

    void activateUser(@NonNull User user);

    void activateUser(@NonNull String userId);

    void clearCompletedUser();

    void refreshUser();

    void deleteAllUser();

    void deleteUser(@NonNull String userId);
}
