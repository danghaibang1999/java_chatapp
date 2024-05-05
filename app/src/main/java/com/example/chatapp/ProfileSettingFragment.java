package com.example.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.example.chatapp.manager.ApiManager;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.DataStorageManager;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileSettingFragment extends Fragment {

    ImageView profileImage;
    EditText usernameInput;
    EditText phoneInput;
    Button updateProfileButton;
    ProgressBar progressBar;
    TextView logoutButton;

    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;

    public ProfileSettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    selectedImageUri = data.getData();
                    AndroidUtil.setProfilePic(getContext(), selectedImageUri, profileImage);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_setting, container, false);
        profileImage = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        updateProfileButton = view.findViewById(R.id.profile_update_btn);
        progressBar = view.findViewById(R.id.login_progress_bar);
        logoutButton = view.findViewById(R.id.logout_btn);

        updateProfileButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            if (username.isEmpty() || username.length() < 3) {
                usernameInput.setError("Username is required");
                return;
            }
            currentUserModel.setUsername(username);
            setInProgress(true);
            if (selectedImageUri != null) {
//                FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                updateToFireStore();
//                            }
//                        });
            } else {
            }

        });

        logoutButton.setOnClickListener(v -> {

            String accessToken = new DataStorageManager(getContext()).getAccessToken();
            ApiManager.getInstance(getContext()).logout(accessToken, new ApiManager.ApiListener() {
                @Override
                public void onResponse(JSONObject response) {
                    removeUserLocally();
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(getContext(), new String(error.networkResponse.data,
                                    StandardCharsets.UTF_8),
                            Toast.LENGTH_LONG).show();

                }
            });
        });

        profileImage.setOnClickListener(v -> {
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
        getUserData();
        return view;
    }

    private void removeUserLocally() {
        new DataStorageManager(getContext()).clearAll();
    }


    void getUserData() {
        setInProgress(true);
        UserModel userModel = new DataStorageManager(getContext()).getCurrentUserModel();
        if (userModel != null) {
            usernameInput.setText(userModel.getUsername());
            phoneInput.setText(userModel.getPhone());
            AndroidUtil.setProfilePic(getContext(), Uri.parse(userModel.getAvatarUrl()), profileImage);
        } else {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
        }
        setInProgress(false);
    }

    private void setInProgress(boolean isProgress) {
        if (isProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileButton.setVisibility(View.VISIBLE);
        }
    }
}