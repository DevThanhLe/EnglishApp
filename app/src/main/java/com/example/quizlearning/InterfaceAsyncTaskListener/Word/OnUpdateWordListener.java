package com.example.quizlearning.InterfaceAsyncTaskListener.Word;

import com.example.quizlearning.model.WordModel;

public interface OnUpdateWordListener {
    void onUpdateWordSuccess(WordModel wordModel);
    void onUpdateWordFailure(Exception e);
}
