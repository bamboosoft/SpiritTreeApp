/*
 *
 *
 *
 *
 *
 *
 */

package com.bamboosoft.spirittreeapp.repository.local;

import android.provider.BaseColumns;

/**
 * The contract used for the db to save the Users locally.
 * 用于数据库的契约可以在本地保存用户。
 */
public final class UsersPersistenceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
	// 为了防止意外地实例化契约类，请给它一个空的构造函数。

    private UsersPersistenceContract() {}

    /* Inner class that defines the table contents */
	/* 定义表内容的内部类 */
    public static abstract class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "Users";
        public static final int COLUMN_ID = 0;
        public static final String COLUMN_ACCOUNT = "Account";
        public static final String COLUMN_MOBILE = "Mobile";
        public static final String COLUMN_EMAIL = "Email";
        public static final String COLUMN_PASSWORD = "Password";
        public static final String COLUMN_CREATETIME = "CreateTime";
        public static final String COLUMN_STATUS = "Status";
        public static final String COLUMN_LEVEL = "CreateTime";
        public static final String COLUMN_TYPE = "Type";
        public static final String COLUMN_MEMO = "Memo";


    }
}
