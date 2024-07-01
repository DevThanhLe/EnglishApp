package com.example.quizlearning.InterfaceAsyncTaskListener.User;

public interface OnAddUserListener {
    void onAddUserSuccess();
    void onAddUserFailure(Exception e);
}
