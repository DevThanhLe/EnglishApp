package com.example.quizlearning.InterfaceAsyncTaskListener.Topic;

import com.example.quizlearning.model.TopicModel;

public interface OnAddTopicListener {
    void onAddTopicSuccess(TopicModel topicModel);
    void onAddTopicFailure(Exception e);
}
