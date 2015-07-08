package com.hozakan.emailchecker.data.entry;

import android.net.Uri;
import android.provider.BaseColumns;

import com.hozakan.emailchecker.data.contract.AccountContract;

/**
 * Created by gimbert on 15-07-07.
 */
public class AccountEntry implements BaseColumns {

    public static final String TABLE_NAME = "account";

    public static final String COLUMN_ACCOUNT_NAME = "accountName";

    public static Uri buildUriWithId(String accountId) {
        return AccountContract.CONTENT_URI.buildUpon()
                .appendPath(accountId)
                .build();
    }
}
