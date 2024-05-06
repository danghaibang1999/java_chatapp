package com.example.chatapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUserNameActivity extends AppCompatActivity {

    private EditText userNameInput;
    private Button letmeInBtn;
    private ProgressBar progressBar;
    private String phoneNumber;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_name);
        initializeViews();
        getUserDetails();

        letmeInBtn.setOnClickListener(v -> setUserName());
    }

    private void initializeViews() {
        userNameInput = findViewById(R.id.login_user_name);
        letmeInBtn = findViewById(R.id.login_let_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        phoneNumber = getIntent().getStringExtra("phone");
    }

    private void getUserDetails() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    userModel = document.toObject(UserModel.class);
                    if (userModel != null) {
                        userNameInput.setText(userModel.getUsername());
                    }
                }
            }
        });
    }

    private void setUserName() {
        String userName = userNameInput.getText().toString().trim();
        if (userName.isEmpty() || userName.length() < 3) {
            userNameInput.setError("User name is required and should be at least 3 characters");
            return;
        }

        setInProgress(true);
        if (userModel == null) {
            userModel = new UserModel(phoneNumber, userName, Timestamp.now(), FirebaseUtil.currentUserUid());
        } else {
            userModel.setUsername(userName);
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                navigateToMainActivity();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginUserNameActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setInProgress(boolean isProgress) {
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        letmeInBtn.setVisibility(isProgress ? View.GONE : View.VISIBLE);
    }
}
