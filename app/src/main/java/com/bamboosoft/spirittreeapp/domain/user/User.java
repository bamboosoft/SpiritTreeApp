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
     *
     * @param account       account of the user
     * @param password password of the user
     */
    public User(@Nullable String account, @Nullable String password,
				@Nullable int mobile,@NonNull String email) {
        mAccount = account;
        mPassword = password;       
		mMobile = mobile;
		mEmail = email;
		mCreateTime = getNow();
		mStatus = 1;
		mLevel =1 ;
		mType = 1;
		mMemo = "";
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
    public String getPassword() {
        return mPassword;
    }

    @Nullable
    public int getMobile() {
        return mMobile;
    }

    @Nullable
    public String getEmail() {
        return mPassword;
    }

    @Nullable
    public DateTime getCreateTime() {
        return mCreateTime;
    }

    @Nullable
    public int getStatus() {
        return mStatus;
    }

    @Nullable
    public int getLevel() {
        return mLevel;
    }


    @Nullable
    public int getType() {
        return mType;
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
