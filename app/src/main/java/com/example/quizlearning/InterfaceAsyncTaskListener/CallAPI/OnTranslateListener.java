package com.example.quizlearning.InterfaceAsyncTaskListener.CallAPI;

public interface OnTranslateListener {
    void onTranslateSuccess(String result);
    void onTranslateFailure(Exception e);
}
