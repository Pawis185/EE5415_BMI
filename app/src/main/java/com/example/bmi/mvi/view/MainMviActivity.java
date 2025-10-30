package com.example.bmi.mvi.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bmi.R;
import com.example.bmi.mvi.intent.MainIntent;
import com.example.bmi.mvi.model.BmiData;
import com.example.bmi.mvi.state.MainViewState;
import com.example.bmi.mvi.viewmodel.MainViewModel;

import java.util.Locale;

public class MainMviActivity extends AppCompatActivity {

    private EditText heightET;
    private EditText weightET;
    private EditText ageET;
    private RadioGroup genderRadioGroup;
    private RadioButton radioMale;
    private RadioButton radioFemale;
    private Button reportBtn;
    private Button aboutBtn;
    private Button historyBtn; // New button for BMI history

    private MainViewModel viewModel;
    private boolean hasNavigated = false;
    private boolean isRestoringState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 在 super.onCreate 之前应用保存的语言设置
        applySavedLocale();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 动态设置标题（会根据当前语言自动选择对应的字符串资源）
        setTitle(R.string.bmi_calculator_mvi);

        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews();
        setupListeners();

        // 恢复导航状态
        if (savedInstanceState != null) {
            hasNavigated = savedInstanceState.getBoolean("hasNavigated", false);
            isRestoringState = true;
        }

        observeViewState();

        // 恢复状态完成
        isRestoringState = false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存导航状态
        outState.putBoolean("hasNavigated", hasNavigated);
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

    private void initViews() {
        heightET = findViewById(R.id.heightET);
        weightET = findViewById(R.id.weightET);
        ageET = findViewById(R.id.ageET);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        reportBtn = findViewById(R.id.reportBtn);
        aboutBtn = findViewById(R.id.aboutBtn);
        historyBtn = findViewById(R.id.historyBtn);

        registerForContextMenu(heightET);
        registerForContextMenu(weightET);
        registerForContextMenu(ageET);
    }

    private void setupListeners() {
        reportBtn.setOnClickListener(v -> {
            String height = heightET.getText().toString().trim();
            String weight = weightET.getText().toString().trim();
            String age = ageET.getText().toString().trim();

            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            String gender = "";
            if (selectedGenderId == R.id.radioMale) {
                gender = "male";
            } else if (selectedGenderId == R.id.radioFemale) {
                gender = "female";
            }

            // 发送Intent给ViewModel
            hasNavigated = false;
            viewModel.processIntent(
                    new MainIntent.CalculateBmi(height, weight, age, gender)
            );
        });

        aboutBtn.setOnClickListener(v -> showAboutDialog());

        // New: History button listener
        historyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainMviActivity.this, BmiHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewState() {
        // 观察ViewState的变化
        viewModel.getViewState().observe(this, this::render);
    }

    private void render(MainViewState state) {
        if (state == null) return;

        switch (state.getStatus()) {
            case IDLE:
                break;

            case SUCCESS:
                if (state.getSavedData() != null) {
                    loadSavedData(state.getSavedData());
                }
                break;

            case ERROR:
                showError(state.getErrorMessage());
                break;

            case NAVIGATE:
                // 只有在未导航过且不是恢复状态时才执行跳转
                if (!hasNavigated && !isRestoringState && state.getCalculatedData() != null) {
                    hasNavigated = true;
                    navigateToReport(state.getCalculatedData());
                }
                break;
        }
    }

    private void loadSavedData(BmiData data) {
        heightET.setText(data.height);
        weightET.setText(data.weight);
        ageET.setText(data.age);

        if (data.gender.equals("male")) {
            radioMale.setChecked(true);
        } else if (data.gender.equals("female")) {
            radioFemale.setChecked(true);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToReport(BmiData data) {
        Intent intent = new Intent(this, ReportMviActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("height", data.height);
        bundle.putString("weight", data.weight);
        bundle.putString("age", data.age);
        bundle.putString("gender", data.gender);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.about_bmi_title);
        builder.setMessage(R.string.about_bmi_message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_english) {
            setLocale("en");
            return true;
        } else if (id == R.id.menu_chinese) {
            setLocale("zh");
            return true;
        } else if (id == R.id.menu_reset) {
            heightET.setText("");
            weightET.setText("");
            ageET.setText("");
            genderRadioGroup.clearCheck();
            viewModel.processIntent(new MainIntent.ResetForm());
            return true;
        } else if (id == R.id.menu_about) {
            Toast.makeText(this, "BMI Calculator v3.0 (MVI)", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_history) {
            Intent intent = new Intent(this, BmiHistoryActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setLocale(String lang) {
        // 保存语言设置到 SharedPreferences
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        prefs.edit().putString("Language", lang).apply();

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        recreate();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.context_clear) {
            if (getCurrentFocus() instanceof EditText) {
                ((EditText) getCurrentFocus()).setText("");
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }
}