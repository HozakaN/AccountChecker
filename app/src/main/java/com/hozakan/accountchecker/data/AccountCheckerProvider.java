package com.hozakan.accountchecker.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.hozakan.accountchecker.data.contract.AccountContract;
import com.hozakan.accountchecker.data.contract.BaseContract;
import com.hozakan.accountchecker.data.contract.DataClassContract;
import com.hozakan.accountchecker.data.contract.DataClassToBreachContract;
import com.hozakan.accountchecker.data.contract.BreachContract;
import com.hozakan.accountchecker.data.entry.AccountEntry;
import com.hozakan.accountchecker.data.entry.DataClassEntry;
import com.hozakan.accountchecker.data.entry.DataClassToBreachEntry;
import com.hozakan.accountchecker.data.entry.BreachEntry;
import com.hozakan.accountchecker.tool.Constants;

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
    static final int BREACH_FOR_ACCOUNT_ID = 402;

    private static UriMatcher sUriMatcher;

    private static final SQLiteQueryBuilder sAccountWithBreachNumberQueryBuilder;

    static {
        sUriMatcher = buildUriMatcher();

        sAccountWithBreachNumberQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sAccountWithBreachNumberQueryBuilder.setTables(
                AccountEntry.TABLE_NAME + " LEFT JOIN " +
                        BreachEntry.TABLE_NAME +
                        " ON " + AccountEntry.TABLE_NAME +
                        "." + AccountEntry._ID +
                        " = " + BreachEntry.TABLE_NAME +
                        "." + BreachEntry.COLUMN_ACCOUNT_ID);
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
    public static final String sDataclassToBreachWithPwndIdSelection =
            DataClassToBreachEntry.TABLE_NAME+
                    "." + DataClassToBreachEntry.COLUMN_BREACH_ID + " = ? ";

    //dataclassToPwnd.pwndId = ? and dataclassToPwnd.dataclassId = ?
    public static final String sDataclassToPwndWithPwndIdAndDataclassIdSelection =
            DataClassToBreachEntry.TABLE_NAME+
                    "." + DataClassToBreachEntry.COLUMN_BREACH_ID + " = ? "+
                    " AND " + DataClassToBreachEntry.TABLE_NAME+
                    "." + DataClassToBreachEntry.COLUMN_DATACLASS_ID + " = ? ";

    //pwnd._id = ?
    public static final String sBreachWithIdSelection =
            BreachEntry.TABLE_NAME+
                    "." + BreachEntry._ID + " = ? ";

    //pwnd.accountId = ? and pwnd.name = ? and pwnd.title = ? and pwnd.breachDate = ? and pwnd.addedDate = ?
    public static final String sBreachForAccountIdNameTitleBreachDateAddedDateSelection =
            BreachEntry.TABLE_NAME+
                    "." + BreachEntry.COLUMN_ACCOUNT_ID + " = ? ";

    //pwnd.accountId = ?
    public static final String sBreachesForAccountIdSelection =
            BreachEntry.TABLE_NAME+
                    "." + BreachEntry.COLUMN_ACCOUNT_ID + " = ? ";

    private PwndDbOpenHelper mOpenHelper;
    private SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {
        mOpenHelper = new PwndDbOpenHelper(getContext());
        mDb = mOpenHelper.getWritableDatabase();
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
                retCursor = getBreachById(uri, projection, sortOrder);
                break;
            case BREACH_FOR_ACCOUNT_ID:
                retCursor = getBreachesForAccountId(uri, projection, sortOrder);
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

        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case ACCOUNT:
                long _id = checkAccountExistsFromName(values.getAsString(AccountEntry.COLUMN_ACCOUNT_NAME));
                if (_id != Constants.INVALID_ACCOUNT_ID) {
                    returnUri = AccountContract.buildAccountUri(_id);
                } else {
                    _id = mDb.insertOrThrow(AccountEntry.TABLE_NAME, null, values);
                    if (_id > 0) {
                        returnUri = AccountContract.buildAccountUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }
                break;
            case DATACLASS:
                _id = checkDataclassExistsFromName(mDb, values.getAsString(DataClassEntry.COLUMN_NAME));
                if (_id != Constants.INVALID_DATACLASS_ID) {
                    returnUri = DataClassContract.buildDataclassUri(_id);
                } else {
                    _id = mDb.insertOrThrow(DataClassEntry.TABLE_NAME, null, values);
                    if (_id > 0) {
                        returnUri = DataClassContract.buildDataclassUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }
                break;
            case DATACLASS_TO_PWND:
                _id = checkDataclassToPwndExists(
                        mDb,
                        values.getAsString(DataClassToBreachEntry.COLUMN_DATACLASS_ID),
                        values.getAsString(DataClassToBreachEntry.COLUMN_BREACH_ID));
                if (_id != Constants.INVALID_DATACLASS_TO_PWND_ID) {
                    returnUri = DataClassToBreachContract.buildDataclassToBreachUri(_id);
                } else {
                    _id = mDb.insertOrThrow(DataClassToBreachEntry.TABLE_NAME, null, values);
                    if (_id > 0) {
                        returnUri = DataClassToBreachContract.buildDataclassToBreachUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }
                break;
            case PWND:
                _id = checkBreachExists(
                        mDb,
                        values.getAsString(BreachEntry.COLUMN_ACCOUNT_ID),
                        values.getAsString(BreachEntry.COLUMN_NAME),
                        values.getAsString(BreachEntry.COLUMN_TITLE),
                        values.getAsString(BreachEntry.COLUMN_BREACH_DATE),
                        values.getAsString(BreachEntry.COLUMN_ADDED_DATE));
                if (_id != Constants.INVALID_BREACH_ID) {
                    returnUri = BreachContract.buildBreachUri(_id);
                } else {
                    _id = mDb.insertOrThrow(BreachEntry.TABLE_NAME, null, values);
                    if ( _id > 0 ) {
                        returnUri = BreachContract.buildBreachUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null, true);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int insertedRows = 0;
        switch (sUriMatcher.match(uri)) {
            case DATACLASS_TO_PWND:
                mDb.beginTransaction();
                long _id;
                for (ContentValues contentValues : values) {
                    _id = checkDataclassToPwndExists(
                            mDb,
                            contentValues.getAsString(DataClassToBreachEntry.COLUMN_DATACLASS_ID),
                            contentValues.getAsString(DataClassToBreachEntry.COLUMN_BREACH_ID));
                    if (_id == Constants.INVALID_DATACLASS_TO_PWND_ID) {
                        _id = mDb.insert(DataClassToBreachEntry.TABLE_NAME, null, contentValues);
                        if (_id > 0) {
                            insertedRows++;
                        }
                    }
                }
                mDb.endTransaction();
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return insertedRows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int affetedRows = -1;
        switch (sUriMatcher.match(uri)) {
            case PWND:
                affetedRows = mOpenHelper.getWritableDatabase()
                    .delete(
                            BreachEntry.TABLE_NAME,
                            selection,
                            selectionArgs
                    );
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null, true);
        return affetedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private Cursor getAccounts(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        Cursor cursor = mOpenHelper.getReadableDatabase().query(
//                AccountEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        return sAccountWithBreachNumberQueryBuilder.query(
                mDb,
                projection,
                selection,
                selectionArgs,
                AccountContract.ACCOUNT_GROUPBY,
                null,
                sortOrder
        );
//        return cursor;
    }

    private Cursor getAccount(Uri uri, String[] projection, String sortOrder) {
        String accountId = AccountContract.getIdFromUri(uri);

        return sAccountWithBreachNumberQueryBuilder.query(
                mDb,
                projection,
                sAccountWithIdSelection,
                new String[] {accountId},
                AccountContract.ACCOUNT_GROUPBY,
                null,
                sortOrder
        );
    }

    private Cursor getDataclasses(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = mDb.query(
                DataClassEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    private Cursor getPwnds(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = mDb.query(
                BreachEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    private Cursor getDataclassesToPwndByPwndId(Uri uri, String[] projection, String sortOrder) {
        String pwndId = DataClassToBreachContract.getBreachIdFromUri(uri);
        return mDb.query(
                DataClassToBreachEntry.TABLE_NAME,
                projection,
                sDataclassToBreachWithPwndIdSelection,
                new String[]{pwndId},
                null, null, sortOrder
        );
    }

    private Cursor getBreachById(Uri uri, String[] projection, String sortOrder) {
        String id = BreachContract.getIdFromUri(uri);
        return mDb.query(
                BreachEntry.TABLE_NAME,
                projection,
                sBreachWithIdSelection,
                new String[]{id},
                null, null, sortOrder

        );
    }

    private Cursor getBreachesForAccountId(Uri uri, String[] projection, String sortOrder) {
        String id = BreachContract.getAccountIdFromUri(uri);
        return mDb.query(
                BreachEntry.TABLE_NAME,
                projection,
                sBreachesForAccountIdSelection,
                new String[]{id},
                null, null, sortOrder

        );
    }

    private long checkAccountExistsFromName(String accountName) {
        Cursor cursor = mDb.query(
                AccountEntry.TABLE_NAME,
                new String[]{AccountEntry._ID},
                sAccountWithNameSelection,
                new String[]{accountName},
                null, null, null

        );
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return Constants.INVALID_ACCOUNT_ID;
    }

    private static long checkDataclassExistsFromName(SQLiteDatabase db, String name) {
        long dataclassId = Constants.INVALID_DATACLASS_ID;
        Cursor cursor = db.query(
                DataClassEntry.TABLE_NAME,
                new String[] {DataClassEntry._ID},
                sDataclassWithNameSelection,
                new String[] {name},
                null, null, null

        );
        if (cursor.moveToFirst()) {
            dataclassId = cursor.getLong(0);
        }
        cursor.close();
        return dataclassId;
    }

    private static long checkDataclassToPwndExists(SQLiteDatabase db, String dtaclassId, String pwndId) {
        long dataclassToPwndId = Constants.INVALID_DATACLASS_TO_PWND_ID;
        Cursor cursor = db.query(
                DataClassToBreachEntry.TABLE_NAME,
                new String[]{DataClassToBreachEntry._ID},
                sDataclassToPwndWithPwndIdAndDataclassIdSelection,
                new String[]{pwndId, dtaclassId},
                null, null, null

        );
        if (cursor.moveToFirst()) {
            dataclassToPwndId = cursor.getLong(0);
        }
        cursor.close();
        return dataclassToPwndId;
    }

    private static long checkBreachExists(
            SQLiteDatabase db,
            final String accountId,
            final String breachName,
            final String breachTitle,
            final String breachDate,
            final String addedDate) {
        long breachId = Constants.INVALID_BREACH_ID;
        Cursor cursor = db.query(
                BreachEntry.TABLE_NAME,
                new String[]{BreachEntry._ID},
                sBreachForAccountIdNameTitleBreachDateAddedDateSelection,
                new String[]{accountId, breachName, breachTitle, breachDate, addedDate},
                null, null, null

        );
        if (cursor.moveToFirst()) {
            breachId = cursor.getLong(0);
        }
        cursor.close();
        return breachId;
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(BaseContract.CONTENT_AUTHORITY, AccountContract.PATH_ACCOUNT, ACCOUNT);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, AccountContract.PATH_ACCOUNT + "/#", ACCOUNT_WITH_ID);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, DataClassContract.PATH_DATACLASS, DATACLASS);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, DataClassContract.PATH_DATACLASS + "/#", DATACLASS_WITH_ID);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, DataClassToBreachContract.PATH_DATACLASS_TO_BREACH_PATH, DATACLASS_TO_PWND);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, DataClassToBreachContract.PATH_DATACLASS_TO_BREACH_PATH + "/#", DATACLASS_TO_PWND_WITH_ID);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, DataClassToBreachContract.PATH_DATACLASS_TO_BREACH_PATH + "/" + BreachContract.PATH_BREACH + "/#", DATACLASS_TO_PWND_WITH_PWND_ID);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, BreachContract.PATH_BREACH, PWND);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, BreachContract.PATH_BREACH + "/#", PWND_WITH_ID);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, BreachContract.PATH_BREACH + "/" + AccountContract.PATH_ACCOUNT + "/#", BREACH_FOR_ACCOUNT_ID);

        return matcher;
    }
}
