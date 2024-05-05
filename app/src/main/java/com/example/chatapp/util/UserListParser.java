package com.example.chatapp.util;

import com.example.chatapp.models.Conversation;
import com.example.chatapp.models.Friend;
import com.example.chatapp.models.FriendRequest;
import com.example.chatapp.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserListParser {

    public static List<UserModel> parseUserList(String json) {
        List<UserModel> userList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userObject = jsonArray.getJSONObject(i);
                UserModel userModel = parseUserObject(userObject);
                userList.add(userModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userList;
    }

    private static UserModel parseUserObject(JSONObject userObject) throws JSONException {
        String avatarUrl = userObject.getString("avatar_url");
        // Parse conversations
        JSONArray conversationsArray = userObject.optJSONArray("conversations");
        List<Conversation> conversations = new ArrayList<>();
        if (conversationsArray != null) {
            for (int i = 0; i < conversationsArray.length(); i++) {
                JSONObject conversationObject = conversationsArray.getJSONObject(i);
                Conversation conversation = parseConversation(conversationObject);
                conversations.add(conversation);
            }
        }
        // Parse other attributes similarly
        // Extract other attributes from the userObject
        String createdAt = userObject.getString("created_at");
        String email = userObject.getString("email");
        // Parse friend requests
        JSONArray friendRequestsArray = userObject.optJSONArray("friend_requests");
        List<FriendRequest> friendRequests = new ArrayList<>();
        if (friendRequestsArray != null) {
            for (int i = 0; i < friendRequestsArray.length(); i++) {
                JSONObject friendRequestObject = friendRequestsArray.getJSONObject(i);
                FriendRequest friendRequest = parseFriendRequest(friendRequestObject);
                friendRequests.add(friendRequest);
            }
        }

        // Parse friends
        JSONArray friendsArray = userObject.optJSONArray("friends");
        List<Friend> friends = new ArrayList<>();
        if (friendsArray != null) {
            for (int i = 0; i < friendsArray.length(); i++) {
                JSONObject friendObject = friendsArray.getJSONObject(i);
                Friend friend = parseFriend(friendObject);
                friends.add(friend);
            }
        }

        // Parse the remaining attributes
        String id = userObject.getString("id");
        String lastLoggedIn = userObject.getString("last_logged_in");
        String name = userObject.getString("name");
        String phone = userObject.getString("phone");
        String role = userObject.getString("role");
        String status = userObject.getString("status");
        String updatedAt = userObject.getString("updated_at");
        String username = userObject.getString("username");
        // Create and return the UserModel object
        return new UserModel(avatarUrl, conversations, createdAt, email, friendRequests, friends, id, lastLoggedIn, name, phone, role, status, updatedAt, username);
    }

    private static Conversation parseConversation(JSONObject conversationObject) throws JSONException {
        String createdAt = conversationObject.getString("created_at");
        String id = conversationObject.getString("id");
        String name = conversationObject.getString("name");
        String updatedAt = conversationObject.getString("updated_at");
        return new Conversation(createdAt, id, name, updatedAt);
    }

    private static FriendRequest parseFriendRequest(JSONObject friendRequestObject) throws JSONException {
        // Parse friend request attributes and return a FriendRequest object
        return null;
    }

    private static Friend parseFriend(JSONObject friendObject) throws JSONException {
        // Parse friend attributes and return a Friend object
        return null;
    }
}
