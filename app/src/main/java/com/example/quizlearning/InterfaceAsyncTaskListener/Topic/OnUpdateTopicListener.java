package com.example.quizlearning.InterfaceAsyncTaskListener.Topic;

import com.example.quizlearning.model.TopicModel;

public interface OnUpdateTopicListener {
    void onUpdateTopicSuccess(TopicModel topicModel);
    void onUpdateTopicFailure(Exception e);
}
