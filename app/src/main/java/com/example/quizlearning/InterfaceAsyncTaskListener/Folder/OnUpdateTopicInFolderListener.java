package com.example.quizlearning.InterfaceAsyncTaskListener.Folder;

import com.example.quizlearning.model.TopicModel;

public interface OnUpdateTopicInFolderListener {
    void onUpdateTopicInFolderSuccess(TopicModel res);
    void onUpdateTopicInFolderFailure(Exception e);
}
