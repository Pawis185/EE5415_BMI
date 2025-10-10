package com.example.bmi.mvi.view;

import android.app.AlertDialog;
import android.content.Intent;
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

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initViews();
        setupListeners();
        observeViewState();
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

            // 发送Intent给ViewModel
            viewModel.processIntent(
                    new MainIntent.CalculateBmi(height, weight, age, gender)
            );
        });

        aboutBtn.setOnClickListener(v -> showAboutDialog());
    }

    private void observeViewState() {
        // 观察ViewState的变化
        viewModel.getViewState().observe(this, this::render);
    }

    // 根据ViewState渲染UI
    private void render(MainViewState state) {
        if (state == null) return;

        switch (state.getStatus()) {
            case IDLE:
                // 空闲状态 - 不做任何操作
                break;

            case SUCCESS:
                // 成功加载保存的数据
                if (state.getSavedData() != null) {
                    loadSavedData(state.getSavedData());
                }
                break;

            case ERROR:
                // 显示错误消息
                showError(state.getErrorMessage());
                break;

            case NAVIGATE:
                // 导航到结果页面
                if (state.getCalculatedData() != null) {
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