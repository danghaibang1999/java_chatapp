package com.example.chatapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.chatapp.ChatActivity;
import com.example.chatapp.R;
import com.example.chatapp.manager.ApiManager;
import com.example.chatapp.models.Conversation;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.DataStorageManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RecentChatRecyclerAdapter extends RecyclerView.Adapter<RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;
    List<Conversation> conversationList;

    public RecentChatRecyclerAdapter(List<Conversation> conversations, Context context) {
        this.context = context;
        this.conversationList = conversations;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int i) {
        Conversation conversation = conversationList.get(i);
        getUserModelFromConversation(holder, conversation, AndroidUtil.getCurrentUserModel(context).getId());
    }

    void UpdateHolder(ChatroomModelViewHolder holder, Conversation conversation, UserModel otherUser) {
        if (otherUser == null) {
            return;
        }
        AndroidUtil.setProfilePic(context, Uri.parse(otherUser.getAvatarUrl()), holder.profilePic);
        holder.usernameText.setText(otherUser.getUsername());

//        holder.lastMessageText.setText(lastMessage);

        holder.lastMessageTime.setText(AndroidUtil.formatTime(conversation.getUpdatedAt()));

        holder.itemView.setOnClickListener((v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, otherUser);
            String chatroomId = conversation.getId();
            intent.putExtra("chatroomId", chatroomId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }));
    }

    private void getUserModelFromConversation(ChatroomModelViewHolder holder, Conversation conversation, String currentUserID) {
        List<UserModel> otherUser = new ArrayList<>();
        String accessToken = new DataStorageManager(context).getAccessToken();
        ApiManager.getInstance(context).getConversations(accessToken, conversation.getId(), new ApiManager.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray usersArray = response.optJSONArray("users");
                UserModel otherUserModel = null;

                if (usersArray != null) {
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject userObject = usersArray.optJSONObject(i);
                        if (userObject != null && !userObject.optString("id").equals(currentUserID)) {
                            String userId = userObject.optString("id");
                            otherUserModel = new UserModel();
                            // Populate the otherUserModel fields with the relevant data from the userObject
                            // Assuming you have appropriate methods in UserModel to set these values
                            otherUserModel.setUsername(userObject.optString("username"));
                            otherUserModel.setPhone(userObject.optString("phone"));
                            otherUserModel.setId(userId);
                            otherUserModel.setAvatarUrl(userObject.optString("avatar_url"));
                            otherUserModel.setRole(userObject.optString("role"));
                            otherUserModel.setStatus(userObject.optString("status"));
                            otherUser.add(otherUserModel);
                            // Set other fields as needed
                            break; // Break the loop once the other user is found
                        }
                    }
                }
                UpdateHolder(holder, conversation, otherUser.isEmpty() ? null : otherUser.get(0));
            }

            @Override
            public void onError(VolleyError error) {
                // Handle error
                Toast.makeText(context, new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setConversationList(List<Conversation> conversations) {
        this.conversationList = conversations;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addConversation(Conversation conversation) {
        conversationList.add(conversation);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        //        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
//            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
