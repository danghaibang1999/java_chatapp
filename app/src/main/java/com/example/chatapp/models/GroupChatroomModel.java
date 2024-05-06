package com.example.chatapp.models;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatroomModel {
    private String groupChatRoomId;
    private String groupName;
    private List<String> listUserIds;
    private Timestamp lastMessageTime;
    private String lastMessageSenderId;
    private String lastMessageSenderUsername;
    private String lastMessage;
    private String lastMessageTypeName;
    private Map<String, String> mapGroupUsernames;

    public GroupChatroomModel() {
        // Required empty public constructor
    }

    public GroupChatroomModel(String groupChatRoomId, String groupName, List<String> listUserIds, Timestamp lastMessageTime, String lastMessageSenderId, String lastMessageSenderUsername, String lastMessage, String lastMessageTypeName) {
        this.groupChatRoomId = groupChatRoomId;
        this.groupName = groupName;
        this.listUserIds = listUserIds;
        this.lastMessageTime = lastMessageTime;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessageSenderUsername = lastMessageSenderUsername;
        this.lastMessage = lastMessage;
        this.lastMessageTypeName = lastMessageTypeName;
    }

    public void addNewUserToGroup(String userId, String username) {
        if (mapGroupUsernames == null) {
            mapGroupUsernames = new HashMap<>();
        }
        mapGroupUsernames.put(userId, username);
    }

    public void removeUserFromGroup(String userId) {
        if (mapGroupUsernames != null) {
            mapGroupUsernames.remove(userId);
        }
    }

    public String getGroupChatRoomId() {
        return groupChatRoomId;
    }

    public void setGroupChatRoomId(String groupChatRoomId) {
        this.groupChatRoomId = groupChatRoomId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getListUserIds() {
        return listUserIds;
    }

    public void setListUserIds(List<String> listUserIds) {
        this.listUserIds = listUserIds;
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

    public String getLastMessageSenderUsername() {
        return lastMessageSenderUsername;
    }

    public void setLastMessageSenderUsername(String lastMessageSenderUsername) {
        this.lastMessageSenderUsername = lastMessageSenderUsername;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTypeName() {
        return lastMessageTypeName;
    }

    public void setLastMessageTypeName(String lastMessageTypeName) {
        this.lastMessageTypeName = lastMessageTypeName;
    }

    public Map<String, String> getMapGroupUsernames() {
        return mapGroupUsernames;
    }

    public void setMapGroupUsernames(Map<String, String> mapGroupUsernames) {
        this.mapGroupUsernames = mapGroupUsernames;
    }
}
