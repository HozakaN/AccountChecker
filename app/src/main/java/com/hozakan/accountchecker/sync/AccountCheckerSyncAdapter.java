package com.hozakan.accountchecker.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Created by gimbert on 15-07-14.
 */
public class AccountCheckerSyncAdapter extends AbstractThreadedSyncAdapter {

    public AccountCheckerSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        doSync();
    }

    private void doSync() {

    }
}
