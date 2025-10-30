package com.example.bmi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * SQLite Database Helper for storing BMI records
 */
public class BmiDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bmi_records.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_BMI = "bmi_records";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_BMI = "bmi";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public BmiDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_BMI + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE + " TEXT NOT NULL, "
                + COLUMN_BMI + " REAL NOT NULL, "
                + COLUMN_HEIGHT + " TEXT NOT NULL, "
                + COLUMN_WEIGHT + " TEXT NOT NULL, "
                + COLUMN_AGE + " TEXT NOT NULL, "
                + COLUMN_GENDER + " TEXT NOT NULL, "
                + COLUMN_TIMESTAMP + " INTEGER NOT NULL, "
                + "UNIQUE(" + COLUMN_DATE + ") ON CONFLICT REPLACE"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BMI);
        onCreate(db);
    }

    /**
     * Insert or update BMI record for today
     */
    public long insertOrUpdateBmiRecord(double bmi, String height, String weight,
                                        String age, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, today);
        values.put(COLUMN_BMI, bmi);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());

        // Insert or replace (will update if date exists)
        long result = db.insertWithOnConflict(TABLE_BMI, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return result;
    }

    /**
     * Get all BMI records ordered by date
     */
    public List<BmiRecord> getAllBmiRecords() {
        List<BmiRecord> records = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_BMI + " ORDER BY " + COLUMN_DATE + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BmiRecord record = new BmiRecord();
                record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                record.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                record.setBmi(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BMI)));
                record.setHeight(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)));
                record.setWeight(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)));
                record.setAge(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AGE)));
                record.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)));
                record.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));

                records.add(record);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return records;
    }

    /**
     * Get BMI records for the last N days
     */
    public List<BmiRecord> getRecentBmiRecords(int days) {
        List<BmiRecord> records = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_BMI
                + " ORDER BY " + COLUMN_DATE + " DESC LIMIT " + days;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BmiRecord record = new BmiRecord();
                record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                record.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                record.setBmi(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BMI)));
                record.setHeight(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)));
                record.setWeight(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)));
                record.setAge(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AGE)));
                record.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)));
                record.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));

                records.add(record);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // Reverse to get chronological order
        List<BmiRecord> reversed = new ArrayList<>();
        for (int i = records.size() - 1; i >= 0; i--) {
            reversed.add(records.get(i));
        }
        return reversed;
    }

    /**
     * Delete a BMI record by ID
     */
    public void deleteBmiRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BMI, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * Delete all BMI records
     */
    public void deleteAllRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BMI, null, null);
        db.close();
    }

    /**
     * Get total number of records
     */
    public int getRecordCount() {
        String countQuery = "SELECT * FROM " + TABLE_BMI;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
}