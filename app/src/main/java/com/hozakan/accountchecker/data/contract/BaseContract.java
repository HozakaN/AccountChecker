package com.hozakan.accountchecker.data.contract;

import android.net.Uri;

/**
 * Created by gimbert on 15-07-07.
 */
public interface BaseContract {

    public static final String CONTENT_AUTHORITY = "com.hozakan.accountChecker";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

}
