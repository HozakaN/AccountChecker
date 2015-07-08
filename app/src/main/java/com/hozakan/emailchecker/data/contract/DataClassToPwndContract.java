package com.hozakan.emailchecker.data.contract;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by gimbert on 15-07-08.
 */
public class DataClassToPwndContract implements BaseContract {

    public static final String PATH_DATACLASS_TO_PWND_PATH = "DataClassToPwnd";

    public static final Uri CONTENT_URI =
            BaseContract.BASE_CONTENT_URI.buildUpon().appendPath(PATH_DATACLASS_TO_PWND_PATH).build();

    public static String getPwndIdFromUri(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static Uri buildDataclassToPwndUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}
