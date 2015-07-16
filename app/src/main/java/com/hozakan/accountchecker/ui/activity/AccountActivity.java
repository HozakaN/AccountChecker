package com.hozakan.accountchecker.ui.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hozakan.accountchecker.R;
import com.hozakan.accountchecker.data.contract.AccountContract;
import com.hozakan.accountchecker.data.entry.AccountEntry;
import com.hozakan.accountchecker.tool.Constants;
import com.hozakan.accountchecker.ui.fragment.ListBreachesFragment;

/**
 * Created by gimbert on 15-07-08.
 */
public class AccountActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ACCOUNT_ID_EXTRA_KEY = "ACCOUNT_ID_EXTRA_KEY";
    private static final int ACCOUNT_LOADER_ID = 1;

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
                    .replace(R.id.container, ListBreachesFragment.newInstance(mAccountId))
                    .commit();
        }
        
        getLoaderManager().initLoader(ACCOUNT_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = AccountContract.buildAccountUri(mAccountId);
        return new CursorLoader(
                this,
                uri,
                new String[] {AccountEntry.COLUMN_ACCOUNT_NAME},
                null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            getSupportActionBar().setTitle(data.getString(0));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
