package com.example.findhope.Models;

import com.google.firebase.database.ServerValue;

public class PostModel {

    private String postKey;
    private String name;
    private String picture; // image post
    private String status; // status missing people or found people
    private String description;
    private String nohp;
    private String email;
    private String userId;
    private String userPhoto;
    private Object timeStamp;

    // constructor
    public PostModel(String name, String picture, String status, String description, String nohp, String email, String userId, String userPhoto) {
        this.name = name;
        this.picture = picture;
        this.status = status;
        this.description = description;
        this.nohp = nohp;
        this.email = email;
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.timeStamp = ServerValue.TIMESTAMP; // otomatis get data waktu dan jam post upload
    }

    public PostModel() {
    }

    // getter and setter
    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
