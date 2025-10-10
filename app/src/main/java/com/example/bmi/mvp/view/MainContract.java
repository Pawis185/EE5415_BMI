package com.example.bmi.mvp.view;

import com.example.bmi.mvp.model.BmiModel;

public interface MainContract {

    interface View {
        void showError(String message);
        void showAgeError();
        void navigateToReport(BmiModel.BmiData data);
        void loadSavedData(BmiModel.BmiData data);
    }

    interface Presenter {
        void onCalculateClicked(String height, String weight, String age, String gender);
        void onViewCreated();
        void saveData(String height, String weight, String age, String gender);
    }
}