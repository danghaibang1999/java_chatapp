package com.example.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.models.Conversation;

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
//        ChatroomModel model = chatroomModels.get(i);
//        String otherUserId = model.getUserIds().get(0);
//
//        AndroidUtil.setProfilePic(context, t.getResult(), holder.profilePic);
//        if (otherUser.getUserId().equals(FirebaseUtil.currentUserUid())) {
//            holder.usernameText.setText(otherUser.getUsername() + " (You)");
//        } else {
//            holder.usernameText.setText(otherUser.getUsername());
//        }
//
//        String lastMessage = model.getLastMessage();
//
//        if (model.getLastMessageTypeName().equals("image")) {
//            lastMessage = "Image";
//        } else if (model.getLastMessageTypeName().equals("text")) {
//            if (lastMessage.length() > 30) {
//                lastMessage = lastMessage.substring(0, 30) + "...";
//            }
//        }
//
//        if (lastMessageSenderIdIsCurrentUser) {
//            holder.lastMessageText.setText("You: " + lastMessage);
//        } else {
//            holder.lastMessageText.setText(lastMessage);
//        }
//        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTime()));
//
//        holder.itemView.setOnClickListener((v -> {
//            Intent intent = new Intent(context, ChatActivity.class);
//            AndroidUtil.passUserModelAsIntent(intent, otherUser);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        }));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setConversationList(List<Conversation> conversations) {
        this.conversationList = conversations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecentChatRecyclerAdapter.ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
