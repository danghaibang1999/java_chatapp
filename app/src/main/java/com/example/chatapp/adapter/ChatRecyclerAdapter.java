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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    private final List<JSONObject> messages = new ArrayList<>();

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        if (model.getSenderId().equals(FirebaseUtil.currentUserUid())) {
            holder.textLeftChatLayout.setVisibility(View.GONE);
            holder.imageLeftChatLayout.setVisibility(View.GONE);
            if (model.getMessageType().equals("image")) {
                holder.imageRightChatLayout.setVisibility(View.VISIBLE);
                holder.textRightChatLayout.setVisibility(View.GONE);
                FirebaseUtil.getChatroomImageStorageRef(model.getChatroomId(), model.getMessage()).getDownloadUrl()
                        .addOnCompleteListener(t -> {
                            if (t.isSuccessful()) {
                                AndroidUtil.setChatImage(context, t.getResult(), holder.rightChatImageView);
                            }
                        });
            } else if (model.getMessageType().equals("text")) {
                holder.imageRightChatLayout.setVisibility(View.GONE);
                holder.rightChatImageView.setVisibility(View.GONE);
                holder.rightChatTextview.setVisibility(View.VISIBLE);
                holder.rightChatTextview.setText(model.getMessage());
            }
            holder.leftProfilePic.setVisibility(View.GONE);
            holder.rightProfilePic.setVisibility(View.VISIBLE);
            FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                    .addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {
                            AndroidUtil.setProfilePic(context, t.getResult(), holder.rightProfilePic);
                        }
                    });
        } else {
            holder.textRightChatLayout.setVisibility(View.GONE);
            holder.imageRightChatLayout.setVisibility(View.GONE);
            if (model.getMessageType().equals("image")) {
                holder.imageLeftChatLayout.setVisibility(View.VISIBLE);
                holder.textLeftChatLayout.setVisibility(View.GONE);
                FirebaseUtil.getChatroomImageStorageRef(model.getChatroomId(), model.getMessage()).getDownloadUrl()
                        .addOnCompleteListener(t -> {
                            if (t.isSuccessful()) {
                                AndroidUtil.setChatImage(context, t.getResult(), holder.leftChatImageView);
                            }
                        });
            } else if (model.getMessageType().equals("text")) {
                holder.imageLeftChatLayout.setVisibility(View.GONE);
                holder.textLeftChatLayout.setVisibility(View.VISIBLE);
                holder.leftChatTextview.setVisibility(View.VISIBLE);
                holder.leftChatTextview.setText(model.getMessage());
            }
            holder.rightProfilePic.setVisibility(View.GONE);
            holder.leftProfilePic.setVisibility(View.VISIBLE);

            FirebaseUtil.getOtherProfilePicStorageRef(model.getSenderId()).getDownloadUrl()
                    .addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {
                            AndroidUtil.setProfilePic(context, t.getResult(), holder.leftProfilePic);
                        }
                    });
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    public void addItem(JSONObject jsonObject) {
        messages.add(jsonObject);
        notifyDataSetChanged();
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