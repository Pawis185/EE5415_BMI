package com.example.bmi.mvi.state;

import com.example.bmi.mvi.model.BmiResult;

// MVI架构中的ViewState表示UI的状态
public class ReportViewState {

    public enum Status {
        IDLE,           // 空闲状态
        LOADING,        // 加载中
        SUCCESS,        // 成功
        ERROR           // 错误
    }

    private Status status;
    private String errorMessage;
    private BmiResult result;

    public ReportViewState() {
        this.status = Status.IDLE;
    }

    // Getters
    public Status getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public BmiResult getResult() {
        return result;
    }

    // 创建新的状态 - 成功
    public static ReportViewState success(BmiResult result) {
        ReportViewState state = new ReportViewState();
        state.status = Status.SUCCESS;
        state.result = result;
        return state;
    }

    // 创建新的状态 - 错误
    public static ReportViewState error(String message) {
        ReportViewState state = new ReportViewState();
        state.status = Status.ERROR;
        state.errorMessage = message;
        return state;
    }

    // 创建新的状态 - 空闲
    public static ReportViewState idle() {
        ReportViewState state = new ReportViewState();
        state.status = Status.IDLE;
        return state;
    }

    // 创建新的状态 - 加载中
    public static ReportViewState loading() {
        ReportViewState state = new ReportViewState();
        state.status = Status.LOADING;
        return state;
    }
}