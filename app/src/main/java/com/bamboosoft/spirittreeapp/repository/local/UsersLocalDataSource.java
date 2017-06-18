/*
 *
 *
 *
 *
 *
 */

package com.example.android.architecture.blueprints.todoapp.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.example.android.architecture.blueprints.todoapp.data.User;
import com.example.android.architecture.blueprints.todoapp.data.source.UserDataSource;
import com.example.android.architecture.blueprints.todoapp.data.source.local.UserPersistenceContract.UserEntry;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Concrete implementation of a data source as a db.
 */
public class UserLocalDataSource implements UserDataSource {

    private static UserLocalDataSource INSTANCE;

    private UserDbHelper mDbHelper;

    // Prevent direct instantiation.
    private UserLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new UserDbHelper(context);
    }

    public static UserLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new UserLocalDataSource(context);
        }
        return INSTANCE;
    }

    /**
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
            callback.onDataNotAvailable();
        } else {
            callback.onUserLoaded(User);
        }

    }

    /**
     * Note: {@link GetUserCallback#onDataNotAvailable()} is fired if the {@link User} isn't
     * found.
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

    @Override
    public void saveUser(@NonNull User user) {
        checkNotNull(user);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_ENTRY_ID, user.getId());
        values.put(UserEntry.COLUMN_NAME_TITLE, user.getTitle());
        values.put(UserEntry.COLUMN_NAME_DESCRIPTION, user.getDescription());
        values.put(UserEntry.COLUMN_NAME_COMPLETED, user.isCompleted());

        db.insert(UserEntry.TABLE_NAME, null, values);

        db.close();
    }

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
        // Not required for the local data source because the {@link UserRepository} handles
        // converting from a {@code userId} to a {@link user} using its cached data.
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
        // Not required for the local data source because the {@link UserRepository} handles
        // converting from a {@code userId} to a {@link user} using its cached data.
    }

    @Override
    public void clearCompletedUser() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = UserEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = { "1" };

        db.delete(UserEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public void refreshUser() {
        // Not required because the {@link UserRepository} handles the logic of refreshing the
        // User from all the available data sources.
    }

    @Override
    public void deleteAllUser() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(UserEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void deleteUser(@NonNull String userId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = UserEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { userId };

        db.delete(UserEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }
}
