package com.hozakan.emailchecker.data.contract;

import android.content.ContentUris;
import android.net.Uri;

import com.hozakan.emailchecker.data.entry.AccountEntry;

/**
 * Created by gimbert on 15-07-07.
 */
public class AccountContract {

    public static final String PATH_ACCOUNT = "Account";

    public static final Uri CONTENT_URI =
            BaseContract.BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNT).build();

    public static final String[] PROJECTION = new String[] {
            AccountEntry._ID,
            AccountEntry.COLUMN_ACCOUNT_NAME
    };

    public static final int COL_ID = 0;
    public static final int COL_ACCOUNT_NAME = 1;

    public static String getIdFromUri(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static Uri buildAccountUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}
