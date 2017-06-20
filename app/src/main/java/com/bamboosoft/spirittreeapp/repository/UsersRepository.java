/*
 *
 */

package com.bamboosoft.spirittreeapp.repository;

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.architecture.blueprints.todoapp.data.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation to load users from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class UserRepository implements UserDao {

    private static UserRepository INSTANCE = null;

    private final UserDao mUserRemoteDao;

    private final UserDao mUserLocalDao;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, User> mCachedUser;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private UserRepository(@NonNull UserDao usersRemoteDao,
                            @NonNull UserDao usersLocalDao) {
        mUserRemoteDao = checkNotNull(usersRemoteDao);
        mUserLocalDao = checkNotNull(usersLocalDao);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param usersRemoteDao the backend data source
     * @param usersLocalDao  the device storage data source
     * @return the {@link UserRepository} instance
     */
    public static UserRepository getInstance(UserDao usersRemoteDao,
                                              UserDao usersLocalDao) {
        if (INSTANCE == null) {
            INSTANCE = new UserRepository(usersRemoteDao, usersLocalDao);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(UserDao, UserDao)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets users from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadUserCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getUser(@NonNull final LoadUserCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedUser != null && !mCacheIsDirty) {
            callback.onUserLoaded(new ArrayList<>(mCachedUser.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getUserFromRemoteDao(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            mUserLocalDao.getUser(new LoadUserCallback() {
                @Override
                public void onUserLoaded(List<User> users) {
                    refreshCache(users);
                    callback.onUserLoaded(new ArrayList<>(mCachedUser.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getUserFromRemoteDao(callback);
                }
            });
        }
    }

    @Override
    public void saveUser(@NonNull User user) {
        checkNotNull(user);
        mUserRemoteDao.saveUser(user);
        mUserLocalDao.saveUser(user);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedUser == null) {
            mCachedUser = new LinkedHashMap<>();
        }
        mCachedUser.put(user.getId(), user);
    }



    public void clearCompletedUser() {
        //mUserRemoteDao.clearCompletedUser();
        //mUserLocalDao.clearCompletedUser();

        // Do in memory cache update to keep the app UI up to date
        if (mCachedUser == null) {
            mCachedUser = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, User>> it = mCachedUser.entrySet().iterator();
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
     * <p>
     * Note: {@link GetUserCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     */
    @Override
    public void getUser(@NonNull final String userId, @NonNull final GetUserCallback callback) {
        checkNotNull(userId);
        checkNotNull(callback);

        User cachedUser = getUserWithId(userId);

        // Respond immediately with cache if available
        if (cachedUser != null) {
            callback.onUserLoaded(cachedUser);
            return;
        }

        // Load from server/persisted if needed.

        // Is the user in the local data source? If not, query the network.
        mUserLocalDao.getUser(userId, new GetUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedUser == null) {
                    mCachedUser = new LinkedHashMap<>();
                }
                mCachedUser.put(user.getId(), user);
                callback.onUserLoaded(user);
            }

            @Override
            public void onDataNotAvailable() {
                mUserRemoteDao.getUser(userId, new GetUserCallback() {
                    @Override
                    public void onUserLoaded(User user) {
                        // Do in memory cache update to keep the app UI up to date
                        if (mCachedUser == null) {
                            mCachedUser = new LinkedHashMap<>();
                        }
                        mCachedUser.put(user.getId(), user);
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
    public void refreshUser() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllUser() {
        mUserRemoteDao.deleteAllUser();
        mUserLocalDao.deleteAllUser();

        if (mCachedUser == null) {
            mCachedUser = new LinkedHashMap<>();
        }
        mCachedUser.clear();
    }

    @Override
    public void deleteUser(@NonNull String userId) {
        mUserRemoteDao.deleteUser(checkNotNull(userId));
        mUserLocalDao.deleteUser(checkNotNull(userId));

        mCachedUser.remove(userId);
    }

    private void getUserFromRemoteDao(@NonNull final LoadUserCallback callback) {
        mUserRemoteDao.getUser(new LoadUserCallback() {
            @Override
            public void onUserLoaded(List<User> users) {
                refreshCache(users);
                refreshLocalDao(users);
                callback.onUserLoaded(new ArrayList<>(mCachedUser.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<User> users) {
        if (mCachedUser == null) {
            mCachedUser = new LinkedHashMap<>();
        }
        mCachedUser.clear();
        for (User user : users) {
            mCachedUser.put(user.getId(), user);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDao(List<User> users) {
        mUserLocalDao.deleteAllUser();
        for (User user : users) {
            mUserLocalDao.saveUser(user);
        }
    }

    @Nullable
    private User getUserWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedUser == null || mCachedUser.isEmpty()) {
            return null;
        } else {
            return mCachedUser.get(id);
        }
    }
}
