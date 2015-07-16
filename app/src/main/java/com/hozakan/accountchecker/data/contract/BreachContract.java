package com.hozakan.accountchecker.data.contract;

import android.content.ContentUris;
import android.net.Uri;

import com.hozakan.accountchecker.data.entry.BreachEntry;

/**
 * Created by gimbert on 15-07-07.
 */
public class BreachContract {

    public static final String PATH_BREACH = "Breach";

    public static final Uri CONTENT_URI =
            BaseContract.BASE_CONTENT_URI.buildUpon().appendPath(PATH_BREACH).build();

    public static final String[] PROJECTION = new String[] {
            BreachEntry._ID,
            BreachEntry.COLUMN_NAME
    };

    public static final int COL_ID = 0;
    public static final int COL_NAME = 1;

    public static String getIdFromUri(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static String getAccountIdFromUri(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static Uri buildBreachUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static Uri buildBreachUriWithAccountId(long accountId) {
        return CONTENT_URI
                .buildUpon()
                .appendPath(AccountContract.PATH_ACCOUNT)
                .appendPath("" + accountId)
                .build();
    }
}
