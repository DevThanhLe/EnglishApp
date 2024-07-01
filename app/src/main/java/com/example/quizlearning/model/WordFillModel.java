package com.example.quizlearning.model;

import java.io.Serializable;

public class WordFillModel implements Serializable {
    private String meanWord;
    private String word;
    private String userSubmit;

    private String question;

    public WordFillModel(String meanWord, String word, String userSubmit, String question) {
        this.meanWord = meanWord;
        this.word = word;
        this.userSubmit = userSubmit;
        this.question = question;
    }

    public String getMeanWord() {
        return meanWord;
    }

    public void setMeanWord(String meanWord) {
        this.meanWord = meanWord;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getUserSubmit() {
        return userSubmit;
    }

    public void setUserSubmit(String userSubmit) {
        this.userSubmit = userSubmit;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
