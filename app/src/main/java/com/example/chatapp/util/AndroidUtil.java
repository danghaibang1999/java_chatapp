package com.example.chatapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.chatapp.models.UserModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AndroidUtil {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel userModel) {
        intent.putExtra("username", userModel.getUsername());
        intent.putExtra("phone", userModel.getPhone());
        intent.putExtra("id", userModel.getId());
        intent.putExtra("avatar_url", userModel.getAvatarUrl());
        intent.putExtra("role", userModel.getRole());
        intent.putExtra("status", userModel.getStatus());
        intent.putExtra("name", userModel.getName());
    }

    public static UserModel getCurrentUserModel(Context context) {
        UserModel userModel = new UserModel();
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userModel.setUsername(sharedPreferences.getString("username", ""));
        userModel.setPhone(sharedPreferences.getString("phone", ""));
        userModel.setId(sharedPreferences.getString("id", ""));
        userModel.setAvatarUrl(sharedPreferences.getString("avatar_url", ""));
        userModel.setRole(sharedPreferences.getString("role", ""));
        userModel.setStatus(sharedPreferences.getString("status", ""));
        userModel.setName(sharedPreferences.getString("name", ""));

        return userModel;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).diskCacheStrategy(DiskCacheStrategy.ALL).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    public static void setChatImage(Context context, Uri imageUrl, ImageView imageView) {
        Glide.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    // Method to convert timestamp string to hh:mm format
    public static String formatTime(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            Date date = inputFormat.parse(timestamp);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Return the original timestamp if parsing fails
        return timestamp;
    }

    public static UserModel getOtherUserModelAsIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setId(intent.getStringExtra("userId"));
        userModel.setAvatarUrl(intent.getStringExtra("avatar_url"));
        userModel.setRole(intent.getStringExtra("role"));
        userModel.setStatus(intent.getStringExtra("status"));
        userModel.setName(intent.getStringExtra("name"));
        // Add other attributes if needed
        return userModel;
    }
}
