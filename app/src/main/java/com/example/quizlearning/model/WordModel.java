package com.example.quizlearning.model;

import com.example.quizlearning.WORDSTATE;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WordModel {
    private String word;
    private String wordMean;
    private String wordImage;
    private String topicId;
    private String wordId;
    private WORDSTATE state;
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("word", word);
        map.put("wordMean", wordMean);
        map.put("wordImage", wordImage);
        map.put("topicId", topicId);
        map.put("wordId", wordId);
        map.put("state", state);
        return map;
    }

    public WordModel() {
    }

    public WordModel(String wordId) {
        this.wordId = wordId;
    }

    public WordModel(String word, String wordMean, String wordImage, String topicId, String wordId, WORDSTATE state) {
        this.word = word;
        this.wordMean = wordMean;

        this.wordImage = wordImage;
        this.topicId = topicId;
        this.wordId = wordId;
        this.state = state;
    }
    public WordModel(String word, String wordMean, String wordImage, String topicId, WORDSTATE state) {
        this.word = word;
        this.wordMean = wordMean;
        this.wordImage = wordImage;
        this.topicId = topicId;
        this.state = state;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordMean() {
        return wordMean;
    }

    public void setWordMean(String wordMean) {
        this.wordMean = wordMean;
    }

    public String getWordImage() {
        return wordImage;
    }

    public void setWordImage(String wordImage) {
        this.wordImage = wordImage;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public WORDSTATE getState() {
        return state;
    }

    public void setState(WORDSTATE state) {
        this.state = state;
    }

    public void setWordWithMap(Map<String, Object> word) {
        this.word = (String) word.get("word");
        this.wordImage = (String) word.get("wordImage");
        this.wordMean = (String) word.get("wordMean");
//        this.idWord = (String)  word.get("idWord");
//        this.timeCreate = (Date) topic.get("timeCreate");
    }
}
