package com.hozakan.emailchecker.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hozakan.emailchecker.CheckAccountTask;
import com.hozakan.emailchecker.R;
import com.hozakan.emailchecker.data.contract.PwndContract;
import com.hozakan.emailchecker.ui.adapter.PwndAdapter;

/**
 * Created by gimbert on 15-07-08.
 */
public class ListPwndsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ACCOUNT_ID_ARG_KEY = "ACCOUNT_ID_ARG_KEY";
    
    private static final int LIST_PWNDS_LOADER_ID = 0;

    public static ListPwndsFragment newInstance(long accountId) {
        ListPwndsFragment fragment = new ListPwndsFragment();
        Bundle args = new Bundle();
        args.putLong(ACCOUNT_ID_ARG_KEY, accountId);
        fragment.setArguments(args);
        return fragment;
    }

    //views
    private ListView mListView;

    //technical attributes
    private PwndAdapter mAdapter;

    //model attributes
    private long mAccountId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountId = getArguments().getLong(ACCOUNT_ID_ARG_KEY);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_list_pwnds, container, false);
        mListView = (ListView) rootView.findViewById(R.id.list);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new PwndAdapter(getActivity(), null);
        mListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(LIST_PWNDS_LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_pwnds, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_pwnds:
                menuRefreshClicked();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = PwndContract.buildPwndUriWithAccountId(mAccountId);
        return new CursorLoader(
                getActivity(),
                uri,
                PwndContract.PROJECTION,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void menuRefreshClicked() {
        new CheckAccountTask(getActivity()).execute("" + mAccountId);
    }
}
