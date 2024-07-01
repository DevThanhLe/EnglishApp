package com.example.quizlearning.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TopicModel implements Serializable {
    private String topicTitle;
    private String topicDescription;
    private Boolean topicShare;
    private String topicId;
    private String topicAuth;
    private String username;
    private Date timeCreate;
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("topicTitle", topicTitle);
        map.put("topicDescription", topicDescription);
        map.put("topicShare", topicShare);
        map.put("topicId", topicId);
        map.put("topicAuth", topicAuth);
        map.put("username", username);
        map.put("timeCreate", timeCreate);
        return map;
    }

    public TopicModel( String topicId, String topicTitle, String topicDescription, Boolean topicShare, String topicAuth, Date timeCreate) {
        this.topicTitle = topicTitle;
        this.topicDescription = topicDescription;
        this.topicShare = topicShare;
        this.topicId = topicId;
        this.topicAuth = topicAuth;
        this.timeCreate = timeCreate;
    }
    public TopicModel(String topicTitle, String topicDescription, Boolean topicShare, String topicAuth, Date timeCreate) {
        this.topicTitle = topicTitle;
        this.topicDescription = topicDescription;
        this.topicShare = topicShare;
        this.topicAuth = topicAuth;
        this.timeCreate = timeCreate;
    }
    public TopicModel(String topicTitle, String topicDescription, Boolean topicShare, String topicAuth, String authName, Date timeCreate) {
        this.topicTitle = topicTitle;
        this.topicDescription = topicDescription;
        this.topicShare = topicShare;
        this.topicAuth = topicAuth;
        this.username = authName;
        this.timeCreate = timeCreate;
    }

    public TopicModel(String topicTitle, String topicDescription, Boolean topicShare, String topicAuth, String authName) {
        this.topicTitle = topicTitle;
        this.topicDescription = topicDescription;
        this.topicShare = topicShare;
        this.topicAuth = topicAuth;
    }

    public void setTopicWithMap(Map<String, Object> topic) {
        this.topicTitle = (String) topic.get("topicTitle");
        this.topicDescription = (String) topic.get("topicDescription");;
        this.topicShare = (Boolean) topic.get("topicShare");
        this.topicAuth = (String) topic.get("topicAuth");
        this.username = (String)  topic.get("username");
        this.timeCreate = (Date) topic.get("timeCreate");
    }

    public TopicModel(){
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTopicDescription() {
        return topicDescription;
    }

    public void setTopicDescription(String topicDescription) {
        this.topicDescription = topicDescription;
    }

    public Boolean getTopicShare() {
        return topicShare;
    }

    public void setTopicShare(Boolean topicShare) {
        this.topicShare = topicShare;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicAuth() {
        return topicAuth;
    }

    public void setTopicAuth(String topicAuth) {
        this.topicAuth = topicAuth;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }


}
