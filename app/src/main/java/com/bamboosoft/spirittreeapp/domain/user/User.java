/*
 * Copyright 2016, 
 *
 */

package com.bamboosoft.spirittreeapp.domain.user;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Date;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Immutable model class for a User.
 * 不可变 用户model类
 */
public final class User {

    @NonNull
    private final String mUserId;

    @Nullable
    private final String mAccount;
    
	@Nullable
	private final String mPassword;

    @Nullable
    private final int mMobile;

    @Nullable
    private final String mEmail;

    @Nullable
    private final Date mCreateTime;

    @Nullable
    private final int mStatus;

    @Nullable
    private final int mLevel;

    @Nullable
    private final int mType;

    private final String mDescription;

    /**
     *
     * @param account       account of the user
     * @param password password of the user
     */
    public User(@Nullable String account, @Nullable String password,
				@Nullable int mobile,@NonNull String email) {
        mUserId = UUID.randomUUID().toString();
        mAccount = account;
        mPassword = password;       
		mMobile = mobile;
		mEmail = email;
		mCreateTime = getCreateTime();
		mStatus = 1;
		mLevel =1 ;
		mType = 1;
        mDescription = "";
    }

    public User(@Nullable String account, @Nullable String password,
                @Nullable int mobile,@NonNull String email,@Nullable String userId) {
        mUserId = userId;
        mAccount = account;
        mPassword = password;
        mMobile = mobile;
        mEmail = email;
        mCreateTime = getCreateTime();
        mStatus = 1;
        mLevel =1 ;
        mType = 1;
        mDescription = "";


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
    public Date getCreateTime() {
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

    @Nullable
    public String getDescription() {
        return mDescription;
    }
    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mAccount) &&
                Strings.isNullOrEmpty(mPassword);
    }


    public boolean isCompleted()
    {

        return true;
    }

    public boolean isActive()
    {

        return true;
    }

    public void setCompleted(boolean completed) {
        //mCompleted = completed;
    }


    @Nullable
    public String getAccountForList() {
        if (!Strings.isNullOrEmpty(mAccount)) {
            return mAccount;
        } else {
            return mEmail;
        }
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
