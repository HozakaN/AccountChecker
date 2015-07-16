package com.hozakan.accountchecker.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by gimbert on 15-07-14.
 */
public class AccountCheckerAuthenticatorService extends Service {

    private AccountCheckerAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new AccountCheckerAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
