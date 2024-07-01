package com.example.quizlearning.auth;

import java.util.HashMap;

public class UserModel {
    private String userID;
    private String username;
    private String userEmail;
    private String userImageURL;

    public UserModel() {
    }

    public UserModel(String userID, String username, String userEmail, String userImageURL) {
        this.userID = userID;
        this.username = username;
        this.userEmail = userEmail;
        this.userImageURL = userImageURL;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userID);
        result.put("username", username);
        result.put("userEmail", userEmail);
        result.put("userImageURL", userImageURL);
        return result;
    }
}
