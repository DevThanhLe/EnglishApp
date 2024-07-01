package com.example.quizlearning.InterfaceAsyncTaskListener.Folder;

import java.util.HashMap;

public interface OnUpdateFolderListener {
    void onUpdateFolderSuccess(HashMap<String, Object> updates);
    void onUpdateFolderFailure(Exception e);
}
