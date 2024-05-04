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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", 0);
        String userId = sharedPreferences.getString("id", null);
        String accessToken = sharedPreferences.getString("accessToken", null);
        if (userId == null || accessToken == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        } else {
            ApiManager.getInstance(getContext()).getUser(userId, accessToken, new ApiManager.ApiListener() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray conversationsArray = null;
                    try {
                        conversationsArray = response.getJSONArray("conversations");
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
                        recentChatRecyclerAdapter.setConversationList(conversations);
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
}