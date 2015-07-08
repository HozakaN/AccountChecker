package com.hozakan.emailchecker.data.contract;

import android.content.ContentUris;
import android.net.Uri;

import com.hozakan.emailchecker.data.entry.AccountEntry;
import com.hozakan.emailchecker.data.entry.PwndEntry;

/**
 * Created by gimbert on 15-07-07.
 */
public class PwndContract {

    public static final String PATH_PWND = "Pwnd";

    public static final Uri CONTENT_URI =
            BaseContract.BASE_CONTENT_URI.buildUpon().appendPath(PATH_PWND).build();

    public static final String[] PROJECTION = new String[] {
            PwndEntry._ID,
            PwndEntry.COLUMN_NAME
    };

    public static final int COL_ID = 0;
    public static final int COL_PWND_NAME = 1;

    public static String getIdFromUri(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static String getAccountIdFromUri(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static Uri buildPwndUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static Uri buildPwndUriWithAccountId(long accountId) {
        return CONTENT_URI
                .buildUpon()
                .appendPath(AccountContract.PATH_ACCOUNT)
                .appendPath("" + accountId)
                .build();
    }
}
