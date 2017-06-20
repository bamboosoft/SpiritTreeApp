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

package com.bamboosoft.spirittreeapp.repository.remote;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.data.User;
import com.example.android.architecture.blueprints.todoapp.data.source.UsersDao;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class UsersRemoteDao implements UsersDao {

    private static UsersRemoteDao INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 2000;

    private final static Map<String, User> USER_SERVICE_DATA;

    static {
        USER_SERVICE_DATA = new LinkedHashMap<>(2);
        addUser("Build tower in Pisa", "Ground looks good, no foundation work required.", "0");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "1");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "2");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "3");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "4");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "5");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "6");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "7");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "8");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "12");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "13");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "14");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "15");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "16");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "17");
        addUser("Finish bridge in Tacoma", "Found awesome girders at half the cost!", "18");
    }

    public static UsersRemoteDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UsersRemoteDao();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private UsersRemoteDao() {}

    private static void addUser(String title, String description, String id) {
        User newUser = new User(title, description, id);
        USER_SERVICE_DATA.put(newUser.getId(), newUser);
    }

    /**
     * Note: {@link LoadUsersCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getUsers(final @NonNull LoadUsersCallback callback) {
        // Simulate network by delaying the execution.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onUsersLoaded(Lists.newArrayList(USER_SERVICE_DATA.values()));
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    /**
     * Note: {@link GetUserCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getUser(@NonNull String userId, final @NonNull GetUserCallback callback) {
        final User user = USER_SERVICE_DATA.get(userId);

        // Simulate network by delaying the execution.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onUserLoaded(user);
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void saveUser(@NonNull User user) {
        USER_SERVICE_DATA.put(user.getId(), user);
    }

    @Override
    public void refreshUsers() {
        // Not required because the {@link UsersRepository} handles the logic of refreshing the
        // users from all the available data sources.
    }

    @Override
    public void deleteAllUsers() {
        USER_SERVICE_DATA.clear();
    }

    @Override
    public void deleteUser(@NonNull String userId) {
        USER_SERVICE_DATA.remove(userId);
    }
}
