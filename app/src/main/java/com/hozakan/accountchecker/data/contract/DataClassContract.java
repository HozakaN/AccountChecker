package com.hozakan.accountchecker.data.contract;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by gimbert on 15-07-07.
 */
public class DataClassContract {

    public static final String PATH_DATACLASS = "Dataclass";

    public static final Uri CONTENT_URI =
            BaseContract.BASE_CONTENT_URI.buildUpon().appendPath(PATH_DATACLASS).build();

    public static String getIdFromUri(Uri uri) {
        return uri.getLastPathSegment();
    }

    public static Uri buildDataclassUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}
