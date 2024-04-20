package com.example.chatapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.AndroidUtil;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    EditText messageInput;
    ImageButton sendMessageButton;
    ImageButton backButton;
    TextView otherUserName;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelAsIntent(getIntent());
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageButton = findViewById(R.id.message_send_btn);
        backButton = findViewById(R.id.back_btn);
        otherUserName = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        backButton.setOnClickListener((v -> {
            onBackPressed();
        }));
        otherUserName.setText(otherUser.getUsername());

    }
}