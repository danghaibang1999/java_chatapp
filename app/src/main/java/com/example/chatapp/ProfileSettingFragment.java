package com.example.chatapp;

import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.AndroidUtil;
import com.example.chatapp.util.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.messaging.FirebaseMessaging;

public class ProfileSettingFragment extends Fragment {

    private ImageView profileImage;
    private EditText usernameInput;
    private EditText phoneInput;
    private Button updateProfileButton;
    private ProgressBar progressBar;
    private TextView logoutButton;

    private UserModel currentUserModel;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;

    public ProfileSettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            getActivity();
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    selectedImageUri = data.getData();
                    AndroidUtil.setProfilePic(getContext(), selectedImageUri, profileImage);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_setting, container, false);
        profileImage = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        updateProfileButton = view.findViewById(R.id.profile_update_btn);
        progressBar = view.findViewById(R.id.login_progress_bar);
        logoutButton = view.findViewById(R.id.logout_btn);

        getUserData();

        updateProfileButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            if (username.isEmpty() || username.length() < 3) {
                usernameInput.setError("Username is required");
                return;
            }
            currentUserModel.setUsername(username);
            setInProgress(true);
            if (selectedImageUri != null) {
                FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                updateToFireStore();
                            }
                        });
            } else {
                updateToFireStore();
            }

        });

        logoutButton.setOnClickListener(v -> {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUtil.logout();
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        });

        profileImage.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare()
                    .compress(512).maxResultSize(512, 512)
                    .createIntent(intent -> {
                        imagePickerLauncher.launch(intent);
                        return null;
                    });
        });

        return view;
    }

    void updateToFireStore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        usernameInput.setError(null);
                        AndroidUtil.showToast(getContext(), "Profile updated successfully");
                    } else {
                        AndroidUtil.showToast(getContext(), "Failed to update profile");
                    }
                });
    }

    void getUserData() {
        setInProgress(true);

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AndroidUtil.setProfilePic(getContext(), task.getResult(), profileImage);
                    }
                });

        FirebaseUtil.currentUserDetails()
                .get()
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        currentUserModel = task.getResult().toObject(UserModel.class);
                        usernameInput.setText(currentUserModel.getUsername());
                        phoneInput.setText(currentUserModel.getPhone());
                    }
                });
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
