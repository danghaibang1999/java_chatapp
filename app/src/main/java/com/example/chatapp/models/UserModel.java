package com.example.chatapp.models;

import java.util.List;

public class UserModel {
    private String avatarUrl;
    private List<Conversation> conversations;
    private String createdAt;
    private String email;
    private List<FriendRequest> friendRequests;
    private List<Friend> friends;
    private String id;
    private String lastLoggedIn;
    private String name;
    private String phone;
    private String role;
    private String status;
    private String updatedAt;
    private String username;

    public UserModel() {
        // Required empty public constructor
    }

    public UserModel(String avatarUrl, List<Conversation> conversations,
                     String createdAt, String email, List<FriendRequest> friendRequests,
                     List<Friend> friends, String id, String lastLoggedIn, String name,
                     String phone, String role, String status, String updatedAt, String username) {
        this.avatarUrl = avatarUrl;
        this.conversations = conversations;
        this.createdAt = createdAt;
        this.email = email;
        this.friendRequests = friendRequests;
        this.friends = friends;
        this.id = id;
        this.lastLoggedIn = lastLoggedIn;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.updatedAt = updatedAt;
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<FriendRequest> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<FriendRequest> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(String lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
