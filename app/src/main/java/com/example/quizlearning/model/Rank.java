package com.example.quizlearning.model;

import java.util.Calendar;
import java.util.Date;

public class Rank {
    private int correctNum;
    private long timeFinish;
    private String userId;
    private Date currentTimestampQuiz;

    public Rank(int correctNum, long timeFinish, String userId, Date currentTimestampQuiz) {
        this.correctNum = correctNum;
        this.timeFinish = timeFinish;
        this.userId = userId;
        this.currentTimestampQuiz = currentTimestampQuiz;
    }
    public Rank(){
    }
    public int getCorrectNum() {
        return correctNum;
    }

    public void setCorrectNum(int correctNum) {
        this.correctNum = correctNum;
    }

    public long getTimeFinish() {
        return timeFinish;
    }

    public void setTimeFinish(long timeFinish) {
        this.timeFinish = timeFinish;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCurrentTimestampQuiz() {
        return currentTimestampQuiz;
    }

    public void setCurrentTimestampQuiz(Date currentTimestampQuiz) {
        this.currentTimestampQuiz = currentTimestampQuiz;
    }
}
