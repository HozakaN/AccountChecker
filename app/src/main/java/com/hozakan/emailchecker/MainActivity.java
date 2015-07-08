package com.hozakan.emailchecker;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.hozakan.emailchecker.data.entry.AccountEntry;
import com.hozakan.emailchecker.data.contract.AccountContract;
import com.hozakan.emailchecker.ui.activity.AccountActivity;
import com.hozakan.emailchecker.ui.fragment.ListAccountsFragment;

public class MainActivity extends AppCompatActivity implements ListAccountsFragment.ListAccountsFragmentCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_account:
                menuAddAccountClicked();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onAccountClicked(long accountId) {
        startActivity(AccountActivity.createIntent(this, accountId));
    }

    private void menuAddAccountClicked() {
        ContentValues cv = new ContentValues();
        cv.put(AccountEntry.COLUMN_ACCOUNT_NAME, "hozakan_521@hotmail.com");
        String id = AccountContract.getIdFromUri(getContentResolver().insert(AccountContract.CONTENT_URI, cv));
    }
}
