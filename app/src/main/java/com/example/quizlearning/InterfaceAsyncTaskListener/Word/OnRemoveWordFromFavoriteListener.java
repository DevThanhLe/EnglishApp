package com.example.quizlearning.InterfaceAsyncTaskListener.Word;

import com.example.quizlearning.model.WordModel;

public interface OnRemoveWordFromFavoriteListener {
    void onRemoveWordFromFavoriteSuccess(WordModel word);
    void onRemoveWordFromFavoriteFailure(Exception e);
}
