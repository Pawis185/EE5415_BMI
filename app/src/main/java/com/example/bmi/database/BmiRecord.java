package com.example.bmi.database;

/**
 * Model class for BMI Record
 */
public class BmiRecord {
    private int id;
    private String date;
    private double bmi;
    private String height;
    private String weight;
    private String age;
    private String gender;
    private long timestamp;

    public BmiRecord() {
    }

    public BmiRecord(String date, double bmi, String height, String weight,
                     String age, String gender, long timestamp) {
        this.date = date;
        this.bmi = bmi;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.gender = gender;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "BmiRecord{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", bmi=" + bmi +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}