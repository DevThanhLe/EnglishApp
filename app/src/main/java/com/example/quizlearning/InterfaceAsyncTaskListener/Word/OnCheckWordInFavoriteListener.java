package com.example.quizlearning.InterfaceAsyncTaskListener.Word;

import com.example.quizlearning.model.WordModel;

public interface OnCheckWordInFavoriteListener {
    void onHasWordInFavorite(WordModel word);
    void onNoWordInFavorite(Exception e);
}
