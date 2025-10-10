package com.example.bmi.mvi.intent;

// MVI架构中的Intent表示用户的意图/操作
public abstract class MainIntent {

    // 加载保存的数据
    public static class LoadSavedData extends MainIntent {}

    // 计算BMI
    public static class CalculateBmi extends MainIntent {
        public final String height;
        public final String weight;
        public final String age;
        public final String gender;

        public CalculateBmi(String height, String weight, String age, String gender) {
            this.height = height;
            this.weight = weight;
            this.age = age;
            this.gender = gender;
        }
    }

    // 重置表单
    public static class ResetForm extends MainIntent {}
}