package com.example.bmi.mvi.model;

// BmiData - 用于存储BMI输入数据
public class BmiData {
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