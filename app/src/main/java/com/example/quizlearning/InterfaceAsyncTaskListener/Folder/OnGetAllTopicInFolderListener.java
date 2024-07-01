package com.example.quizlearning.InterfaceAsyncTaskListener.Folder;

import com.example.quizlearning.model.TopicModel;

import java.util.ArrayList;

public interface OnGetAllTopicInFolderListener {
    void onGetAllTopicInFolderSuccess(ArrayList<TopicModel> topics);
    void onGetAllTopicInFolderFailure(Exception e);
}
