package com.example.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.models.ChatMessageModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {
    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        // Check if the message is sent by the current user
        if (model.getSenderId().equals(FirebaseUtil.currentUserUid())) {
            // Hide views for messages sent by the current user
            holder.textLeftChatLayout.setVisibility(View.GONE);
            holder.imageLeftChatLayout.setVisibility(View.GONE);
            holder.leftChatTextview.setVisibility(View.GONE);
            holder.leftChatImageView.setVisibility(View.GONE);

            // Determine message type and display appropriate views
            if (model.getMessageType().equals("image")) {
                holder.imageRightChatLayout.setVisibility(View.VISIBLE);
                holder.rightChatImageView.setVisibility(View.VISIBLE);
                holder.textRightChatLayout.setVisibility(View.GONE);
                holder.rightChatTextview.setVisibility(View.GONE);

                // Load and display image from Firebase storage
                FirebaseUtil.getChatroomImageStorageRef(model.getChatroomId(), model.getMessage())
                        .getDownloadUrl()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                AndroidUtil.setChatImage(context, task.getResult(), holder.rightChatImageView);
                            }
                        });
            } else if (model.getMessageType().equals("text")) {
                holder.imageRightChatLayout.setVisibility(View.GONE);
                holder.rightChatImageView.setVisibility(View.GONE);
                holder.rightChatTextview.setVisibility(View.VISIBLE);
                holder.textRightChatLayout.setVisibility(View.VISIBLE);
                holder.rightChatTextview.setText(model.getMessage());
            }

            // Display current user's profile picture
            holder.leftProfilePic.setVisibility(View.GONE);
            holder.rightProfilePic.setVisibility(View.VISIBLE);
            FirebaseUtil.getCurrentProfilePicStorageRef()
                    .getDownloadUrl()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            AndroidUtil.setProfilePic(context, task.getResult(), holder.rightProfilePic);
                        }
                    });
        } else {
            // Hide views for messages sent by other users
            holder.textRightChatLayout.setVisibility(View.GONE);
            holder.imageRightChatLayout.setVisibility(View.GONE);
            holder.rightChatTextview.setVisibility(View.GONE);
            holder.rightChatImageView.setVisibility(View.GONE);

            // Determine message type and display appropriate views
            if (model.getMessageType().equals("image")) {
                holder.imageLeftChatLayout.setVisibility(View.VISIBLE);
                holder.leftChatImageView.setVisibility(View.VISIBLE);
                holder.textLeftChatLayout.setVisibility(View.GONE);
                holder.leftChatTextview.setVisibility(View.GONE);

                // Load and display image from Firebase storage
                FirebaseUtil.getChatroomImageStorageRef(model.getChatroomId(), model.getMessage())
                        .getDownloadUrl()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                AndroidUtil.setChatImage(context, task.getResult(), holder.leftChatImageView);
                            }
                        });
            } else if (model.getMessageType().equals("text")) {
                holder.imageLeftChatLayout.setVisibility(View.GONE);
                holder.textLeftChatLayout.setVisibility(View.VISIBLE);
                holder.leftChatTextview.setVisibility(View.VISIBLE);
                holder.leftChatImageView.setVisibility(View.GONE);
                holder.leftChatTextview.setText(model.getMessage());
            }

            // Display profile picture of the sender
            holder.rightProfilePic.setVisibility(View.GONE);
            holder.leftProfilePic.setVisibility(View.VISIBLE);
            FirebaseUtil.getOtherProfilePicStorageRef(model.getSenderId())
                    .getDownloadUrl()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            AndroidUtil.setProfilePic(context, task.getResult(), holder.leftProfilePic);
                        }
                    });
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout textLeftChatLayout, textRightChatLayout, imageLeftChatLayout, imageRightChatLayout;
        TextView leftChatTextview, rightChatTextview;

        ImageView leftProfilePic, rightProfilePic, leftChatImageView, rightChatImageView;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            textLeftChatLayout = itemView.findViewById(R.id.text_left_chat_layout);
            textRightChatLayout = itemView.findViewById(R.id.text_right_chat_layout);
            imageLeftChatLayout = itemView.findViewById(R.id.image_left_chat_layout);
            imageRightChatLayout = itemView.findViewById(R.id.image_right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_text_view);
            rightChatTextview = itemView.findViewById(R.id.right_chat_text_view);
            leftChatImageView = itemView.findViewById(R.id.left_chat_image_view);
            rightChatImageView = itemView.findViewById(R.id.right_chat_image_view);
            leftProfilePic = itemView.findViewById(R.id.left_icon_chat);
            rightProfilePic = itemView.findViewById(R.id.right_icon_chat);

        }
    }
}