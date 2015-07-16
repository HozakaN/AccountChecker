package com.hozakan.accountchecker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hozakan.accountchecker.data.entry.AccountEntry;
import com.hozakan.accountchecker.data.entry.DataClassEntry;
import com.hozakan.accountchecker.data.entry.DataClassToBreachEntry;
import com.hozakan.accountchecker.data.entry.BreachEntry;

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

            "CREATE TABLE " + BreachEntry.TABLE_NAME + " (" +
                    BreachEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                    BreachEntry.COLUMN_ACCOUNT_ID + " INTEGER NOT NULL, " +
                    BreachEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                    BreachEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                    BreachEntry.COLUMN_DOMAIN + " TEXT, " +
                    BreachEntry.COLUMN_BREACH_DATE + " TEXT NOT NULL, " +
//                    PwndEntry.COLUMN_ADDED_DATE + " INTEGER NOT NULL, " +
                    BreachEntry.COLUMN_ADDED_DATE + " TEXT NOT NULL, " +
                    BreachEntry.COLUMN_BREACH_COUNT + " INTEGER, " +
                    BreachEntry.COLUMN_DESCRIPTION + " TEXT, " +
                    BreachEntry.COLUMN_IS_VERIFIED + " INTEGER, " +
                    BreachEntry.COLUMN_IS_NEW + " INTEGER NOT NULL, " +

                    " FOREIGN KEY (" + BreachEntry.COLUMN_ACCOUNT_ID + ") REFERENCES " +
                    AccountEntry.TABLE_NAME + " (" + AccountEntry._ID + "));";

    private final String CREATE_DATACLASS_TO_PWND_ENTRY_SCRIPT =

            "CREATE TABLE " + DataClassToBreachEntry.TABLE_NAME + " (" +
                DataClassToBreachEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                DataClassToBreachEntry.COLUMN_BREACH_ID + " INTEGER NOT NULL, " +
                DataClassToBreachEntry.COLUMN_DATACLASS_ID + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + DataClassToBreachEntry.COLUMN_BREACH_ID + ") REFERENCES " +
                BreachEntry.TABLE_NAME + " (" + BreachEntry._ID + "), " +

                " FOREIGN KEY (" + DataClassToBreachEntry.COLUMN_DATACLASS_ID + ") REFERENCES " +
                DataClassEntry.TABLE_NAME + " (" + DataClassEntry._ID + "), " +

                " UNIQUE (" + DataClassToBreachEntry.COLUMN_BREACH_ID + ", " +
                DataClassToBreachEntry.COLUMN_DATACLASS_ID + ") ON CONFLICT IGNORE);"; // ON CONFLICT REPLACE);";

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
        db.execSQL("DROP TABLE IF EXISTS " + BreachEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataClassToBreachEntry.TABLE_NAME);
        onCreate(db);
    }
}
