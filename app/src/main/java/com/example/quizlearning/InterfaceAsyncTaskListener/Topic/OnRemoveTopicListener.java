package com.example.quizlearning.InterfaceAsyncTaskListener.Topic;

public interface OnRemoveTopicListener {
    void onRemoveTopicSuccess();
    void onRemoveTopicFailure(Exception e);
}
