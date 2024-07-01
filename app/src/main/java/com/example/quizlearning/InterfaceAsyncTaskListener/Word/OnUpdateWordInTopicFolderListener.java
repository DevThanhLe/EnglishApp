package com.example.quizlearning.InterfaceAsyncTaskListener.Word;

import com.example.quizlearning.model.WordModel;

public interface OnUpdateWordInTopicFolderListener {
    void onUpdateWordInTopicFolderSuccess(WordModel wordModel);
    void onUpdateWordInTopicFolderFailure(Exception e);
}
