package com.hozakan.emailchecker.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hozakan.emailchecker.MainActivity;
import com.hozakan.emailchecker.R;
import com.hozakan.emailchecker.tool.Constants;
import com.hozakan.emailchecker.ui.fragment.ListPwndsFragment;

/**
 * Created by gimbert on 15-07-08.
 */
public class AccountActivity extends AppCompatActivity {

    private static final String ACCOUNT_ID_EXTRA_KEY = "ACCOUNT_ID_EXTRA_KEY";

    public static Intent createIntent(Context context, long accountId) {
        Intent intent = new Intent(context, AccountActivity.class);
        intent.putExtra(ACCOUNT_ID_EXTRA_KEY, accountId);
        return intent;
    }

    //model attributes
    private long mAccountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountId = getIntent().getLongExtra(ACCOUNT_ID_EXTRA_KEY, Constants.INVALID_ACCOUNT_ID);

        if (mAccountId == Constants.INVALID_ACCOUNT_ID) {
            finish();
        }

        setContentView(R.layout.activity_account);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, ListPwndsFragment.newInstance(mAccountId))
                    .commit();
        }
    }
}
