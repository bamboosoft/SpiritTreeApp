/*
 * Copyright (C) 2015 
 * 
 */

package com.bamboosoft.spirittreeapp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bamboosoft.spirittreeapp.data.FakeUsersRemoteDao;
import com.bamboosoft.spirittreeapp.repository.UsersDao;
import com.bamboosoft.spirittreeapp.repository.UsersRepository;
import com.bamboosoft.spirittreeapp.repository.local.UsersLocalDao;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for
 * {@link UsersDao} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 * 启用模拟实现的注入
 * 在编译时使用@ link UsersDao }。这对测试很有用，因为它允许我们使用
 * 一个类的假实例，以分离依赖项并在这里运行测试。
 */
public class Injection {

    public static UsersRepository provideUsersRepository(@NonNull Context context) {
        checkNotNull(context);
        return UsersRepository.getInstance(FakeUsersRemoteDao.getInstance(),
                UsersLocalDao.getInstance(context));
    }
}
