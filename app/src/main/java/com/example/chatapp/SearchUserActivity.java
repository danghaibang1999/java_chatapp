package com.example.chatapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.chatapp.adapter.SearchUserRecyclerAdapter;
import com.example.chatapp.manager.ApiManager;
import com.example.chatapp.models.Conversation;
import com.example.chatapp.models.Friend;
import com.example.chatapp.models.FriendRequest;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.UserListParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter searchUserRecyclerAdapter;

    public static List<UserModel> generateDummyUserData(int count) {
        List<UserModel> userList = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            UserModel user = new UserModel();
            user.setAvatarUrl("https://example.com/avatar_" + i + ".jpg");
            user.setCreatedAt("2024-05-06T12:00:00Z");
            user.setEmail("user" + i + "@example.com");
            user.setId("user_id_" + i);
            user.setLastLoggedIn("2024-05-06T12:00:00Z");
            user.setName("User " + i);
            user.setPhone("123456789" + i);
            user.setRole("user");
            user.setStatus("active");
            user.setUpdatedAt("2024-05-06T12:00:00Z");
            user.setUsername("username" + i);

            // Add conversations
            List<Conversation> conversations = new ArrayList<>();
            Conversation conversation = new Conversation();
            conversation.setCreatedAt("2024-05-06T12:00:00Z");
            conversation.setId("conversation_id_" + i);
            conversation.setName("Conversation " + i);
            conversation.setUpdatedAt("2024-05-06T12:00:00Z");
            conversations.add(conversation);
            user.setConversations(conversations);

            // Add friends
            List<Friend> friends = new ArrayList<>();
            Friend friend = new Friend();
            friend.setAvatarUrl("https://example.com/avatar_friend_" + i + ".jpg");
            friend.setEmail("friend" + i + "@example.com");
            friend.setId("friend_id_" + i);
            friend.setLastLoggedIn("2024-05-06T12:00:00Z");
            friend.setName("Friend " + i);
            friend.setPhone("987654321" + i);
            friend.setRole("friend");
            friend.setStatus("active");
            friend.setUsername("friend_username" + i);
            friends.add(friend);
            user.setFriends(friends);

            // Add friend requests
            List<FriendRequest> friendRequests = new ArrayList<>();
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setAvatarUrl("https://example.com/avatar_friend_request_" + i + ".jpg");
            friendRequest.setEmail("friend_request" + i + "@example.com");
            friendRequest.setId("friend_request_id_" + i);
            friendRequest.setLastLoggedIn("2024-05-06T12:00:00Z");
            friendRequest.setName("Friend Request " + i);
            friendRequest.setPhone("111222333" + i);
            friendRequest.setRole("friend_request");
            friendRequest.setStatus("pending");
            friendRequest.setUsername("friend_request_username" + i);
            friendRequests.add(friendRequest);
            user.setFriendRequests(friendRequests);

            userList.add(user);
        }

        return userList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.search_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.search_back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();

        backButton.setOnClickListener((v -> {
            onBackPressed();
        }));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Perform search operation when text is changed
                String searchTerm = s.toString();
                setupRecyclerView(searchTerm);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchButton.setOnClickListener((v -> {
            String searchTerm = searchInput.getText().toString().trim();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchInput.setError("Invalid Username");
                return;
            }
            setupRecyclerView(searchTerm);

        }));
    }

    void setupRecyclerView(String searchTerm) {
        searchUserRecyclerAdapter = new SearchUserRecyclerAdapter(new ArrayList<>(), getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchUserActivity.this));
        recyclerView.setAdapter(searchUserRecyclerAdapter);
        getUserList(searchTerm);
    }

    void getUserList(String searchTerm) {
        // Get the access token from your session management or wherever you store it
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("accessToken", null);

        // Call the getUserList method from ApiManager
        ApiManager.getInstance(this).getUserList(accessToken, 1, 20, "name", "asc", searchTerm, new ApiManager.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                // Handle the response here
                // Pass the response JSONObject to the adapter for display
                List<UserModel> userList = UserListParser.parseUserList(response.toString());

                searchUserRecyclerAdapter.setUserModelList(userList);
            }

            @Override
            public void onError(VolleyError error) {
                // Handle the error here
                Toast.makeText(SearchUserActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
