package com.example.bmi.mvp.view;

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

import com.example.bmi.R;
import com.example.bmi.mvp.model.BmiModel;
import com.example.bmi.mvp.presenter.MainPresenter;

import java.util.Locale;

public class MainMvpActivity extends AppCompatActivity implements MainContract.View {

    private EditText heightET;
    private EditText weightET;
    private EditText ageET;
    private RadioGroup genderRadioGroup;
    private RadioButton radioMale;
    private RadioButton radioFemale;
    private Button reportBtn;
    private Button aboutBtn;

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 在 super.onCreate 之前应用保存的语言设置
        applySavedLocale();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 动态设置标题
        setTitle(R.string.bmi_calculator_mvp);

        // 初始化Presenter
        presenter = new MainPresenter(this, this);

        initViews();
        setupListeners();

        // 通知Presenter视图已创建
        presenter.onViewCreated();
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

            presenter.onCalculateClicked(height, weight, age, gender);
        });

        aboutBtn.setOnClickListener(v -> showAboutDialog());
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAgeError() {
        Toast.makeText(this, "For minors, age must be between 6-18", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToReport(BmiModel.BmiData data) {
        Intent intent = new Intent(this, ReportMvpActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("height", data.height);
        bundle.putString("weight", data.weight);
        bundle.putString("age", data.age);
        bundle.putString("gender", data.gender);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void loadSavedData(BmiModel.BmiData data) {
        heightET.setText(data.height);
        weightET.setText(data.weight);
        ageET.setText(data.age);

        if (data.gender.equals("male")) {
            radioMale.setChecked(true);
        } else if (data.gender.equals("female")) {
            radioFemale.setChecked(true);
        }
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
            return true;
        } else if (id == R.id.menu_about) {
            Toast.makeText(this, "BMI Calculator v3.0 (MVP)", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setLocale(String lang) {
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