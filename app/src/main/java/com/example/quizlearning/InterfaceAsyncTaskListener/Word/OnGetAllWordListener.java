package com.example.quizlearning.InterfaceAsyncTaskListener.Word;

import com.example.quizlearning.model.WordModel;

import java.util.ArrayList;

public interface OnGetAllWordListener {
    void onGetAllWordSuccess(ArrayList<WordModel> words);
    void onGetAllWordFailure(Exception e);
}
