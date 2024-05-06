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

import java.util.ArrayList;
import java.util.List;

public class SearchUserRecyclerAdapter extends RecyclerView.Adapter<SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;
    List<UserModel> userModelList;

    public SearchUserRecyclerAdapter(@NonNull List<UserModel> options, Context context) {
        this.context = context;
        this.userModelList = options; // Assign options to userModelList
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserModelViewHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        holder.usernameText.setText(userModel.getUsername());
        holder.phoneText.setText(userModel.getPhone());
        AndroidUtil.setProfilePic(context, Uri.parse(userModel.getAvatarUrl()), holder.profilePic);
        if (userModel.getId().equals(AndroidUtil.getCurrentUserModel(context).getId())) {
            holder.usernameText.setText(userModel.getUsername() + " (Me)");
        } else {
            holder.usernameText.setText(userModel.getUsername());
        }

        holder.itemView.setOnClickListener((v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, userModel);
            getChatroomId(userModel.getId(), AndroidUtil.getCurrentUserModel(context).getId(), intent);
        }));
    }

    @NonNull
    private void getChatroomId(String userId, String currentUserId, Intent intent) {
        DataStorageManager dataStorageManager = new DataStorageManager(context);
        String accessToken = dataStorageManager.getAccessToken();
        List<Conversation> conversations = dataStorageManager.getConversations();
        List<String> conversationIds = new ArrayList<>();
        if (conversations == null || conversations.size() == 0) {
            return;
        }
        for (Conversation conversation : conversations) {
            String conversationId = conversation.getId();
            ApiManager.getInstance(context).getConversations(accessToken, conversationId, new ApiManager.ApiListener() {
                @Override
                public void onResponse(JSONObject response) {
                    String conversationId = response.optString("id");
                    JSONArray usersArray = response.optJSONArray("users");

                    List<String> userIds = new ArrayList<>();
                    // Assuming otherUserId and currentUserId are already defined somewhere in your code
                    boolean foundOtherUserId = false;
                    boolean foundCurrentUserId = false;

                    // Extract user IDs from the users array and add them to the list
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject userObject = usersArray.optJSONObject(i);
                        String userId = userObject.optString("id");
                        userIds.add(userId);
                        if (userId.equals(userId)) {
                            foundOtherUserId = true;
                        } else if (userId.equals(currentUserId)) {
                            foundCurrentUserId = true;
                        }
                    }

                    // Check if both otherUserId and currentUserId are found in the list
                    if (foundOtherUserId && foundCurrentUserId) {
                        intent.putExtra("chatroomId", conversationId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userModelList.size(); // Return the size of userModelList
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
        notifyDataSetChanged();
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.user_name_phone);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
