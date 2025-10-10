package com.example.bmi.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.bmi.mvp.model.BmiModel;
import com.example.bmi.mvp.view.MainContract;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private BmiModel model;
    private Context context;

    public MainPresenter(MainContract.View view, Context context) {
        this.view = view;
        this.model = new BmiModel();
        this.context = context;
    }

    @Override
    public void onCalculateClicked(String height, String weight, String age, String gender) {
        // 验证输入
        if (!model.isValidInput(height, weight, age, gender)) {
            view.showError("Height/Weight/Age/Gender cannot be empty!");
            return;
        }

        // 验证年龄
        if (!model.isValidAge(age)) {
            view.showAgeError();
            return;
        }

        // 保存数据
        saveData(height, weight, age, gender);

        // 跳转到结果页
        BmiModel.BmiData data = new BmiModel.BmiData(height, weight, age, gender);
        view.navigateToReport(data);
    }

    @Override
    public void onViewCreated() {
        // 加载保存的数据
        BmiModel.BmiData savedData = loadSavedData();
        if (savedData != null) {
            view.loadSavedData(savedData);
        }
    }

    @Override
    public void saveData(String height, String weight, String age, String gender) {
        SharedPreferences pref = context.getSharedPreferences("BMI_DATA", Context.MODE_PRIVATE);
        pref.edit()
                .putString("saved_height", height)
                .putString("saved_weight", weight)
                .putString("saved_age", age)
                .putString("saved_gender", gender)
                .apply();
    }

    private BmiModel.BmiData loadSavedData() {
        SharedPreferences pref = context.getSharedPreferences("BMI_DATA", Context.MODE_PRIVATE);
        String height = pref.getString("saved_height", "");
        String weight = pref.getString("saved_weight", "");
        String age = pref.getString("saved_age", "");
        String gender = pref.getString("saved_gender", "");

        if (height.isEmpty()) {
            return null;
        }

        return new BmiModel.BmiData(height, weight, age, gender);
    }
}