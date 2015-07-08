package com.hozakan.emailchecker.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.hozakan.emailchecker.data.contract.AccountContract;
import com.hozakan.emailchecker.data.contract.BaseContract;
import com.hozakan.emailchecker.data.contract.DataClassContract;
import com.hozakan.emailchecker.data.contract.DataClassToPwndContract;
import com.hozakan.emailchecker.data.contract.PwndContract;
import com.hozakan.emailchecker.data.entry.AccountEntry;
import com.hozakan.emailchecker.data.entry.DataClassEntry;
import com.hozakan.emailchecker.data.entry.DataClassToPwndEntry;
import com.hozakan.emailchecker.data.entry.PwndEntry;
import com.hozakan.emailchecker.tool.Constants;

/**
 * Created by gimbert on 15-07-08.
 */
public class AccountCheckerProvider extends ContentProvider {

    static final int ACCOUNT = 100;
    static final int ACCOUNT_WITH_ID = 101;
    static final int DATACLASS = 200;
    static final int DATACLASS_WITH_ID = 201;
    static final int DATACLASS_TO_PWND = 300;
    static final int DATACLASS_TO_PWND_WITH_ID = 301;
    static final int DATACLASS_TO_PWND_WITH_PWND_ID = 302;
    static final int PWND = 400;
    static final int PWND_WITH_ID = 401;
    static final int PWND_FOR_ACCOUNT_ID = 402;

    private static UriMatcher sUriMatcher;

    static {
        sUriMatcher = buildUriMatcher();
    }

    //account._id = ?
    public static final String sAccountWithIdSelection =
            AccountEntry.TABLE_NAME+
                    "." + AccountEntry._ID + " = ?";

    //account.accountName = ?
    public static final String sAccountWithNameSelection =
            AccountEntry.TABLE_NAME+
                    "." + AccountEntry.COLUMN_ACCOUNT_NAME + " = ?";

    //dataclass.name = ?
    public static final String sDataclassWithNameSelection =
            DataClassEntry.TABLE_NAME+
                    "." + DataClassEntry.COLUMN_NAME + " = ?";

    //dataclassToPwnd.pwndId = ?
    public static final String sDataclassToPwndWithPwndIdSelection =
            DataClassToPwndEntry.TABLE_NAME+
                    "." + DataClassToPwndEntry.COLUMN_PWND_ID + " = ? ";

    //dataclassToPwnd.pwndId = ? and dataclassToPwnd.dataclassId = ?
    public static final String sDataclassToPwndWithPwndIdAndDataclassIdSelection =
            DataClassToPwndEntry.TABLE_NAME+
                    "." + DataClassToPwndEntry.COLUMN_PWND_ID + " = ? "+
                    " AND " + DataClassToPwndEntry.TABLE_NAME+
                    "." + DataClassToPwndEntry.COLUMN_DATACLASS_ID + " = ? ";

    //pwnd._id = ?
    public static final String sPwndWithIdSelection =
            PwndEntry.TABLE_NAME+
                    "." + PwndEntry._ID + " = ? ";

    //pwnd.accountId = ?
    public static final String sPwndForAccountIdSelection =
            PwndEntry.TABLE_NAME+
                    "." + PwndEntry.COLUMN_ACCOUNT_ID + " = ? ";

