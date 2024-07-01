package com.example.quizlearning.InterfaceAsyncTaskListener.CallAPI;

public interface OnCallAPIOpenAIListener {
    void onCallAPIOpenAISuccess(String result);
    void onCallAPIOpenAIFailure(Exception e);
}
