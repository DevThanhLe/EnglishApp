package com.example.quizlearning.InterfaceAsyncTaskListener.Topic;

import com.example.quizlearning.model.TopicModel;

import java.util.ArrayList;

public interface OnGetTopicByCurrentUserListener {
    void onGetTopicByCurrentUserSuccess(ArrayList<TopicModel> topicID);
    void onGetTopicByCurrentUserFailure(Exception e);
}
