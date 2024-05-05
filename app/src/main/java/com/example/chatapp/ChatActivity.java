package com.example.chatapp;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.chatapp.adapter.ChatRecyclerAdapter;
import com.example.chatapp.manager.ApiManager;
import com.example.chatapp.models.ChatMessageModel;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.DataStorageManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    String chatroomId;
    ChatRecyclerAdapter chatRecyclerAdapter;
    EditText messageInput;
    ImageButton sendMessageButton;
    ImageButton sendImageMessageButton;
    ImageButton backButton;
    TextView otherUserName;
    RecyclerView recyclerView;
    ImageView profilePic;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;
    ZegoSendCallInvitationButton voiceCallButton;
    ZegoSendCallInvitationButton videoCallButton;
    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatroomId = getIntent().getStringExtra("chatroomId");
        initializeView();
        initWebSocket();

        //initiateSocketConnection();
    }


    private void initializeView() {
        otherUser = AndroidUtil.getOtherUserModelAsIntent(getIntent());
        messageInput = findViewById(R.id.chat_message_input);
        sendImageMessageButton = findViewById(R.id.message_image_send_btn);
        sendMessageButton = findViewById(R.id.message_send_btn);
        backButton = findViewById(R.id.back_btn);
        otherUserName = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        profilePic = findViewById(R.id.profile_pic_image_view);
        videoCallButton = findViewById(R.id.chat_video_btn);

        otherUserName.setText(otherUser.getUsername());
        AndroidUtil.setProfilePic(this, Uri.parse(otherUser.getAvatarUrl()), profilePic);

        voiceCallButton = findViewById(R.id.chat_call_btn);
        voiceCallButton.setIsVideoCall(false);

        voiceCallButton.setResourceID("zego_data");
        voiceCallButton.setOnClickListener(v -> {
            String targetUserID = otherUser.getId();
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = otherUser.getUsername();
                users.add(new ZegoUIKitUser(userID, userName));
            }
            voiceCallButton.setInvitees(users);
        });

        videoCallButton = findViewById(R.id.chat_video_btn);
        videoCallButton.setIsVideoCall(true);

        videoCallButton.setResourceID("zego_data");
        videoCallButton.setOnClickListener(v -> {
            String targetUserID = otherUser.getId();
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = userID + "_name";
                users.add(new ZegoUIKitUser(userID, userName));
            }
            videoCallButton.setInvitees(users);
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    String messageId = createID();
                    selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
//                        FirebaseUtil.getChatroomImageStorageRef(chatroomId, messageId).putFile(selectedImageUri)
//                                .addOnCompleteListener(task -> {
//                                    if (task.isSuccessful()) {
//                                        sendMessageToUser(String.valueOf(messageId), "image");
//                                    }
//                                });
                    } else {
                        //sendMessageToUser(String.valueOf(messageId), "image");
                    }
                }
            }
        });

        sendImageMessageButton.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare()
                    .compress(512).maxResultSize(512, 512)
                    .createIntent(intent -> {
                        imagePickerLauncher.launch(intent);
                        return null;
                    });
        });

        backButton.setOnClickListener((v -> {
            onBackPressed();
        }));

        sendMessageButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            messageInput.setText("");
            if (message.isEmpty()) {
                return;
            }

            sendNewChatMessage(message, chatroomId, AndroidUtil.getCurrentUserModel(this).getId());

            ChatMessageModel chatMessageModel = new ChatMessageModel();
            chatMessageModel.setMessage(message);
            chatMessageModel.setSenderId(AndroidUtil.getCurrentUserModel(this).getId());
            chatMessageModel.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());
            chatRecyclerAdapter.addItem(chatMessageModel);

            recyclerView.smoothScrollToPosition(chatRecyclerAdapter.getItemCount() - 1);
        });

        setupChatRecyclerView();

        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                        String message = "We need your consent for the following permissions in order to use the offline call function properly";
                        scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny");
                    }
                }).request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                         @NonNull List<String> deniedList) {
                    }
                });
    }

    private void setupChatRecyclerView() {

        chatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<>(), otherUser, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(chatRecyclerAdapter);
        chatRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        if (chatroomId.isEmpty()) {

        } else {
            getChatMessages(1, 20, "desc", "");
        }
    }

    private void getChatMessages(int page, int pageSize, String sortType, String search) {
        DataStorageManager dataStorageManager = new DataStorageManager(this);
        String accessToken = dataStorageManager.getAccessToken();

        // Call ApiManager to get chat messages
        ApiManager.getInstance(this).getChatList(accessToken, chatroomId, page, pageSize, sortType, search, new ApiManager.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                // Parse the response and update the adapter with chat messages
                List<ChatMessageModel> chatMessages = parseChatMessages(response);
                chatRecyclerAdapter.setChatMessageModels(chatMessages);
            }

            @Override
            public void onError(VolleyError error) {
                // Handle error
            }
        });
    }

    private List<ChatMessageModel> parseChatMessages(JSONObject response) {
        // Parse JSON response and create list of ChatMessage objects
        List<ChatMessageModel> chatMessages = new ArrayList<>();
        try {
            JSONArray chatList = response.optJSONArray("list");
            for (int i = 0; i < chatList.length(); i++) {
                JSONObject chatObject = chatList.getJSONObject(i);
                String messageId = chatObject.getString("id");
                String message = chatObject.getString("message");
                String fromUserId = chatObject.getString("from");
                String toUserId = chatObject.getString("to");
                String timestamp = chatObject.getString("timestamp");

                // Create a ChatMessage object and add it to the list
                ChatMessageModel chatMessage = new ChatMessageModel(messageId, message, fromUserId, toUserId, timestamp);
                chatMessages.add(chatMessage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return chatMessages;
    }

    public String createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss").format(now));
        return chatroomId + '_' + id;
    }

    private void initWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://34.92.61.98/api/ws")
                .build();

        WebSocketListener webSocketListener = new WebSocketListener() {
            @Override
            public void onOpen(@NotNull WebSocket webSocket, okhttp3.Response response) {
                super.onOpen(webSocket, response);
                ChatActivity.this.webSocket = webSocket;
                // Send a message to subscribe to new chat events
                subscribeToNewChatEvents();
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                // Handle incoming messages
                handleMessage(text);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, okhttp3.Response response) {
                super.onFailure(webSocket, t, response);
            }
        };

        webSocket = client.newWebSocket(request, webSocketListener);
    }

    private void subscribeToNewChatEvents() {
        // Subscribe to new chat events, if required
    }

    private void handleMessage(String message) {
        // Handle incoming messages here
        // For example, update the UI with the received message
    }

    private void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    // Call this method when you want to send a chat message
    private void sendChatMessage(String message) {
        // Construct the chat message format according to your WebSocket API
        JSONObject chatMessage = new JSONObject();
        try {
            chatMessage.put("type", "chat");
            chatMessage.put("message", message);
            // Add other necessary fields as per your API requirements
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Convert the JSON object to string and send it over WebSocket
        sendMessage(chatMessage.toString());
    }

    private void sendNewChatMessage(String message, String conversationId, String fromUserId) {
        // Construct the new chat message format according to your WebSocket API
        JSONObject newChatMessage = new JSONObject();
        JSONObject chatData = new JSONObject();
        try {
            newChatMessage.put("type", "");

            chatData.put("from", fromUserId);
            chatData.put("to", conversationId);
            chatData.put("message", message);

            newChatMessage.put("chat", chatData);

            // Check if it's a new conversation
            if (conversationId == null || conversationId.isEmpty()) {
                newChatMessage.put("type", "newChat");
                JSONArray listUser = new JSONArray();
                // Add user IDs to the list_user array
                // For example, if you're creating a conversation with user IDs userId1 and userId2
                listUser.put(otherUser.getId());
                listUser.put(AndroidUtil.getCurrentUserModel(this).getId());
                newChatMessage.put("list_user", listUser);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Convert the JSON object to string and send it over WebSocket
        sendMessage(newChatMessage.toString());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }
}