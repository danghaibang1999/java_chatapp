package com.example.chatapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.chatapp.adapter.RecentChatRecyclerAdapter;
import com.example.chatapp.manager.ApiManager;
import com.example.chatapp.models.Conversation;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.DataStorageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    RecentChatRecyclerAdapter recentChatRecyclerAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_chat);
        setupRecyclerView();
        return view;
    }

    void setupRecyclerView() {
        recentChatRecyclerAdapter = new RecentChatRecyclerAdapter(new ArrayList<>(), getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recentChatRecyclerAdapter);
        fetchChatroomModelsAndUpdateList();
    }

    private void fetchChatroomModelsAndUpdateList() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", 0);
        String userId = AndroidUtil.getCurrentUserModel(requireContext()).getId();
        String accessToken = sharedPreferences.getString("accessToken", null);
        if (accessToken == null || accessToken.isEmpty()) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        } else {
            ApiManager.getInstance(getContext()).getUser(userId, accessToken, new ApiManager.ApiListener() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray conversationsArray = null;
                    try {
                        conversationsArray = response.optJSONArray("conversations");
                        if (conversationsArray == null) {
                            conversationsArray = new JSONArray();
                        }
                        List<Conversation> conversations = new ArrayList<>();
                        for (int i = 0; i < conversationsArray.length(); i++) {
                            JSONObject convObject = conversationsArray.getJSONObject(i);
                            Conversation conversation = new Conversation();
                            conversation.setId(convObject.getString("id"));
                            conversation.setName(convObject.getString("name"));
                            conversation.setCreatedAt(convObject.getString("created_at"));
                            conversation.setUpdatedAt(convObject.getString("updated_at"));
                            conversations.add(conversation);
                        }
                        filterConversations(conversations);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        }
    }

    private void filterConversations(List<Conversation> conversations) {
        String currentUserID = new DataStorageManager(requireContext()).getCurrentUserModel().getId();
        for (Conversation conversation : conversations) {
            List<UserModel> otherUser = new ArrayList<>();
            String accessToken = new DataStorageManager(requireContext()).getAccessToken();
            ApiManager.getInstance(requireContext()).getConversations(accessToken, conversation.getId(), new ApiManager.ApiListener() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray usersArray = response.optJSONArray("users");
                    UserModel otherUserModel = null;

                    if (usersArray != null) {
                        for (int i = 0; i < usersArray.length(); i++) {
                            JSONObject userObject = usersArray.optJSONObject(i);
                            if (userObject != null && !userObject.optString("id").equals(currentUserID)) {
                                recentChatRecyclerAdapter.addConversation(conversation);
                                break; // Break the loop once the other user is found
                            }
                        }
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    // Handle error
                    Toast.makeText(getContext(), new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}