package com.example.quizlearning.InterfaceAsyncTaskListener.CallAPI;

public interface OnRecognizeTextListener {
    void onRecognizeTextSuccess(String text);
    void onRecognizeTextFailure(Exception e);
}
