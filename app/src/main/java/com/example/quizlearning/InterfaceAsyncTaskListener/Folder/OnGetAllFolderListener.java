package com.example.quizlearning.InterfaceAsyncTaskListener.Folder;

import com.example.quizlearning.model.FolderModel;

import java.util.ArrayList;

public interface OnGetAllFolderListener {
    void onGetAllFolderSuccess(ArrayList<FolderModel> folders);
    void onGetAllFolderFailure(Exception e);
}
