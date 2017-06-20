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

package com.example.android.architecture.blueprints.todoapp.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;




/**

       ,
       ,
       


 * Immutable model class for a User.
 */
public final class User {

    @NonNull
    private final int mUserId;

    @Nullable
    private final String mAccount;
    
	@Nullable
	private final String mPassword

    @Nullable
    private final int mMobile;

    @Nullable
    private final String mEmail;

    @Nullable
    private final DateTime mCreateTime;

    @Nullable
    private final int mStatus;

    @Nullable
    private final int mLevel;

    @Nullable
    private final int mType;

    private final String mMemo;

    /**
     * Use this constructor to create a new active User.
     *
     * @param account       account of the user
     * @param password password of the user
     */
    public User(@Nullable String account, @Nullable String password) {
        this(account, password, mUserId, false);
    }

    /**
     * Use this constructor to create an active User if the User already has an id (copy of another
     * User).
     *
     * @param account       account of the user
     * @param password password of the user
     * @param id          id of the user
     */
    public User(@Nullable String account, @Nullable String password, @NonNull String id) {
        this(account, password, false);
    }

    /**
     * Use this constructor to create a new completed User.
     *
     * @param account       account of the user
     * @param password password of the user
     * @param completed   true if the user is completed, false if it's active
     */
    public User(@Nullable String account, @Nullable String password, boolean completed) {
        this(account, password, UUID.randomUUID().toString(), completed);
    }

    /**
     * Use this constructor to specify a completed User if the User already has an id (copy of
     * another User).
     *
     * @param account       account of the user
     * @param password password of the user
     * @param id          id of the user
     * @param completed   true if the user is completed, false if it's active
     */
    public User(@Nullable String account, @Nullable String password,
                @NonNull String id, boolean completed) {
        mUserId = id;
        mAccount = account;
        mPassword = password;
        mCompleted = completed;
    }

    @NonNull
    public String getId() {
        return mUserId;
    }

    @Nullable
    public String getAccount() {
        return mAccount;
    }

    @Nullable
    public String getAccountForList() {
        if (!Strings.isNullOrEmpty(mAccount)) {
            return mAccount;
        } else {
            return mPassword;
        }
    }

    @Nullable
    public String getPassword() {
        return mPassword;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public void setCompleted(boolean completed) {
        mCompleted = completed;
    }

    public boolean isActive() {
        return mStatus==1;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mAccount) &&
               Strings.isNullOrEmpty(mPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equal(mUserId, user.mUserId) &&
               Objects.equal(mAccount, user.mAccount) &&
               Objects.equal(mPassword, user.mPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mUserId, mAccount, mPassword);
    }

    @Override
    public String toString() {
        return "User with account " + mAccount;
    }
}
