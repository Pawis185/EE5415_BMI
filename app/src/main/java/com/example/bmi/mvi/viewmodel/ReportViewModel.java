package com.example.bmi.mvi.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bmi.mvi.api.LLMApiService;
import com.example.bmi.mvi.intent.ReportIntent;
import com.example.bmi.mvi.model.BmiModel;
import com.example.bmi.mvi.model.BmiResult;
import com.example.bmi.mvi.state.ReportViewState;

/**
 * MVI Architecture ViewModel - Handles Intent and updates ViewState
 * Enhanced with LLM API integration
 */
public class ReportViewModel extends AndroidViewModel {

    private BmiModel model;
    private LLMApiService llmApiService;
    private MutableLiveData<ReportViewState> viewStateLiveData = new MutableLiveData<>();
    private MutableLiveData<String> llmSuggestionLiveData = new MutableLiveData<>();
    private MutableLiveData<String> llmErrorLiveData = new MutableLiveData<>();

    public ReportViewModel(@NonNull Application application) {
        super(application);
        model = new BmiModel(application);
        llmApiService = new LLMApiService();

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

    // Cleanup resources
    public void cleanup() {
        if (llmApiService != null) {
            llmApiService.shutdown();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleanup();
    }
}