/*
 *
 *
 *
 *
 *
 */

package com.bamboosoft.spirittreeapp.repository.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.data.User;
import com.example.android.architecture.blueprints.todoapp.data.source.UserDao;
import com.example.android.architecture.blueprints.todoapp.data.source.local.UserPersistenceContract.UserEntry;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 * 将数据源作为数据库的具体实现。
 */
public class UserLocalDao implements UserDao {

    private static UserLocalDao INSTANCE;

    private UserDbHelper mDbHelper;

    //Prevent direct instantiation.
	//禁止实例化
    private UserLocalDao(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new UserDbHelper(context);
    }

    /**
	*数据库单例实例
	*
	*/
    public static UserLocalDao getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new UserLocalDao(context);
        }
        return INSTANCE;
    }

    /**
     * 注:{ @ link LoadUserCallback # onDataNotAvailable()}
	 * 如果数据库不存在或表为空，则会被触发。
     * Note: {@link LoadUserCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getUsers(@NonNull LoadUsersCallback callback) {
        List<User> users = new ArrayList<User>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                UserEntry.COLUMN_ID,
                UserEntry.COLUMN_ACCOUNT,
                UserEntry.COLUMN_MOBILE,
                UserEntry.COLUMN_EMAIL
        };

        Cursor c = db.query(
                UserEntry.TABLE_NAME, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_ENTRY_ID));
                String title = c.getString(c.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_TITLE));
                String description =
                        c.getString(c.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_DESCRIPTION));
                boolean completed =
                        c.getInt(c.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_COMPLETED)) == 1;
                User user = new User(title, description, itemId, completed);
                User.add(user);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (User.isEmpty()) {
            // This will be called if the table is new or just empty.
			// 如果表是新的或者只是空的，就会调用这个。
            callback.onDataNotAvailable();
        } else {
            callback.onUserLoaded(User);
        }

    }

    /**
     * 获取单个实体.
     * Note: {@link GetUserCallback#onDataNotAvailable()} is fired if the {@link User} isn't
     * found.
	 * 注意:如果没有找到{ @ link用户}，
	 * 就会触发{ @ link GetUserCallback # onDataNotAvailable()}。
     */
    @Override
    public void getUser(@NonNull String userId, @NonNull GetUserCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                UserEntry.COLUMN_NAME_ENTRY_ID,
                UserEntry.COLUMN_NAME_TITLE,
                UserEntry.COLUMN_NAME_DESCRIPTION,
                UserEntry.COLUMN_NAME_COMPLETED
        };

        String selection = UserEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { userId };

        Cursor c = db.query(
                UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        User user = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String itemId = c.getString(c.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_ENTRY_ID));
            String title = c.getString(c.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_TITLE));
            String description =
                    c.getString(c.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_DESCRIPTION));
            boolean completed =
                    c.getInt(c.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_COMPLETED)) == 1;
            user = new User(title, description, itemId, completed);
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (user != null) {
            callback.onUserLoaded(user);
        } else {
            callback.onDataNotAvailable();
        }
    }

    /**
	*增加数据
	*保存用户实体数据
	*/
    @Override
    public void saveUser(@NonNull User user) {
        checkNotNull(user);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(UserEntry.COLUMN_ACCOUNT, user.getAccount());
        values.put(UserEntry.COLUMN_MOBILE, user.getMobile());
        values.put(UserEntry.COLUMN_EMAIL, user.getEmail());
        values.put(UserEntry.COLUMN_PASSWORD, user.GetPassword());


        db.insert(UserEntry.TABLE_NAME, null, values);

        db.close();
    }

    /**
	*修改用户
	*
	*/
    @Override
    public void updateUser(@NonNull User user) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_COMPLETED, true);

        String selection = UserEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { user.getId() };

        db.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void refreshUser() {
        // Not required because the {@link UsersRepository} handles the logic of refreshing the
        // users from all the available data sources.

		// 不需要，因为{ @ link UserRepository }
		// 处理从所有可用数据源刷新用户的逻辑。
    }
   
    /**
	*删除所有用户
	*
	*/
    @Override
    public void deleteAllUser() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(UserEntry.TABLE_NAME, null, null);

        db.close();
    }

    /**
	*删除单个用户
	*
	*/
    @Override
    public void deleteUser(@NonNull String userId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = UserEntry.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = { userId };

        db.delete(UserEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

	// --------------------------------------------------------------------

	
    @Override
    public void completeUser(@NonNull User user) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_COMPLETED, true);

        String selection = UserEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { user.getId() };

        db.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void completeUser(@NonNull String userId) {
        // Not required for the local data source because the {@link UsersRepository} handles
        // converting from a {@code userId} to a {@link user} using its cached data.
		// 不需要本地数据源，因为{ @ link UsersRepository }
		// 处理从{ @ code userId }转换为{ @ link用户}使用它的缓存数据。

    }

    @Override
    public void activateUser(@NonNull User user) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_COMPLETED, false);

        String selection = UserEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { user.getId() };

        db.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void activateUser(@NonNull String userId) {
        // Not required for the local data source because the {@link UsersRepository} handles
        // converting from a {@code userId} to a {@link user} using its cached data.
		// 不需要本地数据源，因为{ @ link UsersRepository }处理从{ @ code userId }
		// 转换为{ @ link用户}使用它的缓存数据。

    }

    @Override
    public void clearCompletedUsers() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = UserEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = { "1" };

        db.delete(UserEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }





}
