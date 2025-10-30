package com.example.bmi.mvi.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bmi.database.BmiDatabaseHelper;
import com.example.bmi.mvi.api.LLMApiService;
import com.example.bmi.mvi.intent.ReportIntent;
import com.example.bmi.mvi.model.BmiModel;
import com.example.bmi.mvi.model.BmiResult;
import com.example.bmi.mvi.state.ReportViewState;

/**
 * MVI Architecture ViewModel - Handles Intent and updates ViewState
 * Enhanced with LLM API integration and SQLite database storage
 */
public class ReportViewModel extends AndroidViewModel {

    private BmiModel model;
    private LLMApiService llmApiService;
    private BmiDatabaseHelper dbHelper;
    private MutableLiveData<ReportViewState> viewStateLiveData = new MutableLiveData<>();
    private MutableLiveData<String> llmSuggestionLiveData = new MutableLiveData<>();
    private MutableLiveData<String> llmErrorLiveData = new MutableLiveData<>();

    // 保存当前的BMI结果，用于横竖屏切换时恢复
    private BmiResult currentResult;
    private String currentHeight;
    private String currentWeight;
    private String currentAge;
    private String currentGender;

    public ReportViewModel(@NonNull Application application) {
        super(application);
        model = new BmiModel(application);
        llmApiService = new LLMApiService();
        dbHelper = new BmiDatabaseHelper(application);

        // Initialize to idle state
        viewStateLiveData.setValue(ReportViewState.idle());
    }

    // Get ViewState LiveData
    public LiveData<ReportViewState> getViewState() {
        return viewStateLiveData;
    }

    // Get LLM Suggestion LiveData
    public LiveData<String> getLlmSuggestion() {
        return llmSuggestionLiveData;
    }

    // Get LLM Error LiveData
    public LiveData<String> getLlmError() {
        return llmErrorLiveData;
    }

    // Process Intent
    public void processIntent(ReportIntent intent) {
        if (intent instanceof ReportIntent.CalculateResult) {
            handleCalculateResult((ReportIntent.CalculateResult) intent);
        }
    }

    // Handle calculate result
    private void handleCalculateResult(ReportIntent.CalculateResult intent) {
        try {
            // Set loading state
            viewStateLiveData.setValue(ReportViewState.loading());

            // Calculate BMI result
            BmiResult result = model.getBmiResult(
                    intent.height,
                    intent.weight,
                    intent.age,
                    intent.gender
            );

            // 保存当前结果和输入数据
            currentResult = result;
            currentHeight = intent.height;
            currentWeight = intent.weight;
            currentAge = intent.age;
            currentGender = intent.gender;

            // Save BMI record to SQLite database
            saveBmiToDatabase(result.bmiValue, intent.height, intent.weight,
                    intent.age, intent.gender);

            // Set success state
            viewStateLiveData.setValue(ReportViewState.success(result));

            // Get current language from SharedPreferences
            SharedPreferences prefs = getApplication().getSharedPreferences(
                    "Settings",
                    Application.MODE_PRIVATE
            );
            String language = prefs.getString("Language", "en");

            // Call LLM API for health suggestion
            int age = Integer.parseInt(intent.age);
            llmApiService.getHealthSuggestion(
                    result.bmiValue,
                    age,
                    intent.gender,
                    language,
                    new LLMApiService.LLMCallback() {
                        @Override
                        public void onSuccess(String suggestion) {
                            llmSuggestionLiveData.setValue(suggestion);
                        }

                        @Override
                        public void onError(String error) {
                            llmErrorLiveData.setValue(error);
                        }
                    }
            );

        } catch (Exception e) {
            // Set error state
            viewStateLiveData.setValue(
                    ReportViewState.error("Calculation error: " + e.getMessage())
            );
        }
    }

    /**
     * Save BMI record to SQLite database
     */
    private void saveBmiToDatabase(double bmi, String height, String weight,
                                   String age, String gender) {
        try {
            long result = dbHelper.insertOrUpdateBmiRecord(bmi, height, weight, age, gender);
            if (result == -1) {
                // Log error but don't interrupt the flow
                android.util.Log.e("ReportViewModel", "Failed to save BMI record to database");
            }
        } catch (Exception e) {
            android.util.Log.e("ReportViewModel", "Error saving BMI record", e);
        }
    }

    /**
     * 恢复BMI结果（用于横竖屏切换）
     */
    public void restoreResult() {
        if (currentResult != null) {
            viewStateLiveData.setValue(ReportViewState.success(currentResult));
        }
    }

    // Cleanup resources (不再立即关闭线程池)
    public void cleanup() {
        // 不在这里关闭llmApiService，避免横竖屏切换时出错
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // 只在ViewModel真正被清除时才关闭服务
        if (llmApiService != null) {
            llmApiService.forceShutdown();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}