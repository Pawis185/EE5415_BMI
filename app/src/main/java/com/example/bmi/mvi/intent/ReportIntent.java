package com.example.bmi.mvi.intent;

// MVI架构中的Intent表示用户的意图/操作
public abstract class ReportIntent {

    // 计算BMI结果
    public static class CalculateResult extends ReportIntent {
        public final String height;
        public final String weight;
        public final String age;
        public final String gender;

        public CalculateResult(String height, String weight, String age, String gender) {
            this.height = height;
            this.weight = weight;
            this.age = age;
            this.gender = gender;
        }
    }
}