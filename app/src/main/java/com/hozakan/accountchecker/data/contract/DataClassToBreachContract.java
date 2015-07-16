package com.hozakan.accountchecker.data.contract;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by gimbert on 15-07-08.
 */
public class DataClassToBreachContract implements BaseContract {

    public static final String PATH_DATACLASS_TO_BREACH_PATH = "DataClassToBreach";

    public static final Uri CONTENT_URI =
            BaseContract.BASE_CONTENT_URI.buildUpon().appendPath(PATH_DATACLASS_TO_BREACH_PATH).build();

    public static String getBreachIdFromUri(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static Uri buildDataclassToBreachUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}
