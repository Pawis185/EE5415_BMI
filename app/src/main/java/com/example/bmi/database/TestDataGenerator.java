package com.example.bmi.database;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Utility class to generate test BMI data for demonstration
 * This helps create sample data spanning one month with at least 4 data points
 */
public class TestDataGenerator {

    private BmiDatabaseHelper dbHelper;
    private Context context;
    private Random random;

    public TestDataGenerator(Context context) {
        this.context = context;
        this.dbHelper = new BmiDatabaseHelper(context);
        this.random = new Random();
    }

    /**
     * Generate realistic BMI test data for the past 30 days
     * Creates entries every 7 days (4-5 data points)
     */
    public void generateMonthlyData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        // Base values for a person
        String height = "170"; // 170 cm
        String baseWeight = "65"; // 65 kg
        String age = "25";
        String gender = "male";

        // Generate data for the past 30 days, one entry per week
        for (int daysAgo = 28; daysAgo >= 0; daysAgo -= 7) {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -daysAgo);
            String date = sdf.format(calendar.getTime());

            // Simulate weight fluctuation (±3 kg)
            double weightVariation = (random.nextDouble() * 6) - 3;
            double weight = Double.parseDouble(baseWeight) + weightVariation;
            String weightStr = String.format(Locale.getDefault(), "%.1f", weight);

            // Calculate BMI
            double heightM = Double.parseDouble(height) / 100.0;
            double bmi = weight / (heightM * heightM);

            // Insert manually created test record
            insertTestRecord(date, bmi, height, weightStr, age, gender);
        }
    }

    /**
     * Generate BMI data showing weight loss trend
     * Useful for demonstrating positive progress
     */
    public void generateWeightLossTrend() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        String height = "165";
        String age = "30";
        String gender = "female";

        // Starting weight: 70kg, ending weight: 62kg
        double startWeight = 70.0;
        double endWeight = 62.0;
        double weightDiff = startWeight - endWeight;

        // Generate 8 data points over 8 weeks
        for (int week = 8; week >= 0; week--) {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -(week * 7));
            String date = sdf.format(calendar.getTime());

            // Progressive weight loss
            double progress = (8.0 - week) / 8.0;
            double currentWeight = startWeight - (weightDiff * progress);
            // Add small random variation
            currentWeight += (random.nextDouble() * 0.6 - 0.3);

            String weightStr = String.format(Locale.getDefault(), "%.1f", currentWeight);

            // Calculate BMI
            double heightM = Double.parseDouble(height) / 100.0;
            double bmi = currentWeight / (heightM * heightM);

            insertTestRecord(date, bmi, height, weightStr, age, gender);
        }
    }

    /**
     * Generate diverse BMI data showing various health scenarios
     */
    public void generateDiverseData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        String height = "175";
        String age = "28";
        String gender = "male";

        // Different weight scenarios
        double[] weights = {58.0, 65.0, 70.0, 75.0, 80.0, 85.0};

        for (int i = 0; i < weights.length; i++) {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -(30 - i * 6));
            String date = sdf.format(calendar.getTime());

            String weightStr = String.format(Locale.getDefault(), "%.1f", weights[i]);

            // Calculate BMI
            double heightM = Double.parseDouble(height) / 100.0;
            double bmi = weights[i] / (heightM * heightM);

            insertTestRecord(date, bmi, height, weightStr, age, gender);
        }
    }

    /**
     * Internal method to insert test record with custom date
     */
    private void insertTestRecord(String date, double bmi, String height,
                                  String weight, String age, String gender) {
        android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();

        values.put("date", date);
        values.put("bmi", bmi);
        values.put("height", height);
        values.put("weight", weight);
        values.put("age", age);
        values.put("gender", gender);
        values.put("timestamp", System.currentTimeMillis());

        db.insertWithOnConflict("bmi_records", null, values,
                android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    /**
     * Clear all test data
     */
    public void clearAllData() {
        dbHelper.deleteAllRecords();
    }

    /**
     * Get count of current records
     */
    public int getRecordCount() {
        return dbHelper.getRecordCount();
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}