package com.example.bmi.mvi.model;

// BmiResult - 用于存储BMI计算结果
public class BmiResult {
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