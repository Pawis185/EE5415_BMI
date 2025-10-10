package com.example.bmi.mvvm.model;

import android.content.Context;
import android.content.SharedPreferences;

public class BmiModel {

    private Context context;

    public BmiModel(Context context) {
        this.context = context;
    }

    public static class BmiData {
        public String height;
        public String weight;
        public String age;
        public String gender;

        public BmiData(String height, String weight, String age, String gender) {
            this.height = height;
            this.weight = weight;
            this.age = age;
            this.gender = gender;
        }
    }

    public static class BmiResult {
        public double bmiValue;
        public String category;
        public int imageResource;
        public int adviceResource;
        public boolean isSevere;

        public BmiResult(double bmiValue, String category, int imageResource,
                         int adviceResource, boolean isSevere) {
            this.bmiValue = bmiValue;
            this.category = category;
            this.imageResource = imageResource;
            this.adviceResource = adviceResource;
            this.isSevere = isSevere;
        }
    }

    // 验证输入
    public boolean isValidInput(String height, String weight, String age, String gender) {
        return !height.isEmpty() && !weight.isEmpty() &&
                !age.isEmpty() && !gender.isEmpty();
    }

    // 验证年龄
    public boolean isValidAge(String age) {
        try {
            int ageNum = Integer.parseInt(age);
            return ageNum >= 6;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 计算BMI
    public double calculateBMI(String heightStr, String weightStr) {
        double height = Double.parseDouble(heightStr) / 100;
        double weight = Double.parseDouble(weightStr);
        return weight / (height * height);
    }

    // 获取BMI结果
    public BmiResult getBmiResult(String heightStr, String weightStr,
                                  String ageStr, String gender) {
        double bmi = calculateBMI(heightStr, weightStr);
        int age = Integer.parseInt(ageStr);

        // 四舍五入到一位小数
        bmi = Math.round(bmi * 10) / 10.0;

        if (age > 18) {
            return getAdultBmiResult(bmi);
        } else {
            return getChildBmiResult(bmi, age, gender);
        }
    }

    // 保存数据
    public void saveData(String height, String weight, String age, String gender) {
        SharedPreferences pref = context.getSharedPreferences("BMI_DATA", Context.MODE_PRIVATE);
        pref.edit()
                .putString("saved_height", height)
                .putString("saved_weight", weight)
                .putString("saved_age", age)
                .putString("saved_gender", gender)
                .apply();
    }

    // 加载数据
    public BmiData loadSavedData() {
        SharedPreferences pref = context.getSharedPreferences("BMI_DATA", Context.MODE_PRIVATE);
        String height = pref.getString("saved_height", "");
        String weight = pref.getString("saved_weight", "");
        String age = pref.getString("saved_age", "");
        String gender = pref.getString("saved_gender", "");

        if (height.isEmpty()) {
            return null;
        }

        return new BmiData(height, weight, age, gender);
    }

    // 成人BMI判断
    private BmiResult getAdultBmiResult(double bmi) {
        if (bmi >= 25) {
            return new BmiResult(bmi, "obese",
                    com.example.bmi.R.drawable.bot_fat,
                    com.example.bmi.R.string.advice_obese, false);
        } else if (bmi >= 23) {
            return new BmiResult(bmi, "overweight",
                    com.example.bmi.R.drawable.bot_fat,
                    com.example.bmi.R.string.advice_overweight, false);
        } else if (bmi >= 18.5) {
            return new BmiResult(bmi, "normal",
                    com.example.bmi.R.drawable.bot_fit,
                    com.example.bmi.R.string.advice_normal, false);
        } else {
            return new BmiResult(bmi, "underweight",
                    com.example.bmi.R.drawable.bot_thin,
                    com.example.bmi.R.string.advice_underweight, false);
        }
    }

    // 儿童BMI判断
    private BmiResult getChildBmiResult(double bmi, int age, String gender) {
        BmiRange range = getBmiRange(age, gender);

        if (range == null) {
            return new BmiResult(bmi, "out_of_range",
                    com.example.bmi.R.drawable.bot_fit,
                    com.example.bmi.R.string.advice_normal, false);
        }

        if (bmi <= range.severelyUnderweight) {
            return new BmiResult(bmi, "severely_underweight",
                    com.example.bmi.R.drawable.bot_thin,
                    com.example.bmi.R.string.advice_severe_underweight, true);
        } else if (bmi <= range.underweight) {
            return new BmiResult(bmi, "underweight",
                    com.example.bmi.R.drawable.bot_thin,
                    com.example.bmi.R.string.advice_underweight, false);
        } else if (bmi <= range.normal) {
            return new BmiResult(bmi, "normal",
                    com.example.bmi.R.drawable.bot_fit,
                    com.example.bmi.R.string.advice_normal, false);
        } else if (bmi <= range.overweight) {
            return new BmiResult(bmi, "overweight",
                    com.example.bmi.R.drawable.bot_fat,
                    com.example.bmi.R.string.advice_overweight, false);
        } else {
            return new BmiResult(bmi, "severely_overweight",
                    com.example.bmi.R.drawable.bot_fat,
                    com.example.bmi.R.string.advice_severe_overweight, true);
        }
    }

    private static class BmiRange {
        double severelyUnderweight;
        double underweight;
        double normal;
        double overweight;

        BmiRange(double su, double u, double n, double o) {
            this.severelyUnderweight = su;
            this.underweight = u;
            this.normal = n;
            this.overweight = o;
        }
    }

    private BmiRange getBmiRange(int age, String gender) {
        if (gender.equals("male")) {
            switch (age) {
                case 6: return new BmiRange(12.8, 13.1, 18.8, 21.4);
                case 7: return new BmiRange(13.0, 13.3, 19.8, 23.0);
                case 8: return new BmiRange(13.2, 13.6, 20.9, 24.6);
                case 9: return new BmiRange(13.5, 13.8, 21.8, 26.0);
                case 10: return new BmiRange(13.8, 14.1, 22.7, 27.3);
                case 11: return new BmiRange(14.1, 14.5, 23.6, 28.3);
                case 12: return new BmiRange(14.4, 14.8, 24.3, 29.2);
                case 13: return new BmiRange(14.7, 15.1, 25.0, 30.0);
                case 14: return new BmiRange(15.0, 15.4, 25.5, 30.6);
                case 15: return new BmiRange(15.3, 15.8, 26.1, 31.2);
                case 16: return new BmiRange(15.6, 16.1, 26.5, 31.7);
                case 17: return new BmiRange(15.9, 16.3, 27.0, 32.1);
                case 18: return new BmiRange(16.1, 16.6, 27.4, 32.4);
            }
        } else {
            switch (age) {
                case 6: return new BmiRange(12.6, 12.8, 18.3, 20.5);
                case 7: return new BmiRange(12.8, 13.1, 19.1, 21.8);
                case 8: return new BmiRange(13.1, 13.4, 20.1, 23.1);
                case 9: return new BmiRange(13.4, 13.7, 21.0, 24.4);
                case 10: return new BmiRange(13.7, 14.1, 21.9, 25.6);
                case 11: return new BmiRange(14.1, 14.4, 22.7, 26.6);
                case 12: return new BmiRange(14.4, 14.8, 23.4, 27.5);
                case 13: return new BmiRange(14.8, 15.2, 24.0, 28.3);
                case 14: return new BmiRange(15.1, 15.5, 24.6, 28.9);
                case 15: return new BmiRange(15.4, 15.8, 25.0, 29.4);
                case 16: return new BmiRange(15.7, 16.1, 25.4, 29.7);
                case 17: return new BmiRange(15.9, 16.3, 25.7, 30.0);
                case 18: return new BmiRange(16.1, 16.5, 25.9, 30.3);
            }
        }
        return null;
    }
}