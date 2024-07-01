package com.example.quizlearning.InterfaceAsyncTaskListener.Topic.Community;

import com.example.quizlearning.model.TopicModel;

public interface OnCheckUserCloneTopicCommunity {
    void onCloneTopicAlready(TopicModel topic);
    void onHaveNotCloneTopic();
}
