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

import com.example.chatapp.adapter.ChatRecyclerAdapter;
import com.example.chatapp.models.ChatroomModel;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.AndroidUtil;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity {

    private final String SERVER_PATH = "https://34.92.61.98/api/ws";
    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
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

        //initiateSocketConnection();
    }


    private void initializeView() {
        otherUser = AndroidUtil.getUserModelAsIntent(getIntent());
//        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserUid(), otherUser.getUserId());
        messageInput = findViewById(R.id.chat_message_input);
        sendImageMessageButton = findViewById(R.id.message_image_send_btn);
        sendMessageButton = findViewById(R.id.message_send_btn);
        backButton = findViewById(R.id.back_btn);
        otherUserName = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        profilePic = findViewById(R.id.profile_pic_image_view);
        videoCallButton = findViewById(R.id.chat_video_btn);

        otherUserName.setText(otherUser.getUsername());
//        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
//                .addOnCompleteListener(t -> {
//                    if (t.isSuccessful()) {
//                        AndroidUtil.setProfilePic(this, t.getResult(), profilePic);
//                    }
//                });

        voiceCallButton = findViewById(R.id.chat_call_btn);
        voiceCallButton.setIsVideoCall(false);

        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        //        newVoiceCall.setResourceID("zegouikit_call");
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

        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        //        newVoiceCall.setResourceID("zegouikit_call");
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
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickerLauncher.launch(intent);
                            return null;
                        }
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

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", "");
                jsonObject.put("list_user", "[]");

                JSONObject chat = new JSONObject();
                chat.put("from", MainActivity.currentUser.getId());
                chat.put("to", chatroomId);
                chat.put("message", message);

                jsonObject.put("chat", chat);

                webSocket.send(jsonObject.toString());

                jsonObject.put("isSent", true);
                chatRecyclerAdapter.addItem(jsonObject);

                recyclerView.smoothScrollToPosition(chatRecyclerAdapter.getItemCount() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        // Setup chat recycler view
//        Query query = FirebaseUtil.getChatroomMessagesReference(chatroomId)
//                .orderBy("timestamp", Query.Direction.DESCENDING);
//
//        FirestoreRecyclerOptions<ChatMessageModel> options
//                = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
//                .setQuery(query, ChatMessageModel.class).build();

        chatRecyclerAdapter = new ChatRecyclerAdapter(null, getApplicationContext());
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

    public String createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss").format(now));
        return chatroomId + '_' + id;
    }

    private void initWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("ws://your_server_url/api/ws")
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
        JSONObject subscribeMessage = new JSONObject();
        try {
            subscribeMessage.put("type", "subscribe");
            subscribeMessage.put("list_user", new JSONArray().put("newChat"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendMessage(subscribeMessage.toString());
    }

    private void handleMessage(String message) {
        try {
            JSONObject jsonMessage = new JSONObject(message);
            String type = jsonMessage.getString("type");
            switch (type) {
                case "newChat":
                    // Handle new chat event
                    break;
                case "chat":
                    // Handle chat message
                    break;
                default:
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        } else {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }
}