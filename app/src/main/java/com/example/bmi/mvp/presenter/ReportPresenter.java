package com.example.bmi.mvp.presenter;

import com.example.bmi.mvp.model.BmiModel;
import com.example.bmi.mvp.view.ReportContract;

public class ReportPresenter implements ReportContract.Presenter {

    private ReportContract.View view;
    private BmiModel model;

    public ReportPresenter(ReportContract.View view) {
        this.view = view;
        this.model = new BmiModel();
    }

    @Override
    public void onViewCreated(String height, String weight, String age, String gender) {
        try {
            // 使用Model计算BMI结果
            BmiModel.BmiResult result = model.getBmiResult(height, weight, age, gender);

            // 通知View显示结果
            view.displayResult(result);
        } catch (Exception e) {
            view.showError("Calculation error: " + e.getMessage());
        }
    }
}