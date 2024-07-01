package com.example.quizlearning.InterfaceAsyncTaskListener.Auth;

import com.example.quizlearning.auth.UserModel;

public interface OnLoginListener {
    void onLoginSuccess(UserModel currentUser);
    void onLoginFailure(Exception e);
}
