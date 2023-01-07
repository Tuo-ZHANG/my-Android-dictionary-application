package com.tuo.mydictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String COLUMN_ENTRY = "ENTRY";
    public static final String ENTRY_TABLE = COLUMN_ENTRY + "_TABLE";
    public static final String COLUMN_QUERIED_TIMES = "QUERIED_TIMES";
    public static final String COLUMN_HAS_RECORD = "HAS_RECORD";
    public static final String COLUMN_ID = "ID";

//    public DatabaseHelper(@Nullable Context context) {
//        super(context, "entry.db", null, 1);
//    }

    public DatabaseHelper(@Nullable Context context) {
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + "sqlite-databases" + File.separator + "entry.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + ENTRY_TABLE + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ENTRY + " TEXT, " + COLUMN_QUERIED_TIMES
                + " INT, " + COLUMN_HAS_RECORD + " BOOL)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(EntryInformationModel entryInformationModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ENTRY, entryInformationModel.getEntry().toLowerCase(Locale.ROOT));
        cv.put(COLUMN_QUERIED_TIMES, entryInformationModel.getQueriedTimes());
        cv.put(COLUMN_HAS_RECORD, entryInformationModel.isHasRecord());
        long insert = db.insert(ENTRY_TABLE, null, cv);
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public int returnQuriedTimes(String record) {
        record = record.toLowerCase(Locale.ROOT);

        SQLiteDatabase db = this.getReadableDatabase();

        String queryString = "SELECT " + COLUMN_QUERIED_TIMES + " FROM " + ENTRY_TABLE + " WHERE "
                + COLUMN_ENTRY + "=?";
        Cursor cursor = db.rawQuery(queryString, new String[]{record});
        if (cursor.moveToFirst()) {
            int queriedTimes = cursor.getInt(0);
            cursor.close();
            return queriedTimes;
        } else {
            cursor.close();
            return 0;
        }
    }

    public boolean checkIfRecordExists(String record) {
        record = record.toLowerCase(Locale.ROOT);

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_ENTRY + " FROM " + ENTRY_TABLE + " WHERE "
                + COLUMN_ENTRY + "=?";
        Cursor cursor = db.rawQuery(queryString, new String[]{record});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean updateRecord(String record) {
        record = record.toLowerCase(Locale.ROOT);

        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "SELECT * FROM " + ENTRY_TABLE + " WHERE "
                + COLUMN_ENTRY + "=?";
        Cursor cursor = db.rawQuery(queryString, new String[]{record});
        int updatedQueriedTimes = 0;
        if (cursor.moveToFirst()) {
            updatedQueriedTimes = cursor.getInt(2) + 1;
        }
        cursor.close();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ENTRY, record);
        cv.put(COLUMN_QUERIED_TIMES, updatedQueriedTimes);
        cv.put(COLUMN_HAS_RECORD, true);
        long update = db.update(ENTRY_TABLE, cv, COLUMN_ENTRY + "=?", new String[]{record});
        if (update == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + ENTRY_TABLE);
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + ENTRY_TABLE + "'");
    }

    public void deleteLastRow() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + ENTRY_TABLE + " WHERE id = (SELECT MAX(id) FROM " + ENTRY_TABLE + ")");
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + ENTRY_TABLE + "'");

    }

    public String getLastQuery() {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_ENTRY + " FROM " + ENTRY_TABLE + " WHERE id = (SELECT MAX(id) FROM " + ENTRY_TABLE + ")";
        Cursor cursor = db.rawQuery(queryString, new String[]{});
        if (cursor.moveToFirst()) {
            String query = cursor.getString(0);
            cursor.close();
            return query;
        } else {
            cursor.close();
            return null;
        }
    }

    public boolean isEmpty() {
        SQLiteDatabase database = this.getReadableDatabase();
        long numberOfRows = DatabaseUtils.queryNumEntries(database, ENTRY_TABLE);
        return numberOfRows == 0;
    }
}
