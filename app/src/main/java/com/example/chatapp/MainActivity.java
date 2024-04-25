package com.example.chatapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.util.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    ImageButton groupButton;
    ChatFragment chatFragment;
    ProfileSettingFragment settingFragment;
    GroupChatFragment groupFragment;
    ContactFragment contactFragment;
    TextView mainToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatFragment = new ChatFragment();
        settingFragment = new ProfileSettingFragment();
        groupFragment = new GroupChatFragment();
        contactFragment = new ContactFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);
        groupButton = findViewById(R.id.group_add_btn);
        mainToolbarTitle = findViewById(R.id.main_toolbar_title);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_group_add);
        groupButton.setOnClickListener(v -> {
            dialog.show();
        });

        searchButton.setOnClickListener((v -> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
        }));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_chat) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
            } else if (item.getItemId() == R.id.profile_settings) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, settingFragment).commit();
            } else if (item.getItemId() == R.id.menu_group_chat) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, groupFragment).commit();
            } else if (item.getItemId() == R.id.menu_contact) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, contactFragment).commit();
            }
            mainToolbarTitle.setText(item.getTitle());
            if (item.getItemId() == R.id.menu_chat || item.getItemId() == R.id.menu_group_chat) {
                searchButton.setVisibility(searchButton.VISIBLE);
                groupButton.setVisibility(groupButton.VISIBLE);
            } else {
                searchButton.setVisibility(searchButton.GONE);
                groupButton.setVisibility(groupButton.GONE);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
        getFCMTokens();
    }

    private void getFCMTokens() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                FirebaseUtil.currentUserDetails().update("fcmToken", token);
            }
        });
    }
}