package com.example.quizlearning.InterfaceAsyncTaskListener.Word;


import com.example.quizlearning.model.WordModel;

public interface OnAddWordToFavoriteListener {
    void onAddWordToFavoriteSuccess(WordModel word);
    void onAddWordToFavoriteFailure(Exception e);
}
