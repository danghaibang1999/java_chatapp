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
        if (model.getSenderId().equals(FirebaseUtil.currentUserUid())) {
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
            holder.leftProfilePic.setVisibility(View.GONE);
            holder.rightProfilePic.setVisibility(View.VISIBLE);
            FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                    .addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {
                            AndroidUtil.setProfilePic(context, t.getResult(), holder.rightProfilePic);
                        }
                    });
        } else {
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
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

    class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextview, rightChatTextview;

        ImageView leftProfilePic, rightProfilePic;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_text_view);
            rightChatTextview = itemView.findViewById(R.id.right_chat_text_view);
            leftProfilePic = itemView.findViewById(R.id.left_icon_chat);
            rightProfilePic = itemView.findViewById(R.id.right_icon_chat);

        }
    }
}