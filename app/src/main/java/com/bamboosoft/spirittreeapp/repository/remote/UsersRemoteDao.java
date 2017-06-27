/*
 * Copyright 2016, 
 *
 */

package com.bamboosoft.spirittreeapp.repository.remote;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.bamboosoft.spirittreeapp.domain.user.User;
import com.bamboosoft.spirittreeapp.repository.UsersDao;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of the data source that adds a latency simulating network.
 * 数据来源的实现，增加了一个延时模拟网络。
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
	// 防止直接实例化。
    private UsersRemoteDao() {}

    private static void addUser(String title, String description, String id) {
        User newUser = new User(title, description, id);
        USER_SERVICE_DATA.put(newUser.getId(), newUser);
    }

    /**
     * Note: {@link LoadUsersCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
	 * 注意:{ @ link LoadUsersCallback # onDataNotAvailable()}永远不会被触发。
	 * 在实际的远程数据源实现中，如果无法联系服务器或服务器返回错误，
	 * 则会触发此操作。
     */
    @Override
    public void getUsers(final @NonNull LoadUserCallback callback) {
        // Simulate network by delaying the execution.
		// 通过延迟执行来模拟网络。
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
	 * 注意:{ @ link GetUserCallback # onDataNotAvailable()}永远不会被触发。
	 * 在实际的远程数据源实现中，如果无法联系服务器或服务器返回错误，
	 * 则会触发此操作。
     */
    @Override
    public void getUser(@NonNull String userId, final @NonNull GetUserCallback callback) {
        final User user = USER_SERVICE_DATA.get(userId);

        // Simulate network by delaying the execution.
		// 通过延迟执行来模拟网络。
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
		// 不需要，因为{ @ link UsersRepository }处理从所有可用数据源中刷新用户的逻辑。

    }

    @Override
    public void deleteAllUsers() {
        USER_SERVICE_DATA.clear();
    }

    @Override
    public void deleteUser(@NonNull String userId) {
        USER_SERVICE_DATA.remove(userId);
    }

	//----------------------------------------------------------------

    @Override
    public void completeUser(@NonNull User user) {
        User completedUser = new User(user.getTitle(), user.getDescription(), user.getId(), true);
        TASKS_SERVICE_DATA.put(user.getId(), completedUser);
    }

    @Override
    public void completeUser(@NonNull String userId) {
        // Not required for the remote data source because the {@link UsersRepository} handles
        // converting from a {@code userId} to a {@link user} using its cached data.
		// 不需要远程数据源，因为{ @ link UsersRepository }处理从{ @ code userId }转换为{ @ link用户}使用它的缓存数据。
	}

    @Override
    public void activateUser(@NonNull User user) {
        User activeUser = new User(user.getTitle(), user.getDescription(), user.getId());
        TASKS_SERVICE_DATA.put(user.getId(), activeUser);
    }

    @Override
    public void activateUser(@NonNull String userId) {
        // Not required for the remote data source because the {@link UsersRepository} handles
        // converting from a {@code userId} to a {@link user} using its cached data.
		// 不需要远程数据源，因为{ @ link UsersRepository }处理从{ @ code userId }转换为{ @ link用户}使用它的缓存数据。

    }

    @Override
    public void clearCompletedUsers() {
        Iterator<Map.Entry<String, User>> it = TASKS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, User> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }


}
