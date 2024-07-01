package com.example.quizlearning.InterfaceAsyncTaskListener.Folder;

import com.example.quizlearning.model.FolderModel;

public interface OnAddFolderListener {
    void onAddFolderSuccess(FolderModel folderModel);
    void onAddFolderFailure(Exception e);
}
