package com.hozakan.accountchecker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hozakan.accountchecker.ui.activity.AccountActivity;
import com.hozakan.accountchecker.ui.fragment.ListAccountsFragment;

public class MainActivity extends AppCompatActivity implements ListAccountsFragment.ListAccountsFragmentCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onAccountClicked(long accountId) {
        startActivity(AccountActivity.createIntent(this, accountId));
    }
}
