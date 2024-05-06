package com.example.chatapp.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomModel {
    private String chatRoomId;
    private List<String> userIds;
    private Timestamp lastMessageTime;
    private String lastMessageSenderId;
    private String lastMessage;
    private String lastMessageTypeName;

    public ChatroomModel() {
        // Required empty public constructor
    }

    public ChatroomModel(String chatRoomId, List<String> userIds, Timestamp lastMessageTime, String lastMessageSenderId) {
        this.chatRoomId = chatRoomId;
        this.userIds = userIds;
        this.lastMessageTime = lastMessageTime;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessageTypeName() {
        return lastMessageTypeName;
    }

    public void setLastMessageTypeName(String lastMessageTypeName) {
        this.lastMessageTypeName = lastMessageTypeName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Timestamp lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }
}
