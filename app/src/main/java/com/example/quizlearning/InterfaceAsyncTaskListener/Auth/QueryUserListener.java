package com.example.quizlearning.InterfaceAsyncTaskListener.Auth;

import com.example.quizlearning.auth.UserModel;

public interface QueryUserListener {
    void onQueryUserSuccess(UserModel currentUser);
    void onQueryUserFailure(Exception e);
}
