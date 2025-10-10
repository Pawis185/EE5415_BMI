package com.example.bmi.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bmi.mvvm.model.BmiModel;

public class MainViewModel extends AndroidViewModel {

    private BmiModel model;

    // LiveData for UI updates
    private MutableLiveData<BmiModel.BmiData> savedDataLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> navigateToReportLiveData = new MutableLiveData<>();
    private MutableLiveData<BmiModel.BmiData> currentDataLiveData = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        model = new BmiModel(application);
        loadSavedData();
    }

    // Getters for LiveData
    public LiveData<BmiModel.BmiData> getSavedDataLiveData() {
        return savedDataLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<Boolean> getNavigateToReportLiveData() {
        return navigateToReportLiveData;
    }

    public LiveData<BmiModel.BmiData> getCurrentDataLiveData() {
        return currentDataLiveData;
    }

    // 加载保存的数据
    private void loadSavedData() {
        BmiModel.BmiData data = model.loadSavedData();
        if (data != null) {
            savedDataLiveData.setValue(data);
        }
    }

    // 处理计算按钮点击
    public void onCalculateClicked(String height, String weight, String age, String gender) {
        // 验证输入
        if (!model.isValidInput(height, weight, age, gender)) {
            errorMessageLiveData.setValue("Height/Weight/Age/Gender cannot be empty!");
            return;
        }

        // 验证年龄
        if (!model.isValidAge(age)) {
            errorMessageLiveData.setValue("For minors, age must be between 6-18");
            return;
        }

        // 保存数据
        model.saveData(height, weight, age, gender);

        // 准备跳转数据
        BmiModel.BmiData data = new BmiModel.BmiData(height, weight, age, gender);
        currentDataLiveData.setValue(data);
        navigateToReportLiveData.setValue(true);
    }

    // 重置导航状态
    public void onNavigationComplete() {
        navigateToReportLiveData.setValue(false);
    }
}