package com.example.bmi.mvi.view;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bmi.R;
import com.example.bmi.database.BmiDatabaseHelper;
import com.example.bmi.database.BmiRecord;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Activity to display BMI history with line chart
 */
public class BmiHistoryActivity extends AppCompatActivity {

    private LineChart lineChart;
    private TextView tvRecordCount;
    private TextView tvLatestBmi;
    private BmiDatabaseHelper dbHelper;
    private List<BmiRecord> bmiRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_history);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.bmi_history_title);
        }

        dbHelper = new BmiDatabaseHelper(this);

        initViews();
        loadBmiHistory();
    }

    private void initViews() {
        lineChart = findViewById(R.id.lineChart);
        tvRecordCount = findViewById(R.id.tv_record_count);
        tvLatestBmi = findViewById(R.id.tv_latest_bmi);
    }

    private void loadBmiHistory() {
        bmiRecords = dbHelper.getAllBmiRecords();

        if (bmiRecords.isEmpty()) {
            Toast.makeText(this, R.string.no_bmi_records, Toast.LENGTH_LONG).show();
            tvRecordCount.setText(getString(R.string.total_records) + ": 0");
            tvLatestBmi.setText(getString(R.string.latest_bmi) + ": N/A");
            return;
        }

        // Update statistics
        tvRecordCount.setText(getString(R.string.total_records) + ": " + bmiRecords.size());
        BmiRecord latestRecord = bmiRecords.get(bmiRecords.size() - 1);
        tvLatestBmi.setText(String.format(Locale.getDefault(),
                "%s: %.1f (%s)",
                getString(R.string.latest_bmi),
                latestRecord.getBmi(),
                latestRecord.getDate()));

        // Setup chart
        setupLineChart();
    }

    private void setupLineChart() {
        List<Entry> entries = new ArrayList<>();
        final List<String> dates = new ArrayList<>();

        for (int i = 0; i < bmiRecords.size(); i++) {
            BmiRecord record = bmiRecords.get(i);
            entries.add(new Entry(i, (float) record.getBmi()));

            // Format date for display (MM-dd)
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
                dates.add(outputFormat.format(inputFormat.parse(record.getDate())));
            } catch (Exception e) {
                dates.add(record.getDate());
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.bmi_trend));

        // Customize line appearance
        dataSet.setColor(Color.rgb(33, 150, 243)); // Blue color
        dataSet.setCircleColor(Color.rgb(33, 150, 243));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleRadius(2.5f);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.rgb(173, 216, 230)); // Light blue
        dataSet.setFillAlpha(50);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth curve
        dataSet.setDrawValues(true);

        // Add BMI reference lines colors
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setHighlightEnabled(true);
        dataSet.setHighLightColor(Color.RED);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Customize chart
        Description description = new Description();
        description.setText(getString(R.string.bmi_history_chart_desc));
        description.setTextSize(12f);
        lineChart.setDescription(description);

        // X-axis (dates)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < dates.size()) {
                    return dates.get(index);
                }
                return "";
            }
        });
        xAxis.setLabelRotationAngle(-45f); // Rotate labels for better readability

        // Left Y-axis (BMI values)
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextSize(10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(10f); // Minimum BMI value
        leftAxis.setAxisMaximum(35f); // Maximum BMI value

        // Add reference lines for BMI categories
        leftAxis.addLimitLine(createLimitLine(18.5f, getString(R.string.underweight_line), Color.BLUE));
        leftAxis.addLimitLine(createLimitLine(23f, getString(R.string.normal_line), Color.GREEN));
        leftAxis.addLimitLine(createLimitLine(25f, getString(R.string.overweight_line), Color.rgb(255, 165, 0))); // Orange
        leftAxis.setDrawLimitLinesBehindData(true);

        // Right Y-axis
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Chart interactions
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDoubleTapToZoomEnabled(true);

        // Animation
        lineChart.animateX(1000);

        // Legend
        lineChart.getLegend().setEnabled(true);
        lineChart.getLegend().setTextSize(12f);

        // Refresh chart
        lineChart.invalidate();
    }

    private com.github.mikephil.charting.components.LimitLine createLimitLine(float limit, String label, int color) {
        com.github.mikephil.charting.components.LimitLine limitLine =
                new com.github.mikephil.charting.components.LimitLine(limit, label);
        limitLine.setLineWidth(2f);
        limitLine.setLineColor(color);
        limitLine.setTextSize(10f);
        limitLine.setTextColor(color);
        return limitLine;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applySavedLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String languageCode = prefs.getString("Language", "en");

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}