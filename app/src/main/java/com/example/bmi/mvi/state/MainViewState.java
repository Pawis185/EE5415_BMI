package com.example.bmi.mvi.state;

import com.example.bmi.mvi.model.BmiData;

// MVI架构中的ViewState表示UI的状态
public class MainViewState {

    public enum Status {
        IDLE,           // 空闲状态
        LOADING,        // 加载中
        SUCCESS,        // 成功
        ERROR,          // 错误
        NAVIGATE        // 需要导航
    }

    private Status status;
    private String errorMessage;
    private BmiData savedData;
    private BmiData calculatedData;

    public MainViewState() {
        this.status = Status.IDLE;
    }

    // Getters
    public Status getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public BmiData getSavedData() {
        return savedData;
    }

    public BmiData getCalculatedData() {
        return calculatedData;
    }

    // 创建新的状态 - 加载保存的数据成功
    public static MainViewState loadSavedDataSuccess(BmiData data) {
        MainViewState state = new MainViewState();
        state.status = Status.SUCCESS;
        state.savedData = data;
        return state;
    }

    // 创建新的状态 - 计算成功，准备导航
    public static MainViewState calculateSuccess(BmiData data) {
        MainViewState state = new MainViewState();
        state.status = Status.NAVIGATE;
        state.calculatedData = data;
        return state;
    }

    // 创建新的状态 - 错误
    public static MainViewState error(String message) {
        MainViewState state = new MainViewState();
        state.status = Status.ERROR;
        state.errorMessage = message;
        return state;
    }

    // 创建新的状态 - 空闲
    public static MainViewState idle() {
        MainViewState state = new MainViewState();
        state.status = Status.IDLE;
        return state;
    }

    // 创建新的状态 - 加载中
    public static MainViewState loading() {
        MainViewState state = new MainViewState();
        state.status = Status.LOADING;
        return state;
    }
}