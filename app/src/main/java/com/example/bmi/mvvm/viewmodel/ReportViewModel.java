package com.example.bmi.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bmi.mvvm.model.BmiModel;

public class ReportViewModel extends AndroidViewModel {

    private BmiModel model;

    // LiveData for UI updates
    private MutableLiveData<BmiModel.BmiResult> bmiResultLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    public ReportViewModel(@NonNull Application application) {
        super(application);
        model = new BmiModel(application);
    }

    // Getters for LiveData
    public LiveData<BmiModel.BmiResult> getBmiResultLiveData() {
        return bmiResultLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    // 计算BMI
    public void calculateBmi(String height, String weight, String age, String gender) {
        try {
            BmiModel.BmiResult result = model.getBmiResult(height, weight, age, gender);
            bmiResultLiveData.setValue(result);
        } catch (Exception e) {
            errorMessageLiveData.setValue("Calculation error: " + e.getMessage());
        }
    }
}