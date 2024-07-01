package com.example.quizlearning.InterfaceAsyncTaskListener.Word;

import com.example.quizlearning.model.WordModel;

public interface OnAddWordListener {
    void onAddWordSuccess(WordModel wordModel);
    void onAddWordFailure(Exception e);
}
