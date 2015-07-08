package com.hozakan.emailchecker.data.entry;

import android.provider.BaseColumns;

/**
 * Created by gimbert on 15-07-07.
 */
public class PwndEntry implements BaseColumns {

    public static final String TABLE_NAME = "pwnd";

    public static final String COLUMN_ACCOUNT_ID = "accountId";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DOMAIN = "domain";
    public static final String COLUMN_BREACH_DATE = "breachDate";
    public static final String COLUMN_ADDED_DATE = "addedDate";
    public static final String COLUMN_PWND_COUNT = "pwndCount";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IS_VERIFIED = "isVerified";

}
