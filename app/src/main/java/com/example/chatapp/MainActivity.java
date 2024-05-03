package com.example.chatapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatapp.models.UserModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

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
    public static UserModel currentUser;
    private static final String USER_URL = "http://34.92.61.98/api/user/profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String access_token = getIntent().getStringExtra("access_token");
        getCurrentUserProfile(access_token);

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
                searchButton.setVisibility(View.VISIBLE);
                groupButton.setVisibility(View.VISIBLE);
            } else {
                searchButton.setVisibility(View.GONE);
                groupButton.setVisibility(View.GONE);
            }
            return true;
        });
    }

    private void getCurrentUserProfile(String access_token) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, USER_URL, null, response -> {
                    //If we are getting success from server
                    try {
                        if (!response.toString().isEmpty()) {
                            currentUser = new UserModel(response.getString("phone"),
                                    response.getString("name"), Timestamp.now(), response.getString("id"));
                            initCallInviteService(1363654772,
                                    "64b6d2ac0af446ebcb8e737c8e03512bdbe3bbb09c4ee655094da8daef0acb51",
                                    currentUser.getUserId(), currentUser.getUsername());
                            bottomNavigationView.setSelectedItemId(R.id.menu_chat);
                        } else {
                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(MainActivity.this, "Invalid user cell or password", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },

                        error -> {
                            try {
                                Toast.makeText(MainActivity.this, new String(error.networkResponse.data, "UTF-8"), Toast.LENGTH_LONG).show();
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + access_token);
                return headers;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sign Out");
        builder.setMessage("Are you sure to Sign Out?After Sign out you can't receive offline calls");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
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