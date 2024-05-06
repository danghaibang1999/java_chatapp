package com.example.chatapp.models;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    private String chatroomId;
    private String message;
    private String messageType;
    private String senderId;
    private Timestamp timestamp;

    public ChatMessageModel(String chatroomId, String message, String messageType, String senderId, Timestamp timestamp) {
        this.chatroomId = chatroomId;
        this.message = message;
        this.messageType = messageType;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public ChatMessageModel() {
        // Required empty public constructor
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
