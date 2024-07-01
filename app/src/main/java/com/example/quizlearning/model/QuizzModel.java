package com.example.quizlearning.model;

import java.io.Serializable;

public class QuizzModel implements Serializable {
    String question;
    String option1;
    String option2;
    String option3;
    String option4;
    String userSubmit;
    String correctAnswer;
    String wordId;

    public QuizzModel(String question, String option1, String option2, String option3, String option4, String userSubmit, String correctAnswer, String wordId) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.userSubmit = userSubmit;
        this.correctAnswer = correctAnswer;
        this.wordId = wordId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getUserSubmit() {
        return userSubmit;
    }

    public void setUserSubmit(String userSubmit) {
        this.userSubmit = userSubmit;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }
}
