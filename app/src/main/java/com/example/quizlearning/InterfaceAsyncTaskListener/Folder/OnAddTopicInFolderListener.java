package com.example.quizlearning.InterfaceAsyncTaskListener.Folder;

import com.example.quizlearning.model.TopicModel;

public interface OnAddTopicInFolderListener {
    void onAddTopicInFolderSuccess(TopicModel topicModel);
    void onAddTopicInFolderFailure(Exception e);
}
