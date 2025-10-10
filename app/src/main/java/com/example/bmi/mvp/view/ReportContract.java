package com.example.bmi.mvp.view;

import com.example.bmi.mvp.model.BmiModel;

public interface ReportContract {

    interface View {
        void displayResult(BmiModel.BmiResult result);
        void showError(String message);
    }

    interface Presenter {
        void onViewCreated(String height, String weight, String age, String gender);
    }
}