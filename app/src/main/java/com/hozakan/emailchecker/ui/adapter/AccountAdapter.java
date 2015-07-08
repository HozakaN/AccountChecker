package com.hozakan.emailchecker.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.hozakan.emailchecker.R;
import com.hozakan.emailchecker.data.contract.AccountContract;

/**
 * Created by gimbert on 15-07-08.
 */
public class AccountAdapter  extends CursorAdapter {

    private final LayoutInflater mInflater;
    private View mTmpView;
    private ViewHolder mTmpHolder;

    public AccountAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        mTmpView = mInflater.inflate(R.layout.list_item_account, parent, false);
        mTmpView.setTag(new ViewHolder(mTmpView));
        return mTmpView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mTmpHolder = (ViewHolder) view.getTag();
        mTmpHolder.accountName.setText(cursor.getString(AccountContract.COL_ACCOUNT_NAME));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    static class ViewHolder {

        public final TextView accountName;

        public ViewHolder(View itemView) {
            accountName = (TextView) itemView.findViewById(R.id.tv_account_name);
        }
    }
}
