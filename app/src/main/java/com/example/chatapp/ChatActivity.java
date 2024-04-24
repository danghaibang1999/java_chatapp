package com.example.chatapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.adapter.ChatRecyclerAdapter;
import com.example.chatapp.models.ChatMessageModel;
import com.example.chatapp.models.ChatroomModel;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.util.Arrays;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter chatRecyclerAdapter;
    EditText messageInput;
    ImageButton sendMessageButton;
    ImageButton backButton;
    TextView otherUserName;
    RecyclerView recyclerView;
    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelAsIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserUid(), otherUser.getUserId());
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageButton = findViewById(R.id.message_send_btn);
        backButton = findViewById(R.id.back_btn);
        otherUserName = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        profilePic = findViewById(R.id.profile_pic_image_view);

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        AndroidUtil.setProfilePic(this, t.getResult(), profilePic);
                    }
                });

        backButton.setOnClickListener((v -> {
            onBackPressed();
        }));
        otherUserName.setText(otherUser.getUsername());

        sendMessageButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            messageInput.setText("");
            if (message.isEmpty()) {
                return;
            }
            sendMessageToUser(message, "text");
        });

        getOrCreateChatroomModel();

        setupChatRecyclerView();

    }

    private void setupChatRecyclerView() {
        // Setup chat recycler view
        Query query = FirebaseUtil.getChatroomMessagesReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options
                = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        chatRecyclerAdapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(chatRecyclerAdapter);
        chatRecyclerAdapter.startListening();
        chatRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMessageToUser(String message, String messageType) {
        // Send message to user
        chatroomModel.setLastMessageTime(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserUid());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, messageType, FirebaseUtil.currentUserUid(), Timestamp.now());

        FirebaseUtil.getChatroomMessagesReference(chatroomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (!task.isSuccessful()) {
                    AndroidUtil.showToast(ChatActivity.this, "Failed to send message");
                } else {
                    sendNotificationToUser(message);
                }
            }
        });
    }

    private void getOrCreateChatroomModel() {
        // Get or create chatroom model
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    chatroomModel = new ChatroomModel(chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserUid(), otherUser.getUserId()),
                            Timestamp.now(), "");
                }
                FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
            } else {
                AndroidUtil.showToast(this, "Failed to get chatroom");
            }
        });
    }

    private void sendNotificationToUser(String message) {
        // Send notification to user
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("title", currentUser.getUsername());
                    notification.put("body", message);
                    JSONObject data = new JSONObject();
                    data.put("userId", currentUser.getUserId());
                    jsonObject.put("notification", notification);
                    jsonObject.put("data", data);
                    jsonObject.put("to", otherUser.getFcmToken());
                    callApi(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer AAAASz5-HW0:APA91bErWMpjmi8PMHTI-f4SYE9BJKfQ8l40eBt_rKnW46bHeo8kB2thswsbV1h4X0evhoupi22BSQNuROXPBAsa5WonIGioWzYGhg5K0uKpauFbkHinIKgLTubSsc6pbeMxLk60ybKP")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    System.out.println(myResponse);
                }
            }
        });
    }

}