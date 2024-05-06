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

    private ProgressBar progressBar;
    private Button loginBtn;
    private EditText emailInput;
    private EditText passwordInput;
    private TextView forgotPassword;
    private TextView signUp;
    private TextView loginWithPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main_screen);

        initializeViews();

        loginBtn.setOnClickListener(v -> {
            loginUser();
        });

        forgotPassword.setOnClickListener(v -> {
            navigateToForgotPassword();
        });

        signUp.setOnClickListener(v -> {
            navigateToSignUp();
        });

        loginWithPhone.setOnClickListener(v -> {
            navigateToPhoneLogin();
        });
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.login_progress_bar);
        loginBtn = findViewById(R.id.login_btn);
        emailInput = findViewById(R.id.login_email_address);
        passwordInput = findViewById(R.id.login_password);
        forgotPassword = findViewById(R.id.forgot_password_text_view);
        signUp = findViewById(R.id.sign_up_text_view);
        loginWithPhone = findViewById(R.id.login_with_phone_number_text_view);
    }

    private void loginUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            setInProgress(true);
            // Perform login
            // On success, call setInProgress(false);
            // On failure, call setInProgress(false);
        }
    }

    private void navigateToForgotPassword() {
        Intent intent = new Intent(this, LoginPhoneNumberActivity.class);
        intent.putExtra("isForgotPassword", true);
        startActivity(intent);
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    private void navigateToPhoneLogin() {
        Intent intent = new Intent(this, LoginPhoneNumberActivity.class);
        startActivity(intent);
    }

    private void setInProgress(boolean isProgress) {
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        loginBtn.setVisibility(isProgress ? View.GONE : View.VISIBLE);
    }
}
