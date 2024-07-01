package com.example.quizlearning.InterfaceAsyncTaskListener.Word;

import com.example.quizlearning.model.WordModel;

import java.util.ArrayList;

public interface OnGetAllWordInFolderListener {
    void onGetAllWordInFolderSuccess(ArrayList<WordModel> words);
    void onGetAllWordInFolderFailure(Exception e);
}
