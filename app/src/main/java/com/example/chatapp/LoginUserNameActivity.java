package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.models.UserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUserNameActivity extends AppCompatActivity {

    EditText userNameInput;
    Button letmeInBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_user_name);

        userNameInput = findViewById(R.id.login_user_name);
        letmeInBtn = findViewById(R.id.login_let_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        getUserName();


        phoneNumber = getIntent().getExtras().getString("phone");

        letmeInBtn.setOnClickListener(v -> setUserName());
    }

    void setUserName() {

        String userName = userNameInput.getText().toString();
        if (userName.isEmpty() || userName.length() < 3) {
            userNameInput.setError("User name is required");
            return;
        }
        setInProgress(true);
        if (userModel == null) {
            userModel = new UserModel(phoneNumber, userName, Timestamp.now(), FirebaseUtil.currentUserUid());
        } else {
            userModel.setUsername(userName);
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginUserNameActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    void getUserName() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        userNameInput.setText(userModel.getUsername());
                    }
                }
            }
        });
    }

    private void setInProgress(boolean isProgress) {
        if (isProgress) {
            progressBar.setVisibility(View.VISIBLE);
            letmeInBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            letmeInBtn.setVisibility(View.VISIBLE);
        }
    }
}