    private PwndDbOpenHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new PwndDbOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case ACCOUNT:
                retCursor = getAccounts(projection, selection, selectionArgs, sortOrder);
                break;
            case ACCOUNT_WITH_ID:
                retCursor = getAccount(uri, projection, sortOrder);
                break;
            case DATACLASS:
                retCursor = getDataclasses(projection, selection, selectionArgs, sortOrder);
                break;
            case DATACLASS_TO_PWND_WITH_PWND_ID:
                retCursor = getDataclassesToPwndByPwndId(uri, projection, sortOrder);
                break;
            case PWND:
                retCursor = getPwnds(projection, selection, selectionArgs, sortOrder);
                break;
            case PWND_WITH_ID:
                retCursor = getPwndById(uri, projection, sortOrder);
                break;
            case PWND_FOR_ACCOUNT_ID:
                retCursor = getPwndForAccountId(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case ACCOUNT:
                long _id = checkAccountExistsFromName(values.getAsString(AccountEntry.COLUMN_ACCOUNT_NAME));
                if (_id != Constants.INVALID_ACCOUNT_ID) {
                    returnUri = AccountContract.buildAccountUri(_id);
                } else {
                    _id = db.insertOrThrow(AccountEntry.TABLE_NAME, null, values);
                    if (_id > 0) {
                        returnUri = AccountContract.buildAccountUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }
                break;
            case DATACLASS:
                _id = checkDataclassExistsFromName(values.getAsString(DataClassEntry.COLUMN_NAME));
                if (_id != Constants.INVALID_DATACLASS_ID) {
                    returnUri = DataClassContract.buildDataclassUri(_id);
                } else {
                    _id = db.insertOrThrow(DataClassEntry.TABLE_NAME, null, values);
                    if (_id > 0) {
                        returnUri = DataClassContract.buildDataclassUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }
                break;
            case DATACLASS_TO_PWND:
                _id = checkDataclassToPwndExists(
                        values.getAsString(DataClassToPwndEntry.COLUMN_DATACLASS_ID),
                        values.getAsString(DataClassToPwndEntry.COLUMN_PWND_ID));
                if (_id != Constants.INVALID_DATACLASS_TO_PWND_ID) {
                    returnUri = DataClassToPwndContract.buildDataclassToPwndUri(_id);
                } else {
                    _id = db.insertOrThrow(DataClassToPwndEntry.TABLE_NAME, null, values);
                    if (_id > 0) {
                        returnUri = DataClassToPwndContract.buildDataclassToPwndUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }
                break;
            case PWND:
                _id = db.insertOrThrow(PwndEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = PwndContract.buildPwndUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null, true);
        db.close();
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int insertedRows = 0;
        switch (sUriMatcher.match(uri)) {
            case DATACLASS_TO_PWND:
                db.beginTransaction();
                long _id;
                for (ContentValues contentValues : values) {
                    _id = checkDataclassToPwndExists(
                            contentValues.getAsString(DataClassToPwndEntry.COLUMN_DATACLASS_ID),
                            contentValues.getAsString(DataClassToPwndEntry.COLUMN_PWND_ID));
                    if (_id == Constants.INVALID_DATACLASS_TO_PWND_ID) {
                        _id = db.insert(DataClassToPwndEntry.TABLE_NAME, null, contentValues);
                        if (_id > 0) {
                            insertedRows++;
                        }
                    }
                }
                db.endTransaction();
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return insertedRows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int affetedRows = -1;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case PWND:
                affetedRows = mOpenHelper.getWritableDatabase()
                    .delete(
                            PwndEntry.TABLE_NAME,
                            selection,
                            selectionArgs
                    );
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null, true);
        db.close();
        return affetedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private Cursor getAccounts(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                AccountEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    private Cursor getAccount(Uri uri, String[] projection, String sortOrder) {
        String accountId = AccountContract.getIdFromUri(uri);
        return mOpenHelper.getReadableDatabase().query(
                AccountEntry.TABLE_NAME,
                projection,
                sAccountWithIdSelection,
                new String[] {accountId},
                null, null, sortOrder
        );
    }

    private Cursor getDataclasses(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                DataClassEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    private Cursor getPwnds(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                PwndEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    private Cursor getDataclassesToPwndByPwndId(Uri uri, String[] projection, String sortOrder) {
        String pwndId = DataClassToPwndContract.getPwndIdFromUri(uri);
        return mOpenHelper.getReadableDatabase().query(
                DataClassToPwndEntry.TABLE_NAME,
                projection,
                sDataclassToPwndWithPwndIdSelection,
                new String[] {pwndId},
                null, null, sortOrder
        );
    }

    private Cursor getPwndById(Uri uri, String[] projection, String sortOrder) {
        String id = PwndContract.getIdFromUri(uri);
        return mOpenHelper.getReadableDatabase().query(
                PwndEntry.TABLE_NAME,
                projection,
                sPwndWithIdSelection,
                new String[] {id},
                null, null, sortOrder

        );
    }

    private Cursor getPwndForAccountId(Uri uri, String[] projection, String sortOrder) {
        String id = PwndContract.getAccountIdFromUri(uri);
        return mOpenHelper.getReadableDatabase().query(
                PwndEntry.TABLE_NAME,
                projection,
                sPwndForAccountIdSelection,
                new String[] {id},
                null, null, sortOrder

        );
    }

    private long checkAccountExistsFromName(String accountName) {
        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                AccountEntry.TABLE_NAME,
                new String[] {AccountEntry._ID},
                sAccountWithNameSelection,
                new String[] {accountName},
                null, null, null

        );
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return Constants.INVALID_ACCOUNT_ID;
    }

    private long checkDataclassExistsFromName(String name) {
        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                DataClassEntry.TABLE_NAME,
                new String[] {DataClassEntry._ID},
                sDataclassWithNameSelection,
                new String[] {name},
                null, null, null

        );
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return Constants.INVALID_DATACLASS_ID;
    }

    private long checkDataclassToPwndExists(String dtaclassId, String pwndId) {
        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                DataClassToPwndEntry.TABLE_NAME,
                new String[] {DataClassToPwndEntry._ID},
                sDataclassToPwndWithPwndIdAndDataclassIdSelection,
                new String[] {pwndId, dtaclassId},
                null, null, null

        );
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return Constants.INVALID_DATACLASS_TO_PWND_ID;
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(BaseContract.CONTENT_AUTHORITY, AccountContract.PATH_ACCOUNT, ACCOUNT);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, AccountContract.PATH_ACCOUNT + "/#", ACCOUNT_WITH_ID);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, DataClassContract.PATH_DATACLASS, DATACLASS);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, DataClassContract.PATH_DATACLASS + "/#", DATACLASS_WITH_ID);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, DataClassToPwndContract.PATH_DATACLASS_TO_PWND_PATH, DATACLASS_TO_PWND);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, DataClassToPwndContract.PATH_DATACLASS_TO_PWND_PATH + "/#", DATACLASS_TO_PWND_WITH_ID);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, DataClassToPwndContract.PATH_DATACLASS_TO_PWND_PATH + "/" + PwndContract.PATH_PWND + "/#", DATACLASS_TO_PWND_WITH_PWND_ID);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, PwndContract.PATH_PWND, PWND);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, PwndContract.PATH_PWND + "/#", PWND_WITH_ID);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, PwndContract.PATH_PWND + "/" + AccountContract.PATH_ACCOUNT + "/#", PWND_FOR_ACCOUNT_ID);

        return matcher;
    }
}
