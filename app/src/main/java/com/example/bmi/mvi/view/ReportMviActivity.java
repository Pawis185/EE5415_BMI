package com.example.bmi.mvi.view;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bmi.R;
import com.example.bmi.mvi.intent.ReportIntent;
import com.example.bmi.mvi.model.BmiResult;
import com.example.bmi.mvi.state.ReportViewState;
import com.example.bmi.mvi.viewmodel.ReportViewModel;

import java.text.DecimalFormat;
import java.util.Locale;

public class ReportMviActivity extends AppCompatActivity {

    private ImageView reportImage;
    private TextView reportResult;
    private TextView reportAdvice;
    private TextView llmSuggestion;
    private ProgressBar loadingProgress;

    private ReportViewModel viewModel;
    private String currentLanguage;

    // 标记是否已经处理过Intent（避免横竖屏切换重复计算）
    private boolean hasProcessedIntent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved locale before super.onCreate
        applySavedLocale();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Set title dynamically
        setTitle(R.string.bmi_report_mvi);

        // Get current language
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        currentLanguage = prefs.getString("Language", "en");

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        initViews();
        observeViewState();

        // 恢复状态或处理新的Intent
        if (savedInstanceState != null) {
            hasProcessedIntent = savedInstanceState.getBoolean("hasProcessedIntent", false);
            // 横竖屏切换时恢复结果
            if (hasProcessedIntent) {
                viewModel.restoreResult();
            }
        }

        // Get passed data (只在首次创建时处理)
        if (!hasProcessedIntent) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String height = bundle.getString("height");
                String weight = bundle.getString("weight");
                String age = bundle.getString("age");
                String gender = bundle.getString("gender");

                // Send Intent to ViewModel to calculate BMI
                viewModel.processIntent(
                        new ReportIntent.CalculateResult(height, weight, age, gender)
                );
                hasProcessedIntent = true;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存状态标记
        outState.putBoolean("hasProcessedIntent", hasProcessedIntent);
    }

    private void initViews() {
        reportImage = findViewById(R.id.report_image);
        reportResult = findViewById(R.id.report_result);
        reportAdvice = findViewById(R.id.report_advice);
        llmSuggestion = findViewById(R.id.llm_suggestion);
        loadingProgress = findViewById(R.id.loading_progress);
    }

    private void observeViewState() {
        // Observe ViewState changes
        viewModel.getViewState().observe(this, this::render);

        // Observe LLM suggestion
        viewModel.getLlmSuggestion().observe(this, suggestion -> {
            if (suggestion != null && !suggestion.isEmpty()) {
                llmSuggestion.setVisibility(View.VISIBLE);
                llmSuggestion.setText(suggestion);
                loadingProgress.setVisibility(View.GONE);
            }
        });

        // Observe LLM error
        viewModel.getLlmError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                loadingProgress.setVisibility(View.GONE);
                llmSuggestion.setVisibility(View.VISIBLE);
                llmSuggestion.setText(currentLanguage.equals("zh") ?
                        "无法获取AI建议，请稍后重试" :
                        "Unable to get AI suggestion, please try again later");
            }
        });
    }

    // Render UI based on ViewState
    private void render(ReportViewState state) {
        if (state == null) return;

        switch (state.getStatus()) {
            case IDLE:
                // Idle state - do nothing
                break;

            case LOADING:
                // Loading state - show progress
                loadingProgress.setVisibility(View.VISIBLE);
                break;

            case SUCCESS:
                // Success state - display result
                if (state.getResult() != null) {
                    displayResult(state.getResult());
                }
                break;

            case ERROR:
                // Error state - show error message
                showError(state.getErrorMessage());
                loadingProgress.setVisibility(View.GONE);
                break;
        }
    }

    private void displayResult(BmiResult result) {
        // Format BMI value
        DecimalFormat df = new DecimalFormat("0.0");
        String bmiValue = df.format(result.bmiValue);

        // Display BMI value
        reportResult.setText(getString(R.string.bmi_result) + " " + bmiValue);

        // Set image
        reportImage.setImageResource(result.imageResource);

        // Set advice
        reportAdvice.setText(result.adviceResource);

        // Add special style and animation for severe cases
        if (result.isSevere) {
            reportAdvice.setTextAppearance(this, R.style.SevereWarningText);
            reportAdvice.setBackgroundResource(R.drawable.severe_warning_bg);
            startBlinkAnimation(reportAdvice);
        }

        // Show loading for LLM suggestion
        loadingProgress.setVisibility(View.VISIBLE);
        llmSuggestion.setVisibility(View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Add blink animation
    private void startBlinkAnimation(TextView textView) {
        android.view.animation.Animation blink =
                new android.view.animation.AlphaAnimation(0.3f, 1.0f);
        blink.setDuration(500);
        blink.setRepeatMode(android.view.animation.Animation.REVERSE);
        blink.setRepeatCount(android.view.animation.Animation.INFINITE);
        textView.startAnimation(blink);
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
        // 只在Activity真正销毁时清理资源
        if (isFinishing()) {
            viewModel.cleanup();
        }
    }
}