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
import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.AndroidUtil;

import java.util.List;

public class SearchUserRecyclerAdapter extends RecyclerView.Adapter<SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;
    List<UserModel> userModelList;

    public SearchUserRecyclerAdapter(@NonNull List<UserModel> options, Context context) {
        this.context = context;
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
        if (userModel.getId().equals(MainActivity.currentUser.getId())) {
            holder.usernameText.setText(userModel.getUsername() + " (Me)");
        } else {
            holder.usernameText.setText(userModel.getUsername());
        }

        holder.itemView.setOnClickListener((v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, userModel);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

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
