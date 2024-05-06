package com.example.chatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ChatActivity;
import com.example.chatapp.R;
import com.example.chatapp.models.ChatroomModel;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    private final Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            UserModel otherUser = document.toObject(UserModel.class);
                            if (otherUser != null) {
                                displayUserDetails(holder, model, otherUser);
                            }
                        }
                    }
                });
    }

    private void displayUserDetails(@NonNull ChatroomModelViewHolder holder, @NonNull ChatroomModel model, @NonNull UserModel otherUser) {
        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId())
                .getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AndroidUtil.setProfilePic(context, task.getResult(), holder.profilePic);
                    }
                });

        String username = otherUser.getUserId().equals(FirebaseUtil.currentUserUid()) ?
                otherUser.getUsername() + " (You)" : otherUser.getUsername();
        holder.usernameText.setText(username);

        String lastMessage = model.getLastMessage();
        if ("image".equals(model.getLastMessageTypeName())) {
            lastMessage = "Image";
        } else if ("text".equals(model.getLastMessageTypeName()) && lastMessage.length() > 30) {
            lastMessage = lastMessage.substring(0, 30) + "...";
        }

        holder.lastMessageText.setText(model.getLastMessageSenderId().equals(FirebaseUtil.currentUserUid()) ?
                "You: " + lastMessage : lastMessage);

        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTime()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, otherUser);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
