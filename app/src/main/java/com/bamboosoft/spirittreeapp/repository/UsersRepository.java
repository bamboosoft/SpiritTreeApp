/*
 *
 */

package com.bamboosoft.spirittreeapp.repository;

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bamboosoft.spirittreeapp.domain.user.User;

/**
 * Concrete implementation to load users from the data sources into a cache.
 * 具体实现将用户从数据源加载到缓存中。
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 * 为简单起见，这实现了本地持久化数据和数据之间的非同步的同步
 * 从服务器获取，只有在本地数据库不使用时才使用远程数据源存在或者是空的。
 */
public class UsersRepository implements UsersDao {

    private static UsersRepository INSTANCE = null;

    private final UsersDao mUsersRemoteDao;

    private final UsersDao mUsersLocalDao;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     * 该变量具有包本地可见性，因此可以从测试中访问。
	 */
    Map<String, User> mCachedUsers;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
	 * 将缓存标记为无效，以便在请求下一次数据时强制更新。这个变量
	 * 具有包本地可见性，因此可以从测试中访问它。
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
	// 防止直接实例化。
    private UsersRepository(@NonNull UsersDao usersRemoteDao,
                            @NonNull UsersDao usersLocalDao) {
        mUsersRemoteDao = checkNotNull(usersRemoteDao);
        mUsersLocalDao = checkNotNull(usersLocalDao);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     * 返回这个类的单个实例，如果需要，创建它。
	 *			
     * @param usersRemoteDao 后端数据源
     * @param usersLocalDao  设备存储数据源
     * @return the {@link UsersRepository} instance
     */
    public static UsersRepository getInstance(UsersDao usersRemoteDao,
                                              UsersDao usersLocalDao) {
        if (INSTANCE == null) {
            INSTANCE = new UsersRepository(usersRemoteDao, usersLocalDao);
        }
        return INSTANCE;
    }

    /**
	 * 用于强制{ @ link # getInstance(UserDao,UserDao)}来创建一个新实例
     * Used to force {@link #getInstance(UsersDao, UsersDao)} to create a new instance
     * next time it's called.
	 * 下次它被调用
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets users from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
	 * 从缓存、本地数据源(SQLite)或远程数据源获取用户，无论哪一个都要是可用的。
     * <p>
     * Note: {@link LoadUsersCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
	 * 注:{ @ link LoadUserCallback # onDataNotAvailable()}如果所有数据源无法获取数据，则会被触发。
     */

    @Override
    public void getUsers(@NonNull final LoadUsersCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedUsers != null && !mCacheIsDirty) {
            callback.onUsersLoaded(new ArrayList<>(mCachedUsers.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getUsersFromRemoteDao(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            mUsersLocalDao.getUsers(new LoadUsersCallback() {
                @Override
                public void onUsersLoaded(List<User> users) {
                    refreshCache(users);
                    callback.onUsersLoaded(new ArrayList<>(mCachedUsers.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getUsersFromRemoteDao(callback);
                }
            });
        }
    }

    @Override
    public void saveUser(@NonNull User user) {
        checkNotNull(user);
        mUsersRemoteDao.saveUser(user);
        mUsersLocalDao.saveUser(user);

        // Do in memory cache update to keep the app UI up to date
		// 是否在内存缓存更新中保持应用程序的UI更新
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.put(user.getId(), user);
    }

    @Override
    public void updateUser(@NonNull User user) {
        checkNotNull(user);
        mUsersRemoteDao.saveUser(user);
        mUsersLocalDao.saveUser(user);

        // Do in memory cache update to keep the app UI up to date
        // 是否在内存缓存更新中保持应用程序的UI更新
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.put(user.getId(), user);
    }


    public void clearCompletedUser() {
        //mUserRemoteDao.clearCompletedUser();
        //mUserLocalDao.clearCompletedUser();

        // Do in memory cache update to keep the app UI up to date
		// 是否在内存缓存更新中保持应用程序的UI更新
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, User>> it = mCachedUsers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, User> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    /**
     * Gets users from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * 从本地数据源(sqlite)获取用户，除非该表是新的或空的。在这种情况下它
	 * 使用网络数据源。这样做是为了简化样本。	 
	 * <p>
     * Note: {@link GetUserCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
	 * 注:{ @ link GetUserCallback # onDataNotAvailable()}如果两个数据源无法获取数据，就会被触发。
	 *
     */
    @Override
    public void getUser(@NonNull final String userId, @NonNull final GetUserCallback callback) {
        checkNotNull(userId);
        checkNotNull(callback);

        User cachedUser = getUserWithId(userId);

        // Respond immediately with cache if available
		// 如果可用，立即响应缓存
        if (cachedUser != null) {
            callback.onUserLoaded(cachedUser);
            return;
        }

        // Load from server/persisted if needed.
		// 如果需要，可以从服务器加载。

        // Is the user in the local data source? If not, query the network.
        // 用户是否在本地数据源?如果没有，查询网络。
		mUsersLocalDao.getUser(userId, new GetUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                // Do in memory cache update to keep the app UI up to date
				// 是否在内存缓存更新中保持应用程序的UI更新
                if (mCachedUsers == null) {
                    mCachedUsers = new LinkedHashMap<>();
                }
                mCachedUsers.put(user.getId(), user);
                callback.onUserLoaded(user);
            }

            @Override
            public void onDataNotAvailable() {
                mUsersRemoteDao.getUser(userId, new GetUserCallback() {
                    @Override
                    public void onUserLoaded(User user) {
                        // Do in memory cache update to keep the app UI up to date
                        // 是否在内存缓存更新中保持应用程序的UI更新
						if (mCachedUsers == null) {
                            mCachedUsers = new LinkedHashMap<>();
                        }
                        mCachedUsers.put(user.getId(), user);
                        callback.onUserLoaded(user);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void refreshUsers() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllUsers() {
        mUsersRemoteDao.deleteAllUsers();
        mUsersLocalDao.deleteAllUsers();

        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.clear();
    }

    @Override
    public void deleteUser(@NonNull String userId) {
        mUsersRemoteDao.deleteUser(checkNotNull(userId));
        mUsersLocalDao.deleteUser(checkNotNull(userId));

        mCachedUsers.remove(userId);
    }


    private void getUsersFromRemoteDao(@NonNull final LoadUsersCallback callback) {
        mUsersRemoteDao.getUsers(new LoadUsersCallback() {
            @Override
            public void onUsersLoaded(List<User> users) {
                refreshCache(users);
                refreshLocalDao(users);
                callback.onUsersLoaded(new ArrayList<>(mCachedUsers.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }



    private void refreshCache(List<User> users) {
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.clear();
        for (User user : users) {
            mCachedUsers.put(user.getId(), user);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDao(List<User> users) {
        mUsersLocalDao.deleteAllUsers();
        for (User user : users) {
            mUsersLocalDao.saveUser(user);
        }
    }

    @Nullable
    private User getUserWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedUsers == null || mCachedUsers.isEmpty()) {
            return null;
        } else {
            return mCachedUsers.get(id);
        }
    }

	//-------------------------------------------------------------

    @Override
    public void completeUser(@NonNull User user) {
        checkNotNull(user);
        mUsersRemoteDao.completeUser(user);
        mUsersLocalDao.completeUser(user);

        User completedUser = new User(user.getAccount(), user.getPassword(), user.getMobile(),user.getEmail());

        // Do in memory cache update to keep the app UI up to date
		// 是否在内存缓存更新中保持应用程序的UI更新
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.put(user.getId(), completedUser);
    }

    @Override
    public void completeUser(@NonNull String userId) {
        checkNotNull(userId);
        completeUser(getUserWithId(userId));
    }

    @Override
    public void activateUser(@NonNull User user) {
        checkNotNull(user);
        mUsersRemoteDao.activateUser(user);
        mUsersLocalDao.activateUser(user);

        User activeUser = new User(user.getAccount(), user.getPassword(), user.getMobile(),user.getEmail());

        // Do in memory cache update to keep the app UI up to date
		// 是否在内存缓存更新中保持应用程序的UI更新
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.put(user.getId(), activeUser);
    }

    @Override
    public void activateUser(@NonNull String userId) {
        checkNotNull(userId);
        activateUser(getUserWithId(userId));
    }

    @Override
    public void clearCompletedUsers() {
        mUsersRemoteDao.clearCompletedUsers();
        mUsersLocalDao.clearCompletedUsers();

        // Do in memory cache update to keep the app UI up to date
		// 是否在内存缓存更新中保持应用程序的UI更新
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, User>> it = mCachedUsers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, User> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }




}
