package com.example.chatapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;

public class LoginMainScreenActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button loginBtn;
    EditText emailInput;
    EditText passwordInput;
    TextView forgotPassword;
    TextView signUp;
    TextView loginWithPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main_screen);

        progressBar = findViewById(R.id.login_progress_bar);
        loginBtn = findViewById(R.id.login_btn);
        emailInput = findViewById(R.id.login_email_address);
        passwordInput = findViewById(R.id.login_password);
        forgotPassword = findViewById(R.id.forgot_password_text_view);
        signUp = findViewById(R.id.sign_up_text_view);
        loginWithPhone = findViewById(R.id.login_with_phone_number_text_view);

        setInProgress(false);
        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            login(email, password);
        });

        forgotPassword.setOnClickListener(v -> {
            // Open forgot password activity
        });

        signUp.setOnClickListener(v -> {
            // Open sign up activity
        });

        loginWithPhone.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginPhoneNumberActivity.class);
            startActivity(intent);
        });
    }

    private void login(String email, String password) {
        setInProgress(true);
    }

    private void setInProgress(boolean isProgress) {
        if (isProgress) {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }
}