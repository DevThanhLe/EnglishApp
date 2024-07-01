package com.example.quizlearning.InterfaceAsyncTaskListener.Topic;

import com.example.quizlearning.model.TopicModel;

import java.util.ArrayList;

public interface OnGetAllTopicListener {
    void onGetAllTopicSuccess(ArrayList<TopicModel> topics);
    void onGetAllTopicFailure(Exception e);
}
