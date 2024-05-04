package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.login.LoginAddressActivity;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        new Handler().postDelayed(() -> {
            if (isLoggedIn()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginAddressActivity.class));
            }
            finish();
        }, 1000);

    }

    // Method to retrieve access token
    public String getAccessToken() {
        return sharedPreferences.getString("accessToken", null);
    }

    // Method to check if user is logged in
    private boolean isLoggedIn() {
        return getAccessToken() != null;
    }
}