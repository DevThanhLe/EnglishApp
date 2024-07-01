package com.example.quizlearning.InterfaceAsyncTaskListener.Word;

import com.example.quizlearning.model.WordModel;

import java.util.ArrayList;

public interface OnGetWordByTopicListener {
    void onGetWordByTopicSuccess(ArrayList<WordModel> words);
    void onGetWordByTopicFailure(Exception e);
}
