package com.example.chatapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.chatapp.models.Conversation;
import com.example.chatapp.models.Friend;
import com.example.chatapp.models.FriendRequest;
import com.example.chatapp.models.UserModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class DataStorageManager {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_CONVERSATIONS = "conversations";
    private static final String KEY_FRIEND_REQUESTS = "friend_requests";
    private static final String KEY_FRIENDS = "friends";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public DataStorageManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Save Conversations
    public void saveConversations(List<Conversation> conversations) {
        String conversationsJson = gson.toJson(conversations);
        sharedPreferences.edit().putString(KEY_CONVERSATIONS, conversationsJson).apply();
    }

    // Get Conversations
    public List<Conversation> getConversations() {
        String conversationsJson = sharedPreferences.getString(KEY_CONVERSATIONS, "");
        Type type = new TypeToken<List<Conversation>>() {
        }.getType();
        return gson.fromJson(conversationsJson, type);
    }

    // Save Friend Requests
    public void saveFriendRequests(List<FriendRequest> friendRequests) {
        String friendRequestsJson = gson.toJson(friendRequests);
        sharedPreferences.edit().putString(KEY_FRIEND_REQUESTS, friendRequestsJson).apply();
    }

    // Get Friend Requests
    public List<FriendRequest> getFriendRequests() {
        String friendRequestsJson = sharedPreferences.getString(KEY_FRIEND_REQUESTS, "");
        Type type = new TypeToken<List<FriendRequest>>() {
        }.getType();
        return gson.fromJson(friendRequestsJson, type);
    }

    // Save Friends
    public void saveFriends(List<Friend> friends) {
        String friendsJson = gson.toJson(friends);
        sharedPreferences.edit().putString(KEY_FRIENDS, friendsJson).apply();
    }

    // Get Friends
    public List<Friend> getFriends() {
        String friendsJson = sharedPreferences.getString(KEY_FRIENDS, "");
        Type type = new TypeToken<List<Friend>>() {
        }.getType();
        return gson.fromJson(friendsJson, type);
    }

    public String getAccessToken() {
        return sharedPreferences.getString("accessToken", "");
    }

    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }

    public UserModel getCurrentUserModel() {
        UserModel userModel = new UserModel();
        userModel.setAvatarUrl(sharedPreferences.getString("avatar_url", ""));
        userModel.setCreatedAt(sharedPreferences.getString("createdAt", ""));
        userModel.setPhone(sharedPreferences.getString("phone", ""));
        userModel.setEmail(sharedPreferences.getString("email", ""));
        userModel.setId(sharedPreferences.getString("id", ""));
        userModel.setName(sharedPreferences.getString("name", ""));
        userModel.setLastLoggedIn(sharedPreferences.getString("last_logged_in", ""));
        userModel.setUsername(sharedPreferences.getString("username", ""));
        return userModel;
    }
}
