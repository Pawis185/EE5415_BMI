package com.example.bmi.mvi.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bmi.mvi.intent.ReportIntent;
import com.example.bmi.mvi.model.BmiModel;
import com.example.bmi.mvi.model.BmiResult;
import com.example.bmi.mvi.state.ReportViewState;

// MVI架构的ViewModel - 处理Intent并更新ViewState
public class ReportViewModel extends AndroidViewModel {

    private BmiModel model;
    private MutableLiveData<ReportViewState> viewStateLiveData = new MutableLiveData<>();

    public ReportViewModel(@NonNull Application application) {
        super(application);
        model = new BmiModel(application);

        // 初始化为空闲状态
        viewStateLiveData.setValue(ReportViewState.idle());
    }

    // 获取ViewState的LiveData
    public LiveData<ReportViewState> getViewState() {
        return viewStateLiveData;
    }

    // 处理Intent
    public void processIntent(ReportIntent intent) {
        if (intent instanceof ReportIntent.CalculateResult) {
            handleCalculateResult((ReportIntent.CalculateResult) intent);
        }
    }

    // 处理计算结果
    private void handleCalculateResult(ReportIntent.CalculateResult intent) {
        try {
            // 设置加载状态
            viewStateLiveData.setValue(ReportViewState.loading());

            // 计算BMI结果
            BmiResult result = model.getBmiResult(
                    intent.height,
                    intent.weight,
                    intent.age,
                    intent.gender
            );

            // 设置成功状态
            viewStateLiveData.setValue(ReportViewState.success(result));
        } catch (Exception e) {
            // 设置错误状态
            viewStateLiveData.setValue(
                    ReportViewState.error("Calculation error: " + e.getMessage())
            );
        }
    }
}