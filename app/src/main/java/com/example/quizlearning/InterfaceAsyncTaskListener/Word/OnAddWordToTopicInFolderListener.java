package com.example.quizlearning.InterfaceAsyncTaskListener.Word;

import com.example.quizlearning.model.WordModel;

public interface OnAddWordToTopicInFolderListener {
    void onAddWordToTopicInFolderSuccess(WordModel wordModel);
    void onAddWordToTopicInFolderFailure(Exception e);
}
