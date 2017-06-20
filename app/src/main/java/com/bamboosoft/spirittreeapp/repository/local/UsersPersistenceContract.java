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
 */
public final class UsersPersistenceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private UsersPersistenceContract() {}

    /* Inner class that defines the table contents */
    public static abstract class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "Users";
        public static final int COLUMN_ID = "UserId";
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
