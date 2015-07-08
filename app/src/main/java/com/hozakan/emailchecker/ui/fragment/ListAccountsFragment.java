package com.hozakan.emailchecker.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hozakan.emailchecker.R;
import com.hozakan.emailchecker.data.contract.AccountContract;
import com.hozakan.emailchecker.ui.adapter.AccountAdapter;

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AccountContract.CONTENT_URI,
                AccountContract.PROJECTION,
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
