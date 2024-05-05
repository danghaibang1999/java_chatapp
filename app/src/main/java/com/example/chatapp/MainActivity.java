package com.example.chatapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.chatapp.login.LoginAddressActivity;
import com.example.chatapp.manager.ApiManager;
import com.example.chatapp.models.Conversation;
import com.example.chatapp.models.Friend;
import com.example.chatapp.models.FriendRequest;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.DataStorageManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.event.CallEndListener;
import com.zegocloud.uikit.prebuilt.call.event.ErrorEventsListener;
import com.zegocloud.uikit.prebuilt.call.event.SignalPluginConnectListener;
import com.zegocloud.uikit.prebuilt.call.event.ZegoCallEndReason;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallInvitationData;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider;
import com.zegocloud.uikit.service.express.IExpressEngineEventHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    ImageButton groupButton;
    ChatFragment chatFragment;
    ProfileSettingFragment settingFragment;
    GroupChatFragment groupFragment;
    ContactFragment contactFragment;
    TextView mainToolbarTitle;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        initializeViews();
        initializeFragments();
        setupListeners();

        String accessToken = getAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(MainActivity.this, LoginAddressActivity.class));
            finish();
            return;
        } else {
            // Fetch current user profile
            getCurrentUserProfile(accessToken);
        }
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
    }

    private void initializeViews() {
        // Initialize views...
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
    }

    public String getAccessToken() {
        return sharedPreferences.getString("accessToken", null);
    }

    private void initializeFragments() {
        // Initialize fragments...
        chatFragment = new ChatFragment();
        settingFragment = new ProfileSettingFragment();
        groupFragment = new GroupChatFragment();
        contactFragment = new ContactFragment();
    }

    private void setupListeners() {
        // Set up listeners for views...
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
                searchButton.setVisibility(View.VISIBLE);
                groupButton.setVisibility(View.VISIBLE);
            } else {
                searchButton.setVisibility(View.GONE);
                groupButton.setVisibility(View.GONE);
            }
            return true;
        });
    }

    private void getCurrentUserProfile(String accessToken) {
        // Call getProfile method
        ApiManager.getInstance(this).getProfile(accessToken, new ApiManager.ApiListener() {
            @Override
            public void onResponse(JSONObject response) {
                // Parse and save the profile locally
                try {
                    String name = response.getString("name");
                    String email = response.getString("email");
                    String avatarUrl = response.getString("avatar_url");
                    String id = response.getString("id");
                    String lastLoggedIn = response.getString("last_logged_in");
                    String role = response.getString("role");
                    String status = response.getString("status");
                    String username = response.getString("username");
                    String phone = response.getString("phone");

                    // Now handle conversations, friend requests, and friends arrays
                    JSONArray conversationsArray = response.optJSONArray("conversations");
                    if (conversationsArray == null) {
                        conversationsArray = new JSONArray();
                    }
                    List<Conversation> conversations = new ArrayList<>();
                    for (int i = 0; i < conversationsArray.length(); i++) {
                        JSONObject convObject = conversationsArray.getJSONObject(i);
                        Conversation conversation = new Conversation();
                        conversation.setId(convObject.getString("id"));
                        conversation.setName(convObject.getString("name"));
                        conversation.setCreatedAt(convObject.getString("created_at"));
                        conversation.setUpdatedAt(convObject.getString("updated_at"));
                        conversations.add(conversation);
                    }

                    // Handle friend requests
                    JSONArray friendRequestsArray = response.optJSONArray("friend_requests");
                    if (friendRequestsArray == null) {
                        friendRequestsArray = new JSONArray();
                    }
                    List<FriendRequest> friendRequests = new ArrayList<>();
                    for (int i = 0; i < friendRequestsArray.length(); i++) {
                        JSONObject friendRequestObject = friendRequestsArray.getJSONObject(i);
                        FriendRequest friendRequest = new FriendRequest();
                        // Parse attributes and set them to friend request object
                        friendRequests.add(friendRequest);
                    }

                    // Handle friends
                    JSONArray friendsArray = response.optJSONArray("friends");
                    if (friendsArray == null) {
                        friendsArray = new JSONArray();
                    }
                    List<Friend> friends = new ArrayList<>();
                    for (int i = 0; i < friendsArray.length(); i++) {
                        JSONObject friendObject = friendsArray.getJSONObject(i);
                        Friend friend = new Friend();
                        // Parse attributes and set them to friend object
                        friends.add(friend);
                    }

                    // Save the profile locally
                    saveProfileLocally(name, email, phone, avatarUrl, id, lastLoggedIn, role, status, username, conversations, friendRequests, friends);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(MainActivity.this, new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveProfileLocally(String name, String email, String phone, String avatarUrl, String id,
                                    String lastLoggedIn, String role, String status, String username,
                                    List<Conversation> conversations, List<FriendRequest> friendRequests, List<Friend> friends) {
        // Here you can save the profile information locally using SharedPreferences, Room Database, etc.
        // For example, using SharedPreferences:
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("avatar_url", avatarUrl);
        editor.putString("id", id);
        editor.putString("last_logged_in", lastLoggedIn);
        editor.putString("role", role);
        editor.putString("status", status);
        editor.putString("username", username);
        editor.putString("phone", phone);
        // Save conversations, friend requests, and friends using Gson or any other serialization method
        // Then put them into SharedPreferences
        // Initialize DataStorageManager
        DataStorageManager dataStorageManager = new DataStorageManager(this);

        // Example of saving conversations
        dataStorageManager.saveConversations(conversations);
        dataStorageManager.saveFriendRequests(friendRequests);
        dataStorageManager.saveFriends(friends);
        editor.apply();
    }

    private void handleUserResponse(JSONObject response) {
        try {
            if (!response.toString().isEmpty()) {
                // Process user profile response...
                UserModel currentUser = new UserModel();
                currentUser.setId(response.getString("id"));
                currentUser.setName(response.getString("name"));
                currentUser.setEmail(response.getString("email"));
                currentUser.setCreatedAt(response.getString("created_at"));
                currentUser.setUpdatedAt(response.getString("updated_at"));
                currentUser.setLastLoggedIn(response.getString("last_logged_in"));
                currentUser.setRole(response.getString("role"));
                currentUser.setStatus(response.getString("status"));
                currentUser.setUsername(response.getString("username"));
                currentUser.setAvatarUrl(response.getString("avatar_url"));
                initCallInviteService(
                        1363654772,
                        "64b6d2ac0af446ebcb8e737c8e03512bdbe3bbb09c4ee655094da8daef0acb51",
                        currentUser.getId(),
                        currentUser.getUsername()
                );
                bottomNavigationView.setSelectedItemId(R.id.menu_chat);
            } else {
                Toast.makeText(MainActivity.this, "Invalid user cell or password", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error parsing user profile", Toast.LENGTH_LONG).show();
        }
    }

    private void handleErrorResponse(VolleyError error) {
        Toast.makeText(MainActivity.this, new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
    }

    public void initCallInviteService(long appID, String appSign, String userID, String userName) {

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        callInvitationConfig.provider = new ZegoUIKitPrebuiltCallConfigProvider() {
            @Override
            public ZegoUIKitPrebuiltCallConfig requireConfig(ZegoCallInvitationData invitationData) {
                ZegoUIKitPrebuiltCallConfig config = getConfig(invitationData);
                return config;
            }
        };

        ZegoUIKitPrebuiltCallService.events.setErrorEventsListener(new ErrorEventsListener() {
            @Override
            public void onError(int errorCode, String message) {
                Timber.d("onError() called with: errorCode = [" + errorCode + "], message = [" + message + "]");
            }
        });
        ZegoUIKitPrebuiltCallService.events.invitationEvents.setPluginConnectListener(
                new SignalPluginConnectListener() {
                    @Override
                    public void onSignalPluginConnectionStateChanged(ZIMConnectionState state, ZIMConnectionEvent event,
                                                                     JSONObject extendedData) {
                        Timber.d(
                                "onSignalPluginConnectionStateChanged() called with: state = [" + state + "], event = [" + event
                                        + "], extendedData = [" + extendedData + "]");
                    }
                });

        ZegoUIKitPrebuiltCallService.init(getApplication(), appID, appSign, userID, userName,
                callInvitationConfig);

        ZegoUIKitPrebuiltCallService.events.callEvents.setCallEndListener(new CallEndListener() {
            @Override
            public void onCallEnd(ZegoCallEndReason callEndReason, String jsonObject) {
                Timber.d("onCallEnd() called with: callEndReason = [" + callEndReason + "], jsonObject = [" + jsonObject
                        + "]");
            }
        });

        ZegoUIKitPrebuiltCallService.events.callEvents.setExpressEngineEventHandler(
                new IExpressEngineEventHandler() {
                    @Override
                    public void onRoomStateChanged(String roomID, ZegoRoomStateChangedReason reason, int errorCode,
                                                   JSONObject extendedData) {
                        Timber.d("onRoomStateChanged() called with: roomID = [" + roomID + "], reason = [" + reason
                                + "], errorCode = [" + errorCode + "], extendedData = [" + extendedData + "]");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure you want to sign out? After signing out, you won't receive offline calls.");
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            MainActivity.super.onBackPressed(); // Call superclass implementation
        });
        builder.create().show();
    }

    public ZegoUIKitPrebuiltCallConfig getConfig(ZegoCallInvitationData invitationData) {
        boolean isVideoCall = invitationData.type == ZegoInvitationType.VIDEO_CALL.getValue();
        boolean isGroupCall = invitationData.invitees.size() > 1;
        ZegoUIKitPrebuiltCallConfig callConfig;
        if (isVideoCall && isGroupCall) {
            callConfig = ZegoUIKitPrebuiltCallConfig.groupVideoCall();
        } else if (!isVideoCall && isGroupCall) {
            callConfig = ZegoUIKitPrebuiltCallConfig.groupVoiceCall();
        } else if (!isVideoCall) {
            callConfig = ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall();
        } else {
            callConfig = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();
        }
        return callConfig;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // when use minimize feature,it you swipe close this activity,call endCall()
        // to make sure call is ended and the float window is dismissed
        ZegoUIKitPrebuiltCallService.endCall();
    }
}