package com.example.quizlearning.InterfaceAsyncTaskListener.Folder;

import com.example.quizlearning.model.FolderModel;

import java.util.ArrayList;

public interface OnGetAllFolderByCurrentUserListener {
    void onGetAllFolderByCurrentUserSuccess(ArrayList<FolderModel> folders);
    void onGetAllFolderByCurrentUserFailure(Exception e);
}
