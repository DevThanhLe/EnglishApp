package com.example.quizlearning.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FolderModel {
    String folderName;
    String folderId;
    String author;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("folderName", folderName);
        map.put("folderId", folderId);
        map.put("author", author);
        return map;
    }

    public FolderModel() {
    }

    public FolderModel(String folderName, String folderId, String author) {
        this.folderName = folderName;
        this.folderId = folderId;
        this.author = author;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setFolderWithMap(Map<String, Object> folder) {
        this.folderName = (String) folder.get("folderName");
        this.folderId = (String) folder.get("folderId");
        this.author = (String) folder.get("author");
    }
}
