package com.example.chatapp.models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;

    private List<String> listChatroomIds;

    public UserModel() {
    }

    public UserModel(String phone, String username, Timestamp createdTimestamp, String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public List<String> getChatroomIds() {
        if (listChatroomIds == null) {
            listChatroomIds = new ArrayList<>();
        }
        return listChatroomIds;
    }

    public void setListChatroomIds(List<String> chatroomIds) {
        this.listChatroomIds = chatroomIds;
    }

    public void insertChatroomId(String chatroomId) {
        getChatroomIds().add(chatroomId);
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
