package com.example.quizlearning.InterfaceAsyncTaskListener.Auth;

public interface OnRecoveryListener {
    void onRecoverySuccess();
    void onRecoveryFailure(Exception e);
}
