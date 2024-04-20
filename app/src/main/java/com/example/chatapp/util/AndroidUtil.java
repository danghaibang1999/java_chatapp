package com.example.chatapp.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.chatapp.models.UserModel;

public class AndroidUtil {
    public static void showToast (Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel userModel) {
        intent.putExtra("username", userModel.getUsername());
        intent.putExtra("phone", userModel.getPhone());
        intent.putExtra("userId", userModel.getUserId());
    }

    public static UserModel getUserModelAsIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return  userModel;
    }
}
