package com.hozakan.accountchecker.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hozakan.accountchecker.R;
import com.hozakan.accountchecker.data.contract.AccountContract;
import com.hozakan.accountchecker.data.entry.AccountEntry;
import com.hozakan.accountchecker.ui.adapter.AccountAdapter;

/**
 * Created by gimbert on 15-07-08.
 */
public class ListAccountsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LIST_ACCOUNT_LOADER_ID = 0;

    //views
    private ListView mListView;

    //technical attributes
    private AccountAdapter mAdapter;
    private ListAccountsFragmentCallback mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (ListAccountsFragmentCallback) activity;
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_list_accounts, container, false);
        mListView = (ListView) rootView.findViewById(R.id.list);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new AccountAdapter(getActivity(), null);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallback != null) {
                    mCallback.onAccountClicked(id);
                }
            }
        });
        getLoaderManager().initLoader(LIST_ACCOUNT_LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list_accounts, menu);
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

    private void menuAddAccountClicked() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.action_add_account)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(
                        R.string.account_name_text,
                        0,
                        false,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {

                            }
                        }
                )
                .negativeText(android.R.string.cancel)
                .positiveText(R.string.create)
                .autoDismiss(false)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                String newAccountName = dialog.getInputEditText().getText().toString();
                                if (isAvailable(newAccountName)) {
                                    createAccount(newAccountName);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), R.string.account_name_unavailable, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .show();
    }

    private boolean isAvailable(String newAccountName) {
        final String selection = AccountEntry.TABLE_NAME+
                "." + AccountEntry.COLUMN_ACCOUNT_NAME+
                " = ?";
        Cursor cursor = getActivity().getContentResolver().query(
                AccountContract.CONTENT_URI,
                new String[] {AccountEntry._ID},
                selection,
                new String[] {newAccountName},
                null
        );

        boolean ret = !cursor.moveToFirst();
        cursor.close();
        return ret;
    }

    private void createAccount(String newAccountName) {
        ContentValues cv = new ContentValues();
        cv.put(AccountEntry.COLUMN_ACCOUNT_NAME, newAccountName);
        getActivity().getContentResolver().insert(AccountContract.CONTENT_URI, cv);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AccountContract.CONTENT_URI,
                AccountContract.ACCOUNT_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public interface ListAccountsFragmentCallback {
        void onAccountClicked(long accountId);
    }
}
