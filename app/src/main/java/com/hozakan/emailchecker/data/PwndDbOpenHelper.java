package com.hozakan.emailchecker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hozakan.emailchecker.data.entry.AccountEntry;
import com.hozakan.emailchecker.data.entry.DataClassEntry;
import com.hozakan.emailchecker.data.entry.DataClassToPwndEntry;
import com.hozakan.emailchecker.data.entry.PwndEntry;

/**
 * Created by gimbert on 15-07-07.
 */
public class PwndDbOpenHelper extends SQLiteOpenHelper {

    public static final String NAME = "PwndDb.db";
    public static final int VERSION = 1;

    private final String CREATE_ACCOUNT_ENTRY_SCRIPT =

            "CREATE TABLE " + AccountEntry.TABLE_NAME + " (" +
                    AccountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                    AccountEntry.COLUMN_ACCOUNT_NAME + " TEXT NOT NULL, " +

                    " UNIQUE (" + AccountEntry.COLUMN_ACCOUNT_NAME + ") ON CONFLICT IGNORE);";

    private final String CREATE_DATACLASS_ENTRY_SCRIPT =

            "CREATE TABLE " + DataClassEntry.TABLE_NAME + " (" +
                    DataClassEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                    DataClassEntry.COLUMN_NAME + " TEXT NOT NULL, " +

                    " UNIQUE (" + DataClassEntry.COLUMN_NAME + ") ON CONFLICT IGNORE);";

    private final String CREATE_PWND_ENTRY_SCRIPT =

            "CREATE TABLE " + PwndEntry.TABLE_NAME + " (" +
                    PwndEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                    PwndEntry.COLUMN_ACCOUNT_ID + " INTEGER NOT NULL, " +
                    PwndEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                    PwndEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                    PwndEntry.COLUMN_DOMAIN + " TEXT, " +
                    PwndEntry.COLUMN_BREACH_DATE + " TEXT NOT NULL, " +
//                    PwndEntry.COLUMN_BREACH_DATE + " INTEGER NOT NULL, " +
//                    PwndEntry.COLUMN_ADDED_DATE + " INTEGER NOT NULL, " +
                    PwndEntry.COLUMN_ADDED_DATE + " TEXT NOT NULL, " +
                    PwndEntry.COLUMN_PWND_COUNT + " INTEGER, " +
                    PwndEntry.COLUMN_DESCRIPTION + " TEXT, " +
                    PwndEntry.COLUMN_IS_VERIFIED + " INTEGER, " +

                    " FOREIGN KEY (" + PwndEntry.COLUMN_ACCOUNT_ID + ") REFERENCES " +
                    AccountEntry.TABLE_NAME + " (" + AccountEntry._ID + "));";

    private final String CREATE_DATACLASS_TO_PWND_ENTRY_SCRIPT =

            "CREATE TABLE " + DataClassToPwndEntry.TABLE_NAME + " (" +
                DataClassToPwndEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                DataClassToPwndEntry.COLUMN_PWND_ID + " INTEGER NOT NULL, " +
                DataClassToPwndEntry.COLUMN_DATACLASS_ID + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + DataClassToPwndEntry.COLUMN_PWND_ID + ") REFERENCES " +
                PwndEntry.TABLE_NAME + " (" + PwndEntry._ID + "), " +

                " FOREIGN KEY (" + DataClassToPwndEntry.COLUMN_DATACLASS_ID + ") REFERENCES " +
                DataClassEntry.TABLE_NAME + " (" + DataClassEntry._ID + "), " +

                " UNIQUE (" + DataClassToPwndEntry.COLUMN_PWND_ID + ", " +
                DataClassToPwndEntry.COLUMN_DATACLASS_ID + ") ON CONFLICT IGNORE);"; // ON CONFLICT REPLACE);";

    public PwndDbOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACCOUNT_ENTRY_SCRIPT);
        db.execSQL(CREATE_DATACLASS_ENTRY_SCRIPT);
        db.execSQL(CREATE_PWND_ENTRY_SCRIPT);
        db.execSQL(CREATE_DATACLASS_TO_PWND_ENTRY_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AccountEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataClassEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PwndEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataClassToPwndEntry.TABLE_NAME);
        onCreate(db);
    }
}
