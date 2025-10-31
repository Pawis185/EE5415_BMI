package com.example.bmi.mvi.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bmi.mvi.intent.MainIntent;
import com.example.bmi.mvi.model.BmiData;
import com.example.bmi.mvi.model.BmiModel;
import com.example.bmi.mvi.state.MainViewState;

// MVI架构的ViewModel - 处理Intent并更新ViewState
public class MainViewModel extends AndroidViewModel {

    private BmiModel model;
    private MutableLiveData<MainViewState> viewStateLiveData = new MutableLiveData<>();

    // 标记是否已经加载过数据
    private boolean hasLoadedData = false;

    public MainViewModel(@NonNull Application application) {
        super(application);
        model = new BmiModel(application);

        // 初始化为空闲状态
        viewStateLiveData.setValue(MainViewState.idle());

        // 自动加载保存的数据（只在首次创建时）
        if (!hasLoadedData) {
            processIntent(new MainIntent.LoadSavedData());
            hasLoadedData = true;
        }
    }

    // 获取ViewState的LiveData
    public LiveData<MainViewState> getViewState() {
        return viewStateLiveData;
    }

    // 处理Intent
    public void processIntent(MainIntent intent) {
        if (intent instanceof MainIntent.LoadSavedData) {
            handleLoadSavedData();
        } else if (intent instanceof MainIntent.CalculateBmi) {
            handleCalculateBmi((MainIntent.CalculateBmi) intent);
        } else if (intent instanceof MainIntent.ResetForm) {
            handleResetForm();
        }
    }

    // 处理加载保存的数据
    private void handleLoadSavedData() {
        BmiData savedData = model.loadSavedData();
        if (savedData != null) {
            viewStateLiveData.setValue(MainViewState.loadSavedDataSuccess(savedData));
        } else {
            viewStateLiveData.setValue(MainViewState.idle());
        }
    }

    // 处理计算BMI
    private void handleCalculateBmi(MainIntent.CalculateBmi intent) {
        // 验证输入
        if (!model.isValidInput(intent.height, intent.weight, intent.age, intent.gender)) {
            viewStateLiveData.setValue(
                    MainViewState.error("Height/Weight/Age/Gender cannot be empty!")
            );
            return;
        }

        // 验证年龄
        if (!model.isValidAge(intent.age)) {
            viewStateLiveData.setValue(
                    MainViewState.error("For minors, age must be between 6-18")
            );
            return;
        }

        // 保存数据
        model.saveData(intent.height, intent.weight, intent.age, intent.gender);

        // 准备跳转数据
        BmiData data = new BmiData(intent.height, intent.weight, intent.age, intent.gender);
        viewStateLiveData.setValue(MainViewState.calculateSuccess(data));
    }

    // 处理重置表单
    private void handleResetForm() {
        viewStateLiveData.setValue(MainViewState.idle());
    }
}